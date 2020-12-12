package OpenWorldZone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.util.IWordFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PublicMessageEventHandler  extends BaseServerEventHandler 
{
    @Override
	public void handleServerEvent(ISFSEvent event) throws SFSException 
	{
            User user = (User) event.getParameter(SFSEventParam.USER);
            
            IWordFilter filter = getParentExtension().getParentZone().getWordFilter();
            String message = filter.apply((String)event.getParameter(SFSEventParam.MESSAGE)).getMessage();
            
            //send message to all connected users
            Collection<User> users = getParentExtension().getParentZone().getUserList();
            List<User> usersList = new ArrayList<>(users);
            ISFSObject params = new SFSObject();
            params.putUtfString("sender", user.getName());
            params.putInt("senderId", user.getId());
            params.putUtfString("message", message);
            send("RPCChatMessage", params, usersList);
	}
}
