package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class CollectableParametersClass 
{
    int type;
    public int GetType(){return type;}
    float radius;
    public float GetRaius(){return radius;}
    ISFSObject attributes;
    public ISFSObject GetAttributes(){return attributes;}
    
    public CollectableParametersClass(ISFSObject collectableObject)
    {
        type = collectableObject.getInt("type");
        radius = collectableObject.getFloat("radius");
        attributes = collectableObject.getSFSObject("attributes");
    }
}
