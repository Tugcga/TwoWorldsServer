/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Process;

import Game.DataClasses.BulletHitDataClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.MonsterClass;
import Game.DataClasses.PlayerClass;
import Game.DataClasses.TowerClass;

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
                //NetworkDataProcess.SetPlayerState(player, true, false);
                if(player.GetIsDead())
                {
                    player.NoteDeath(atackerType, atackerId);
                    WriteKillFact(0, player.GetId(), atackerType, atackerId);
                    player.StartBlockTime();//если игрок умер, то блокируем его передвижение на какое-то время. Потом оживляем (это выполняется в таске)
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
                //NetworkDataProcess.SetMonsterState(monster, true, false);
                if(monster.GetIsDead())
                {
                    WriteKillFact(1, monster.GetId(), atackerType, atackerId);
                }
                else
                {//пишем монстру, кто нанес удар
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
                //NetworkDataProcess.SetTowerState(tower, true, false);
                if(isDestory && tower.GetIsDead())
                {
                    WriteKillFact(2, tower.GetId(), atackerType, atackerId);
                    tower.StartDeath();
                }
                else
                {//пишем, кто нанес удар
                    tower.AddDamageData(atackerType, atackerId, damage);
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
