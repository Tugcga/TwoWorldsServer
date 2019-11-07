package Game.DataClasses;

import Game.Process.ClientsManagement;

public class FireControllerClass 
{
    float fireCoolDawn;  // in seconds
    float fireCoolDawn_check;
    
    int errorsMaxCount;
    int currentShotErrors;
    
    int playerId;
    
    long lastFireTime;  // in miliseconds
    
    public FireControllerClass(float fireCD, int _errorsMaxCount, int _playerId)
    {
        fireCoolDawn = fireCD;
        fireCoolDawn_check = fireCoolDawn * 0.9f * 1000;
        lastFireTime = 0;
        errorsMaxCount = _errorsMaxCount;
        currentShotErrors = 0;
        playerId = _playerId;
    }
    
    public  float GetCoolDawn()
    {
        return fireCoolDawn;
    }
    
    public boolean TryStartbullet()
    {
        if(System.currentTimeMillis() - lastFireTime > fireCoolDawn_check)
        {
            lastFireTime = System.currentTimeMillis();
            currentShotErrors = 0;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void IncreaseShotError()
    {
        currentShotErrors ++;
        if(currentShotErrors >= errorsMaxCount)
        {
            ClientsManagement.ClientOverShotErrors(playerId);
        }
    }
}
