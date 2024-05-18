package kosmo.pathfinding;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridSquare
{
    // Attributes
    private final int ROW;
    private final int COL;
    private final Rectangle square;
    private State state;
    private Color color;

    // Constructors
    GridSquare(Rectangle rectangle, int row, int col)
    {
        this.ROW = row;
        this.COL = col;

        this.square = rectangle;
        rectangle.setOnMouseClicked(this::changeStateListener);

        state = State.NONE;
        color = Color.LIGHTGRAY;
    }

    GridSquare(GridSquare gridSquare)
    {
        this.ROW = gridSquare.getRow();
        this.COL = gridSquare.getCol();
        this.square = gridSquare.getSquare();
        this.state = gridSquare.state;
        this.color = gridSquare.color;
    }

    // Methods
    private void changeStateListener(MouseEvent event)
    {
        if(!Execution.get().isRefreshed())
            return;

        PaintWand wand = PaintWand.get();

        if(event.getButton() == MouseButton.SECONDARY)
        {
            RemoveObstacle();
            return;
        }

        switch(wand.getFunction())
        {
            case OBSTACLE:
                handleObstacle(wand);
                break;

            case ORIGIN:
                handleOrigin(wand);
                break;

            case DESTINATION:
                handleDestination(wand);
        }

    }

    private void RemoveObstacle()
    {
        if(state == State.OBSTACLE)
            this.setState(State.NONE);
    }

    private void handleObstacle(PaintWand wand)
    {
        if(wand.getDestination() != this && wand.getOrigin() != this)
            this.setState(State.OBSTACLE);
    }

    private void handleOrigin(PaintWand wand)
    {
        if(wand.getDestination() == this || wand.getOrigin() == this)
            swapOriginDestination(wand);

        else
        {
            // Remove previous origin
            wand.getOrigin().setState(State.NONE);

            // Make the new origin
            this.setState(State.ORIGIN);
            wand.setOrigin(this);
        }
    }

    private void handleDestination(PaintWand wand)
    {
        if(wand.getDestination() == this || wand.getOrigin() == this)
            swapOriginDestination(wand);

        else
        {
            // Remove previous destination
            wand.getDestination().setState(State.NONE);

            // Make the new destination
            this.setState(State.DESTINATION);
            wand.setDestination(this);
        }
    }

    private void swapOriginDestination(PaintWand wand)
    {
        // Swap the functions
        wand.getDestination().setState(State.ORIGIN);
        wand.getOrigin().setState(State.DESTINATION);

        // Update the wand
        GridSquare buffer = wand.getDestination();
        wand.setDestination(wand.getOrigin());
        wand.setOrigin(buffer);
    }

    // Getters
    public State getState()
    {
        return state;
    }

    public Color getColor()
    {
        return color;
    }

    public int getRow()
    {
        return ROW;
    }

    public int getCol()
    {
        return COL;
    }

    public Rectangle getSquare()
    {
        return square;
    }

    // Setters
    public void setState(State state)
    {
        setState(state, true);
    }

    public void setState(State state, boolean print)
    {
        // Set color corresponding to the state ordinal
        this.color = state.getColor();
        this.state = state;

        Platform.runLater(() -> square.setFill(color));

        if (print)
            OutputConsole.get().writeLn("row: " + ROW + " col: " + COL + ". Set as: " + state);
    }
}
