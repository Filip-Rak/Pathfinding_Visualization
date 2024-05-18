package kosmo.pathfinding;

import javafx.scene.paint.Color;

public enum State
{
    // Enums
    ORIGIN(Color.BLUE),
    DESTINATION(Color.RED),
    OBSTACLE(Color.BLACK),
    NONE(Color.LIGHTGRAY),
    VISITED(Color.LIGHTBLUE),
    FRONTIER(Color.GREEN),
    PATH(Color.YELLOW),
    CLOSED(Color.DARKGRAY),
    CURRENT(Color.ORANGE);

    // Attributes
    private final Color color;

    // Constructor
    State(Color color)
    {
        this.color = color;
    }

    // Getters
    public Color getColor()
    {
        return color;
    }
}

