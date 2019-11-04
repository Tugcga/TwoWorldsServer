package Game.Process;

import Game.DataClasses.BulletHitDataClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.MonsterClass;
import Game.DataClasses.PlayerClass;
import Game.DataClasses.TowerClass;
import OpenWorldRoom.Logger;

public class BattleController 
{
    public static void ApplyDamage(int targetType, int targetId, int atackerType, int atackerId, int damage, BulletHitDataClass hitData)
    {
        if(targetType == 0)
        {
            if(GlobalGameData.clients.containsKey(targetId))
            {
                PlayerClass player = GlobalGameData.clients.get(targetId);
                player.ApplyDamage(damage);
                
                if(player.GetIsDead())
                {
                    player.NoteDeath(atackerType, atackerId);
                    WriteKillFact(0, player.GetId(), atackerType, atackerId);
                    player.StartBlockTime();  // player is dead, block it
                }
                hitData.AddData(0, targetId, player.GetLife(), player.GetMaxLife(), player.GetIsDead(), player.GetBlockTime(), player.GetPosition());
                NetworkDataProcess.SetPlayerState(player, false, false);
            }
        }
        else if(targetType == 1)
        {
            if(GlobalGameData.monsters.containsKey(targetId))
            {
                MonsterClass monster = GlobalGameData.monsters.get(targetId);
                monster.ApplyDamage(damage);
                
                if(monster.GetIsDead())
                {
                    WriteKillFact(1, monster.GetId(), atackerType, atackerId);  // monster is dead, remember this fact
                }
                else
                {
                    monster.AddDamageData(atackerType, atackerId, damage);
                }
                hitData.AddData(1, targetId, monster.GetLife(), monster.GetMaxLife(), monster.GetIsDead(), 0, monster.GetPosition());
                NetworkDataProcess.SetMonsterState(monster, false, false);
            }
        }
        else if(targetType == 2)
        {
            if(GlobalGameData.towers.containsKey(targetId))
            {
                TowerClass tower = GlobalGameData.towers.get(targetId);
                boolean isDestory = tower.ApplyDamage(damage);
                
                if(isDestory && tower.GetIsDead())
                {
                    WriteKillFact(2, tower.GetId(), atackerType, atackerId);
                    tower.StartDeath();
                    Logger.Log("Tower " + targetId + " destroyed.");
                }
                else
                {
                    tower.AddDamageData(atackerType, atackerId, damage);  // write the atacker to the tower
                }
                hitData.AddData(2, targetId, tower.GetLife(), tower.GetMaxLife(), tower.GetIsDead(), 0, tower.GetPosition());
                NetworkDataProcess.SetTowerState(tower, false, false);
            }
        }
    }
    
    static void WriteKillFact(int targetType, int targetId, int atackerType, int atackerId)
    {
        if(atackerType == 0 && GlobalGameData.clients.containsKey(atackerId))
        {
            GlobalGameData.clients.get(atackerId).NoteKiling(targetType, targetId);
        }
    }
}
