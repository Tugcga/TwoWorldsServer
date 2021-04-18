package Game.DataClasses;

import OpenWorldRoom.Logger;
import java.util.ArrayList;
import java.util.List;

public class QuadrantContainer 
{
    List<CollectableClass> collects;
    int quadrantIndex;
    
    public QuadrantContainer(int q)
    {
        collects = new ArrayList<>();
        quadrantIndex = q;
    }
    
    public int CheckPlayer(PersonClass person)
    {//return index of collectable item, whcich close to the player (and shold be collected)
        for(int i = 0; i < collects.size(); i++)
        {
            //Logger.Log(quadrantIndex + " " + i + " " + collects.get(i).GetId());
            //we should collect item if the distance to the player shorter than it radius
            //if(collects.get(i).GetDistance(player.GetPosition()) < player.GetRadius())
            if(collects.get(i).IsIntersects(person.GetPosition(), person.GetRadius()))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    public String GetItemsIdsString()
    {
        String toReturn = "";
        for(int i = 0; i < collects.size(); i++)
        {
            toReturn += collects.get(i).GetId() + ": " + collects.get(i).GetPosition().toString() + ", ";
        }
        return toReturn;
    }
    
    public CollectableClass GetCollectableAndRemove(int index)
    {
        if(index < collects.size())
        {
            return collects.remove(index);
        }
        return null;
    }
    
    public void AddItem(CollectableClass coll)
    {
        collects.add(coll);
    }
}
