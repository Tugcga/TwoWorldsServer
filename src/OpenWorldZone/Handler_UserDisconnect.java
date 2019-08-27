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
import com.smartfoxserver.v2.util.ClientDisconnectionReason;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Shekn
 */
public class Handler_UserDisconnect extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        //this event calls when user login and disconnect from server
        User user = (User)event.getParameter(SFSEventParam.USER);
        Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        //List<Room> rooms = (List<Room>) event.getParameter(SFSEventParam.JOINED_ROOMS);
        //Map<Room, Integer> playerIds = (Map<Room, Integer>) event.getParameter(SFSEventParam.PLAYER_IDS_BY_ROOM);
        ClientDisconnectionReason reason = (ClientDisconnectionReason) event.getParameter(SFSEventParam.DISCONNECTION_REASON);
        trace("Disconnect user: " + user + " from zone: " + zone + ", reason: " + reason.toString());
        //((OpenWorldZoneExtension)getParentExtension()).RemoveUserData(user.getId());
        ((OpenWorldZoneExtension)getParentExtension()).RemoveUserData(user.getSession().getId());
    }
}
