
package Game.DataClasses;

import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.mmo.Vec3D;

public class PersonClass 
{//this is base class for any person object: npc, monster, player
    int id;
    String name;
    
    LocationClass location;
    float angle;
    float moveSpeed;
    float radius;
    int life;
    int maxLife;
    boolean isDead;
    
    Vec3D lastCorrectPosition;
    boolean forceDestroy;//if on, we should delete item as soon as possible. For example bullet go over map limit
    
    public PersonClass(int i, String n, float speed, float r, int l)
    {
        forceDestroy = false;
        id = i;
        name = n;
        radius = r;
        life = l;
        maxLife = l;
        isDead = false;
        location = new LocationClass();
        moveSpeed = speed;
        
        angle = (float)(Math.random() * 2 * Math.PI);
    }
    
    public void SetForceDestroy()
    {
        forceDestroy = true;
    }
    
    public boolean GetForceDestroy()
    {
        return forceDestroy;
    }
    
    public boolean ApplyDamage(int damage)
    {//return true if person is dead after this damage
        boolean oldDead = isDead;
        if(!isDead)
        {
            life = life - damage;
            if(life <= 0)
            {
                life = 0;
                isDead = true;
            }
        }
        if(oldDead)
        {
            return false;
        }
        else
        {
            return oldDead != isDead;
        }
        //Logger.Log("Apply damage " + damage + " result is " + life + " " + isDead);
    }
    
    public int GetLife()
    {
        return life;
    }
    
    public int GetMaxLife()
    {
        return maxLife;
    }
    
    public boolean GetIsDead()
    {
        return isDead;
    }
    
    public void SetAlive()
    {
        life = maxLife;
        isDead = false;
    }
    
    public float GetRadius()
    {
        return radius;
    }
    
    public int GetId()
    {
        return id;
    }
    
    public String GetName()
    {
        return name;
    }
    
    public Vector2 GetPosition()
    {
        return location.GetPosition();
    }
    
    public Vec3D GetPosition3D()
    {
        Vector2 pos = GetPosition();
        return new Vec3D(pos.GetFloatX(), pos.GetFloatY(), 0);
    }
    
    public void SetLastCorrectPosition(Vec3D pos)
    {
        lastCorrectPosition = pos;
    }
    
    public Vec3D GetLastCorrectPosition()
    {
        return lastCorrectPosition;
    }
    
    public LocationClass GetLocation()
    {
        return location;
    }
    
    public double GetSpeed()
    {
        return moveSpeed;
    }
    
    public void SetLocation(LocationClass loc)
    {
        location = loc;
    }
    
    public void SetAngle(float aValue)
    {
        angle = aValue;
    }
    
    public float GetAngle()
    {
        return angle;
    }
}
