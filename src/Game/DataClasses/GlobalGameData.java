package Game.DataClasses;

import OpenWorldRoom.OpenWorldRoomExtension;
import OpenWorldRoom.ConfigDataClass;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.api.ISFSMMOApi;
import com.smartfoxserver.v2.mmo.MMORoom;
import java.util.Map;

public class GlobalGameData 
{
    public static OpenWorldRoomExtension server;
    public static SmartFoxServer sfs;
    public static ISFSMMOApi mmoApi;
    public static MMORoom room;
    public static ISFSApi api;
    
    public static ConfigDataClass serverConfig;
    public static CollisionDataClass collisionMap;
    public static StartPointsClass startPoints;
    
    public static Map<Integer, PlayerClass> clients;
    public static Map<Integer, MonsterClass> monsters;
    public static Map<Integer, BulletClass> bullets;
    public static Map<Integer, TowerClass> towers;
}
