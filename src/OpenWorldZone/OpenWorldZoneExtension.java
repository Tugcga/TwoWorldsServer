package OpenWorldZone;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenWorldZoneExtension extends SFSExtension
{
    Map<Integer, Integer> userModelIndexes;
    ConfigDataClass serverConfig;
    boolean isServerInit;
    
    @Override
    public void init() 
    {
        try 
        {
            serverConfig = new ConfigDataClass(this.getCurrentFolder() + "config.json");
            isServerInit = true;
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(OpenWorldZoneExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        userModelIndexes = new ConcurrentHashMap<>();
        addEventHandler(SFSEventType.USER_JOIN_ZONE, JoinZoneEventHandler.class);
        addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);
        addRequestHandler("RPCClientSelectCharacter", Handler_RPCClientSelectCharacter.class);
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
    }
    
    public void AddUserModelIndex(int userId, int modelIndex)
    {
        userModelIndexes.put(userId, modelIndex);
    }
    
    public void RemoveUserData(int userId)
    {
        if(userModelIndexes.containsKey(userId))
        {
            userModelIndexes.remove(userId);
        }
    }
    
    @Override
    public Object handleInternalMessage(String cmdName, Object params)
    {
        if(cmdName.equals("GetUserModelIndex"))
        {
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
        else if(cmdName.equals("RemoveUserData"))
        {
            RemoveUserData((int)params);
        }
        return null;
    }
    
    public ConfigDataClass GetServerConfig()
    {
        if(!isServerInit)
        {
            try 
            {
                serverConfig = new ConfigDataClass(this.getCurrentFolder() + "config.json");
                isServerInit = true;
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(OpenWorldZoneExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return serverConfig;
    }
}
