
package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.Vec3D;

public class MonsterClass extends PersonClass
{
    MMOItem monster;
    int monsterType;
    StateClass state;
    MonsterAtackerClass atacker;
    
    int monsterTowerHostId;
    
    public MonsterClass(MMOItem monsterLink, float speed, int mType, float r, int l, int tHost, int mDamage, float mDamageRadius, int mWType, float mESRadius, float coolDawn, float atackLength) 
    {
        super(monsterLink.getId(), "Monster_" + monsterLink.getId(), speed, r, l);
        monsterTowerHostId = tHost;
        Vector2 towerPos = GlobalGameData.towers.get(monsterTowerHostId).GetPosition();
        SetLastCorrectPosition(new Vec3D(towerPos.GetFloatX(), towerPos.GetFloatY(), 0));
        monster = monsterLink;
        monsterType = mType;
        state = new StateClass((float)Math.random(), this, true, mESRadius);
        atacker = new MonsterAtackerClass(state, this, mDamage, mDamageRadius, mWType, coolDawn, atackLength);
    }
    
    public void AddDamageData(int atackerType, int atackerId, int damage)
    {//из пули приходит информация о том, кто нанес удар. Пробрасываем в state
        state.AddDamageData(atackerType, atackerId, damage);
    }
    
    public MonsterAtackerClass GetAtacker()
    {
        return atacker;
    }
    
    public int GetTowerHostId()
    {
        return monsterTowerHostId;
    }
    
    public MMOItem GetMonsterAsItem()
    {
        return monster;
    }
    
    public StateClass GetState()
    {
        return state;
    }
    
    public int GetMonsterType()
    {
        return monsterType;
    }
    
    public ISFSObject GetMinimalParameters()
    {//return some minimal set of parameters
        //caled when monster start and stop moving
        ISFSObject toReturn = new SFSObject();
        toReturn.putInt("id", GetId());
        //life
        toReturn.putInt("life", GetLife());
        toReturn.putInt("maxLife", GetMaxLife());
        
        //movement
        toReturn.putInt("state", GetState().GetState());
        toReturn.putDouble("speed", GetSpeed());
        toReturn.putInt("target_type", GetState().GetTargetEnemyType());
        toReturn.putInt("target_id", GetState().GetTargetEnemyId());
        Vector2 targtPosition = GetState().GetTargetLocation().GetPosition();
        toReturn.putDouble("target_position_x", targtPosition.GetX());
        toReturn.putDouble("target_position_y", targtPosition.GetY());
        
        //position
        Vector2 position = GetLocation().GetPosition();
        toReturn.putDouble("position_x", position.GetX());
        toReturn.putDouble("position_y", position.GetY());
        
        return toReturn;
    }
}
