package kosmo.pathfinding;

import java.util.*;

import static kosmo.pathfinding.State.*;

public class AstarAlgorithm implements Runnable {
    private final GridSquare[][] gridSquares;
    private final int rows;
    private final int cols;
    public AstarAlgorithm(GridSquare[][] gridSquares)
    {
        this.gridSquares = gridSquares;
        this.rows = Scene.GRID_ROWS;
        this.cols = Scene.GRID_COLUMNS;
    }
    private static class Node implements Comparable<Node>
    {
        int x, y;
        double q = 0;
        double h = 0;
        double f = 0;
        Node prev ;
        Node(int x, int y, Node prev)
        {
            this.x = x;
            this.y = y;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "( x: " +x+ "; y: "+ y+ "; f: "+f+" )";
        }

        @Override
        public int compareTo(Node o) {
            return (int)(this.f - o.f);
        }
    }
    @Override
    public void run() {
        // Find start and finish position
        Node lastNode = null;
        Node startNode = null;
        Execution.get().startPoint();
        int[][] types = new int[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++) {
                if (gridSquares[i][j].getState() == ORIGIN) {
                    types[i][j]=0;
                    startNode = new Node(j, i, null);
                }
                else if( gridSquares[i][j].getState() == DESTINATION){
                    types[i][j]=0;
                    lastNode = new Node(j, i, null);
                }
                else if (gridSquares[i][j].getState() == OBSTACLE){
                    types[i][j]=5;
                }
                else {
                    types[i][j]=0;
                }
            }
        }
        if(startNode == null || lastNode == null)
        {
            OutputConsole.get().writeLn("Start or end point not defined!");
            Execution.get().stopPoint();
            return;
        }

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        open.add(startNode);

        boolean foundPath = false;
        while(!open.isEmpty() && !foundPath)
        {

            System.out.println("--------------");
            open.forEach(System.out::println);

            Node q = open.poll();
            if(q==null)
                break;
            gridSquares[q.y][q.x].setState(VISITED);
            Execution.get().Wait();
            types[q.y][q.x] = 1;

            ArrayList<Node> tochk = new ArrayList<>();
            if(q.x+1 != cols)
                if(types[q.y][q.x+1] != 5){
                    tochk.add(new Node(q.x+1,q.y,q));

                }

            if(q.x-1 != -1)
                if(types[q.y][q.x-1] != 5)
                    tochk.add(new Node(q.x-1,q.y,q));



            if(q.y+1 != rows)
                if(types[q.y+1][q.x] != 5)
                    tochk.add(new Node(q.x,q.y+1,q));


            if(q.y-1 != -1)
                if(types[q.y-1][q.x] != 5)
                    tochk.add(new Node(q.x,q.y-1,q));

            for(Node tonode : tochk)
            {
                if (tonode.x == lastNode.x && tonode.y == lastNode.y) {
                    lastNode.prev = tonode;
                    foundPath = true;
                }
                if(types[tonode.y][tonode.x] == 1)
                    continue;
                tonode.q = q.q +0.5;
                // 0.5 == distance between one grid to next, never on a diagonal axis
                tonode.h = Math.abs(tonode.x - lastNode.x)  + Math.abs(tonode.y - lastNode.y) ;
                //Approximate of Heuristic distance, based on "Manhattan Distance" method
                //tonode.h = Math.sqrt(Math.pow(tonode.x - lastNode.x,2)  + Math.pow(tonode.y - lastNode.y,2) );
                //Approximate of Heuristic distance, based on real distance from node to DESTINATION
                tonode.f = tonode.q + tonode.h;
                boolean was = false;
                for(Node toopen: open)
                {
                    if(toopen.x == tonode.x && toopen.y == tonode.y)
                    {
                        if(tonode.f < toopen.f)
                        {
                            toopen.f = tonode.f;
                            toopen.q = tonode.q;
                            toopen.h = tonode.h;
                            toopen.prev = q;
                        }
                        was = true;
                    }
                }
                if(was)
                    continue;
                gridSquares[tonode.y][tonode.x].setState(FRONTIER);
                Execution.get().Wait();
                open.add(tonode);
            }

        }
        Node tmpLast = lastNode;
        int i = 0;
        while(lastNode != null)
        {
            i++;
            gridSquares[lastNode.y][lastNode.x].setState(PATH);
            lastNode = lastNode.prev;
        }
        gridSquares[startNode.y][startNode.x].setState(ORIGIN);
        gridSquares[tmpLast.y][tmpLast.x].setState(DESTINATION);
        Execution.get().stopPoint(i-2);
    }
}
