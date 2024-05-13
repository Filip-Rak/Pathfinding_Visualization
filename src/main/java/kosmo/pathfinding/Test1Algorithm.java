package kosmo.pathfinding;

public class Test1Algorithm implements Runnable
{
    private final GridSquare[][] gridSquares;

    public Test1Algorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
    }

    @Override
    public void run()
    {
        Execution.get().startPoint();
        // Example algorithm logic: Change the color of some squares
        State[] states = State.values(); // Retrieve all states
        int stateIndex = 0; // Start with the first state

        //try {
            for (GridSquare[] row : gridSquares)
            {
                for (GridSquare square : row)
                {
                    State currentState = states[stateIndex];
                    Execution.get().Wait();

                    //OutputConsole.get().write("Column: " + square.getCol() + "\t");
                    //OutputConsole.get().writeSeparator();
                    square.setState(currentState); // Set the current state

                    // Move to the next state, wrap around if at the end
                    stateIndex = (stateIndex + 1) % states.length;
                }
            }
       // }
        //catch (InterruptedException e)
        {
        //    Thread.currentThread().interrupt();
        }

            Execution.get().stopPoint();
    }
}