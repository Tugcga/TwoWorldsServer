package OpenWorldRoom;

import Game.DataClasses.GlobalGameData;
import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinRoomEventHandler extends BaseServerEventHandler 
{ 
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException 
	{
            User user = (User) event.getParameter(SFSEventParam.USER);
            
            boolean needMap = (boolean)GlobalGameData.server.getParentZone().getExtension().handleInternalMessage("GetUserNeedMap", user.getSession().getId());
            if(needMap)
            {
                this.send("RPCMap", GlobalGameData.collisionMap.GetMapParams(), user);
            }
            
            ClientsManagement.ClientJoinToTheGame(user);
	}
    
}
