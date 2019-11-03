package Game.DataClasses;

public class CollisionEdgeClass extends EdgeClass
{
    public int myIndex;//index in collision map dictionary
    
    public CollisionEdgeClass(Vector2 s, Vector2 e, int i)
    {
        super(s, e);
        myIndex = i;
    }
}
