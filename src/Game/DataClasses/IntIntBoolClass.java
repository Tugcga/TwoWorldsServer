/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

public class IntIntBoolClass 
{
    int intValue01;
    int intValue02;
    boolean boolValue;
    
    public IntIntBoolClass()
    {
        intValue01 = 0;
        intValue02 = 0;
        boolValue = false;
    }
    
    public IntIntBoolClass(int v1, int v2, boolean b)
    {
        intValue01 = v1;
        intValue02 = v2;
        boolValue = b;
    }
    
     public void SetData(int v1, int v2, boolean b)
    {
        intValue01 = v1;
        intValue02 = v2;
        boolValue = b;
    }
    
    public int GetIntValue01()
    {
        return intValue01;
    }
    
    public int GetIntValue02()
    {
        return intValue02;
    }
    
    public boolean GetBoolValue()
    {
        return boolValue;
    }
}
