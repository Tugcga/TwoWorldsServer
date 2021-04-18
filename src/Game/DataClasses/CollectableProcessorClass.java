package Game.DataClasses;

import Game.Process.NetworkDataProcess;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.smartfoxserver.v2.mmo.BaseMMOItem;
import com.smartfoxserver.v2.mmo.IMMOItemVariable;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.MMOItemVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectableProcessorClass 
{
    static double minX;
    static double maxX;
    static double minY;
    static double maxY;
    
    static int rows;
    static int colums;
    static double quadrant;
    
    Map<Integer, QuadrantContainer> quadrants;
    
    public CollectableProcessorClass()
    {
        //at this stage collision map already defined
        List<Double> endpoints = GlobalGameData.collisionMap.GetEdgesPoints();
        //get maximum x and y coordinates of collision end points
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < endpoints.size() / 2; i++)
        {
            double x = endpoints.get(2*i);
            double y = endpoints.get(2*i + 1);
            
            if(x < minX){minX = x;}
            if(x > maxX){maxX = x;}
            
            if(y < minY){minY = y;}
            if(y > maxY){maxY = y;}
        }
        
        //increase the area
        quadrant = GlobalGameData.serverConfig.GetCollectableQuadrantSize();
        minX = minX - quadrant;
        maxX = maxX + quadrant;
        minY = minY - quadrant;
        maxY = maxY + quadrant;
        
        rows = (int)Math.round((maxY - minY) / quadrant);
        colums = (int)Math.round((maxX - minX) / quadrant);
        
        //Logger.Log(minX + " " + minY + " " + maxX + " " + maxY);
        //Logger.Log(rows + " " + colums);
        
        //Logger.Log(GetQuadrantIndex(new Vector2(-30.0, -40.0)));
        
        //init quadrants containers
        quadrants = new ConcurrentHashMap<>();
        for(int i = 0; i < rows * colums; i++)
        {
            QuadrantContainer q = new QuadrantContainer(i);
            quadrants.put(i, q);
        }
    }
    
    public static int GetQuadrantIndex(Vector2 position)
    {
        int x = (int)Math.floor((position.x - minX) / quadrant);
        int y = (int)Math.floor((position.y - minY) / quadrant);

        return y * colums + x;
    }
    
    public static void EmitCollectable(int collType, Vector2 position)
    {
        int qIndex = GetQuadrantIndex(position);
        if(GlobalGameData.collectableProcessor.quadrants.containsKey(qIndex))
        {
           CollectableParametersClass params = GlobalGameData.serverConfig.GetCollectableParameters(collType);
           MMOItem newCollItem = new MMOItem();
           //Logger.Log("emit coll " + newCollItem.getId() + " at quadrant " + qIndex + " at position " + position.toString());
           //CollectableClass newColl = new CollectableClass(newCollItem, collType, position);
           CollectableHealClass newColl = new CollectableHealClass(newCollItem, collType, params.GetRaius(), position, params.GetAttributes());
           
           GlobalGameData.collectableProcessor.quadrants.get(qIndex).AddItem(newColl);
           
           //next we should set MMOItem variables and send network message
           newCollItem.setVariables(GetCollectableVariables(newColl));
           
           //send data to clients
           NetworkDataProcess.SetCollectableState(newColl);
           NetworkDataProcess.SendClientCollectable(newColl, true);
        }
    }
    
    public static List<IMMOItemVariable> GetCollectableVariables(CollectableClass collect)
    {
        List<IMMOItemVariable> vars = new ArrayList<>();
        vars.add(new MMOItemVariable(NetworkKeys.key_id, collect.GetId()));
        vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_kind, NetworkKeys.collectKind));
        vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_type, collect.GetType()));
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_x, collect.GetPosition().GetX()));
        vars.add(new MMOItemVariable(NetworkKeys.key_location_position_y, collect.GetPosition().GetY()));
        vars.add(new MMOItemVariable(NetworkKeys.key_person_radius, collect.GetRadius()));
        return vars;
    }
    
    public void LogInfo()
    {
        for (Iterator<Map.Entry<Integer, QuadrantContainer>> it = quadrants.entrySet().iterator(); it.hasNext();)
        {
            QuadrantContainer q = it.next().getValue();
            if(q.collects.size() > 0)
            {
                Logger.Log("quadrant " + q.quadrantIndex + " contains " + q.collects.size() + " items " + q.GetItemsIdsString());
            }
        }
    }
    
    void UpdateStep(PersonClass person, int personType)
    {//personType = 0 for players, 1 for monsters
        if(!person.GetIsDead())
        {//if this player is not dead, then calculate quadrant
            //iterate by 9 quadrants near the current position
            int playerQuadrant = GetQuadrantIndex(person.GetPosition());
            for(int i = -1; i < 2; i++)
            {
                for(int j = -1; j < 2; j++)
                {
                    int qIndex = playerQuadrant + i + colums * j;
                    if(quadrants.containsKey(qIndex))
                    {
                        QuadrantContainer q = quadrants.get(qIndex);
                        int cIndex = q.CheckPlayer(person);
                        //Logger.Log("player " + person.GetId() + " quadrant " + playerQuadrant + " cIndex " + cIndex);
                        if(cIndex >= 0)
                        {//we should collect and destroy the item
                            CollectableClass collItem = q.GetCollectableAndRemove(cIndex);
                            if(collItem != null)
                            {//apply effect to the player
                                //Logger.Log("apply collect " + collItem.id + " to the player " + player.id + " quadrant " + playerQuadrant);
                                //Logger.Log("player " + person.GetId() + " collect " + collItem.GetId() + " in quadrant " + playerQuadrant);
                                collItem.Apply(person);

                                //update person state
                                if(personType == 0)
                                {
                                    PlayerClass player = (PlayerClass)person;
                                    NetworkDataProcess.UpdateClientData(player);
                                    NetworkDataProcess.SetPlayerState(player, false, false);
                                }
                                else if(personType == 1)
                                {
                                    MonsterClass monster = (MonsterClass)person;
                                    NetworkDataProcess.SayMonsterChangeState(monster);
                                    NetworkDataProcess.SetMonsterState(monster, false, false);
                                }

                                //send clients request to remove this item
                                NetworkDataProcess.SendClientCollectable(collItem, false);
                                //remove collectable item
                                BaseMMOItem item = GlobalGameData.room.getMMOItemById(collItem.GetId());
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
                    }
                    else
                    {//this is impossible, but player outside of the location quadrant
                        //Logger.Log("unknown quadrant " + playerQuadrant + " for player " + person.GetId());
                    }
                }
            }
        }
    }
    
    public void Update()
    {
        for (Iterator<Map.Entry<Integer, PlayerClass>> it = GlobalGameData.clients.entrySet().iterator(); it.hasNext();)
        {
            //check each player
            PlayerClass player = it.next().getValue();
            UpdateStep(player, 0);
        }
        
        for (Iterator<Map.Entry<Integer, MonsterClass>> it = GlobalGameData.monsters.entrySet().iterator(); it.hasNext();)
        {
            //check each monster
            MonsterClass monster = it.next().getValue();
            UpdateStep(monster, 1);
        }
    }
}
