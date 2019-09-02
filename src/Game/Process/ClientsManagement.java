
package Game.Process;

import Game.DataClasses.EdgeClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.IntersectionResultClass;
import Game.DataClasses.NetworkKeys;
import Game.DataClasses.PlayerClass;
import Game.DataClasses.PlayerModelParametersClass;
import Game.DataClasses.Vector2;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.metal.MetalBorders;

public class ClientsManagement 
{
    public static void ClientJoinToTheGame(User user)
    {
        //int userModelIndex = (int)GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("GetUserModelIndex", user.getId());
        int userModelIndex = (int)GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("GetUserModelIndex", user.getSession().getId());
        if(userModelIndex != -1)  // -1 - invalid value
        {
            PlayerModelParametersClass playerParams = GlobalGameData.serverConfig.GetPlayerModelParameters(userModelIndex);
            PlayerClass newPlayer = new PlayerClass(user, user.getName(), playerParams.GetPlayerSpeed(), 
                    userModelIndex, 
                    playerParams.GetCoolDawn(), playerParams.GetPlayerRadius(), playerParams.GetPlayerLife());
            //newPlayer.GetLocation().SetRandomLocation(GlobalGameData.serverConfig.GetPlayerEmitRandomRadius());
            newPlayer.GetLocation().SetPosition(GlobalGameData.startPoints.GetPoint());
            GlobalGameData.clients.put(newPlayer.GetId(), newPlayer);

            NetworkDataProcess.SetPlayerState(newPlayer, true, true);
        }
        else
        {//user does not registered with some model_index, disconnect him
            GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("DisconnectUser", user);
        }
    }
    
    public static void ClientLeaveTheGame(User user)
    {
        int userId = user.getId();
        int sessionId = user.getSession().getId();
        GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("RemoveUserData", sessionId);
        RemovePlayer(userId);
    }
    
    public static void RemovePlayer(int userId)
    {
        if(GlobalGameData.clients.containsKey(userId))
        {
            GlobalGameData.clients.remove(userId);    
        }
    }
    
    public static List<UserVariable> GetClietnVariables(PlayerClass player, boolean isFirstCall)
    {
        List<UserVariable> vars = new ArrayList<UserVariable>();
        
        if(isFirstCall)
        {
            vars.add(new SFSUserVariable(NetworkKeys.key_name, player.GetName()));
            vars.add(new SFSUserVariable(NetworkKeys.key_client_modelIndex, player.GetModelIndex()));
            vars.add(new SFSUserVariable(NetworkKeys.key_client_fireCoolDawn, player.GetFireController().GetCoolDawn()));
            vars.add(new SFSUserVariable(NetworkKeys.key_person_radius, player.GetRadius()));
            vars.add(new SFSUserVariable(NetworkKeys.key_person_maxLife, player.GetMaxLife()));
        }
        vars.add(new SFSUserVariable(NetworkKeys.key_speed, player.GetMovement().GetSpeed()));
        vars.add(new SFSUserVariable(NetworkKeys.key_location_position_x, player.GetPosition().GetX()));
        vars.add(new SFSUserVariable(NetworkKeys.key_location_position_y, player.GetPosition().GetY()));
        vars.add(new SFSUserVariable(NetworkKeys.key_client_isMove, player.GetMovement().IsMove()));
        //vars.add(new SFSUserVariable(NetworkKeys.key_client_directionIndex, player.GetMovement().DirectionIndex()));
        vars.add(new SFSUserVariable(NetworkKeys.key_client_moveAngle, player.GetMovement().GetMoveAngle()));
        vars.add(new SFSUserVariable(NetworkKeys.key_client_angle, player.GetAngle()));
        vars.add(new SFSUserVariable(NetworkKeys.key_person_life, player.GetLife()));
        
        return vars;
    }
    
    public static void ClientChangeMovement(int clientId, int dirIndex, float playerAngle)
    {
        if(GlobalGameData.clients.containsKey(clientId))
        {
            PlayerClass player = GlobalGameData.clients.get(clientId);
            if(!player.GetIsDead())
            {
                player.GetMovement().SetDirIndex(dirIndex);
                player.SetAngle(playerAngle);
            }
        }
    }
    
    public static void ClientFire(int clientId, float posX, float posY, float angle)  // posX and posY - coordinates of the players cursor
    {
        Vector2 cursorPosition = new Vector2(posX, posY);
        if(GlobalGameData.clients.containsKey(clientId))
        {
            PlayerClass player = GlobalGameData.clients.get(clientId);
            player.SetAngle(angle);
            //Check collisions on the bullet trajectory
            EdgeClass bulletEdge = new EdgeClass(player.GetPosition(), cursorPosition);
            IntersectionResultClass collisionResult = GlobalGameData.collisionMap.GetIntersection(bulletEdge);
            int bulletType = player.GetModelIndex();
            boolean isBulletEffectTarget = GlobalGameData.serverConfig.GetBulletParameters(bulletType).IsDamageOnlyTarget();
            /*if(!isBulletEffectTarget || (isBulletEffectTarget && !collisionResult.isIntersection))
            {//there are no intersections and bullet damage only target point or bullet is line-damager
                if(!player.GetIsDead() && player.GetFireController().TryStartbullet())
                {
                    MonstersManagement.AddBullet(player.GetPosition(), cursorPosition, bulletType, player, collisionResult, 0);
                }
            }
            else
            {
                if(isBulletEffectTarget && collisionResult.isIntersection)
                {//Notify client that target is invisible
                    NetworkDataProcess.SendClientResponse(player.GetUser(), 0);
                }
            }*/
            if(!player.GetIsDead() && player.GetFireController().TryStartbullet())
            {
                //recalculate cursor point
                if(isBulletEffectTarget && collisionResult.isIntersection)
                {
                    cursorPosition = collisionResult.intersectionPoint;
                }
                MonstersManagement.AddBullet(player.GetPosition(), cursorPosition, bulletType, player, collisionResult, 0);
            }
        }
    }
}
