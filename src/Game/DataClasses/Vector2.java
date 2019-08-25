
package Game.DataClasses;

import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.mmo.Vec3D;

public class Vector2 
{
    double x;
    double y;
    
    public Vector2()
    {
        x = 0;
        y = 0;
    }
    
    public Vector2(double px, double py)
    {
        x = px;
        y = py;
    }
    
    public Vector2(Vector2 newVector)
    {
        x = newVector.GetX();
        y = newVector.GetY();
    }
    
    public Vector2(String xStr, String yStr)
    {
        x = Double.parseDouble(xStr);
        y = Double.parseDouble(yStr);
    }
    
    public Vector2(Vec3D vec3d)
    {
        x = vec3d.floatX();
        y = vec3d.floatY();
    }
    
    public Vec3D GetVec3D()
    {
        return new Vec3D((float)x, (float)y);
    }
    
    public void Set(double xVal, double yVal)
    {
        x = xVal;
        y = yVal;
    }
    
    public void Set(Vector2 vec)
    {
        x = vec.GetX();
        y = vec.GetY();
    }
    
    public double GetX()
    {
        return x;
    }
    
    public float GetFloatX()
    {
        return (float)x;
    }
    
    public float GetFloatY()
    {
        return (float)y;
    }
    
    public double GetY()
    {
        return y;
    }
    
    public double GetLength()
    {
        return Math.sqrt(x*x + y*y);
    }
    
    public void Normalize()
    {
        double length = GetLength();
        if(length > 0)
        {
            x = x / length;
            y = y / length;
        }
    }
        
    public static double Dot(Vector2 a, Vector2 b)
    {
        //Logger.Log(a.toString() + " " + b.toString());
        return a.GetX()*b.GetX() + a.GetY() * b.GetY();
    }
    
    public static Vector2 Add(Vector2 a, Vector2 b)
    {
        return new Vector2(a.GetX() + b.GetX(), a.GetY() + b.GetY());
    }
    
    public static Vector2 MultiplyByScalar(Vector2 v, double a)
    {
        return new Vector2(v.GetX() * a, v.GetY() * a);
    }
    
    public static Vector2 MultiplyByScalar(Vector2 v, float a)
    {
        return new Vector2(v.GetX() * a, v.GetY() * a);
    }
    
    public static Vector2 Subtract(Vector2 a, Vector2 b)
    {
        return new Vector2(a.GetX() - b.GetX(), a.GetY() - b.GetY());
    }
    
    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
    
    public static Vector2 GetDirectionFromIndex(int dirIndex)
    {
        return new Vector2(-1 * Math.sin((double)dirIndex * Math.PI / 4), Math.cos((double)dirIndex * Math.PI / 4));
    }
    
    public static Vector2 Random()
    {
        double rAngle = Math.random() * 2 * Math.PI;
        return new Vector2(Math.sin(rAngle), Math.cos(rAngle));
    }
    
    public static double GetDistance(Vector2 a, Vector2 b)
    {
        double v1 = a.GetX() - b.GetX();
        double v2 = a.GetY() - b.GetY();
        return Math.sqrt(v1*v1 + v2*v2);
    }
    
    public static Vector2 FromPolarToVector(float angle, double radius)
    {
        return new Vector2(Math.cos(angle) * radius, Math.sin(angle) * radius);
    }
    
    public static Vector2 GetInterpolatePoint(Vector2 s, Vector2 e, float t)
    {
        return new Vector2(s.GetX() * (1 - t) + e.GetX() * t, s.GetY() * (1 - t) + e.GetY() * t);
    }
}
