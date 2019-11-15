package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import java.util.List;

public class Handler_UserJoinZone extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        User user = (User)event.getParameter(SFSEventParam.USER);
        //Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
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
