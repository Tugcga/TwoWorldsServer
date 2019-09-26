package Game.DataClasses;

import Game.Process.BattleController;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.mmo.BaseMMOItem;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.Vec3D;
import java.util.ArrayList;
import java.util.List;

public class BulletClass extends PersonClass
{
    MMOItem bulletItem;
    int bulletType;
    boolean isTracing;  //true - we should simpulate the fly by using speed to target point
    float delay; // time is seconds before we start the bullet (used when isTracing = false)
    boolean isDelayOver;
    boolean isDamageOnlyTarget;
    StateClass state;
    PersonClass hostPerson;
    int hostType;//0 - player, 1 - monster, 2 - tower
    int hostId;
    int damage;
    
    boolean isHit;
    BulletHitDataClass hitData;
    
    
    public BulletClass(MMOItem bulletLink, float speed, float bRadius, int d, 
            Vector2 startPosition, Vector2 targetPosition, int bType,
            boolean isDOT, PersonClass host, int hType, boolean _isTrace, float _delay)
    {//bRadius - damage size for line bullet and damage radius for rocket bullet
        super(bulletLink.getId(), "", speed, bRadius, 0);
        isHit = false;
        hitData = new BulletHitDataClass();
        hostPerson = host;
        hostType = hType;
        hostId = host.GetId();
        damage = d;
        bulletItem = bulletLink;
        bulletType = bType;
        isDamageOnlyTarget = isDOT;
        isTracing = _isTrace;  // if isTracing=false, damage after delay time
        delay = _delay;
        isDelayOver = false;
        state = new StateClass(speed, this, false, 0);
        LocationClass location = new LocationClass();
        location.SetPosition(startPosition);
        this.SetLocation(location);
        state.SetTargetPosition(targetPosition);
        SetLastCorrectPosition(startPosition.GetVec3D());
    }
    
    public void SetPosition(Vector2 pos)
    {
        GetLocation().SetPosition(pos);
    }
    
    public void CalculateDamage()
    {
        CalculateDamage(GetCollisionsList());
    }
    
    void CalculateDamage(List<IntIntFloatClass> collisions)
    {
        for(IntIntFloatClass col : collisions)
        {//type, id, distance
            BattleController.ApplyDamage(col.int01Value, col.int02Value, hostType, hostId, damage, hitData);
        }
        
        isHit = true;
    }
    
    List<IntIntFloatClass> GetCollisionsList()
    {
        Vec3D pos = GetPosition3D();
        List<BaseMMOItem> mmoItems = GlobalGameData.room.getProximityItems(pos);
        List<User> users = GlobalGameData.room.getProximityList(pos);

        List<IntIntFloatClass> collisions = new ArrayList<IntIntFloatClass>();
        for(BaseMMOItem item : mmoItems)
        {
            int itemId = item.getId();
            if(!(hostType == 1 && itemId == hostId) && GlobalGameData.monsters.containsKey(itemId))
            {//this is the monster
                MonsterClass monster = GlobalGameData.monsters.get(itemId);
                if(!monster.GetIsDead())
                {
                    float distance = (float)Vector2.GetDistance(GetPosition(), monster.GetPosition());
                    if(distance < monster.GetRadius() + GetRadius())
                    {//type, id, distance
                        IntIntFloatClass newCollision = new IntIntFloatClass(1, itemId, distance);
                        collisions.add(newCollision);
                    }
                }
            }
            else if(!(hostType == 2 && itemId == hostId) && GlobalGameData.towers.containsKey(itemId))
            {//check collision with tower
                TowerClass tower = GlobalGameData.towers.get(itemId);
                if(!tower.GetIsDead())
                {
                    float distance = (float)Vector2.GetDistance(GetPosition(), tower.GetPosition());
                    if(distance < tower.GetRadius() + GetRadius())
                    {//type, id, distance
                        IntIntFloatClass newCollision = new IntIntFloatClass(2, itemId, distance);
                        collisions.add(newCollision);
                    }
                }
            }
        }
        for(User user : users)
        {
            int userId = user.getId();
            if(!(hostType == 0 && userId == hostId) && GlobalGameData.clients.containsKey(userId))
            {
                PlayerClass player = GlobalGameData.clients.get(userId);
                if(!player.GetIsDead())
                {
                    float distance = (float)Vector2.GetDistance(GetPosition(), player.GetPosition());
                    if(distance < player.GetRadius() + GetRadius())
                    {//type, id, distance
                        IntIntFloatClass newCollision = new IntIntFloatClass(0, userId, distance);
                        collisions.add(newCollision);
                    }
                }
            }
        }
        return collisions;
    }
    
    public void CheckCollisions()
    {
        if(!IsEffectedOnlyEnd())
        {
            List<IntIntFloatClass> collisions = GetCollisionsList();
            //Find the most close target
            if(collisions.size() > 0)
            {
                int closestIndex = 0;
                for(int i = 1; i < collisions.size(); i++)
                {
                    IntIntFloatClass d = collisions.get(i);
                    IntIntFloatClass minD = collisions.get(closestIndex);
                    if(d.floatValue < minD.floatValue)
                    {
                        closestIndex = i;
                    }
                }
                state.UpShouldDestroy();
                List<IntIntFloatClass> toCalculate = new ArrayList<IntIntFloatClass>();
                toCalculate.add(collisions.get(closestIndex));
                CalculateDamage(toCalculate);
            }
        }
    }
    
    public boolean IsEffectedOnlyEnd()
    {//true if the bullet can damage only at the end
        return isDamageOnlyTarget;
    }
    
    public boolean IsDelayOver()
    {
        if(isDelayOver)
        {
            return true;
        }
        else
        {
            if(System.currentTimeMillis() - state.GetEmitTime() > delay * 1000)
            {
                isDelayOver = true;
            }
            return isDelayOver;
        }
    }
    
    public int GetBulletType()
    {
        return bulletType;
    }
        
    public boolean GetIsHit()
    {
        return isHit;
    }
    
    public BulletHitDataClass GetHitData()
    {
        return hitData;
    }
    
    public MMOItem GetBulletItem()
    {
        return bulletItem;
    }
    
    public StateClass GetState()
    {
        return state;
    }
    
    /*public int GetMMOItemType()
    {
        return mmoItemType;
    }*/
    
    public int GetHostType()
    {
        return hostType;
    }
    
    public float GetDelay()
    {
        return delay;
    }
    
    public boolean IsTracing()
    {
        return isTracing;
    }
    
    public int GetHostId()
    {
        //return hostPerson.GetId();
        return hostId;
    }
    
    public PersonClass GetHostPerson()
    {
        return hostPerson;
    }
}
