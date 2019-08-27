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
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import java.util.List;

/**
 *
 * @author Shekn
 */
public class Handler_UserJoinZone extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        User user = (User)event.getParameter(SFSEventParam.USER);
        Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        
        trace("User: " + user + " connect to zone: " + zone);
        
        //and here we can automaticaly join to the room
        List<Room> rooms = ((OpenWorldZoneExtension) getParentExtension()).GetRooms();
        if(rooms.size() > 0)
        {
            //join user to the first room
            if(rooms.get(0) != null)
            {
                this.getApi().joinRoom(user, rooms.get(0));
            }
        }
    }
}
