/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import java.util.List;
import java.util.ArrayList;

class OneHitData 
{
    int type;
    int id;
    int life;
    int maxLife;
    boolean isDead;
    float blockTime;
    
    double positionX;
    double positionY;
    
    public OneHitData(int targetType, int targetId, int targetLife, int targetMaxLife, boolean isTargetDead, float bTime, double posX, double posY)
    {
        type = targetType;
        id = targetId;
        life = targetLife;
        isDead = isTargetDead;
        blockTime = bTime;
        positionX = posX;
        positionY = posY;
        maxLife = targetMaxLife;
    }
    
    public ISFSObject GetSFSObject()
    {
        ISFSObject toReturn = new SFSObject();
        toReturn.putInt("type", type);
        toReturn.putInt("id", id);
        toReturn.putInt("life", life);
        toReturn.putInt("maxLife", maxLife);
        toReturn.putBool("isDead", isDead);
        toReturn.putFloat("blockTime", blockTime);
        toReturn.putDouble("posX", positionX);
        toReturn.putDouble("posY", positionY);
        return toReturn;
    }
}

public class BulletHitDataClass 
{
    List<OneHitData> hitsData;
    
    public BulletHitDataClass()
    {
        hitsData = new ArrayList<OneHitData>();
    }
    
    public void AddData(int targetType, int targetId, int targetLife, int targetMaxLife, boolean isTargetDead, float blockTime, Vector2 position)
    {//position - position of the target
        OneHitData newData = new OneHitData(targetType, targetId, targetLife, targetMaxLife, isTargetDead, blockTime, position.GetX(), position.GetY());
        hitsData.add(newData);
    }
    
    public ISFSArray GetSFSArray()
    {
        ISFSArray toReturn = new SFSArray();
        for(OneHitData data : hitsData)
        {
            toReturn.addSFSObject(data.GetSFSObject());
        }
        return toReturn;
    }
}
