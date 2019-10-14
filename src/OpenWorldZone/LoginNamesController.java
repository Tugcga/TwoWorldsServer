package OpenWorldZone;

import Game.DataClasses.GlobalGameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginNamesController 
{
    public static String AddLoginName(String name)
    {
        if(GlobalGameData.loginNames.containsKey(name))
        {
            List<Integer> nameIndexes = GlobalGameData.loginNames.get(name);
            //find the first free index for the name
            Integer newIndex = nameIndexes.size() + 1;
            for(Integer i = 1; i < nameIndexes.size() + 2; i++)
            {
                if(!nameIndexes.contains(i))
                {
                    newIndex = i;
                    i = nameIndexes.size() + 1;
                }
            }
            nameIndexes.add(newIndex);
            GlobalGameData.loginNames.replace(name, nameIndexes);
            if(newIndex > 1)
            {
                return name + "#" + newIndex;
            }
            else
            {
                return name;
            }
        }
        else
        {
            List<Integer> newIndexes = new ArrayList<>();
            newIndexes.add(1);
            GlobalGameData.loginNames.put(name, newIndexes);
            return name;
        }
    }
    
    static String NameToName(String name)
    {
        int i = name.indexOf("#");
        String toReturn = name;
        if(i > 0)
        {
            toReturn = name.substring(0, i);
        }
        return toReturn;
    }
    
    static Integer NameToIndex(String name)
    {
        int i = name.indexOf("#");
        Integer toReturn = 1;
        if(i > 0)
        {
            toReturn = Integer.parseInt(name.substring(i + 1, name.length()));
        }
        return toReturn;
    }
    
    public static void RemoveLoginName(String name)
    {
        String toDelete = NameToName(name);
        Integer nameIndex = NameToIndex(name);
        if(GlobalGameData.loginNames.containsKey(toDelete))
        {
            List<Integer> nameIndexes = GlobalGameData.loginNames.get(toDelete);
            if(name.equals(toDelete) || nameIndexes.size() == 1)
            {//the name has only one index 1
                GlobalGameData.loginNames.remove(name);
            }
            else
            {
                nameIndexes.remove(nameIndex);
            }
        }
        else
        {
            
        }
    }
    
    public static void FilterLoggedUsers(ArrayList<String> names, OpenWorldZoneExtension zone)
    {
        //we should count all names with the same prefix
        Map<String, List<Integer>> namesCount = new ConcurrentHashMap<String, List<Integer>>();
        for(String name : names)
        {
            String prefix = NameToName(name);
            Integer index = NameToIndex(name);
            if(namesCount.containsKey(prefix))
            {
                namesCount.get(prefix).add(index);
            }
            else
            {
                List<Integer> newIndexes = new ArrayList<Integer>();
                newIndexes.add(index);
                namesCount.put(prefix, newIndexes);
            }
        }
        //next set values in the GlobalGameData.loginNames
        GlobalGameData.loginNames.clear();
        GlobalGameData.loginNames.putAll(namesCount);
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
