package kosmo.pathfinding;

// Update here requires color update in GridSquare.setState()
public enum State
{
    ORIGIN,
    DESTINATION,
    OBSTACLE,
    NONE,
    VISITED,
    FRONTIER,
    PATH,
    CLOSED,
    CURRENT
}

