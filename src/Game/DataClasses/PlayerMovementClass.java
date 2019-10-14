/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.DataClasses;

import OpenWorldRoom.Logger;

class DestinationPointData
{
    public Vector2 point;//destination point coordinates
    public boolean isLastPoint;//should player stop after destination or try to recreate one
    public boolean isMoveble;//true if player can move to destination point
}

public class PlayerMovementClass 
{
    float speed;//player speed from it properies
    PersonClass parent;
    boolean isMove;
    int dirIndex;//come from client. we should not use it for calculations, only for destination point
    //direction index: 0 - front, 1 - frontLeft, 2 - left, 3 - backLeft, 4 - back, 5 - backRight, 6 - right, 7 - frontRight, -1 - stop
    Vector2 clientDirection;
    
    //technical parameters
    //float deltaWall;
    //float rebuildDestTime;
    
    //calculated for move parameters
    //Vector2 destinationWalkPosition;
    DestinationPointData destinationData;
    Vector2 direction; 
    float moveSpeed;//actual move speed
    
    boolean isStateNew;
    long lastTickTime;
    
    public PlayerMovementClass(float s, PersonClass pers)
    {
        parent = pers;
        speed = s;
        isMove = false;
        dirIndex = -1;
        //deltaWall = GlobalGameData.serverConfig.GetPlayerMovementDeltaWallDistance();
        //rebuildDestTime = GlobalGameData.serverConfig.GetPlayerMovementRebuildPointTime();
        //destinationWalkPosition = new Vector2();
        destinationData = new DestinationPointData();
        moveSpeed = speed;//at default moveSpeed is equal to player's speed
        direction = new Vector2(1, 0);
    }
    
    /*DestinationPointData GetDestinationPoint()
    {
        DestinationPointData toReturn = new DestinationPointData();
        toReturn.isMoveble = false;
        toReturn.isLastPoint = true;
        toReturn.point = Vector2.Add(parent.GetPosition(), Vector2.MultiplyByScalar(clientDirection, speed * rebuildDestTime));
        EdgeClass walkPath = new EdgeClass(parent.GetPosition(), toReturn.point);        
        IntersectionResultClass walkPathIntersection = GlobalGameData.collisionMap.GetIntersection(walkPath);
        if(walkPathIntersection.isIntersection)
        {
            CollisionEdgeClass wall = GlobalGameData.collisionMap.GetEdge(walkPathIntersection.intersectedEdgeIndex);
            //Check we in positive side
            if(wall.IsPointOnPositiveSide(parent.GetPosition()))
            {
                //Check is we inside the delta layer of the wall
                if(wall.GetDistance(parent.GetPosition()) < deltaWall)
                {//we inside, stop moving
                    toReturn.isMoveble = false;
                }
                else
                {
                    //we near the wall. Calculate detination point in 0.5 of layer thickness
                    toReturn.point = Vector2.Add(walkPathIntersection.intersectionPoint, Vector2.MultiplyByScalar(wall.GetNormal(), 0.5 * deltaWall));
                    toReturn.isMoveble = true;
                    toReturn.isLastPoint = true;
                }
            }
            else
            {//we inside the wall. Move freely
                toReturn.isMoveble = true;
                toReturn.isLastPoint = false;
            }
        }
        else
        {//no intersection
            toReturn.isMoveble = true;
            toReturn.isLastPoint = false;
        }
        //Before return calculate direction
        direction = Vector2.Subtract(toReturn.point, parent.GetPosition());
        direction.Normalize();
        return toReturn;
    }*/
    
    public void SetDirIndex(int newDI)
    {
        if(newDI != dirIndex)
        {
            dirIndex = newDI;
            if(dirIndex >=0 && dirIndex <= 7)
            {
                //clientDirection = Vector2.GetDirectionFromIndex(dirIndex);
                direction = Vector2.GetDirectionFromIndex(dirIndex);
                /*destinationData = GetDestinationPoint();
                isMove = destinationData.isMoveble;
                if(isMove == false)
                {
                    dirIndex = -1;
                }*/
                isMove = true;
                isStateNew = true;
                lastTickTime = System.currentTimeMillis();
            }
            else if(dirIndex == -1)
            {
                isMove = false;
                isStateNew = true;
            }
        }        
    }
    
    public void MoveTick()
    {
        if(isMove)
        {
            float deltaTime = (float)(System.currentTimeMillis() - lastTickTime) / 1000f;
            Vector2 currentPosition = parent.GetPosition();
            Vector2 newPosition = new Vector2(currentPosition.GetX() + moveSpeed * deltaTime * direction.GetX(), currentPosition.GetY() + moveSpeed * deltaTime * direction.GetY());
            
            //check intersection with the wall
            EdgeClass walkPath = new EdgeClass(currentPosition, newPosition);        
            //create an esge slightly bigger than from start to end
            //Vector2 dirShift = Vector2.MultiplyByScalar(direction, GlobalGameData.serverConfig.GetPlayerMovementDeltaWallDistance());
            //EdgeClass walkPath = new EdgeClass(Vector2.Subtract(currentPosition, dirShift), Vector2.Add(newPosition, dirShift));
            IntersectionResultClass walkPathIntersection = GlobalGameData.collisionMap.GetIntersection(walkPath);
            //if we obtain intersection, we should confirm that this intersection pont not in the negative direction
            if(walkPathIntersection.isIntersection/* && 
               Vector2.Dot(direction, Vector2.Subtract(walkPathIntersection.intersectionPoint, currentPosition)) > 0 &&
               Vector2.GetDistance(currentPosition, walkPathIntersection.intersectionPoint) < Vector2.GetDistance(currentPosition, newPosition)*/)
            {
                CollisionEdgeClass wall = GlobalGameData.collisionMap.GetEdge(walkPathIntersection.intersectedEdgeIndex);
                //Check we in positive side
                if(wall.IsPointOnPositiveSide(currentPosition))
                {
                    Vector2 normalShift = Vector2.MultiplyByScalar(wall.normal, -1 * Vector2.Dot(wall.normal, walkPath.direction));
                    Vector2 newPositionShift = Vector2.Add(newPosition, normalShift);
                    //if shifted position too close to original position, then we collide with horizontal or vertical wall, stop here
                    if(Vector2.GetDistance(currentPosition, newPositionShift) < 0.05f)
                    {
                        isMove = false;
                        dirIndex = -1;
                        isStateNew = true;
                    }
                    else
                    {
                        //next we should check is this new shifted position intersect with the walls
                        //if so, stop moving
                        //again, create the path
                        EdgeClass secondWalkPath = new EdgeClass(currentPosition, newPositionShift);
                        IntersectionResultClass secondIntersection = GlobalGameData.collisionMap.GetIntersection(secondWalkPath);
                        if(secondIntersection.isIntersection)
                        {//there is intersection, stop the moving
                            isMove = false;
                            dirIndex = -1;
                            isStateNew = true;
                        }
                        else
                        {//no intersection, move the player
                            parent.GetLocation().SetPosition(newPositionShift);
                        }
                    }
                }
                else
                {//we inside the wall. Move freely
                    parent.GetLocation().SetPosition(newPosition);
                }
            }
            else
            {//no itersection, set the point
                parent.GetLocation().SetPosition(newPosition);
            }
        }
        lastTickTime = System.currentTimeMillis();
    }
    
    public boolean IsStateNew()
    {
        if(isStateNew)
        {
            isStateNew = false;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean IsMove()
    {
        return isMove;
    }
    
    public int DirectionIndex()
    {
        return dirIndex;
    }
    
    public float GetSpeed()
    {
        return moveSpeed;
    }
    
    public float GetMoveAngle()
    {
        //return (float)(Math.PI / 2 + dirIndex * Math.PI / 4);
        if(direction.GetX() > 0)
        {
            return (float)Math.asin(direction.GetY());
        }
        else
        {
            return (float)(Math.PI - Math.asin(direction.GetY()));
        }
    }
}
