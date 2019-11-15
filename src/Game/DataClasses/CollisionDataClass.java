package Game.DataClasses;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;

public class CollisionDataClass 
{
    ConcurrentHashMap<Integer, CollisionEdgeClass> edges;
    RTree<CollisionEdgeClass, Rectangle> tree;
    List<Double> edgesPoints;
    ISFSObject mapParams;
    
    public CollisionDataClass(String dataFile) throws IOException
    {
        String dataText = FileUtils.readFileToString(new File(dataFile));
        edgesPoints = new ArrayList<Double>();
        edges = new ConcurrentHashMap<>();
        tree = RTree.star().maxChildren(GlobalGameData.serverConfig.GetMaxCountRTree()).create();
        
        if(!"".equals(dataText))
        {
            String[] parts = dataText.split("\\|");
            boolean isEven = false;
            Vector2 tempVector = new Vector2();
            int edgeIndex = 0;
            for (String part : parts) 
            {
                if(!"".equals(part))
                {
                    String[] coordinates = part.split(",");
                    if(coordinates.length == 2 && !"".equals(coordinates[0]) && !"".equals(coordinates[1]))
                    {
                        if(!isEven)
                        {
                            tempVector = new Vector2(coordinates[0], coordinates[1]);
                            edgesPoints.add(tempVector.GetX());
                            edgesPoints.add(tempVector.GetY());
                        }
                        else
                        {
                            Vector2 newVector = new Vector2(coordinates[0], coordinates[1]);
                            edgesPoints.add(newVector.GetX());
                            edgesPoints.add(newVector.GetY());
                            //Create edge by two vectors
                            CollisionEdgeClass newEdge = new CollisionEdgeClass(tempVector, newVector, edgeIndex);
                            edges.put(edgeIndex, newEdge);
                            tree = tree.add(newEdge, newEdge.GetRectangle(GlobalGameData.serverConfig.GetCollisionEdgeDelta()));
                            edgeIndex++;
                        }
                        isEven = !isEven;
                    }
                }
            }
        }
        
        mapParams = new SFSObject();
        mapParams.putDoubleArray("points", edgesPoints);
    }
    
    public List<Double> GetEdgesPoints()
    {
       return edgesPoints;
    }
    
    public ISFSObject GetMapParams()
    {
        return mapParams;
    }
    
    public CollisionEdgeClass GetEdge(int index)
    {
        if(edges.containsKey(index))
        {
            return edges.get(index);
        }
        else
        {
            return null;
        }
    }
    
    public IntersectionResultClass GetIntersection(EdgeClass edge)
    {
        float delta = GlobalGameData.serverConfig.GetCollisionEdgeDelta();
        Rectangle edgeRectangle = edge.GetRectangle(delta);
        Iterable<Entry<CollisionEdgeClass, Rectangle>> it = tree.search(edgeRectangle).toBlocking().toIterable();
        IntersectionResultClass toReturn = new IntersectionResultClass();
        toReturn.isIntersection = false;
        toReturn.parameter = 1f;
        toReturn.calculationsCout = 0;
        for(Entry<CollisionEdgeClass, Rectangle> e : it)
        {
            toReturn.calculationsCout++;
            CollisionEdgeClass innerEdge = e.value();
            float innerT = edge.GetIntersectParameter(innerEdge);
            if(innerT > 0 && innerT < 1)
            {
                if(innerT < toReturn.parameter)
                {
                    toReturn.isIntersection = true;
                    toReturn.intersectedEdgeIndex = innerEdge.myIndex;
                    toReturn.parameter = innerT;
                }
            }
        }
        if(toReturn.isIntersection)
        {
            toReturn.intersectionPoint = edge.GetPointByParameter(toReturn.parameter);
        }
        return toReturn;
    }
}
