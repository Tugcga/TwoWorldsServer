package Game.DataClasses;

import Game.Process.NetworkDataProcess;
import com.smartfoxserver.v2.mmo.MMOItem;

public class TowerClass extends PersonClass
{
    MMOItem tower;
    int towerType;
    TowerMonsterSpawnerClass monsterSpawner;
    TowerAtackerClass atacker;
    
    long startDeadTime;
    boolean isDeadInit;
    long actualDeadTime;
    
    boolean isSpawnMonsterInDead;
    boolean isAgreMonsterToAtacker;
    
    public TowerClass(MMOItem towerLink, int tType, String towerName, float radius, int life, int mCount, 
            float mMinRadius, float mMaxRadius, float atackRadius, float atackCD, int bulletType,
            float accRadius, long resurectTime, boolean tIsSpawnInDead, boolean tIsAgreMonsterToAtacker, int bPerShot, int shotBDelay)
    {
        super(towerLink.getId(), towerName, 0, radius, life);
        tower = towerLink;
        towerType = tType;
        isSpawnMonsterInDead = tIsSpawnInDead;
        isAgreMonsterToAtacker = tIsAgreMonsterToAtacker;
        monsterSpawner = new TowerMonsterSpawnerClass(this.GetId(), mCount, mMinRadius, mMaxRadius);
        atacker = new TowerAtackerClass(this, atackRadius, atackCD, bulletType, accRadius, bPerShot, shotBDelay);
        
        isDeadInit = false;
        actualDeadTime = resurectTime;
    }
    
    public void AddDamageData(int atackerType, int atackerId, int damage)
    {
        if(isAgreMonsterToAtacker)
        {
            int[] activeMonsters = monsterSpawner.GetActiveMonsters();
            for(int mId : activeMonsters)
            {
                GlobalGameData.monsters.get(mId).AddDamageData(atackerType, atackerId, damage);
            }
        }
    }
    
    public boolean IsSpawnMonsterInDead()
    {
        return isSpawnMonsterInDead;
    }
    
    public MMOItem GetTowerAsItem()
    {
        return tower;
    }
    
    public int GetTowerType()
    {
        return towerType;
    }
    
    public TowerMonsterSpawnerClass GetMonsterSpawner()
    {
        return monsterSpawner;
    }
    
    public TowerAtackerClass GetAtacker()
    {
        return atacker;
    }
    
    public void StartDeath()
    {
        isDeadInit = true;
        startDeadTime = System.currentTimeMillis();
    }
    
    public void TryToResurect()
    {
        if(isDeadInit && System.currentTimeMillis() - startDeadTime > actualDeadTime)
        {
            isDeadInit = false;
            SetAlive();
            //Notificate all clients that tower resurects
            NetworkDataProcess.SayTowerResurect(this);
            NetworkDataProcess.SetTowerState(this, true, false);
        }
    }
}
