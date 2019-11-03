package Game.DataClasses;

import Game.Process.BattleController;
import Game.Process.NetworkDataProcess;

public class MonsterAtackerClass 
{
    StateClass state;
    PersonClass myPerson;
    
    int damage;
    float damagRadius;
    int weaponType;  // for mosnter bullet, for non-shot is -1
    
    long lCoolDawn;
    long lastAtackTime;
    
    boolean isWaitEndAtack;
    long atackStartTime;
    long atackLengthTime;  // the length of the atack, after this time damage
    float fAtackLengthTime;
    int targetType;  // who start the atack
    int targetId;
    
    public MonsterAtackerClass(StateClass sLink, PersonClass persLink, int mDamage, float mDamageRadius, int mWType, float coolDawn, float atackLength)
    {
        state = sLink;
        myPerson = persLink;
        damage = mDamage;
        damagRadius = mDamageRadius;
        weaponType = mWType;
        lCoolDawn = (long)(coolDawn * 1000);
        atackLengthTime = (long)(atackLength * 1000);
        fAtackLengthTime = atackLength;
        lastAtackTime = System.currentTimeMillis();
        isWaitEndAtack = false;
        atackStartTime = System.currentTimeMillis();
    }
    
    public int GetDamage()
    {
        return damage;
    }
    
    public float GetDamageRadius()
    {
        return damagRadius;
    }
    
    public void AtackTick()
    {
        if(isWaitEndAtack)
        {//damage process, wait the end
            if(System.currentTimeMillis() - atackStartTime > atackLengthTime)
            {
                FinishAtack();
            }
        }
        else
        {
            if(state.GetState() == 2 && (System.currentTimeMillis() - lastAtackTime > lCoolDawn))
            {// follow to enemy
                BoolFloatVec2Class enemyData = state.GetEnemyData();//bool - is enemy exist, float - enemy radius, Vec2 - enemy position
                if(enemyData.GetBoolValue())
                {
                    if(Vector2.GetDistance(enemyData.GetVec2Value(), myPerson.GetPosition()) < damagRadius + myPerson.GetRadius() + enemyData.floatValue)
                    {
                        //come to atack
                        StartAtack(state.GetTargetEnemyType(), state.GetTargetEnemyId());
                    }
                }
                else
                {//state = 2, but enemy data incorrect. Change to iddle
                    state.SetStateToIddle();
                }
            }
        }
    }
    
    void StartAtack(int tType, int tId)
    {
        lastAtackTime = System.currentTimeMillis();
        atackStartTime = System.currentTimeMillis();
        isWaitEndAtack = true;
        targetType = tType;
        targetId = tId;
        NetworkDataProcess.SayMonsterStartAtack(myPerson.GetId(), targetType, targetId, fAtackLengthTime);
        if(weaponType == -1)
        {//close distance atack
            
        }
    }
    
    Vector2 GetAtackPoint()
    {
        Vector2 toReturn = new Vector2(myPerson.GetPosition());
        if(weaponType == -1)
        {//contact atack. Calculate point in the border of the mosnter
            BoolFloatVec2Class eData = state.GetEnemyData(targetType, targetId);
            if(eData.GetBoolValue())
            {
                Vector2 toVector = Vector2.Subtract(eData.GetVec2Value(), myPerson.GetPosition());
                toVector.Normalize();
                toReturn = Vector2.Add(myPerson.GetPosition(), Vector2.MultiplyByScalar(toVector, myPerson.GetRadius()));
            }
        }
        
        return toReturn;
    }
    
    void FinishAtack()
    {
        isWaitEndAtack = false;
        //check is monster near target to apply atack
        BoolFloatVec2Class enemyData = state.GetEnemyData();//bool - is enemy exist, float - enemy radius, Vec2 - enemy position
        if(enemyData.GetBoolValue())
        {
            if(Vector2.GetDistance(enemyData.GetVec2Value(), myPerson.GetPosition()) < damagRadius + myPerson.GetRadius() + enemyData.floatValue)
            {
                //apply damage
                BulletHitDataClass hitData = new BulletHitDataClass();
                BattleController.ApplyDamage(targetType, targetId, 1, myPerson.GetId(), damage, hitData);
                Vector2 atackPoint = GetAtackPoint();
                NetworkDataProcess.SayClientAtackResult(false, -1, -1, atackPoint.GetX(), atackPoint.GetY(), hitData);
            }
        }
    }
}
