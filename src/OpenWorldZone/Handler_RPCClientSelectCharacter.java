package OpenWorldZone;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler_RPCClientSelectCharacter extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {//param should contains model index and room name
        int modelIndex = params.getInt("index");
        String roomName = params.getText("room");
        
        Room room = getParentExtension().getParentZone().getRoomByName(roomName);
        if(room == null)
        {//no room with name as in client, do nothing
            trace("Client " + sender + " want connect to the room " + roomName + ", but it does not exists.");
        }
        else
        {
            try 
            {
                ((OpenWorldZoneExtension) this.getParentExtension()).AddUserModelIndex(sender.getId(), modelIndex);
                this.getApi().joinRoom(sender, room);
            } 
            catch (SFSJoinRoomException ex) 
            {
                Logger.getLogger(Handler_RPCClientSelectCharacter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
