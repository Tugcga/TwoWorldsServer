/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Process;

import Game.DataClasses.BulletClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.MonsterClass;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

import java.util.Iterator;
import java.util.Map;

public class MonsterControllerTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            for (Iterator<Map.Entry<Integer, MonsterClass>> it = GlobalGameData.monsters.entrySet().iterator(); it.hasNext();)
            {
                MonsterClass monster = it.next().getValue();
                monster.GetState().TaskTick();
                monster.GetAtacker().AtackTick();
                if(monster.GetIsDead())
                {
                    NetworkDataProcess.SayMonsterDead(monster.GetId());
                    MonstersManagement.DeleteMonster(monster.GetId());
                    it.remove();
                }
                else
                {
                    //update variables of the monster
                    boolean isStateNew = monster.GetState().IsStateNew();
                    if(isStateNew)
                    {
                        NetworkDataProcess.SayMonsterChangeState(monster);
                    }
                    NetworkDataProcess.SetMonsterState(monster, isStateNew, false);
                }
            }
            for(Iterator<Map.Entry<Integer, BulletClass>> it = GlobalGameData.bullets.entrySet().iterator(); it.hasNext();)
            {
                BulletClass bullet = it.next().getValue();
                if(bullet.GetForceDestroy())
                {
                    MonstersManagement.DestoyBullet(bullet.GetId());
                    it.remove();
                }
                else
                {
                    if(bullet.IsDelayOver())
                    {
                        bullet.GetState().TaskTick();// просто передвигаем снаряд
                        if(!bullet.GetState().GetShouldDestroy())
                        {//до цели не долетели
                            bullet.CheckCollisions();// проверяем, не столкнулись ли с кем
                        }
                        if(bullet.GetState().GetShouldDestroy())//shouldDestoy поднимается, когда снаряд долетел до конечной точки. То есть кончился срок жизни или же с кем-то встретился
                        {
                            if(bullet.IsEffectedOnlyEnd())// это rocket bullet, долетел до цели
                            {
                                bullet.CalculateDamage();
                            }
                            MonstersManagement.DestoyBullet(bullet.GetId());
                            it.remove();
                        }
                        else
                        {
                            NetworkDataProcess.SetBulletState(bullet, bullet.GetState().IsStateNew(), false);
                        }
                    }
                    
                }
                
            }
        }
        catch (Exception e)
        {
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
    
}
