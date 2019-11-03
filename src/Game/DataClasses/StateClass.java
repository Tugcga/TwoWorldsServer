package Game.DataClasses;

import OpenWorldRoom.Logger;

public class StateClass 
{
    float switchTime;//время, через которое происходит смена состояния. Выбрасывает попытку идти куда-то
    PersonClass person;//here we can read current location and change it
    
    long lastSwitchTime;//считается в милисекундах
    long emitTime; // time of the appereance
    int currentState;//0 - do nothing, 1 - walk to the point, 2 -- follow to enemy
    LocationClass toWalkLocation;//store the point in the state = 1
    IntIntClass toEnemyData;//type, id для цели, за которой следует, когда state=2 (type=0 - player, 1 - monster)
    long lastWalkTickTime;
    double locationDelta = 0.1f;
    boolean isStateble;
    boolean shouldDestroy;  // if true, destroy the item
    
    boolean isStateNew;//if true, we should update the state on clients
    
    PersonEnemiesClass enemies;
    float enemySearchRadius;
    
    public StateClass(float sTime, PersonClass parent, boolean isStatebleValue, float mESRadius)//sTime from 0 to 1. isStateble=true for monsters, false fo bullet
    {
        isStateble = isStatebleValue;
        enemySearchRadius = mESRadius;
        enemies = new PersonEnemiesClass();
        shouldDestroy = false;
        currentState = 0;
        person = parent;
        switchTime = 1 * (sTime + 1) * 1000;
        lastSwitchTime = System.currentTimeMillis();
        emitTime = System.currentTimeMillis();
        toWalkLocation = new LocationClass();
        toEnemyData = new IntIntClass(-1, -1);
    }
    
    public void AddDamageData(int atackerType, int atackerId, int damage)
    {
        //Logger.Log("Monster " + person.GetId() + " obtain damage from " + atackerType + " " + atackerId + " " + damage);
        enemies.AddDamageData(atackerType, atackerId, damage);
    }
    
    //Update monsters and bullets 50 times per second
    public void TaskTick()
    {
        if(isStateble)//bullet is not stateble
        {
            if(!person.GetIsDead())
            {
                //Find target
                IntIntBoolClass enemyTarget = enemies.GetEnemy(person.GetPosition(), enemySearchRadius);//type, id, isExist
                if(enemyTarget.GetBoolValue())
                {//target is visible
                    if(currentState == 2)
                    {
                        WalkToEnemyTick();
                    }
                    SetStateToFollowEnemy(enemyTarget.GetIntValue01(), enemyTarget.GetIntValue02());
                }
                else
                {//no visible target
                    if(currentState == 2)
                    {//targt invisible, switch to iddle
                        SetStateToMove(person.GetPosition());
                        ComeToTarget();
                    }
                    else
                    {//no targets, continue the state
                        if(currentState == 0)
                        {
                            if(System.currentTimeMillis() - lastSwitchTime > switchTime)
                            {
                                //try to change the state
                                double r = Math.random();
                                if(r < 0.5)
                                {//Change state to 1
                                    Vector2 randomPosition = GetRandomTargetPosition();
                                    if(Vector2.GetDistance(randomPosition, person.GetPosition()) > 2*person.radius)
                                    {
                                        SetStateToMove(randomPosition);
                                    }
                                }
                                else
                                {
                                    //Logger.Log("M" + person.GetId() + " fail change state");
                                }
                                lastSwitchTime = System.currentTimeMillis();
                            }
                        }
                        else if(currentState == 1)
                        {
                            WalkTick();
                        }
                    }
                }
            }
            else
            {
                if(currentState == 1)
                {
                    SetStateToMove(person.GetPosition());
                    ComeToTarget();
                }
            }
        }
        else
        {
            if(currentState == 1)
            {
                WalkTick();
            }
        }
    }
    
    void SetStateToFollowEnemy(int tType, int tId)
    {
        if(currentState == 0 || currentState == 1)
        {
            currentState = 2;
            SetEnemyTargetData(tType, tId);
            isStateNew = true;
        }
        else if(currentState == 2)
        {//already fo to the target, change it
            isStateNew = SetEnemyTargetData(tType, tId);
        }
        else
        {//other state
            
        }
        lastWalkTickTime = System.currentTimeMillis();
    }
    
    boolean SetEnemyTargetData(int tType, int tId)
    {//return true if target is new from prev tick
        if(toEnemyData.GetValue01() == tType && toEnemyData.GetValue02() == tId)
        {
            return false;
        }
        else
        {
            toEnemyData.SetData(tType, tId);
            return true;
        }
    }
    
    BoolFloatVec2Class GetEnemyData(int eType, int eId)
    {
        BoolFloatVec2Class toReturn = new BoolFloatVec2Class();
        if(eType == 0)
        {//enemy is player
            if(GlobalGameData.clients.containsKey(eId))
            {
                PlayerClass player = GlobalGameData.clients.get(eId);
                toReturn.Set(true, player.GetRadius(), player.GetPosition());
            }
        }
        else if(eType == 1)
        {//enemy is monster
            if(GlobalGameData.monsters.containsKey(eId))
            {
                MonsterClass monster = GlobalGameData.monsters.get(eId);
                toReturn.Set(true, monster.GetRadius(), monster.GetPosition());
            }
        }
        else if(eType == 2)
        {//enemy is a tower
            if(GlobalGameData.towers.containsKey(eId))
            {
                TowerClass tower = GlobalGameData.towers.get(eId);
                toReturn.Set(true, tower.GetRadius(), tower.GetPosition());
            }
        }
        return toReturn;
    }
    
    BoolFloatVec2Class GetEnemyData()//bool - is enemy exist, float - enemy radius, Vec2 - enemy position
    {
        if(currentState == 2)
        {
            return GetEnemyData(toEnemyData.GetValue01(), toEnemyData.GetValue02());
        }
        else
        {
            return new BoolFloatVec2Class();
        }
    }
    
    public void SetTargetPosition(Vector2 targetPosition)
    {//only for non-stateble objects (bullets)
        if(!isStateble)
        {
            toWalkLocation = new LocationClass();
            toWalkLocation.SetPosition(targetPosition);
            lastWalkTickTime = System.currentTimeMillis();
            currentState = 1;
            isStateNew = true;
        }
    }
    
    Vector2 GetRandomTargetPosition()
    {
        float angle = (float)(Math.random() * 2 * Math.PI);
        Vector2 s = person.GetPosition();
        Vector2 e = Vector2.Add(s, Vector2.FromPolarToVector(angle, GlobalGameData.serverConfig.GetMonsterMoveRadius()));
        //find collisions with walls between s and e
        EdgeClass edge = new EdgeClass(s, e);
        IntersectionResultClass intersection = GlobalGameData.collisionMap.GetIntersection(edge);
        if(intersection.isIntersection)
        {
            e = intersection.intersectionPoint;
        }
        float t = (float)(Math.random() * 0.9);
        return Vector2.GetInterpolatePoint(s, e, t);
    }
    
    void SetStateToMove(Vector2 newPosition)
    {
        toWalkLocation = new LocationClass();
        toWalkLocation.SetPosition(newPosition);
        lastWalkTickTime = System.currentTimeMillis();
        currentState = 1;
        toEnemyData.SetData(-1, -1);
        isStateNew = true;
    }
    
    void SetStateToMove(float shift)
    {//should not used
        toWalkLocation = new LocationClass();
        toWalkLocation.SetPosition(person.GetPosition());
        toWalkLocation.SetRandomShift(shift);
        lastWalkTickTime = System.currentTimeMillis();
        currentState = 1;
        isStateNew = true;
    }
    
    public void SetStateToIddle()
    {
        lastSwitchTime = System.currentTimeMillis();
        toWalkLocation = new LocationClass();
        toWalkLocation.SetPosition(person.GetPosition());
        currentState = 0;
        toEnemyData.SetData(-1, -1);
        isStateNew = true;
    }
    
    public void SetPosition(Vector2 pos)
    {
        person.GetLocation().SetPosition(pos);
    }
    
    void WalkToEnemyTick()
    {
        boolean isCorrect = false;
        Vector2 targetPosition = new Vector2();
        if(toEnemyData.GetValue01() == 0)
        {//target is player
            if(GlobalGameData.clients.containsKey(toEnemyData.GetValue02()))
            {
                isCorrect = true;
                targetPosition = GlobalGameData.clients.get(toEnemyData.GetValue02()).GetPosition();
            }
        }
        else if(toEnemyData.GetValue01() == 1)
        {//target is monster
            
        }
        else if(toEnemyData.GetValue01() == 2)
        {//target is tower
            
        }
        if(isCorrect)
        {//make move tick
            Vector2 myPosition = person.GetPosition();
            double deltaTime = (float)(System.currentTimeMillis() - lastWalkTickTime) / 1000f;
            if(Vector2.GetDistance(myPosition, targetPosition) > deltaTime * person.GetSpeed())
            {
                Vector2 toTarget = Vector2.Subtract(targetPosition, myPosition);
                toTarget.Normalize();
                Vector2 newPosition = Vector2.Add(myPosition, Vector2.MultiplyByScalar(toTarget, deltaTime * person.GetSpeed()));
                person.GetLocation().SetPosition(newPosition);
            }
        }
        lastWalkTickTime = System.currentTimeMillis();
    }
    
    void WalkTick()
    {
        LocationClass s = person.GetLocation();
        Vector2 toTarget = new Vector2(toWalkLocation.GetX() - s.GetX(), toWalkLocation.GetY() - s.GetY());
        double l = toTarget.GetLength();
        if(l < locationDelta)
        {
            //we come to the target
            ComeToTarget();
        }
        else
        {
            Vector2 toTargetNormal = new Vector2(toTarget.GetX() / l, toTarget.GetY() / l);
            double deltaTime = (float)(System.currentTimeMillis() - lastWalkTickTime) / 1000f;
            Vector2 lastPosition = s.GetPosition();
            Vector2 newPosition = new Vector2(lastPosition.GetX() + deltaTime * person.GetSpeed() * toTargetNormal.GetX(), lastPosition.GetY() + deltaTime * person.GetSpeed() * toTargetNormal.GetY());
            Vector2 toTargetNew = new Vector2(toWalkLocation.GetX() - newPosition.GetX(), toWalkLocation.GetY() - newPosition.GetY());
            double d = Vector2.Dot(toTargetNormal, toTargetNew);
            if(Vector2.Dot(toTarget, toTargetNew) > 0)
            {
                person.GetLocation().SetPosition(newPosition);
            }
            else
            {//jump over end point. Come to target
                ComeToTarget();
            }
        }
        lastWalkTickTime = System.currentTimeMillis();
    }
    
    void ComeToTarget()
    {
        person.SetLocation(toWalkLocation);
        SetStateToIddle();
        if(!isStateble)
        {
            shouldDestroy = true;
        }
    }
    
    public void UpShouldDestroy()
    {
        SetStateToIddle();
        if(!isStateble)
        {
            shouldDestroy = true;
        }
    }
    
    //------------Getters--------------
    
    public int GetState()
    {
        return currentState;
    }
    
    public boolean GetShouldDestroy()
    {
        return shouldDestroy;
    }
    
    public LocationClass GetTargetLocation()
    {
        return toWalkLocation;
    }
    
    public int GetTargetEnemyType()
    {
        return toEnemyData.GetValue01();
    }
    
    public int GetTargetEnemyId()
    {
        return toEnemyData.GetValue02();
    }
    
    public long GetEmitTime()
    {
        return emitTime;
    }
    
    public boolean IsStateNew()
    {
        if(isStateNew)
        {
            isStateNew = false;
            return true;
        }
        else
        {
            return false;
        }
    }
}
