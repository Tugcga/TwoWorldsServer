package OpenWorldZone;

import Game.DataClasses.GlobalGameData;
import com.smartfoxserver.v2.controllers.SystemRequest;
import com.smartfoxserver.v2.controllers.filter.ISystemFilterChain;
import com.smartfoxserver.v2.controllers.filter.SysControllerFilterChain;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.extensions.SFSExtension;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpenWorldZoneExtension extends SFSExtension
{
    Map<Integer, Integer> userModelIndexes;  // this map used in RoomExtension for creating player, key - sessionId, but not UserId
    List<Room> roomList;
    SFSArray roomNames;
    boolean isServerInit;
    
    // Overrides of default methods
    //------------------------------------------------------
    @Override
    public void init() 
    {
        userModelIndexes = new ConcurrentHashMap<>();
        InitRoomNames();
        GlobalGameData.loginNames = new ConcurrentHashMap<String, Integer>();
        
        addEventHandler(SFSEventType.SERVER_READY, Handler_ServerReady.class);
        addEventHandler(SFSEventType.USER_JOIN_ZONE, Handler_UserJoinZone.class);
        addEventHandler(SFSEventType.USER_LOGOUT, Handler_UserLogout.class);
        addEventHandler(SFSEventType.USER_LOGIN, Handler_LoginEvent.class);
        //lagSimulationMillis = 2000;
        
        getParentZone().resetSystemFilterChain();
         
        ISystemFilterChain blockingChain = new SysControllerFilterChain();
        blockingChain.addFilter("block_request", new Filter_BlockRequest());
         
        // Plug the filter chain
        getParentZone().setFilterChain(SystemRequest.JoinRoom, blockingChain);
        getParentZone().setFilterChain(SystemRequest.CreateRoom, blockingChain);
        getParentZone().setFilterChain(SystemRequest.SetRoomVariables, blockingChain);
        getParentZone().setFilterChain(SystemRequest.SetUserVariables, blockingChain);
        getParentZone().setFilterChain(SystemRequest.LeaveRoom, blockingChain);
        getParentZone().setFilterChain(SystemRequest.SubscribeRoomGroup, blockingChain);
        getParentZone().setFilterChain(SystemRequest.UnsubscribeRoomGroup, blockingChain);
        getParentZone().setFilterChain(SystemRequest.ModeratorMessage, blockingChain);
        getParentZone().setFilterChain(SystemRequest.AdminMessage, blockingChain);
        getParentZone().setFilterChain(SystemRequest.KickUser, blockingChain);
        getParentZone().setFilterChain(SystemRequest.BanUser, blockingChain);
        getParentZone().setFilterChain(SystemRequest.SetUserPosition, blockingChain);
        getParentZone().setFilterChain(SystemRequest.AddBuddy, blockingChain);
        getParentZone().setFilterChain(SystemRequest.BlockBuddy, blockingChain);
        getParentZone().setFilterChain(SystemRequest.RemoveBuddy, blockingChain);
        getParentZone().setFilterChain(SystemRequest.SetBuddyVariables, blockingChain);
        getParentZone().setFilterChain(SystemRequest.BuddyMessage, blockingChain);
        getParentZone().setFilterChain(SystemRequest.InviteUser, blockingChain);
        getParentZone().setFilterChain(SystemRequest.InvitationReply, blockingChain);
        getParentZone().setFilterChain(SystemRequest.QuickJoinGame, blockingChain);
        getParentZone().setFilterChain(SystemRequest.ObjectMessage, blockingChain);
        getParentZone().setFilterChain(SystemRequest.PrivateMessage, blockingChain);
        
        ISystemFilterChain messageChain = new SysControllerFilterChain();
        messageChain.addFilter("public_message", new Filter_PublicMessage());
        getParentZone().setFilterChain(SystemRequest.PublicMessage, messageChain);
        
        //additional system 
        //Handshake, Login, Logout, GetRoomList, AutoJoin, GenericMessage, ChangeRoomName, ChangeRoomPassword,
        //CallExtension, SpectatorToPlayer, PlayerToSpectator, ChangeRoomCapacity,  ManualDisconnection, FindRooms,
        //FindUsers, PingPong, InitBuddyList, GoOnline, CreateSFSGame, GetLobbyNode, KeepAlive, QuickJoin, OnEnterRoom, 
        //OnRoomCountChange, OnUserLost, OnRoomLost, OnUserExitRoom, OnClientDisconnection, OnReconnectionFailure, OnMMOItemVariablesUpdate, OnJoinAppNode
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
            int sessionId = (int)params;
            if(userModelIndexes.containsKey(sessionId))
            {
                return userModelIndexes.get(sessionId);
            }
            else
            {//return -1 if the data is invalid
                return -1;
            }
        }
        else if(cmdName.equals("RemoveUserData"))
        {//user disconnected, remove data about it model
            RemoveUserData((int)params);
        }
        else if(cmdName.equals("DisconnectUser"))
        {
            User user = (User)params;
            getApi().disconnectUser(user);
        }
        else if(cmdName.equals("RemoveName"))
        {
            String name = (String)params;
            LoginNamesController.RemoveLoginName(name);
        }
        return null;
    }
    
    // Custom methods
    //------------------------------------------------------
    public void AddUserModelIndex(int sessionId, int modelIndex)
    {//used from Handler_RPCClientSelectCharacter, when client select the model
        trace("Save sessionId=" + sessionId + " and model=" + modelIndex);
        if(userModelIndexes.containsKey(sessionId))
        {
            trace("SessionId=" + sessionId + " store in userData yet, rwrite it");
        }
        userModelIndexes.put(sessionId, modelIndex);
    }
    
    public void RemoveUserData(int sessionId)
    {//used from here
        //int sessionId = getApi().getUserById(userId).getSession().getId();
        trace("Try to remove sessionId=" + sessionId);
        if(userModelIndexes.containsKey(sessionId))
        {
            userModelIndexes.remove(sessionId);
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
    
    public List<Room> GetRooms()
    {
        if(roomList == null)
        {
            InitRoomNames();
        }
        return roomList;
    }
    
    public SFSArray GetRoomNames()
    {
        if(roomList == null)
        {
            InitRoomNames();
        }
        return roomNames;
    }
    
    /*void InitServerConfig()
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
    }*/
    
    /*public ConfigDataClass GetServerConfig()
    {//called at LoginEventHandler when client connect and ask models
        if(!isServerInit)
        {
            InitServerConfig();
        }
        return serverConfig;
    }*/
}
