package OpenWorldRoom;

import Game.DataClasses.GlobalGameData;

public class Logger 
{
    public static void Log(String message)
    {
        GlobalGameData.server.trace("[OpenWorld]: " + message);
    }
    
    public static void Log(Object message)
    {
        GlobalGameData.server.trace("[OpenWorld]: " + message);
    }
}
