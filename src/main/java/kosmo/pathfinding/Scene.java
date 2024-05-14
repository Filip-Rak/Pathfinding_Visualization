package kosmo.pathfinding;

public class Scene
{
    // Attributes
    public static final int GRID_ROWS = 15;
    public static final int GRID_COLUMNS = 30;
    public static final double SQUARE_SIZE = 25;

    private final GridSquare[][] gridElements;
    private final String name;
    private final GridSquare origin;
    private final GridSquare destination;
    private final boolean readOnly;

    // Constructors
    Scene(GridSquare[][] gridElements, String name, GridSquare origin, GridSquare destination, boolean readOnly)
    {
        this.name = name;
        this.gridElements = gridElements;
        this.origin = origin;
        this.destination = destination;
        this.readOnly = readOnly;
    }

    // Methods
    public GridSquare[][] copyGrid()
    {
        GridSquare[][] copy = new GridSquare[GRID_ROWS][GRID_COLUMNS];

        for (int i = 0; i < GRID_ROWS; i++)
        {
            for (int j = 0; j < GRID_COLUMNS; j++)
            {
                if (gridElements[i][j] != null)
                    copy[i][j] = new GridSquare(gridElements[i][j]);
            }
        }

        return copy;
    }


    // Getters
    public String getName()
    {
        return name;
    }

    public GridSquare getOrigin()
    {
        return origin;
    }

    public GridSquare getDestination()
    {
        return destination;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }
}
