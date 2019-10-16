package OpenWorldZone;

import Game.DataClasses.ChatMessagesStore;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

public class SaveChatTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            ChatMessagesStore.SaveMessages();
        }
        catch (Exception e)
        {
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
    
}
