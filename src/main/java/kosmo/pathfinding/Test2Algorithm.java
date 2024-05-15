package kosmo.pathfinding;

public class Test2Algorithm implements Runnable
{
    private final GridSquare[][] gridSquares;

    public Test2Algorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
    }

    @Override
    public void run()
    {
        Execution.get().startPoint();
        State[] states = State.values();
        int stateIndex = 0;

        //try {
            for (GridSquare[] row : gridSquares)
            {
                for (GridSquare square : row)
                {
                    State currentState = states[stateIndex];
                    Execution.get().Wait();

                    if(currentState != State.ORIGIN && currentState != State.DESTINATION)
                        square.setState(State.OBSTACLE); // Set the current state

                    stateIndex = (stateIndex + 1) % states.length;
                }
            }

            Execution.get().stopPoint();
    }
}