package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class Handler_RPCFire extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        if(params.containsKey("posX") && params.containsKey("posY") && params.containsKey("angle"))
        {
            ClientsManagement.ClientFire(sender.getId(), params.getFloat("posX"), params.getFloat("posY"), params.getFloat("angle"));
        }
    }
    
}
