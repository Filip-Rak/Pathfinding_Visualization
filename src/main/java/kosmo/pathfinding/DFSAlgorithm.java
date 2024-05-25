package kosmo.pathfinding;

public class DFSAlgorithm implements Runnable
{
    @Override
    public void run()
    {
        // At the start of the algorithm set this
        Execution.get().startPoint();

        // Depth-First Search algorithm implementation
        DFSImplementation();

        // At the end of the algorithm set this
        Execution.get().stopPoint();
    }
    private void DFSImplementation()
    {

    }
}
