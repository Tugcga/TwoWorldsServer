package OpenWorldZone;

import com.smartfoxserver.bitswarm.sessions.Session;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
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
        //read input parameters
        String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
        String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
        Zone zone = (Zone)event.getParameter(SFSEventParam.ZONE);
        Session session = (Session)event.getParameter(SFSEventParam.SESSION);
        SFSObject inData = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);  // get data of the login
        
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
            //if the login is correct, automaticaly join it to the first room
            //in fact, here we can separate the connection flow and connect exeptional user to the other room
            if(((OpenWorldZoneExtension) this.getParentExtension()).GetRoomNames().size() > 0)
            {
                //here we save data about logn model_index
                if(inData.containsKey("model_index"))
                {
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
            
            
            
            /*List<Room> zoneRooms = this.getParentExtension().getParentZone().getRoomList();
            User user = getApi().getUserBySession(session);
            trace("User: " + user);
            if(zoneRooms.size() > 0 && inData.containsKey("model_index"))
            {
                //connect to the first room
                ((OpenWorldZoneExtension) this.getParentExtension()).AddUserModelIndex(session.getId(), inData.getInt("model_index"));
                //this.getApi().joi joinRoom( session.get sender, zoneRooms.get(0));
            }
            else
            {//skip connection process
                SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_INACTIVE_ZONE);
                throw new SFSLoginException("Server zone has no rooms: ", errData);
            }*/
            
            /*ConfigDataClass serverConfig = ((OpenWorldZoneExtension)this.getParentExtension()).GetServerConfig();
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
            }*/
        }
    }
}
