package OpenWorldZone;

import com.smartfoxserver.v2.controllers.filter.SysControllerFilter;
import com.smartfoxserver.v2.controllers.system.GenericMessage;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.filter.FilterAction;

public class Filter_PublicMessage extends SysControllerFilter
{
    @Override
    public FilterAction handleClientRequest(User sender, ISFSObject params) throws SFSException
    {
        String message = params.getUtfString(GenericMessage.KEY_MESSAGE);
        
        //filter message by alphabetical symbols
        String filterMessage = message.replaceAll("[^A-Za-z0-9А-ЯЁа-яё \\(\\)\\*\\/\\.\\,\\#\\%\\^\\:\\;\\!\\?\\{\\}\\-\\+\\@\\$\\^\\&_\\=\\{\\}]", "");
        if(filterMessage.length() > 0)
        {
            // Store new message in parameters list 
            params.putUtfString(GenericMessage.KEY_MESSAGE, message);
            return FilterAction.CONTINUE;
        }
        else
        {//block emty message
            return FilterAction.HALT;
        }
    }
}
