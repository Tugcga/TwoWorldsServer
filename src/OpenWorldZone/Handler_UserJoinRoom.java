/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 *
 * @author Shekn
 */
public class Handler_UserJoinRoom extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        User user = (User)event.getParameter(SFSEventParam.USER);
        Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        Room room = (Room)event.getParameter(SFSEventParam.ROOM);
        
        trace("User " + user + " join to the room " + room);
        
    }
}
