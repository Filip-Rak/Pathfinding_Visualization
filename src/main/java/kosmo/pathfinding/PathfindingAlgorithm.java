package kosmo.pathfinding;

public class PathfindingAlgorithm implements Runnable
{
    private final GridSquare[][] gridSquares;

    public PathfindingAlgorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
    }

    @Override
    public void run()
    {
        // Example algorithm logic: Change the color of some squares
        State[] states = State.values(); // Retrieve all states
        int stateIndex = 0; // Start with the first state

        try {
            for (GridSquare[] row : gridSquares) {
                for (GridSquare square : row) {
                    State currentState = states[stateIndex];
                    square.setState(currentState); // Set the current state
                    Thread.sleep(100); // Delay for visual effect

                    // Move to the next state, wrap around if at the end
                    stateIndex = (stateIndex + 1) % states.length;
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}