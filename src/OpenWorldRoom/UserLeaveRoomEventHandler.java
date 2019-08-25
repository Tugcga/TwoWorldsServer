/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 *
 * @author Philipp
 */
public class UserLeaveRoomEventHandler extends BaseServerEventHandler 
{ 
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException 
	{
            User user = (User) event.getParameter(SFSEventParam.USER);
            trace("User " + user.getName() + " leave the room");
            ClientsManagement.ClientLeaveTheGame(user);
	}
    
}
