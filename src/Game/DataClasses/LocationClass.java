
package Game.DataClasses;

public class LocationClass 
{
    Vector2 position;
    
        
    public LocationClass()
    {
        position = new Vector2();
    }
    
    public void SetPosition(Vector2 newPosition)
    {
        position.Set(newPosition.GetX(), newPosition.GetY());
    }
    
    public void SetLocation(double posX, double posY)
    {
        SetPosition(posX, posY);
    }
    
    public void SetRandomLocation(float limit)
    {
        SetLocation(2*limit * Math.random() - limit, 2*limit*Math.random() - limit);
    }
    
    public void SetRandomShift(float limit)
    {
        Vector2 shiftDir = Vector2.Random();
        double radius = limit * Math.random();
        position.Set(position.GetX() + radius * shiftDir.GetX(), position.GetY() + radius * shiftDir.GetY());
    }
    
    public void SetPosition(double x, double y)
    {
        position.Set(x, y);
    }
    
    public Vector2 GetPosition()
    {
        return position;
    }
    
    public double GetX()
    {
        return position.GetX();
    }
    
    public double GetY()
    {
        return position.GetY();
    }
    
}
