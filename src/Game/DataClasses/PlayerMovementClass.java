package Game.DataClasses;

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
    
    public void SetDirIndex(int newDI)
    {
        if(newDI != dirIndex)
        {
            dirIndex = newDI;
            if(dirIndex >=0 && dirIndex <= 7)
            {
                direction = Vector2.GetDirectionFromIndex(dirIndex);
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
            IntersectionResultClass walkPathIntersection = GlobalGameData.collisionMap.GetIntersection(walkPath);
            //if we obtain intersection, we should confirm that this intersection pont not in the negative direction
            if(walkPathIntersection.isIntersection)
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
