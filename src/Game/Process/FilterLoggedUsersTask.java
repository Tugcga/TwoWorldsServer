package Game.Process;

import Game.DataClasses.GlobalGameData;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

public class FilterLoggedUsersTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("FilterLoggedUsers", GlobalGameData.room.getUserList());
        }
        catch (Exception e)
        {
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
}
