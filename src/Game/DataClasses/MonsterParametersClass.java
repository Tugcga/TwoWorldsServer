package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class MonsterParametersClass 
{
    int monsterType;
    public int GetMonsterType(){return monsterType;}
    String name;
    public String GetMonsterName(){return name;}
    float monsterSpeed;
    public float GetMonsterSpeed(){return monsterSpeed;}
    float monsterRadius;
    public float GetMonsterRadius(){return monsterRadius;}
    int monsterLife;
    public int GetMonsterLife(){return monsterLife;}
    int monsterDamage;
    public int GetMonsterDamage() {return monsterDamage;}
    float monsterDamageRadius;
    public float GetMonsterDamageRadius() {return monsterDamageRadius;}
    int weaponType;
    public int GetWeaponType() {return weaponType;}
    float enemySearchRadius;
    public float GetEnemySearchRadius() {return enemySearchRadius;}
    float coolDawn;
    public float GetCoolDawn() {return coolDawn;}
    float atackLength;
    public float GetAtackLength() {return atackLength;}
    
    public MonsterParametersClass(ISFSObject monsterParams)
    {
        monsterType = monsterParams.getInt("type");
        name = monsterParams.getUtfString("name");
        monsterSpeed = monsterParams.getFloat("speed");
        monsterRadius = monsterParams.getFloat("radius");
        monsterLife = monsterParams.getInt("life");
        monsterDamage = monsterParams.getInt("damage");
        monsterDamageRadius = monsterParams.getFloat("damageDistance");
        weaponType = monsterParams.getInt("bullet");//-1 - contact atack
        enemySearchRadius = monsterParams.getFloat("enemySearchRadius");
        coolDawn = monsterParams.getFloat("coolDawn");
        atackLength = monsterParams.getFloat("atackLength");
    }
}
