/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

public class FireControllerClass 
{
    float fireCoolDawn;  // in seconds
    float fireCoolDawn_check;
    
    long lastFireTime;  // in miliseconds
    
    public FireControllerClass(float fireCD)
    {
        fireCoolDawn = fireCD;
        fireCoolDawn_check = fireCoolDawn * 0.9f * 1000;
        lastFireTime = 0;
    }
    
    public  float GetCoolDawn()
    {
        return fireCoolDawn;
    }
    
    public boolean TryStartbullet()
    {
        if(System.currentTimeMillis() - lastFireTime > fireCoolDawn_check)
        {
            lastFireTime = System.currentTimeMillis();
            return true;
        }
        else
        {
            return false;
        }
    }
}