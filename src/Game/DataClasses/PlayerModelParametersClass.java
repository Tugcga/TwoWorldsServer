/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class PlayerModelParametersClass 
{
    int playerType;
    public int GetModelType(){return playerType;}
    int bulletType;
    public int GetbulletType(){return bulletType;}
    float playerSpeed;
    public float GetPlayerSpeed(){return playerSpeed;}
    float playerRadius;
    public float GetPlayerRadius(){return playerRadius;}
    int playerLife;
    public int GetPlayerLife(){return playerLife;}
    float coolDawn;
    public float GetCoolDawn(){return coolDawn;}
    String modelName;
    public String GetModelName(){return modelName;}
    
    public PlayerModelParametersClass(ISFSObject params)
    {
        playerType = params.getInt("type");
        bulletType = params.getInt("bullet");
        playerSpeed = params.getFloat("speed");
        playerRadius = params.getFloat("radius");
        playerLife = params.getInt("life");
        coolDawn = params.getFloat("coolDawn");
        modelName = params.getUtfString("modelName");
    }
}
