package kosmo.pathfinding;

import java.util.ArrayList;
import java.util.List;

public class DFSRecursiveAlgorithm implements Runnable
{
    private final GridSquare[][] gridSquares;
    private final int rows;
    private final int cols;
    private final GridSquare[][] parent;
    private int pathLength = 0;

    // Constructor
    public DFSRecursiveAlgorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
        this.rows = Scene.GRID_ROWS;
        this.cols = Scene.GRID_COLUMNS;
        this.parent = new GridSquare[rows][cols];
    }

    @Override
    public void run()
    {
        Execution.get().startPoint();

        Point start = new Point(-1, -1);
        Point end = new Point(-1, -1);
        boolean pointsInitialized = findStartingPoint(start, end);

        if (!pointsInitialized)
        {
            OutputConsole.get().writeLn("Start or end point not defined!");
            Execution.get().stopPoint();
            return;
        }

        boolean pathFound = DFSPathFinder(start.x, start.y, end.x, end.y, new boolean[rows][cols]);

        if (pathFound)
        {
            tracePath(start.x, start.y, end.x, end.y);
            Execution.get().stopPoint(pathLength);
        }
        else
        {
            OutputConsole.get().writeLn("No path found from start to end!");
            Execution.get().stopPoint();
        }
    }

    private boolean findStartingPoint(Point start, Point end)
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (gridSquares[i][j].getState() == State.ORIGIN)
                {
                    start.x = i;
                    start.y = j;
                }
                else if (gridSquares[i][j].getState() == State.DESTINATION)
                {
                    end.x = i;
                    end.y = j;
                }
            }
        }

        return start.x != -1 && start.y != -1 && end.x != -1 && end.y != -1;
    }

    private boolean DFSPathFinder(int row, int col, int endX, int endY, boolean[][] visited)
    {
        if (row == endX && col == endY)
        {
            return true;
        }

        visited[row][col] = true;
        gridSquares[row][col].setState(State.VISITED);
        Execution.get().Wait();

        List<GridSquare> neighbors = getNeighbors(gridSquares[row][col]);

        // Loop below is only for correct visualization of State.FRONTIER
        for (GridSquare neighbor : neighbors)
        {
            int nRow = neighbor.getRow();
            int nCol = neighbor.getCol();
            if (!visited[nRow][nCol] && neighbor.getState() != State.OBSTACLE)
            {
                neighbor.setState(State.FRONTIER);
                Execution.get().Wait();
            }
        }

        for (GridSquare neighbor : neighbors)
        {
            int nRow = neighbor.getRow();
            int nCol = neighbor.getCol();

            if (!visited[nRow][nCol] && neighbor.getState() != State.OBSTACLE)
            {
                parent[nRow][nCol] = gridSquares[row][col];

                if (DFSPathFinder(nRow, nCol, endX, endY, visited))
                {
                    return true;
                }

                // Optional: backtrack to none if you want to clear non-solution paths visually
                // neighbor.setState(State.NONE);
            }
        }

        return false;
    }

    private void tracePath(int startX, int startY, int endX, int endY)
    {
        GridSquare current = gridSquares[endX][endY];
        while (current.getRow() != startX || current.getCol() != startY)
        {
            if (current.getState() != State.ORIGIN && current.getState() != State.DESTINATION)
            {
                current.setState(State.PATH);
            }
            Execution.get().Wait();
            current = parent[current.getRow()][current.getCol()];
            pathLength++;
        }

        gridSquares[startX][startY].setState(State.ORIGIN);
        gridSquares[endX][endY].setState(State.DESTINATION);
    }

    private List<GridSquare> getNeighbors(GridSquare square)
    {
        List<GridSquare> neighbors = new ArrayList<>();
        int row = square.getRow();
        int col = square.getCol();

        if (row > 0) neighbors.add(gridSquares[row - 1][col]); // Up
        if (row < rows - 1) neighbors.add(gridSquares[row + 1][col]); // Down
        if (col > 0) neighbors.add(gridSquares[row][col - 1]); // Left
        if (col < cols - 1) neighbors.add(gridSquares[row][col + 1]); // Right

        return neighbors;
    }

    private static class Point
    {
        int x;
        int y;

        Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
