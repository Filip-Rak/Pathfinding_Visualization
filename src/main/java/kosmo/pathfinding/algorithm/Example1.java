package kosmo.pathfinding.algorithm;

import kosmo.pathfinding.framework.Execution;
import kosmo.pathfinding.framework.GridSquare;
import kosmo.pathfinding.framework.State;

public class Example1 implements Runnable
{
    // Attributes
    private final GridSquare[][] gridSquares;

    // Constructor
    public Example1(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
    }

    // Do drawing and the algorithm inside this method
    // The method is called in RootController
    @Override
    public void run()
    {
        // At the start of the algorithm set this
        Execution.get().startPoint();

        // You can nest methods inside it
        method();

        // At the end of the algorithm set this
        Execution.get().stopPoint();
    }

    private void method()
    {
        State[] states = State.values();
        int stateIndex = 0;

        for (GridSquare[] row : gridSquares)
        {
            for (GridSquare square : row)
            {
                State currentState = states[stateIndex];

                if(currentState != State.ORIGIN && currentState != State.DESTINATION)
                {
                    // GridSquare.setState(State, boolean print = true) // Sets the state - visually too
                    square.setState(currentState);
                }

                // Call this to make a delay in animation. Do it after state changes, console outputs or whatever
                Execution.get().Wait();

                stateIndex = (stateIndex + 1) % states.length;
            }
        }
    }
}