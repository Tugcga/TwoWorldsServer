/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldRoom;

import Game.Process.ClientsManagement;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 *
 * @author Philipp
 */
public class Handler_RPCClientSendMovementKey extends BaseClientRequestHandler
{
    @Override
    public void handleClientRequest(User sender, ISFSObject params)
    {
        ClientsManagement.ClientChangeMovement(sender.getId(), params.getInt("dirIndex"), params.getFloat("angle"));
    }
}
