package Game.Process;

import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

public class KillsNotificatorTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            NetworkDataProcess.KillsMessage();
        }
        catch (Exception e)
        {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
    
}
