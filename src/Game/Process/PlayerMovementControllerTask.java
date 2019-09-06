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

/**
 *
 * @author Philipp
 */
public class PlayerMovementControllerTask implements Runnable
{
    @Override
    public void run() 
    {
        try
        {
            for (Iterator<Map.Entry<Integer, PlayerClass>> it = GlobalGameData.clients.entrySet().iterator(); it.hasNext();)
            {
                PlayerClass player = it.next().getValue();
                if(!player.GetIsDead())
                {
                    if(player.GetMovement().IsMove())
                    {
                        player.GetMovement().MoveTick();
                        boolean isStateNew = player.GetMovement().IsStateNew();
                        if(isStateNew)
                        {
                            NetworkDataProcess.UpdateClientData(player);
                        }
                        NetworkDataProcess.SetPlayerState(player, isStateNew, false);
                    }
                    else
                    {
                        if(player.GetMovement().IsStateNew())
                        {
                            NetworkDataProcess.UpdateClientData(player);
                            NetworkDataProcess.SetPlayerState(player, true, false);
                        }
                    }
                }
                else
                {
                    if(player.GetMovement().IsMove())
                    {
                        player.GetMovement().SetDirIndex(-1);
                    }
                    player.TryToResurect();
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
