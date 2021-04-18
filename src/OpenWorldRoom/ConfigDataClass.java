package OpenWorldRoom;

import Game.DataClasses.BulletParametersClass;
import Game.DataClasses.CollectableParametersClass;
import Game.DataClasses.MonsterParametersClass;
import Game.DataClasses.PlayerModelParametersClass;
import Game.DataClasses.TowerParametersClass;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.util.JSONUtil;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;

//Store configuration data of the server. All numeric values
public class ConfigDataClass 
{
    Map<Integer, PlayerModelParametersClass> playerModels;
    Map<Integer, MonsterParametersClass> monsterParameters;
    Map<Integer, BulletParametersClass> bulletsParameters;
    Map<Integer, TowerParametersClass> towerParameters;
    Map<Integer, CollectableParametersClass> collectableParameters;
    ISFSArray playersDataSource;
    public ISFSArray GetPlayersDataSource() {return playersDataSource;}
    ISFSArray bulletsDataSource;
    public ISFSArray GetBulletsDataSource() {return bulletsDataSource;}
    ISFSArray towerDataSource;
    public ISFSArray GetTowerDataSource() {return towerDataSource;}
    ISFSArray collectableDataSource;
    public ISFSArray GetCollectableDataSource() {return collectableDataSource;}
    
    int playerDeadBlockTime;
    public int GetPlayerDeadBlockTime(){return playerDeadBlockTime;}
    float monsterMoveRadius;
    public float GetMonsterMoveRadius(){return monsterMoveRadius;}
    int monsterTaskUpdateTime;
    public int GetMonsterTaskUpdateTime(){return monsterTaskUpdateTime;}
    int playerMovementTaskUpdateTime;
    public int GetPlayerMovementTaskUpdateTime(){return playerMovementTaskUpdateTime;}
    int killsNotificatorTime;
    public int GetKillsNotificatorTime(){return killsNotificatorTime;}
    int maxCountRTree;
    public int GetMaxCountRTree(){return maxCountRTree;}
    int shotMaxErrors;
    public int GetShotMaxErrors() {return shotMaxErrors;}
    float collisionEdgeDelta;
    public float GetCollisionEdgeDelta() {return collisionEdgeDelta;}
    int towerResurectMonsterTime;
    public int GetTowerResurectMonsterTime() {return towerResurectMonsterTime;}
    int towerAtackTickTime;
    public int GetTowerAtackTickTime() {return towerAtackTickTime;}
    int filterLoggedUserNamesTime;
    public int GetFilterLoggedUserNamesTime() {return filterLoggedUserNamesTime;}
    int collectableTaskUpdateTime;
    public int GetCollectableTaskUpdateTime() {return collectableTaskUpdateTime;}
    double collectableQuadrantSize;
    public double GetCollectableQuadrantSize() {return collectableQuadrantSize;}
    float healAfterDeathProbability;
    public float GetHealAfterDeathProbability() {return healAfterDeathProbability;}
        
    public ConfigDataClass(String fileName) throws IOException
    {
        String configDataStr = JSONUtil.stripComments(FileUtils.readFileToString(new File(fileName)));
        ISFSObject configObject = SFSObject.newFromJsonData(configDataStr);
        SetPlayerData(configObject.getSFSArray("playerModels"));
        SetMonsterData(configObject.getSFSArray("monsters"));
        SetServerParameters(configObject.getSFSArray("serverParameters").getSFSObject(0));
        SetBulletsParams(configObject.getSFSArray("bullets"));
        SetTowerParameters(configObject.getSFSArray("towers"));
        SetCollectableParameters(configObject.getSFSArray("collectables"));
    }
    
    private void SetPlayerData(ISFSArray playerData)
    {
        playersDataSource = playerData;
        playerModels = new ConcurrentHashMap<>();
        for(int i = 0; i < playerData.size(); i++)
        {
            ISFSObject pData = playerData.getSFSObject(i);
            PlayerModelParametersClass newPData = new PlayerModelParametersClass(pData);
            playerModels.put(i, newPData);
        }
    }
    
    private void SetMonsterData(ISFSArray monsterData)
    {
        monsterParameters = new ConcurrentHashMap<>();
        for(int i = 0; i < monsterData.size(); i++)
        {
            ISFSObject mData = monsterData.getSFSObject(i);
            MonsterParametersClass newMParams = new MonsterParametersClass(mData);
            monsterParameters.put(i, newMParams);
        }
    }
    
    private void SetServerParameters(ISFSObject serverParams)
    {
        playerDeadBlockTime = serverParams.getInt("playerDeadBlockTime");
        monsterMoveRadius = serverParams.getFloat("monsterMoveRadius");
        monsterTaskUpdateTime = serverParams.getInt("monsterTaskUpdateTime");
        playerMovementTaskUpdateTime = serverParams.getInt("playerMovementTaskUpdateTime");
        killsNotificatorTime = serverParams.getInt("killsNotificatorTime");
        maxCountRTree = serverParams.getInt("maxCountRTree");
        shotMaxErrors = serverParams.getInt("shotMaxErrors");
        collisionEdgeDelta = serverParams.getFloat("collisionEdgeDelta");
        towerResurectMonsterTime = serverParams.getInt("towerResurectMonsterTime");
        towerAtackTickTime = serverParams.getInt("towerAtackTickTime");
        filterLoggedUserNamesTime = serverParams.getInt("filterLoggedUserNamesTime");
        collectableTaskUpdateTime = serverParams.getInt("collectableTaskUpdateTime");
        collectableQuadrantSize = serverParams.getDouble("collectableQuadrantSize");
        healAfterDeathProbability = serverParams.getFloat("healAfterDeathProbability");
    }
    
    private void SetBulletsParams(ISFSArray bulletsParams)
    {
        bulletsDataSource = bulletsParams;
        bulletsParameters = new ConcurrentHashMap<>();
        for(int i = 0; i < bulletsParams.size(); i++)
        {
            ISFSObject bullet = bulletsParams.getSFSObject(i);
            BulletParametersClass newParams = new BulletParametersClass(bullet);
            bulletsParameters.put(newParams.GetType(), newParams);
        }
    }
    
    private void SetTowerParameters(ISFSArray towerParams)
    {
        towerDataSource = towerParams;
        towerParameters = new ConcurrentHashMap<>();
        for(int i = 0; i < towerParams.size(); i++)
        {
            ISFSObject tower = towerParams.getSFSObject(i);
            TowerParametersClass newParams = new TowerParametersClass(tower);
            towerParameters.put(newParams.GetType(), newParams);
        }
    }
    
    private void SetCollectableParameters(ISFSArray collectableParams)
    {
        collectableDataSource = collectableParams;
        collectableParameters = new ConcurrentHashMap<>();
        for(int i = 0; i < collectableParams.size(); i++)
        {
            ISFSObject collectable = collectableParams.getSFSObject(i);
            CollectableParametersClass newParams = new CollectableParametersClass(collectable);
            collectableParameters.put(newParams.GetType(), newParams);
        }
    }
    
    public BulletParametersClass GetBulletParameters(int bulletType)
    {
        if(bulletsParameters.containsKey(bulletType))
        {
            return bulletsParameters.get(bulletType);
        }
        else
        {
            Integer[] keySet = bulletsParameters.keySet().toArray(new Integer[0]);
            return bulletsParameters.get(keySet[0]);
        }
    }
    
    public boolean IsPlayerModelCorrect(int modelType)
    {
        if(playerModels.containsKey(modelType))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public PlayerModelParametersClass GetPlayerModelParameters(int modelType)
    {
        if(playerModels.containsKey(modelType))
        {
            return playerModels.get(modelType);
        }
        else
        {
            Integer[] keySet = playerModels.keySet().toArray(new Integer[0]);
            return playerModels.get(keySet[0]);
        }
    }
    
    public MonsterParametersClass GetMonsterParameters(int monsterType)
    {
        if(monsterParameters.containsKey(monsterType))
        {
            return monsterParameters.get(monsterType);
        }
        else
        {
            Integer[] keySet = monsterParameters.keySet().toArray(new Integer[0]);
            return monsterParameters.get(keySet[0]);
        }
    }
    
    public TowerParametersClass GetTowerParameters(int towerType)
    {
        if(towerParameters.containsKey(towerType))
        {
            return towerParameters.get(towerType);
        }
        else
        {
            Integer[] keySet = towerParameters.keySet().toArray(new Integer[0]);
            return towerParameters.get(keySet[0]);
        }
    }
    
    public CollectableParametersClass GetCollectableParameters(int collectType)
    {
        if(collectableParameters.containsKey(collectType))
        {
            return collectableParameters.get(collectType);
        }
        else
        {
            Integer[] keySet = collectableParameters.keySet().toArray(new Integer[0]);
            return collectableParameters.get(keySet[0]);
        }
    }
}
