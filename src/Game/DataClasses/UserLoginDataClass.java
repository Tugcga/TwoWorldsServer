package Game.DataClasses;

public class UserLoginDataClass 
{
    public int modelIndex;
    public boolean needMap;
    
    public UserLoginDataClass()
    {
        modelIndex = -1;
        needMap = false;
    }
    
    public UserLoginDataClass(int in_modelIndex, boolean in_needMap)
    {
        modelIndex = in_modelIndex;
        needMap = in_needMap;
    }
    
    public int GetModelIndex()
    {
        return modelIndex;
    }
    
    public boolean GetNeedMap()
    {
        return needMap;
    }
}
