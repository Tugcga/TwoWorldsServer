/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldZone;

import com.smartfoxserver.v2.controllers.filter.SysControllerFilter;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.filter.FilterAction;

/**
 *
 * @author Shekn
 */
public class Filter_BlockRequest extends SysControllerFilter
{
    @Override
    public FilterAction handleClientRequest(User sender, ISFSObject params) throws SFSException
    {
        //block user room join
        return FilterAction.HALT;
    }
}
