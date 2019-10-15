
package Game.Process;

import Game.DataClasses.BulletClass;
import Game.DataClasses.BulletParametersClass;
import Game.DataClasses.EdgeClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.IntersectionResultClass;
import Game.DataClasses.LocationClass;
import Game.DataClasses.MonsterClass;
import Game.DataClasses.MonsterParametersClass;
import Game.DataClasses.NetworkKeys;
import Game.DataClasses.PersonClass;
import Game.DataClasses.Vector2;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.smartfoxserver.v2.mmo.BaseMMOItem;
import com.smartfoxserver.v2.mmo.IMMOItemVariable;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.MMOItemVariable;
import java.util.ArrayList;
import java.util.List;

public class MonstersManagement 
{
    public static int AddMonsterToTower(int hostTowerId, Vector2 position)
    {//return added monster id. Called from tower spawner
        int monsterType = 0;
        MonsterParametersClass monsterParams = GlobalGameData.serverConfig.GetMonsterParameters(monsterType);
        LocationClass mLocation = new LocationClass();
        mLocation.SetPosition(position);
        
        MMOItem newMonsterItem = new MMOItem();
        MonsterClass newMonster = new MonsterClass(newMonsterItem, monsterParams.GetMonsterSpeed(), 
                monsterType, monsterParams.GetMonsterRadius(), 
                monsterParams.GetMonsterLife(), hostTowerId,
                monsterParams.GetMonsterDamage(), monsterParams.GetMonsterDamageRadius(),
                monsterParams.GetWeaponType(), monsterParams.GetEnemySearchRadius(),
                monsterParams.GetCoolDawn(), monsterParams.GetAtackLength());
        newMonster.SetLocation(mLocation);
        newMonsterItem.setVariables(GetMonsterVariables(newMonster, true));
        
        GlobalGameData.monsters.put(newMonster.GetId(), newMonster);
        NetworkDataProcess.SetMonsterState(newMonster, false, true);
        return newMonster.GetId();
    }
    
    public static void AddBullet(Vector2 startPoint, Vector2 targetPoint, int bulletType, PersonClass hostPerson, IntersectionResultClass intersectionData, int hostType, float playerAngle)
    {//player angle for players (for tower it is zero) and used only for line-bullet
        BulletParametersClass bulletParams = GlobalGameData.serverConfig.GetBulletParameters(bulletType);
        MMOItem newBulletItem = new MMOItem();
        Vector2 targetPosition = new Vector2();
        Vector2 emitStartPoint = new Vector2(startPoint);
        if(bulletParams.IsTrace())
        {//traceble bullet (rocket or line bullet)
            if(!bulletParams.IsDamageOnlyTarget())
            {//line bullet, check collisions with walls and enemies
                //Vector2 toVector = new Vector2(targetPoint.GetX() - startPoint.GetX(), targetPoint.GetY() - startPoint.GetY());
                //toVector.Normalize();
                //playerAngle in radians and calculated from positive x-direction 
                Vector2 toVector = bulletType == 0 && hostType == 0 ? new Vector2(Math.cos(playerAngle), Math.sin(playerAngle)) : new Vector2(targetPoint.GetX() - startPoint.GetX(), targetPoint.GetY() - startPoint.GetY());
                targetPosition.Set(startPoint.GetX() + bulletParams.GetMaxDistance() * toVector.GetX(), startPoint.GetY() + bulletParams.GetMaxDistance() * toVector.GetY());
                if(intersectionData.isIntersection)
                {//may be we need to change target position
                    if(bulletParams.GetMaxDistance() > Vector2.GetDistance(startPoint, intersectionData.intersectionPoint))
                    {
                        targetPosition.Set(intersectionData.intersectionPoint.GetX(), intersectionData.intersectionPoint.GetY());
                    }
                }
                else
                {//Find intersection with maximal distance
                    EdgeClass maxEdge = new EdgeClass(startPoint, targetPosition);
                    IntersectionResultClass maxIntersection = GlobalGameData.collisionMap.GetIntersection(maxEdge);
                    if(maxIntersection.isIntersection)
                    {
                        targetPosition.Set(maxIntersection.intersectionPoint.GetX(), maxIntersection.intersectionPoint.GetY());
                    }
                }
            }
            else
            {//rocket bullet, only target point
                if(bulletParams.GetMaxDistance() < Vector2.GetDistance(startPoint, targetPoint))
                {
                    Vector2 toVector = new Vector2(targetPoint.GetX() - startPoint.GetX(), targetPoint.GetY() - startPoint.GetY());
                    toVector.Normalize();
                    targetPosition.Set(startPoint.GetX() + bulletParams.GetMaxDistance() * toVector.GetX(), startPoint.GetY() + bulletParams.GetMaxDistance() * toVector.GetY());
                }
                else
                {
                    targetPosition.Set(targetPoint.GetX(), targetPoint.GetY());
                }
            }
        }
        else
        {//bullet is non-traceble
            //set target position not awayr from the host
            if(bulletParams.GetMaxDistance() < Vector2.GetDistance(startPoint, targetPoint))
            {
                Vector2 toVector = new Vector2(targetPoint.GetX() - startPoint.GetX(), targetPoint.GetY() - startPoint.GetY());
                toVector.Normalize();
                targetPosition.Set(startPoint.GetX() + bulletParams.GetMaxDistance() * toVector.GetX(), startPoint.GetY() + bulletParams.GetMaxDistance() * toVector.GetY());
            }
            else
            {
                targetPosition.Set(targetPoint.GetX(), targetPoint.GetY());
            }
            //also rewrite start position
            emitStartPoint = new Vector2(targetPosition);
        }
        
        //GlobalGameData.server.trace("Target position: " + targetPosition);
        BulletClass newBullet = new BulletClass(newBulletItem, bulletParams.GetSpeed(), 
                bulletParams.GetRadius(), bulletParams.GetDamage(),
                emitStartPoint, targetPosition, bulletType, bulletParams.IsDamageOnlyTarget(),
                hostPerson, hostType, bulletParams.IsTrace(), bulletParams.GetDelay());
        newBulletItem.setVariables(GetBulletVariables(newBullet, true));
        GlobalGameData.bullets.put(newBullet.GetId(), newBullet);
        NetworkDataProcess.SetBulletState(newBullet, false, true);
        NetworkDataProcess.SendClientStartBullet(newBullet);
    }
    
    public static void DeleteMonster(int monsterId)
    {
        if(GlobalGameData.monsters.containsKey(monsterId))
        {
            int tHostId = GlobalGameData.monsters.get(monsterId).GetTowerHostId();
            BaseMMOItem item = GlobalGameData.room.getMMOItemById(monsterId);
            
            try
            {
                GlobalGameData.mmoApi.removeMMOItem(item);
            }
            catch(Exception e)
            {
                ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
                Logger.Log(emc.toString());
            }
            
            
            //Remove monster from host tower
            if(GlobalGameData.towers.containsKey(tHostId))
            {
                GlobalGameData.towers.get(tHostId).GetMonsterSpawner().DeleteMonster(monsterId);
            }
        }
    }
    
    public static void DestoyBullet(int bulledId)
    {
        if(GlobalGameData.bullets.containsKey(bulledId))
        {
            //Send destroy event only if it hit to the enemy
            BulletClass bullet = GlobalGameData.bullets.get(bulledId);
            if(bullet.GetIsHit())
            {
                NetworkDataProcess.SendClientsDestroyBullet(GlobalGameData.bullets.get(bulledId));    
            }
            BaseMMOItem item = GlobalGameData.room.getMMOItemById(bulledId);
            try
            {
                GlobalGameData.mmoApi.removeMMOItem(item);
            }
            catch(Exception e)
            {
                ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
                Logger.Log(emc.toString());
            }
        }
    }
    
    public static List<IMMOItemVariable> GetMonsterVariables(MonsterClass monster, boolean isFirstCall) 
    {
        List<IMMOItemVariable> vars = new ArrayList<IMMOItemVariable>();
        if(isFirstCall)
        {
            vars.add(new MMOItemVariable(NetworkKeys.key_id, monster.GetId()));
            vars.add(new MMOItemVariable(NetworkKeys.key_name, monster.GetName()));
            vars.add(new MMOItemVariable(NetworkKeys.key_speed, monster.GetSpeed()));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_kind, NetworkKeys.monsterKind));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_type, monster.GetMonsterType()));
            vars.add(new MMOItemVariable(NetworkKeys.key_person_radius, monster.GetRadius()));
            vars.add(new MMOItemVariable(NetworkKeys.key_person_maxLife, monster.GetMaxLife()));
            vars.add(new MMOItemVariable(NetworkKeys.key_monster_damage, monster.GetAtacker().GetDamage()));
            vars.add(new MMOItemVariable(NetworkKeys.key_monster_damageRadius, monster.GetAtacker().GetDamageRadius()));
        }
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_x, monster.GetLocation().GetPosition().GetX()));
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_y, monster.GetLocation().GetPosition().GetY()));
        vars.add(new MMOItemVariable(NetworkKeys.key_state, monster.GetState().GetState()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetLocation_x, monster.GetState().GetTargetLocation().GetPosition().GetX()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetLocation_y, monster.GetState().GetTargetLocation().GetPosition().GetY()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetEnemy_type, monster.GetState().GetTargetEnemyType()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetEnemy_id, monster.GetState().GetTargetEnemyId()));
        vars.add(new MMOItemVariable(NetworkKeys.key_person_life, monster.GetLife()));
        
        return vars;
    }
    
    public static List<IMMOItemVariable> GetBulletVariables(BulletClass bullet, boolean isFirstCall) 
    {
        List<IMMOItemVariable> vars = new ArrayList<IMMOItemVariable>();
        if(isFirstCall)
        {
            vars.add(new MMOItemVariable(NetworkKeys.key_id, bullet.GetId()));
            vars.add(new MMOItemVariable(NetworkKeys.key_speed, bullet.GetSpeed()));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_kind, NetworkKeys.bulletKind));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_type, bullet.GetBulletType()));
            vars.add(new MMOItemVariable(NetworkKeys.key_person_radius, bullet.GetRadius()));
            vars.add(new MMOItemVariable(NetworkKeys.key_bullet_hostType, bullet.GetHostType()));
            vars.add(new MMOItemVariable(NetworkKeys.key_bullet_hostId, bullet.GetHostId()));
        }
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_x, bullet.GetLocation().GetPosition().GetX()));
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_y, bullet.GetLocation().GetPosition().GetY()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetLocation_x, bullet.GetState().GetTargetLocation().GetPosition().GetX()));
        vars.add(new MMOItemVariable(NetworkKeys.key_targetLocation_y, bullet.GetState().GetTargetLocation().GetPosition().GetY()));
        return vars;
    }
}
