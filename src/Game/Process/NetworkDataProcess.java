
package Game.Process;

import Game.DataClasses.BulletClass;
import Game.DataClasses.BulletHitDataClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.MonsterClass;
import Game.DataClasses.NetworkKeys;
import Game.DataClasses.PlayerClass;
import Game.DataClasses.TowerClass;
import Game.DataClasses.Vector2;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.smartfoxserver.v2.mmo.IMMOItemVariable;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.MMOItemVariable;
import com.smartfoxserver.v2.mmo.Vec3D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NetworkDataProcess 
{
    public static void SetPlayerState(PlayerClass player, boolean isUpdateOnClients, boolean isFirstCall)
    {
        User user = player.GetUser();
        if(user != null)
        {
            Vector2 position = player.GetPosition();
            boolean forceUpdate = false;
            try 
            {
                Vec3D pos = new Vec3D(position.GetFloatX(), position.GetFloatY(), 0);
                GlobalGameData.mmoApi.setUserPosition(user, pos, GlobalGameData.room);
            }
            catch (Exception e)
            {
                forceUpdate = true;
                Vector2 newPosition = GlobalGameData.startPoints.GetClosestPoint(position);
                player.GetLocation().SetPosition(newPosition);
                Vec3D pos = new Vec3D(newPosition.GetFloatX(), newPosition.GetFloatY(), 0);
                GlobalGameData.mmoApi.setUserPosition(user, pos, GlobalGameData.room);
            }
            
            GlobalGameData.api.setUserVariables(user, ClientsManagement.GetClietnVariables(player, isFirstCall), isUpdateOnClients || forceUpdate, false);
        }
        else
        {
            ClientsManagement.RemovePlayer(player.GetId());
        }
    }
    
    public static void SetMonsterState(MonsterClass monster, boolean isUpdateOnClients, boolean isFirstCall)
    {
        MMOItem monsterItem = monster.GetMonsterAsItem();
        Vector2 position = monster.GetPosition();
        Vec3D pos = new Vec3D(position.GetFloatX(), position.GetFloatY(), 0);
        
        if(!isFirstCall)
        {
            List<IMMOItemVariable> vars = MonstersManagement.GetMonsterVariables(monster, false);
            GlobalGameData.mmoApi.setMMOItemVariables(monsterItem, vars, isUpdateOnClients);
        }
        
        try 
        {
            GlobalGameData.mmoApi.setMMOItemPosition(monsterItem, pos, GlobalGameData.room);
            monster.SetLastCorrectPosition(pos);
        }
        catch (Exception e)
        {
            monster.GetState().SetStateToIddle();
            monster.GetState().SetPosition(new Vector2(monster.GetLastCorrectPosition()));
            monster.ApplyDamage(monster.GetMaxLife());
            //ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            //Logger.Log(emc.toString());
            Logger.Log("Monster " + monster.GetId() + " out of map limit. Kill him.");
        }
    }
    
    public static void SetBulletState(BulletClass bullet, boolean isUpdateOnClients, boolean isFirstCall)
    {
        MMOItem bulletItem = bullet.GetBulletItem();
        Vector2 position = bullet.GetPosition();
        if(!isFirstCall)
        {
            List<IMMOItemVariable> vars = MonstersManagement.GetBulletVariables(bullet, false);
            GlobalGameData.mmoApi.setMMOItemVariables(bulletItem, vars, isUpdateOnClients);
        }
        
        Vec3D pos = new Vec3D(position.GetFloatX(), position.GetFloatY(), 0);
        try
        {
            GlobalGameData.mmoApi.setMMOItemPosition(bulletItem, pos, GlobalGameData.room);
            bullet.SetLastCorrectPosition(pos);
        }
        catch (Exception e)
        {
            bullet.SetPosition(new Vector2(bullet.GetLastCorrectPosition()));
            bullet.SetForceDestroy();
            //ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            //Logger.Log(emc.toString());
            Logger.Log("Bullet " + bullet.GetId() + " out of map limit");
        }
        
    }
    
    public static void SetTowerState(TowerClass tower, boolean isUpdateOnClients, boolean isFirstCall)
    {
        MMOItem towerItem = tower.GetTowerAsItem();
        Vector2 position = tower.GetPosition();
        if(!isFirstCall)
        {
            List<IMMOItemVariable> vars = TowersManagement.GetTowerVariables(tower, false);
            GlobalGameData.mmoApi.setMMOItemVariables(towerItem, vars, isUpdateOnClients);
        }
        
        Vec3D pos = new Vec3D(position.GetFloatX(), position.GetFloatY(), 0);
        GlobalGameData.mmoApi.setMMOItemPosition(towerItem, pos, GlobalGameData.room);
    }
    
    public static void SendClientsDestroyBullet(BulletClass bullet)
    {//send players to destroy bullet (as it life ending) and notify damage
        SayClientAtackResult(true, bullet.GetId(), bullet.GetPosition().GetX(), bullet.GetPosition().GetY(), bullet.GetHitData());
    }
    
    public static void SayClientAtackResult(boolean useBullet, int bulletId, double damagePosX, double damagePosY, BulletHitDataClass hitData)
    {
        Vec3D pos = new Vec3D((float)damagePosX, (float)damagePosY, 0);
        List<User> users = GlobalGameData.room.getProximityList(pos);
        ISFSObject params = new SFSObject();
        params.putBool("useBullet", useBullet);
        params.putInt("id", bulletId);
        params.putDouble("x", damagePosX);
        params.putDouble("y", damagePosY);
        params.putSFSArray("hitData", hitData.GetSFSArray());
        GlobalGameData.server.send("RPCDestoyBullet", params, users);
    }
    
    public static void SendClientStartBullet(BulletClass bullet)
    {//Send data about bullet host for animations purposes (for example)
        Vec3D pos = new Vec3D(bullet.GetPosition().GetFloatX(), bullet.GetPosition().GetFloatY(), 0);
        List<User> users = GlobalGameData.room.getProximityList(pos);
        ISFSObject params = new SFSObject();
        params.putInt("hostType", bullet.GetHostType());
        params.putInt("hostId", bullet.GetHostId());
        params.putFloat("hostAngle", bullet.GetHostPerson().GetAngle());
        
        //nex necessary data of the bullet
        params.putInt("id", bullet.GetId());
        params.putFloat("speed", (float)bullet.GetSpeed());
        params.putInt("buleltType", bullet.GetBulletType());
        params.putFloat("damageRadius", bullet.GetRadius());
        
        params.putDouble("targetX", bullet.GetState().GetTargetLocation().GetPosition().GetX());
        params.putDouble("targetY", bullet.GetState().GetTargetLocation().GetPosition().GetY());
        
        GlobalGameData.server.send("RPCStartBullet", params, users);
    }
    
    public static void KillsMessage()
    {
        ISFSObject params = new SFSObject();
        ISFSArray dataArray = new SFSArray();
        for (Iterator<Map.Entry<Integer, PlayerClass>> it = GlobalGameData.clients.entrySet().iterator(); it.hasNext();)
        {
            PlayerClass player = it.next().getValue();
            dataArray.addSFSObject(player.GetKillsData());
        }
        params.putSFSArray("killsArray", dataArray);
        GlobalGameData.server.send("RPCKillsMessage", params, GlobalGameData.room.getUserList());
    }
    
    public static void SayMonsterDead(int mosnterId)
    {
        if(GlobalGameData.monsters.containsKey(mosnterId))
        {
            MonsterClass monster = GlobalGameData.monsters.get(mosnterId);
            Vec3D pos = monster.GetPosition3D();
            List<User> users = GlobalGameData.room.getProximityList(pos);
            ISFSObject params = new SFSObject();
            params.putInt("monsterId", mosnterId);
            if(users.size() > 0)
            {
                GlobalGameData.server.send("SayMonsterDead", params, users);    
            }
            
        }
    }
    
    public static void SayMonsterStartAtack(int monsterId, int targetType, int targetId, float atackTime)
    {
        if(GlobalGameData.monsters.containsKey(monsterId))
        {
            MonsterClass monster = GlobalGameData.monsters.get(monsterId);
            Vec3D pos = monster.GetPosition3D();
            List<User> users = GlobalGameData.room.getProximityList(pos);
            ISFSObject params = new SFSObject();
            params.putInt("monsterId", monsterId);
            params.putInt("targetType", targetType);
            params.putInt("targetId", targetId);
            params.putFloat("atackTime", atackTime);
            if(users.size() > 0)
            {
                GlobalGameData.server.send("SayMonsterStartAtack", params, users);    
            }
        }
    }
    
    public static void SendClientResponse(User user, int taskId)
    {//use it for multiple tasks
        //0 - target is invisible
        ISFSObject params = new SFSObject();
        params.putInt("taskId", taskId);
        GlobalGameData.server.send("ClientResponse", params, user);
    }
}