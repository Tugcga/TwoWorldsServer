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
                    NetworkDataProcess.SetMonsterState(monster, false, false);
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
                        bullet.GetState().TaskTick();// move the bullet
                        if(!bullet.GetState().GetShouldDestroy())
                        {//до цели не долетели
                            bullet.CheckCollisions();
                        }
                        if(bullet.GetState().GetShouldDestroy())//shouldDestoy is on if the bullet comes to the target
                        {
                            if(bullet.IsEffectedOnlyEnd())// rocket bullet come th the target
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
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
    
}
