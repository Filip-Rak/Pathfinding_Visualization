package kosmo.pathfinding;

public class Map
{
    // Attributes
    private static final int GRID_ROWS = 30;
    private static final int GRID_COLUMNS = 15;

    private final GridSquare[][] gridElements;
    private final String name;
    private final GridSquare origin;
    private final GridSquare destination;

    // Constructors
    Map(GridSquare[][] gridElements, String name, GridSquare origin, GridSquare destination)
    {
        this.name = name;
        this.gridElements = gridElements;
        this.origin = origin;
        this.destination = destination;
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
}
