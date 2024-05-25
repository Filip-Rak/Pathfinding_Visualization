package kosmo.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFSAlgorithm implements Runnable {
    private final GridSquare[][] gridSquares;
    private final int rows;
    private final int cols;
    private GridSquare[][] parent; // Deklaracja zmiennej parent

    // Constructor
    public DFSAlgorithm(GridSquare[][] gridSquares) {
        this.gridSquares = gridSquares;
        this.rows = Scene.GRID_ROWS;
        this.cols = Scene.GRID_COLUMNS;
        this.parent = new GridSquare[rows][cols]; // Inicjalizacja zmiennej parent
    }

    @Override
    public void run() {
        // At the start of the algorithm set this
        Execution.get().startPoint();

        // Finding the start and end points
        int startX = -1, startY = -1, endX = -1, endY = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridSquares[i][j].getState() == State.ORIGIN) {
                    startX = i;
                    startY = j;
                } else if (gridSquares[i][j].getState() == State.DESTINATION) {
                    endX = i;
                    endY = j;
                }
            }
        }

        // Check if start or end points are not found
        if (startX == -1 || startY == -1 || endX == -1 || endY == -1) {
            OutputConsole.get().writeLn("Start or end point not defined!");
            Execution.get().stopPoint();
            return;
        }

        // Depth-First Search algorithm implementation
        boolean pathFound = DFSImplementation(startX, startY, endX, endY);

        // At the end of the algorithm set this
        if (pathFound) {
            tracePath(startX, startY, endX, endY);
            Execution.get().stopPoint(getPathLength(startX, startY, endX, endY));
        } else {
            OutputConsole.get().writeLn("No path found from start to end!");
            Execution.get().stopPoint();
        }
    }

    private boolean DFSImplementation(int startX, int startY, int endX, int endY) {
        // Stack for DFS
        Stack<GridSquare> stack = new Stack<>();
        stack.push(gridSquares[startX][startY]);

        // Array to keep track of visited nodes
        boolean[][] visited = new boolean[rows][cols];

        while (!stack.isEmpty()) {
            GridSquare current = stack.pop();
            int row = current.getRow();
            int col = current.getCol();

            if (!visited[row][col]) {
                visited[row][col] = true;
                if (current.getState() != State.ORIGIN && current.getState() != State.DESTINATION) {
                    current.setState(State.VISITED);
                }
                Execution.get().Wait(); // Delay for visualization

                // If we reached the destination
                if (row == endX && col == endY) {
                    OutputConsole.get().writeLn("We reached the destination( " + endX + ", " + endY + " )");
                    return true;
                }

                // Process current node
                OutputConsole.get().writeLn("Visited node at (" + row + ", " + col + ")");

                // Add all unvisited neighbors to the stack
                for (GridSquare neighbor : getNeighbors(current)) {
                    int nRow = neighbor.getRow();
                    int nCol = neighbor.getCol();
                    if (!visited[nRow][nCol] && neighbor.getState() != State.OBSTACLE) {
                        if (neighbor.getState() != State.FRONTIER) {
                            neighbor.setState(State.FRONTIER, true); // Set neighbors as frontier
                        }
                        stack.push(neighbor);
                        parent[nRow][nCol] = current; // Track the parent
                    }
                }
            }
        }

        return false;
    }

    private void tracePath(int startX, int startY, int endX, int endY) {
        GridSquare current = gridSquares[endX][endY];
        OutputConsole.get().writeLn("tracePath(): current = " + current.getRow() + ", " + current.getCol() + ")");
        while (current.getRow() != startX || current.getCol() != startY) {
            if (current.getState() != State.ORIGIN && current.getState() != State.DESTINATION) {
                current.setState(State.PATH);
            }
            Execution.get().Wait(); // Delay for visualization

            // Move to the previous node in the path
            current = getPreviousInPath(current, startX, startY);
        }

        // Ensure the origin and destination retain their original states
        gridSquares[startX][startY].setState(State.ORIGIN, true);
        gridSquares[endX][endY].setState(State.DESTINATION, true);
        OutputConsole.get().writeLn("tracePath(): end of function with current = " + current.getRow() + ", " + current.getCol() + ")");
    }

    private GridSquare getPreviousInPath(GridSquare current, int startX, int startY) {
        int row = current.getRow();
        int col = current.getCol();

        // Return the parent of the current node
        return parent[row][col];
    }

    private List<GridSquare> getNeighbors(GridSquare square) {
        List<GridSquare> neighbors = new ArrayList<>();
        int row = square.getRow();
        int col = square.getCol();

        // Add valid neighbors (up, down, left, right)
        if (row > 0) neighbors.add(gridSquares[row - 1][col]); // Up
        if (row < rows - 1) neighbors.add(gridSquares[row + 1][col]); // Down
        if (col > 0) neighbors.add(gridSquares[row][col - 1]); // Left
        if (col < cols - 1) neighbors.add(gridSquares[row][col + 1]); // Right

        return neighbors;
    }

    private int getPathLength(int startX, int startY, int endX, int endY) {
        int length = 0;
        GridSquare current = gridSquares[endX][endY];
        while (current.getRow() != startX || current.getCol() != startY) {
            length++;
            current = getPreviousInPath(current, startX, startY);
        }
        return length;
    }
}
