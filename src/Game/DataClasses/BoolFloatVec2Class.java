package Game.DataClasses;

public class BoolFloatVec2Class 
{
    boolean boolValue;
    float floatValue;
    Vector2 vec2Value;
    
    public BoolFloatVec2Class()
    {
        boolValue = false;
        floatValue = 0f;
        vec2Value = new Vector2();
    }
    
    public void Set(boolean b, float f, Vector2 v)
    {
        boolValue = b;
        floatValue = f;
        vec2Value = new Vector2(v);
    }
    
    public boolean GetBoolValue()
    {
        return boolValue;
    }
    
    public float GetFloatValue()
    {
        return floatValue;
    }
    
    public Vector2 GetVec2Value()
    {
        return vec2Value;
    }
}
