package Game.DataClasses;

public class IntersectionResultClass 
{
    public boolean isIntersection;  // is intersection exist
    public Vector2 intersectionPoint = new Vector2();  // if exist this is cooridinates
    public float parameter;  // parameter of intersection point on the edges
    public int intersectedEdgeIndex;
    public int calculationsCout;
    
    public IntersectionResultClass()
    {
        isIntersection = false;
    }
}
