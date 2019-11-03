package Game.DataClasses;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonEnemiesClass 
{
    public Map<Integer, Integer> playersDamageData;//ключ - atackerId, значение - общий нанесённый урон
    
    public PersonEnemiesClass()
    {
        playersDamageData = new ConcurrentHashMap<>();
    }
    
    public IntIntBoolClass GetEnemy(Vector2 myPosition, float searchRadius)
    {
        IntIntBoolClass toReturn = new IntIntBoolClass();//type, id, isExist
        if(playersDamageData.size() > 0)
        {
            int maxDamage = -1;
            int pId = -1;
            for (Iterator<Map.Entry<Integer, Integer>> it = playersDamageData.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry<Integer, Integer> e = it.next();
                int playerId = e.getKey();
                int damage = e.getValue();
                if(!GlobalGameData.clients.containsKey(playerId) || GlobalGameData.clients.get(playerId).GetIsDead())
                {//player does not exist. forget it
                    it.remove();
                }
                else
                {
                    //is player on search radius?
                    Vector2 pPosition = GlobalGameData.clients.get(playerId).GetPosition();
                    if(Vector2.GetDistance(myPosition, pPosition) < searchRadius)
                    {
                        //Check visibility
                        EdgeClass toTargetEdge = new EdgeClass(myPosition, pPosition);
                        IntersectionResultClass intersect = GlobalGameData.collisionMap.GetIntersection(toTargetEdge);
                        if(!intersect.isIntersection)
                        {
                            if(damage > maxDamage)
                            {
                                maxDamage = damage;
                                pId = playerId;
                            }    
                        }                    
                    }
                }
            }
            if(pId > -1)
            {
                toReturn.SetData(0, pId, true);
            }
        }
        return toReturn;
    }
    
    public void AddDamageData(int atackerType, int atackerId, int damage)
    {
        if(atackerType == 0)
        {//player
            int oldDamage = 0;
            if(playersDamageData.containsKey(atackerId))
            {
                oldDamage = playersDamageData.get(atackerId);
            }
            playersDamageData.put(atackerId, oldDamage + damage);
        }
    }
}
