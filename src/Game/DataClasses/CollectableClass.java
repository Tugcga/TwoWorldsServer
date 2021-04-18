package Game.DataClasses;

import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.mmo.MMOItem;

public class CollectableClass 
{
    MMOItem collectable;
    int id;
    int collType;
    Vector2 position;
    float radius;
    
    long quadrantIndex;
    
    public CollectableClass(MMOItem collLink, int cType, float r, Vector2 pos)
    {
        collectable = collLink;
        id = collLink.getId();
        collType = cType;
        radius = r;
        position = new Vector2(pos);
        
        quadrantIndex = CollectableProcessorClass.GetQuadrantIndex(position);
    }
    
    public void Apply(PersonClass person)
    {
        Logger.Log("Apply method is not implemented!!");
    }
    
    public float GetRadius()
    {
        return radius;
    }
    
    public double GetDistance(Vector2 other)
    {
        return Vector2.GetDistance(position, other);
    }
    
    public boolean IsIntersects(Vector2 otherPosition, float otherRadius)
    {
        double d = radius + otherRadius;
        //Logger.Log(Vector2.GetDistanceSq(position, otherPosition) + " vs " + (d*d) + "| " + position.toString() + " - " + otherPosition.toString());
        return Vector2.GetDistanceSq(position, otherPosition) < d * d;
    }
    
    public MMOItem GetCollectItem()
    {
        return collectable;
    }
    
    public Vector2 GetPosition()
    {
        return position;
    }
    
    public int GetId()
    {
        return id;
    }
    
    public int GetType()
    {
        return collType;
    }
}
