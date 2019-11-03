package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class Handler_RPCClientSendMovementKey extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        if(params.containsKey("dirIndex") && params.containsKey("angle"))
        {
            ClientsManagement.ClientChangeMovement(sender.getId(), params.getInt("dirIndex"), params.getFloat("angle"));
        }
    }
}
