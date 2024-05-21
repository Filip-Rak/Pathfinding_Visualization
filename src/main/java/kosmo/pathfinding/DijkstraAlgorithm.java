package kosmo.pathfinding;

import java.util.PriorityQueue;
import java.util.Comparator;

public class DijkstraAlgorithm implements Runnable
{
    // Attributes
    private final GridSquare[][] gridSquares;
    private final int rows;
    private final int cols;

    // Constructor
    public DijkstraAlgorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
        this.rows = Scene.GRID_ROWS;
        this.cols = Scene.GRID_COLUMNS;
    }

    // Helper class for priority queue
    private static class Node
    {
        int x, y, cost;

        Node(int x, int y, int cost)
        {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }
    }

    // Algorithm's work and GridSquare.setState only in this method
    // The method is called inside RootController
    @Override
    public void run()
    {
        // Put this at the beginning of run method. It tells the program that the algorithm started work
        Execution.get().startPoint();

        // Finding the start and end points
        int startX = -1, startY = -1, endX = -1, endY = -1;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (gridSquares[i][j].getState() == State.ORIGIN)
                {
                    startX = i;
                    startY = j;
                }
                else if (gridSquares[i][j].getState() == State.DESTINATION)
                {
                    endX = i;
                    endY = j;
                }
            }
        }

        // Check if start or end points are not found
        if (startX == -1 || startY == -1 || endX == -1 || endY == -1)
        {
            OutputConsole.get().writeLn("Start or end point not defined!");
            Execution.get().stopPoint();
            return;
        }

        // Initializing distance and priority queue
        int[][] dist = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));

        // Set all distances to infinity
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }

        // Set distance for the starting point and add it to the queue
        dist[startX][startY] = 0;
        pq.add(new Node(startX, startY, 0));
        gridSquares[startX][startY].setState(State.ORIGIN, true);

        // Possible movements: up, down, left, right
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        boolean pathFound = false;

        while (!pq.isEmpty())
        {
            Node current = pq.poll();
            int x = current.x;
            int y = current.y;

            // If we reached the destination
            if (x == endX && y == endY)
            {
                pathFound = true;
                break;
            }

            // Skip if already visited
            if (visited[x][y])
            {
                continue;
            }

            // Mark as visited
            visited[x][y] = true;
            gridSquares[x][y].setState(State.VISITED, true);
            Execution.get().Wait();

            // Explore neighbors
            for (int i = 0; i < 4; i++)
            {
                int newX = x + dx[i];
                int newY = y + dy[i];

                // Check if new position is within bounds and not an obstacle
                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && !visited[newX][newY] && gridSquares[newX][newY].getState() != State.OBSTACLE)
                {
                    int newDist = dist[x][y] + 1; // All edges have weight 1
                    if (newDist < dist[newX][newY])
                    {
                        dist[newX][newY] = newDist;
                        pq.add(new Node(newX, newY, newDist));
                        gridSquares[newX][newY].setState(State.FRONTIER, true);
                        Execution.get().Wait();
                    }
                }
            }
        }

        int pathLength = 0;
        if (pathFound)
        {
            // Trace back the path
            int pathX = endX;
            int pathY = endY;
            while (pathX != startX || pathY != startY)
            {
                gridSquares[pathX][pathY].setState(State.PATH, true);
                pathLength++;
                Execution.get().Wait();

                for (int i = 0; i < 4; i++)
                {
                    int newX = pathX + dx[i];
                    int newY = pathY + dy[i];
                    if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && dist[newX][newY] == dist[pathX][pathY] - 1)
                    {
                        pathX = newX;
                        pathY = newY;
                        break;
                    }
                }
            }

            gridSquares[startX][startY].setState(State.ORIGIN, true);
            gridSquares[endX][endY].setState(State.DESTINATION, true);
        }
        else
        {
            OutputConsole.get().writeLn("No path found from start to end!");
        }

        // Tells the app that the algorithm finished working and the grid can be refreshed
        Execution.get().stopPoint(pathLength);
    }
}
