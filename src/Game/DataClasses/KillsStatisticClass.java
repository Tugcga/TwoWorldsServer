/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

public class KillsStatisticClass 
{
    int deathCount;
    public int GetDeathCount(){return deathCount;}
    int totalKillsCount;
    public int GetTotalKillsCount(){return totalKillsCount;}
    int playersKillsCount;
    public int GetPlayersKillsCount(){return playersKillsCount;}
    int monstersKillsCount;
    public int GetMonstersKillsCount(){return monstersKillsCount;}
    int towersKillsCount;
    public int GetTowersKillsCount() {return towersKillsCount;}
    
    
    public KillsStatisticClass()
    {
        deathCount = 0;
        totalKillsCount = 0;
        playersKillsCount = 0;
        monstersKillsCount = 0;
        towersKillsCount = 0;
    }
    
    public void WriteDeath()
    {
        deathCount = deathCount + 1;
    }
    
    public void WriteKillPlayer()
    {
        totalKillsCount = totalKillsCount + 1;
        playersKillsCount = playersKillsCount + 1;
    }
    
    public void WriteKillMonster()
    {
        totalKillsCount = totalKillsCount + 1;
        monstersKillsCount = monstersKillsCount + 1;
    }
    
    public void WriteKillTower()
    {
        totalKillsCount++;
        towersKillsCount++;
    }
}
