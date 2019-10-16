package OpenWorldZone;

import Game.DataClasses.ChatMessagesStore;
import Game.DataClasses.GlobalGameData;
import Game.Process.FilterLoggedUsersTask;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.controllers.SystemRequest;
import com.smartfoxserver.v2.controllers.filter.ISystemFilterChain;
import com.smartfoxserver.v2.controllers.filter.SysControllerFilterChain;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.extensions.SFSExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenWorldZoneExtension extends SFSExtension
{
    private ScheduledFuture<?> saveChatTask;
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
        GlobalGameData.loginNames = new ConcurrentHashMap<String, List<Integer>>();
        GlobalGameData.chatMessages = new ConcurrentLinkedQueue<String>();
        try 
        {
            ChatMessagesStore.Init(this.getCurrentFolder() + "chatStore_config.json");
        } catch (IOException ex) 
        {
            Logger.getLogger(OpenWorldZoneExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        
        saveChatTask = SmartFoxServer.getInstance().getTaskScheduler().scheduleAtFixedRate(new SaveChatTask(), ChatMessagesStore.saveInterval, ChatMessagesStore.saveInterval, TimeUnit.SECONDS);
    }
    
    @Override
    public void destroy()
    {
        try 
        {
            ChatMessagesStore.SaveMessages();
        } catch (IOException ex) 
        {
            Logger.getLogger(OpenWorldZoneExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        saveChatTask.cancel(true);
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
        else if(cmdName.equals("FilterLoggedUsers"))
        {
            List<User> users = (List<User>) params;
            ArrayList<String> names = new ArrayList<String>();
            for(User u : users)
            {
                names.add(u.getName());
            }
            LoginNamesController.FilterLoggedUsers(names, this);
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
}
