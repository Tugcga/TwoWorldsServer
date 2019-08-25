/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

public class IntIntClass 
{
    int value01;
    int value02;
    
    public IntIntClass()
    {
        value01 = 0;
        value02 = 0;
    }
    
    public IntIntClass(int v1, int v2)
    {
        value01 = v1;
        value02 = v2;
    }
    
    public void SetData(int v1, int v2)
    {
        value01 = v1;
        value02 = v2;
    }
    
    public int GetValue01()
    {
        return value01;
    }
    
    public int GetValue02()
    {
        return value02;
    }
}
