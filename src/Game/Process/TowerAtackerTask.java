package Game.Process;

import Game.DataClasses.GlobalGameData;
import Game.DataClasses.TowerClass;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import java.util.Iterator;
import java.util.Map;

public class TowerAtackerTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            for (Iterator<Map.Entry<Integer, TowerClass>> it = GlobalGameData.towers.entrySet().iterator(); it.hasNext();)
            {
                TowerClass tower = it.next().getValue();
                if(!tower.GetIsDead())
                {
                    tower.GetAtacker().AtackTick();
                }
                else
                {
                    tower.TryToResurect();
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
