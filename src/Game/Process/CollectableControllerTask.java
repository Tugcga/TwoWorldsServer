package Game.Process;

import Game.DataClasses.GlobalGameData;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

public class CollectableControllerTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            GlobalGameData.collectableProcessor.Update();
        }
        catch (Exception e)
        {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
}
