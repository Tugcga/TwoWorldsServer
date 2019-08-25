/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import java.util.concurrent.ThreadLocalRandom;

public class StartPointsClass 
{
    Map<Integer, Vector2> points;
    int pointsCount;
    
    public StartPointsClass(String fileData) throws IOException
    {
        String dataText = FileUtils.readFileToString(new File(fileData));
        points = new ConcurrentHashMap<Integer, Vector2>();
        int currentIndex = 0;
        if(!"".equals(dataText))
        {
            String[] parts = dataText.split("\\|");
            for (String part : parts) 
            {
                if(!"".equals(part))
                {
                    String[] coords = part.split(",");
                    if(coords.length == 2 && !"".equals(coords[0]) && !"".equals(coords[1]))
                    {
                        Vector2 tPosition = new Vector2(coords[0], coords[1]);
                        points.put(currentIndex, tPosition);
                        currentIndex++;
                    }
                }
            }
        }
        pointsCount = currentIndex;
    }
    
    public Vector2 GetPoint()
    {
        int randomNum = ThreadLocalRandom.current().nextInt(0, pointsCount);
        return points.get(randomNum);
    }
    
    public Vector2 GetClosestPoint(Vector2 pos)
    {
        if(points.size() > 0)
        {
            int minIndex = 0;
            double minDist = Vector2.GetDistance(pos, points.get(0));
            int curretnIndex = 0;
            for (Iterator<Map.Entry<Integer, Vector2>> it = points.entrySet().iterator(); it.hasNext();)
            {
                Vector2 p = it.next().getValue();
                if(curretnIndex > 0)
                {
                    double d = Vector2.GetDistance(p, pos);
                    if(d < minDist)
                    {
                        minIndex = curretnIndex;
                        minDist = d;
                    }
                }
                curretnIndex++;
            }
            return points.get(minIndex);
        }
        else
        {
            return pos;
        }
    }
}
