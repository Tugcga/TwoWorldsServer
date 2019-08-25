/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class TowerParametersClass 
{
    int type;
    public int GetType() {return type;}
    float visibleRadius;
    public float GetVisibleRadius() {return visibleRadius;}
    int life;
    public int GetLife() {return life;}
    float radius;
    public float GetRadius() {return radius;}
    float atackCooldawn;//in seconds
    public float GetAtackCoolDawn() {return atackCooldawn;}
    int bulletType;
    public int GetBulletType() {return bulletType;}
    long resurectTime;
    public long GetResurectTime() {return resurectTime;}
    int monstersCount;
    public int GetMonstersCount() {return monstersCount;}
    float monstersMinRadius;
    public float GetMonstersMinRadius() {return monstersMinRadius;}
    float monstersMaxRadius;
    public float GetMonstersMaxRadius() {return monstersMaxRadius;}
    float targetAccuracyRadius;
    public float GetTargetAccuracyRadius() {return targetAccuracyRadius;}
    boolean isSpawnInDead;
    public boolean GetIsSpawnInDead() {return isSpawnInDead;}
    boolean isAgreMonstersToAtacker;
    public boolean GetIsAgreMonstersToAtacker() {return isAgreMonstersToAtacker;}
    int bulletsPerShot;
    public int GetBulletsPerShot() {return bulletsPerShot;}
    
    public TowerParametersClass(ISFSObject towerParams)
    {
        type = towerParams.getInt("type");
        visibleRadius = towerParams.getFloat("visibleRadius");
        life = towerParams.getInt("life");
        radius = towerParams.getFloat("radius");
        atackCooldawn = towerParams.getFloat("atackCooldawn");
        bulletType = towerParams.getInt("bullet");
        resurectTime = (long)towerParams.getInt("resurectTime");
        monstersCount = towerParams.getInt("monstersCount");
        monstersMinRadius = towerParams.getFloat("monstersMinRadius");
        monstersMaxRadius = towerParams.getFloat("monstersMaxRadius");
        targetAccuracyRadius = towerParams.getFloat("targetAccuracyRadius");
        isSpawnInDead = towerParams.getInt("isSpawnInDead") == 1 ? true : false;
        isAgreMonstersToAtacker = towerParams.getInt("isAgreMonsterToAtacker") == 1 ? true : false;
        bulletsPerShot = towerParams.getInt("bulletsPerShot");
    }
}
