package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class BulletParametersClass 
{
    int type;
    public int GetType(){return type;}
    boolean isDamageOnlyTarget;
    public boolean IsDamageOnlyTarget() {return isDamageOnlyTarget;}
    boolean isTrace;
    public boolean IsTrace() {return isTrace;}
    public float delay;
    public float GetDelay() {return delay;}
    float speed;
    public float GetSpeed(){return speed;}
    float maxDistance;
    public float GetMaxDistance(){return maxDistance;}
    float radius;
    public float GetRadius(){return radius;}
    int damage;
    public int GetDamage(){return damage;}
    
    public BulletParametersClass(ISFSObject bulletObject)
    {
        type = bulletObject.getInt("type");
        isDamageOnlyTarget = bulletObject.getInt("onlyTargetDamage") == 0 ? false : true;
        isTrace = bulletObject.getInt("noTrace") == 0 ? true : false;
        delay = bulletObject.getFloat("delay");
        speed = bulletObject.getFloat("speed");
        maxDistance = bulletObject.getFloat("maxDistance");
        radius = bulletObject.getFloat("radius");
        damage = bulletObject.getInt("damage");
    }
}
