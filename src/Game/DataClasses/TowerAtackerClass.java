/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import Game.Process.MonstersManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.mmo.BaseMMOItem;
import com.smartfoxserver.v2.mmo.Vec3D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TowerAtackerClass 
{
    PersonClass towerPerson;
    
    float atackRadius;
    float atackCooldawn;
    int bulletType;
    float accuracyRadius;
    
    long lastAtackTime;
    long longAT;
    long lastShotTime;
    
    int bulletsPerShot;  // how many shot should be done in one state
    int shotBulletsDelay;  // in milliseconds
    boolean isShootingState;
    int shotCount;  // how many shots wa bade in hte current shooting state
    
    public TowerAtackerClass(PersonClass tower, float aRadius, float cd, int bT, float acRadius, int bPerShot, int shotBDelay)
    {
        towerPerson = tower;
        atackRadius = aRadius;
        atackCooldawn = cd;
        bulletType = bT;
        accuracyRadius = acRadius;
        bulletsPerShot = bPerShot;
        shotBulletsDelay = shotBDelay;
        isShootingState = false;
        shotCount = 0;
        
        longAT = (long)atackCooldawn * 1000;
        lastAtackTime = System.currentTimeMillis();
        lastShotTime = System.currentTimeMillis();
    }
    
    public void AtackTick()
    {
        long currentTime = System.currentTimeMillis();
        if(isShootingState)
        {
            if(currentTime - lastShotTime > shotBulletsDelay)
            {
                List<Integer> targets = GetPotentialTargets();
                if(targets.size() > 0)
                {
                    EmitBullet(targets);
                }
                
                shotCount++;
                lastShotTime = currentTime;
                if(shotCount >= bulletsPerShot)
                {
                    isShootingState = false;
                }
            }
        }
        else
        {
            //check time between shootings
            if(currentTime - lastAtackTime > longAT)
            {
                //tower is ready to start shoting
                List<Integer> targets = GetPotentialTargets();
                if(targets.size() > 0)
                {//there are targets, start atacking
                    lastAtackTime = currentTime;
                    isShootingState = true;
                    shotCount = 0;
                }
            }
        }
    }
    
    List<Integer> GetPotentialTargets()
    {
        Vec3D pos = towerPerson.GetPosition3D();
        Vector2 towerPosition = towerPerson.GetPosition();
        List<User> users = GlobalGameData.room.getProximityList(pos);//<--- players
        List<Integer> userList = new ArrayList<Integer>();//list of all user in atack area
        for(User user : users)
        {
            int userId = user.getId();
            if(GlobalGameData.clients.containsKey(userId))
            {
                PlayerClass pers = GlobalGameData.clients.get(userId);
                Vector2 clientPosition = pers.GetPosition();
                if(!pers.GetIsDead() && Vector2.GetDistance(towerPosition, clientPosition) < atackRadius)
                {
                    //Check visibility
                    EdgeClass toTargetEdge = new EdgeClass(towerPosition, clientPosition);
                    IntersectionResultClass intersect = GlobalGameData.collisionMap.GetIntersection(toTargetEdge);
                    if(!intersect.isIntersection)
                    {
                        //add player to the list of potential targets
                        userList.add(userId);
                    }
                }
            }
        }
        return userList;
    }
    
    void EmitBullet(List<Integer> targets)
    {
        //select random index of the player
        int rIndex = ThreadLocalRandom.current().nextInt(0, targets.size());
        //create location for the bllet target
        LocationClass loc = new LocationClass();
        loc.SetPosition(GlobalGameData.clients.get(targets.get(rIndex)).GetPosition());
        //Make random shift
        loc.SetRandomShift(accuracyRadius);
        StartAtack(towerPerson.GetPosition(), loc.GetPosition());
    }
    
    public void AtackTickOld()
    {
        //Check we can atack
        if(System.currentTimeMillis() - lastAtackTime > longAT)
        {
            //Find players on the radius
            Vec3D pos = towerPerson.GetPosition3D();
            Vector2 towerPosition = towerPerson.GetPosition();
            //List<BaseMMOItem> mmoItems = GlobalGameData.room.getProximityItems(pos);//<-- monsters and towers
            List<User> users = GlobalGameData.room.getProximityList(pos);//<--- players
            List<Integer> userList = new ArrayList<Integer>();//list of all user in atack area
            for(User user : users)
            {
                int userId = user.getId();
                if(GlobalGameData.clients.containsKey(userId))
                {
                    PlayerClass pers = GlobalGameData.clients.get(userId);
                    Vector2 clientPosition = pers.GetPosition();
                    if(!pers.GetIsDead() && Vector2.GetDistance(towerPosition, clientPosition) < atackRadius)
                    {
                        //Check visibility
                        EdgeClass toTargetEdge = new EdgeClass(towerPosition, clientPosition);
                        IntersectionResultClass intersect = GlobalGameData.collisionMap.GetIntersection(toTargetEdge);
                        if(!intersect.isIntersection)
                        {
                            //add player to the list of potential targets
                            userList.add(userId);
                        }
                    }
                }
            }
            //Select random user from the list
            if(userList.size() > 0)
            {//there is a target
                for(int i = 0; i < bulletsPerShot; i++)
                {
                    int rIndex = ThreadLocalRandom.current().nextInt(0, userList.size());
                    LocationClass loc = new LocationClass();
                    loc.SetPosition(GlobalGameData.clients.get(userList.get(rIndex)).GetPosition());
                    //Make random shift
                    loc.SetRandomShift(accuracyRadius);
                    StartAtack(towerPosition, loc.GetPosition());
                }
            }
        }
    }
    
    void StartAtack(Vector2 startPoint, Vector2 targetPoint)
    {//0 - player, 1 - monster
        //Vector2 startPoint, Vector2 targetPoint, int bulletType, PersonClass hostPerson, IntersectionResultClass intersectionData, int hostType
        MonstersManagement.AddBullet(startPoint, targetPoint, bulletType, towerPerson, new IntersectionResultClass(), 2, 0.0f);
        lastAtackTime = System.currentTimeMillis();
    }
    
    public float GetAtackRadius()
    {
        return atackRadius;
    }
}
