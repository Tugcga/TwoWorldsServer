/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class BulletParametersClass 
{
    int type;
    public int GetType(){return type;}
    boolean isDamageOnlyTarget;
    public boolean IsDamageOnlyTarget() {return isDamageOnlyTarget;}
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
        speed = bulletObject.getFloat("speed");
        maxDistance = bulletObject.getFloat("maxDistance");
        radius = bulletObject.getFloat("radius");
        damage = bulletObject.getInt("damage");
    }
}
