package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataType;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.util.ClientDisconnectionReason;

public class Handler_RPCFire extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        if(params.containsKey("posX") && params.containsKey("posY") && params.containsKey("angle"))
        {
            SFSDataWrapper posXData = params.get("posX");
            SFSDataWrapper posYData = params.get("posY");
            SFSDataWrapper angleData = params.get("angle");
            if(posXData.getTypeId() == SFSDataType.FLOAT && posYData.getTypeId() == SFSDataType.FLOAT && angleData.getTypeId() == SFSDataType.FLOAT)
            {
                ClientsManagement.ClientFire(sender.getId(), params.getFloat("posX"), params.getFloat("posY"), params.getFloat("angle"));
            }
            else
            {
                Logger.Log("User " + sender + " send wrong data throw RPCFire request");
                sender.disconnect(ClientDisconnectionReason.KICK);
            }
        }
        else
        {
            Logger.Log("User " + sender + " send wrong data throw RPCFire request");
            sender.disconnect(ClientDisconnectionReason.KICK);
        }
    }
    
}
