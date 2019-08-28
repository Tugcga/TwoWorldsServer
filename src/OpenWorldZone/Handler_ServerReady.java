package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class Handler_ServerReady extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {//no parameters
        trace("Zone event: Server ready");
    }
}
