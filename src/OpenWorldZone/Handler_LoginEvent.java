package OpenWorldZone;

import com.smartfoxserver.bitswarm.sessions.Session;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class Handler_LoginEvent extends BaseServerEventHandler
{
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException 
    {
        //read input parameters
        String userName = LoginNamesController.FilterName((String)event.getParameter(SFSEventParam.LOGIN_NAME));
        //String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
        //Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        Session session = (Session)event.getParameter(SFSEventParam.SESSION);
        SFSObject inData = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);  // get data of the login
        SFSObject outData = (SFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
        
        {//correct user name. Login it and send data about available models
            //if the login is correct, automaticaly join it to the first room
            //in fact, here we can separate the connection flow and connect exeptional user to the other room
            if(((OpenWorldZoneExtension) this.getParentExtension()).GetRoomNames().size() > 0)
            {
                //here we save data about logn model_index
                if(inData != null && inData.containsKey("model_index"))
                {
                    String newName = LoginNamesController.AddLoginName(userName);
                    outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, newName);
                    
                    ((OpenWorldZoneExtension) this.getParentExtension()).AddUserModelIndex(session.getId(), inData.getInt("model_index"));
                }
                else
                {
                    SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
                    throw new SFSLoginException("User login does not contains data about model index, block login process: ", errData);
                }
            }
            else
            {
                //zone does not contains room, skip login to this zone
                SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_INACTIVE_ZONE);
                throw new SFSLoginException("Zone does not contains any room, skip login process: ", errData);
            }
        }
    }
}
