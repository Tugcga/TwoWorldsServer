package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataType;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.util.ClientDisconnectionReason;

public class Handler_RPCClientSendMovementKey extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        if(params.containsKey("dirIndex") && params.containsKey("angle"))
        {
            SFSDataWrapper dirIndexData = params.get("dirIndex");
            SFSDataWrapper angleData = params.get("angle");
            if(dirIndexData.getTypeId() == SFSDataType.INT && angleData.getTypeId() == SFSDataType.FLOAT)
            {
                ClientsManagement.ClientChangeMovement(sender.getId(), params.getInt("dirIndex"), params.getFloat("angle"));
            }
            else
            {
                //requst contains data but wrong types, kick the user
                Logger.Log("User " + sender + " send wrong data throw RPCClientSendMovementKey request");
                sender.disconnect(ClientDisconnectionReason.KICK);
            }
        }
        else
        {
            //requst does not contains proper data, kick the user
            Logger.Log("User " + sender + " send wrong data throw RPCClientSendMovementKey request");
            sender.disconnect(ClientDisconnectionReason.KICK);
        }
    }
}
