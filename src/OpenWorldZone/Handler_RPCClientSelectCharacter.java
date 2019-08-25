/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldZone;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class Handler_RPCClientSelectCharacter extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        int modelIndex = params.getInt("index");
        
        Room room = getParentExtension().getParentZone().getRoomByName("World");
        try 
        {
            ((OpenWorldZoneExtension) this.getParentExtension()).AddUserModelIndex(sender.getId(), modelIndex);
            getApi().joinRoom(sender, room);
        } 
        catch (SFSJoinRoomException ex) 
        {
            Logger.getLogger(Handler_RPCClientSelectCharacter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
