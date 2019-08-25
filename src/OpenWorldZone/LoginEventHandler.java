/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldZone;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class LoginEventHandler extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
        String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
        //ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
        
        if("qwerty".equals(userName))
        {//wrong user name
            SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
            errData.addParameter(userName);
            // This is logged on the server side
            throw new SFSLoginException("Bad user name: " + userName, errData);
        }
        else
        {//correct user name. Login it and send data about available models
            ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
            
            outData.putSFSArray("models", ((OpenWorldZoneExtension)this.getParentExtension()).GetServerConfig().GetPlayersDataSource());
            outData.putSFSArray("bullets", ((OpenWorldZoneExtension)this.getParentExtension()).GetServerConfig().GetBulletsDataSource());
        }
    }
    
}
