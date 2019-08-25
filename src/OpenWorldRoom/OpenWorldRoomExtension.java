/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenWorldRoom;

import Game.DataClasses.BulletClass;
import Game.DataClasses.CollisionDataClass;
import Game.DataClasses.GlobalGameData;
import Game.DataClasses.MonsterClass;
import Game.DataClasses.PlayerClass;
import Game.DataClasses.StartPointsClass;
import Game.DataClasses.TowerClass;
import Game.Process.KillsNotificatorTask;
import Game.Process.MonsterControllerTask;
import Game.Process.PlayerMovementControllerTask;
import Game.Process.TowerAtackerTask;
import Game.Process.TowerMonsterResurectorTask;
import Game.Process.TowersManagement;
import OpenWorldZone.ConfigDataClass;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.mmo.MMORoom;
import com.smartfoxserver.v2.util.JSONUtil;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Philipp
 */
public class OpenWorldRoomExtension extends SFSExtension
{
    private ScheduledFuture<?> monsterControllerTask;
    private ScheduledFuture<?> playerMovementControllerTask;
    private ScheduledFuture<?> killsNotificator;
    private ScheduledFuture<?> towerMonsterResurectorTask;
    private ScheduledFuture<?> towerAtackerTask;

    @Override
    public void init() 
    {
        try 
        {
            GlobalGameData.serverConfig = new ConfigDataClass(this.getCurrentFolder() + "config.json");
            GlobalGameData.startPoints = new StartPointsClass(this.getCurrentFolder() + "PlayerStartPoints.txt");
            InitGameData();
            GlobalGameData.collisionMap = new CollisionDataClass(this.getCurrentFolder() + "CollisionMap.txt");
            TowersManagement.CreateTowers(this.getCurrentFolder() + "Towers.txt");
            //lagSimulationMillis = 1000;

            // Register handler for user join/leave room events
            addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinRoomEventHandler.class);
            addEventHandler(SFSEventType.USER_LEAVE_ROOM, UserLeaveRoomEventHandler.class);
            addEventHandler(SFSEventType.USER_DISCONNECT, UserLeaveRoomEventHandler.class);

            addRequestHandler("RPCClientSendMovementKey", Handler_RPCClientSendMovementKey.class);
            addRequestHandler("RPCFire", Handler_RPCFire.class);

            monsterControllerTask = GlobalGameData.sfs.getTaskScheduler().scheduleAtFixedRate(new MonsterControllerTask(), 0, GlobalGameData.serverConfig.GetMonsterTaskUpdateTime(), TimeUnit.MILLISECONDS);//20 times per second
            playerMovementControllerTask = GlobalGameData.sfs.getTaskScheduler().scheduleAtFixedRate(new PlayerMovementControllerTask(), 0, GlobalGameData.serverConfig.GetPlayerMovementTaskUpdateTime(), TimeUnit.MILLISECONDS);
            killsNotificator = GlobalGameData.sfs.getTaskScheduler().scheduleAtFixedRate(new KillsNotificatorTask(), GlobalGameData.serverConfig.GetKillsNotificatorTime(), GlobalGameData.serverConfig.GetKillsNotificatorTime(), TimeUnit.MILLISECONDS);
            towerMonsterResurectorTask = GlobalGameData.sfs.getTaskScheduler().scheduleAtFixedRate(new TowerMonsterResurectorTask(), 0, GlobalGameData.serverConfig.GetTowerResurectMonsterTime(), TimeUnit.MILLISECONDS);
            towerAtackerTask = GlobalGameData.sfs.getTaskScheduler().scheduleAtFixedRate(new TowerAtackerTask(), 0, GlobalGameData.serverConfig.GetTowerAtackTickTime(), TimeUnit.MILLISECONDS);
        }
        catch (IOException ex) 
        {
            Logger.getLogger(OpenWorldRoomExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    @SuppressWarnings("Convert2Diamond")
    void InitGameData()
    {
        GlobalGameData.server = this;
        GlobalGameData.clients = new ConcurrentHashMap<Integer, PlayerClass>();
        GlobalGameData.monsters = new ConcurrentHashMap<Integer, MonsterClass>();
        GlobalGameData.bullets = new ConcurrentHashMap<Integer, BulletClass>();
        GlobalGameData.towers = new ConcurrentHashMap<Integer, TowerClass>();
        GlobalGameData.room = (MMORoom) this.getParentRoom();
        GlobalGameData.sfs = SmartFoxServer.getInstance();
        GlobalGameData.api = getApi();
        GlobalGameData.mmoApi = GlobalGameData.sfs.getAPIManager().getMMOApi();
        
        //GlobalGameData.serverConfig = (ConfigDataClass) getParentZone().getExtension().handleInternalMessage("GetConfig", 1);
        //trace(GlobalGameData.serverConfig.GetMonsterSpeed() + " ");
    }
    
    @Override
    public void destroy()
    {
        monsterControllerTask.cancel(true);
        playerMovementControllerTask.cancel(true);
        killsNotificator.cancel(true);
        towerMonsterResurectorTask.cancel(true);
        towerAtackerTask.cancel(true);
    }
}
