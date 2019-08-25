/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Process;

import Game.DataClasses.GlobalGameData;
import Game.DataClasses.PlayerClass;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import java.util.Iterator;
import java.util.Map;

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
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            Logger.Log(emc.toString());
        }
    }
    
}
