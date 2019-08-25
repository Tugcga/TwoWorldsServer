/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
