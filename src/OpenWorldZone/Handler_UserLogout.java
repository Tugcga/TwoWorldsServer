package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class Handler_UserLogout extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        User user = (User)event.getParameter(SFSEventParam.USER);
        Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        //List<Room> rooms = (List<Room>) event.getParameter(SFSEventParam.JOINED_ROOMS);
        //Map<Room, Integer> playerIds = (Map<Room, Integer>) event.getParameter(SFSEventParam.PLAYER_IDS_BY_ROOM);
        
        trace("User: " + user + " from zone: " + zone + " logout");
        //delete userData
        ((OpenWorldZoneExtension)getParentExtension()).RemoveUserData(user.getSession().getId());
    }
}
