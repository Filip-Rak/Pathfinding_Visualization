package kosmo.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        double cost = 0;
        Node prev ;
        Node(int x, int y, Node prev)
        {
            this.x = x;
            this.y = y;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "( x: "+x+", y: "+y+" )";
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setCost(Node start, Node finish) {
           // this.cost = Math.pow(this.x - finish.x, 2) + Math.pow(this.y - finish.y, 2) ;
            double px = Math.abs(start.x - finish.x);
            double py = Math.abs(start.y - finish.y);
            if(px != 0 && py != 0)
                this.cost = Math.pow(this.x - finish.x, 2)/px + Math.pow(this.y - finish.y, 2)/py;
            else
                this.cost= Math.pow(this.x - finish.x, 2) + Math.pow(this.y - finish.y, 2);
           // this.cost = Math.max(this.x - finish.x+this.y - start.y, this.x - start.x+this.y - finish.y);
        }

        public double getCost() {
            return cost;
        }

        @Override
        public int compareTo(Node o) {
            if(x == o.x && y == o.y)
                return 0;
            if(x==o.x)
                return -1;
            return 1;
        }
    }
    @Override
    public void run() {
        // Find start and finish position
        Node lastNode = null;
        Node startNode = null;
        Execution.get().startPoint();
        int[][] tab_pos = new int[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++) {
                if (gridSquares[i][j].getState() == ORIGIN) {
                    tab_pos[i][j] = 1;
                    startNode = new Node(j, i, null);
                }
                else if( gridSquares[i][j].getState() == DESTINATION){
                    tab_pos[i][j] = 10;
                    lastNode = new Node(j, i, null);
                }
                else if (gridSquares[i][j].getState() == OBSTACLE){
                    tab_pos[i][j] = 1;
                }
                else {
                    tab_pos[i][j] = 0;
                }
            }
        }
        if(startNode == null || lastNode == null)
        {
            OutputConsole.get().writeLn("Start or end point not defined!");
            Execution.get().stopPoint();
            return;
        }
        ArrayList<Node> tochk = new ArrayList<>(List.of(new Node[]{
                new Node(startNode.x - 1, startNode.y, startNode),
                new Node(startNode.x + 1, startNode.y,startNode),
                new Node(startNode.x, startNode.y - 1,startNode),
                new Node(startNode.x, startNode.y + 1,startNode)
        }));
        boolean foundPath = false;
        gridSquares[startNode.y][startNode.x].setState(VISITED);
        Execution.get().Wait();
        for (int i =0; i < tochk.size(); i++) {
            if(tochk.get(i).x > -1 && tochk.get(i).y > -1 && tochk.get(i).x < cols && tochk.get(i).y < rows ){
                if(tab_pos[tochk.get(i).y][tochk.get(i).x] == 10)
                {
                    lastNode.prev = tochk.get(i);
                    foundPath = true;
                    break;
                }
                else if(tab_pos[tochk.get(i).y][tochk.get(i).x] == 0){
                    gridSquares[tochk.get(i).y][tochk.get(i).x].setState(FRONTIER);
                    tab_pos[tochk.get(i).y][tochk.get(i).x] = 1;
                    Execution.get().Wait();
                    tochk.get(i).setCost(startNode, lastNode);
                }
                else {
                    tochk.remove(i);
                    i--;
                }

            }
            else {
                tochk.remove(i);
                i--;
            }
        }


        System.out.println(lastNode);
        while(!foundPath)
        {
            tochk.sort(Comparator.comparing(Node::getCost));
            if(tochk.isEmpty()) {
                OutputConsole.get().writeLn("NOT FOUND");
                break;
            }
            int x = tochk.getFirst().x;
            int y = tochk.getFirst().y;

            gridSquares[y][x].setState(VISITED);
            tab_pos[y][x] = 1;

            Execution.get().Wait();
            if( x != 0 )
                if(tab_pos[y][x-1] == 0){
                    tochk.add(new Node(x-1,y,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);

                    gridSquares[y][x-1].setState(FRONTIER);
                    tab_pos[y][x-1] = 1;
                    Execution.get().Wait();
                } else if (tab_pos[y][x-1] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;
                    break;
                }
            if( x != cols-1 )
                if( tab_pos[y][x+1] == 0){
                    tochk.add(new Node(x+1,y,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);

                    gridSquares[y][x+1].setState(FRONTIER);
                    tab_pos[y][x+1] = 1;
                    Execution.get().Wait();
                }else if (tab_pos[y][x+1] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;
                    break;
                }
            if( y != 0 )
                if(tab_pos[y-1][x] == 0){
                    tochk.add(new Node(x,y-1,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);

                    gridSquares[y-1][x].setState(FRONTIER);
                    tab_pos[y-1][x]= 1;
                    Execution.get().Wait();
                } else if (tab_pos[y-1][x] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;
                    break;
                }
            if( y !=rows-1 )
                if(tab_pos[y+1][x] == 0){
                    tochk.add(new Node(x,y+1,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);

                    gridSquares[y+1][x].setState(FRONTIER);
                    tab_pos[y+1][x] = 1;
                    Execution.get().Wait();
                }
                else if (tab_pos[y+1][x] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;
                    break;
                }
            tochk.removeFirst();
            //Execution.get().Wait();
        }
        int i = 0;
        if (foundPath)
        {
            while(lastNode != null)
            {
                i++;
                gridSquares[lastNode.y][lastNode.x].setState(PATH);
                Execution.get().Wait();
                lastNode = lastNode.prev;
            }
        }
        Execution.get().stopPoint(i);
    }
}
