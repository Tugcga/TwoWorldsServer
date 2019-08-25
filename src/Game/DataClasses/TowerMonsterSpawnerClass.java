/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import Game.Process.MonstersManagement;
import static Game.Process.MonstersManagement.GetMonsterVariables;
import Game.Process.NetworkDataProcess;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.mmo.MMOItem;
import java.util.ArrayList;
import java.util.List;

public class TowerMonsterSpawnerClass 
{
    int hostTowerId;
    int maxMonstersCount;
    float monsterMinRadius;
    float monsterMaxRadius;
    
    int[] monsterIds;
    
    public TowerMonsterSpawnerClass(int hostId, int mCount, float minRadius, float maxRadius)
    {
        hostTowerId = hostId;
        monsterIds = new int[mCount];
        for(int i = 0; i < mCount; i++)
        {
            monsterIds[i] = -1;
        }
        monsterMinRadius = minRadius;
        monsterMaxRadius = maxRadius;
    }
    
    public int[] GetActiveMonsters()
    {
        List<Integer> toReturn = new ArrayList<Integer>();
        for(int mId : monsterIds)
        {
            if(mId != -1 && GlobalGameData.monsters.containsKey(mId) && !GlobalGameData.monsters.get(mId).GetIsDead())
            {
                toReturn.add(mId);
            }
        }
        
        int[] toReturnArray = new int[toReturn.size()];
        for(int i = 0; i < toReturn.size(); i++)
        {
            toReturnArray[i] = toReturn.get(i);
        }
        return toReturnArray;
    }
    
    public void DeleteMonster(int monsterId)
    {//Delete not from Global class, but from inner menagement
        for(int i = 0; i < monsterIds.length; i++)
        {
            if(monsterIds[i] == monsterId)
            {
                monsterIds[i] = -1;
                i = monsterIds.length;
            }
        }
    }
    
    int GetIndexOfEmptySlot()
    {
        for(int i = 0; i < monsterIds.length; i++)
        {
            if(monsterIds[i] == -1)
            {
                return i;
            }
        }
        return -1;
    }
    
    public void AddMonster()
    {
        //Find empty slot
        int emptySlot = GetIndexOfEmptySlot();
        if(emptySlot != -1 && GlobalGameData.towers.containsKey(hostTowerId))
        {//there exist slot
            TowerClass tower = GlobalGameData.towers.get(hostTowerId);
            Vector2 tPos = tower.GetPosition();
            //Generate random position
            //Generate random angle
            float angle = (float)(Math.random() * 2 * Math.PI);
            //Start and end positions of the edge
            Vector2 sPos = Vector2.Add(tPos, Vector2.FromPolarToVector(angle, monsterMinRadius));
            Vector2 ePos = Vector2.Add(tPos, Vector2.FromPolarToVector(angle, monsterMaxRadius));
            //Check collisions from tower point to edge end
            EdgeClass edge = new EdgeClass(tPos, ePos);
            IntersectionResultClass intersect = GlobalGameData.collisionMap.GetIntersection(edge);
            if(intersect.isIntersection)
            {
                ePos = intersect.intersectionPoint;
            }
            double eDist = Vector2.GetDistance(tPos, ePos);
            double sDist = Vector2.GetDistance(tPos, sPos);
            if(eDist > sDist)
            {
                //there is a gap between start and end point
                //Generate random parameter inside this gap
                float t = (float)(Math.random() * 0.9);
                Vector2 newPosition = Vector2.Add(tPos, Vector2.FromPolarToVector(angle, sDist * (1 - t) + eDist * t));
                //Create monster
                int newMonsterId = MonstersManagement.AddMonsterToTower(hostTowerId, newPosition);
                //fill the slot
                monsterIds[emptySlot] = newMonsterId;
                
                //Logger.Log("Tower " + hostTowerId + " emit monser " + newMonsterId + " to the slot " + emptySlot);
            }
        }
    }
    
    public float GetMinRadius()
    {
        return monsterMinRadius;
    }
    
    public float GetMaxRadius()
    {
        return monsterMaxRadius;
    }
}
