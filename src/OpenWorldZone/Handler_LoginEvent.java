package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import java.util.AbstractList;
import java.util.List;

public class Handler_LoginEvent extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
        String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
        trace("Client try name: " + userName + " and password: " + cryptedPass);
        
        if("qwerty".equals(userName))//for test only
        {//wrong user name
            SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);  // return client message abount wrong name
            errData.addParameter(userName);
            // This is logged on the server side
            throw new SFSLoginException("Bad user name: " + userName, errData);
        }
        else
        {//correct user name. Login it and send data about available models
            ConfigDataClass serverConfig = ((OpenWorldZoneExtension)this.getParentExtension()).GetServerConfig();
            if(serverConfig != null)
            {
                ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);  // successfully connect and return client data
                outData.putSFSArray("models", serverConfig.GetPlayersDataSource());
                outData.putSFSArray("bullets", serverConfig.GetBulletsDataSource());
                outData.putSFSArray("rooms", ((OpenWorldZoneExtension)this.getParentExtension()).GetRoomNames());
            }
            else
            {
                SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_INACTIVE_ZONE);  // return client message abount droped zone
                throw new SFSLoginException("Server zone inactive, it has empty configuration", errData);
            }
        }
    }
}
