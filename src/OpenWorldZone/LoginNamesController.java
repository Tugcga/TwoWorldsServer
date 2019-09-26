package OpenWorldZone;

import Game.DataClasses.GlobalGameData;
import OpenWorldRoom.Logger;

public class LoginNamesController 
{
    public static String AddLoginName(String name)
    {
        if(GlobalGameData.loginNames.containsKey(name))
        {
            int namesCount = GlobalGameData.loginNames.get(name);
            GlobalGameData.loginNames.replace(name, namesCount + 1);
            return name + "#" + (namesCount + 1);
        }
        else
        {
            GlobalGameData.loginNames.put(name, 1);
            return name;
        }
    }
    
    public static void RemoveLoginName(String name)
    {
        int i = name.indexOf("#");
        String toDelete = name;
        if(i > 0)
        {
            toDelete = name.substring(0, i);
        }
        if(GlobalGameData.loginNames.containsKey(toDelete))
        {
            int namesCount = GlobalGameData.loginNames.get(toDelete);
            //Logger.Log("Name " + name + " exist with count = " + namesCount);
            if(namesCount <= 1)
            {
                GlobalGameData.loginNames.remove(toDelete);
            }
            else
            {
                GlobalGameData.loginNames.replace(toDelete, namesCount - 1);
            }
        }
        else
        {
            
        }
    }
    
    public static String FilterName(String name)
    {
        String filterName = name.replaceAll("[^A-Za-z0-9А-ЯЁа-яё]", "");
        if("".equals(filterName))
        {
            return "Unknown";
        }
        else
        {
            return filterName;
        }
    }
    
}
