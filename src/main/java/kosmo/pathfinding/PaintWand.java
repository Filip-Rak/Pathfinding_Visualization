package kosmo.pathfinding;

public class PaintWand
{
    // Attributes
    private GridSquare origin;
    private GridSquare destination;
    private State function;

    // Singleton
    private static PaintWand instance;

    private PaintWand()
    {
        function = State.ORIGIN;
    }

    public static PaintWand getInstance()
    {
        if (instance == null)
            instance = new PaintWand(); // Create the instance if it does not exist

        return instance;
    }

    // Getters
    public State getFunction()
    {
        return function;
    }

    public GridSquare getOrigin()
    {
        return origin;
    }

    public GridSquare getDestination()
    {
        return destination;
    }

    // Setters
    public void setFunction(State function)
    {
        this.function = function;
    }

    public void setOrigin(GridSquare origin)
    {
        this.origin = origin;
    }

    public void setDestination(GridSquare destination)
    {
        this.destination = destination;
    }
}
