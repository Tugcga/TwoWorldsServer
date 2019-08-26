package OpenWorldZone;

import com.smartfoxserver.v2.controllers.SystemRequest;
import com.smartfoxserver.v2.core.SFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.extensions.SFSExtension;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenWorldZoneExtension extends SFSExtension
{
    Map<Integer, Integer> userModelIndexes;
    ConfigDataClass serverConfig = null;
    List<Room> roomList;
    SFSArray roomNames;
    boolean isServerInit;
    
    // Overrides of default methods
    //------------------------------------------------------
    @Override
    public void init() 
    {
        trace("Init Zone " + getParentZone().getName());
        if(!isServerInit)
        {
            InitServerConfig();
        }
        userModelIndexes = new ConcurrentHashMap<>();
        InitRoomNames();
        addEventHandler(SFSEventType.SERVER_READY, Handler_ServerReady.class);
        addEventHandler(SFSEventType.USER_DISCONNECT, Handler_UserDisconnect.class);  // this event should be fired on the room extension
        addEventHandler(SFSEventType.USER_JOIN_ZONE, Handler_UserJoinZone.class);
        addEventHandler(SFSEventType.USER_LOGOUT, Handler_UserLogout.class);
        addEventHandler(SFSEventType.USER_RECONNECTION_SUCCESS, Handler_UserReconnectionSuccess.class);
        addEventHandler(SFSEventType.USER_RECONNECTION_TRY, Handler_UserReconnectionTry.class);
        addEventHandler(SFSEventType.USER_LOGIN, Handler_LoginEvent.class);
        addRequestHandler("RPCClientSelectCharacter", Handler_RPCClientSelectCharacter.class);
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
    }
    
    @Override
    public Object handleInternalMessage(String cmdName, Object params)
    {
        if(cmdName.equals("GetUserModelIndex"))
        {//what model used by the user
            int userId = (int)params;
            if(userModelIndexes.containsKey(userId))
            {
                return userModelIndexes.get(userId);
            }
            else
            {
                return null;
            }
        }
        else if(cmdName.equals("GetServerConfig"))
        {
            return GetServerConfig();
        }
        else if(cmdName.equals("RemoveUserData"))
        {//user disconnected, remove data bout it model
            RemoveUserData((int)params);
        }
        return null;
    }
    
    // Custom methods
    //------------------------------------------------------
    public void AddUserModelIndex(int userId, int modelIndex)
    {//used from Handler_RPCClientSelectCharacter, when client select the model
        userModelIndexes.put(userId, modelIndex);
    }
    
    public void RemoveUserData(int userId)
    {//used from here
        if(userModelIndexes.containsKey(userId))
        {
            userModelIndexes.remove(userId);
        }
    }
    
    void InitRoomNames()
    {
        roomNames = new SFSArray();
        roomList = this.getParentZone().getRoomList();
        for(int i = 0; i < roomList.size(); i++)
        {
            roomNames.addText(roomList.get(i).getName());
        }
    }
    
    SFSArray GetRoomNames()
    {
        if(roomList == null)
        {
            InitRoomNames();
        }
        return roomNames;
    }
    
    void InitServerConfig()
    {//call at startup or when any would like to get this data
        try 
        {
            serverConfig = new ConfigDataClass(this.getCurrentFolder() + "config.json");
            isServerInit = true;
        } 
        catch (IOException ex) 
        {
            isServerInit = false;
            Logger.getLogger(OpenWorldZoneExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ConfigDataClass GetServerConfig()
    {//called at LoginEventHandler when client connect and ask models
        if(!isServerInit)
        {
            InitServerConfig();
        }
        return serverConfig;
    }
}
