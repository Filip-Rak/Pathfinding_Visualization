package kosmo.pathfinding.algorithm;

import kosmo.pathfinding.framework.*;
import kosmo.pathfinding.window.OutputConsole;
import kosmo.pathfinding.window.Scene;

public class Example2 implements Runnable
{
    // Attributes
    private final GridSquare[][] gridSquares;

    // Constructor
    public Example2(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
    }

    // Algorithm's work and GridSquare.setState only in this method
    // The method is called inside RootController
    @Override
    public void run()
    {
        // Put this at the beginning of run method. It tells the program that the algorithm started work
        // Do not update states before it
        Execution.get().startPoint();

        for (int i = 0; i < Scene.GRID_ROWS; i++)
        {
            for (int j = 0; j < Scene.GRID_COLUMNS; j++)
            {
                // You can print info about algorithm's work to console
                OutputConsole.get().writeLn("i = " + i + "\nj = " + j + "\nBefore change: " + gridSquares[i][j].getState());
                OutputConsole.get().writeSeparator();

                // Sets state. With 'false' it won't print into console
                gridSquares[i][j].setState(State.OBSTACLE, false);

                // Makes a delay between frames
                Execution.get().Wait();
            }
        }

        // Tells the app that the algorithm finished working and the grid can be refreshed
        Execution.get().stopPoint();
    }
}