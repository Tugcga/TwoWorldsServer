package Game.DataClasses;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class EdgeClass 
{
    public Vector2 startPoint;
    public Vector2 endPoint;
    
    public Vector2 direction;
    public Vector2 normal;
    
    public EdgeClass(Vector2 s, Vector2 e)
    {
        startPoint = s;
        endPoint = e;
        direction = Vector2.Subtract(endPoint, startPoint);
        normal = CalcNormal();
    }
    
    public double GetDistance(Vector2 point)
    {//calculate the distance between input point and the edge (as infine line)
        return Vector2.Dot(Vector2.MultiplyByScalar(normal, -1), Vector2.Subtract(startPoint, point));
    }
    
    Vector2 CalcNormal()
    {
        Vector2 toReturn = new Vector2(direction.GetY(), -1 * direction.GetX());
        toReturn.Normalize();
        return toReturn;
    }
    
    public boolean IsPointOnPositiveSide(Vector2 point)
    {
        return Vector2.Dot(Vector2.Subtract(endPoint, point), normal) < 0;
    }
    
    public Vector2 GetNormal()
    {
        return normal;
    }
        
    double GetMinX()
    {
        return Double.min(startPoint.GetX(), endPoint.GetX());
    }
    
    double GetMaxX()
    {
        return Double.max(startPoint.GetX(), endPoint.GetX());
    }
    
    double GetMinY()
    {
        return Double.min(startPoint.GetY(), endPoint.GetY());
    }
    
    double GetMaxY()
    {
        return Double.max(startPoint.GetY(), endPoint.GetY());
    }
    
    public Rectangle GetRectangle(float delta)
    {
        return Geometries.rectangle(GetMinX() - delta, GetMinY() - delta, GetMaxX() + delta, GetMaxY() + delta);
    }
    
    public float GetIntersectParameter(CollisionEdgeClass edge)
    {
        double a1 = direction.GetX();
        double a2 = direction.GetY();
        double b1 = edge.direction.GetX();
        double b2 = edge.direction.GetY();
        double x1 = startPoint.GetX();
        double y1 = startPoint.GetY();
        double x2 = edge.startPoint.GetX();
        double y2 = edge.startPoint.GetY();
        if(a2*b1 - a1*b2 != 0)
        {
            double t = (b2*(x1-x2) - b1*(y1-y2)) / (a2*b1-a1*b2);
            double u = (a1*(y2-y1)-a2*(x2-x1)) / (a2*b1-a1*b2);
            if(t < 0f || t > 1f || u < 0f || u > 1f)
            {
                return 1f;
            }
            else
            {
                return (float)t;
            }
        }
        else
        {
            return 1f;
        }
    }
    
    public Vector2 GetPointByParameter(float t)
    {
        return Vector2.Add(Vector2.MultiplyByScalar(startPoint, 1 - t), Vector2.MultiplyByScalar(endPoint, t));
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Edge from ");
        sb.append(startPoint.toString());
        sb.append(" to ");
        sb.append(endPoint.toString());
        return sb.toString();
    }
}
