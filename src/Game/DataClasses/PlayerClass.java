
package Game.DataClasses;

import Game.Process.NetworkDataProcess;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class PlayerClass extends PersonClass
{
    User user;
    PlayerMovementClass movement;
    int modelIndex;
    FireControllerClass fireController;
    KillsStatisticClass killsStat;
    
    long startDeadTime;
    boolean isDeadInit;
    long deadTime;//in milliseconds
    float deadTimeF; // in seconds
    long actualDeadTime;//on server side something less (why?)
    
    public PlayerClass(User userLink, String accauntName, float speed, int mIndex, float fireCD, float r, int l)
    {
        super(userLink.getId(), accauntName, speed, r, l);
        user = userLink;
        movement = new PlayerMovementClass(speed, this);
        killsStat = new KillsStatisticClass();
        modelIndex = mIndex;
        fireController = new FireControllerClass(fireCD);
        deadTime = GlobalGameData.serverConfig.GetPlayerDeadBlockTime();
        actualDeadTime = (int)(deadTime * 1.0);
        deadTimeF = (float)deadTime / 1000f;
        isDeadInit = false;
    }
    
    public void NoteKiling(int targetType, int targetId)
    {
        if(targetType == 0)
        {//I kill the player
            killsStat.WriteKillPlayer();
        }
        else if(targetType == 1)
        {//I kill mosnter
            killsStat.WriteKillMonster();
        }
        else if(targetType == 2)
        {
            killsStat.WriteKillTower();
        }
    }
    
    public void NoteDeath(int atackerType, int atackerId)
    {
        killsStat.WriteDeath();
    }
    
    public void StartBlockTime()
    {
        isDeadInit = true;
        startDeadTime = System.currentTimeMillis();
    }
    
    public float GetBlockTime()
    {
        return deadTimeF;
    }
    
    public void TryToResurect()
    {
        if(isDeadInit && System.currentTimeMillis() - startDeadTime > actualDeadTime)
        {
            isDeadInit = false;
            SetAlive();
            //Notificate all clients that player resurect
            NetworkDataProcess.SetPlayerState(this, true, false);
        }
    }
    
    public User GetUser()
    {
        return user;
    }
    
    public FireControllerClass GetFireController()
    {
        return fireController;
    }
    
    public PlayerMovementClass GetMovement()
    {
        return movement;
    }
    
    public int GetModelIndex()
    {
        return modelIndex;
    }
    
    public ISFSObject GetKillsData()
    {
        ISFSObject toReturn = new SFSObject();
        toReturn.putInt("id", id);
        toReturn.putUtfString("name", name);
        toReturn.putInt("totalKills", killsStat.GetTotalKillsCount());
        toReturn.putInt("playerKills", killsStat.GetPlayersKillsCount());
        toReturn.putInt("monsterKills", killsStat.GetMonstersKillsCount());
        toReturn.putInt("towerKills", killsStat.GetTowersKillsCount());
        toReturn.putInt("death", killsStat.GetDeathCount());
        return toReturn;
    }
    
}
