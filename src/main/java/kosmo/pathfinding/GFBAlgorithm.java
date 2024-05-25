package kosmo.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kosmo.pathfinding.State.*;

public class GFBAlgorithm implements Runnable {
    private final GridSquare[][] gridSquares;
    private final int rows;
    private final int cols;
    public GFBAlgorithm(GridSquare[][] gridSquares)
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
//            double px = Math.abs(start.x - finish.x);
//            double py = Math.abs(start.y - finish.y);
//            if(px != 0 && py != 0)
//                this.cost = Math.pow(this.x - finish.x, 2)/px + Math.pow(this.y - finish.y, 2)/py;
//            else
            this.cost= Math.min(Math.abs(this.x-start.x),Math.abs(this.y-start.y));
            this.cost+= Math.sqrt(Math.pow(this.x-finish.x,2)+Math.pow(this.y-finish.y,2));


        }
//        public void setCost( Node startNode, Node finishNode, double h_val) {
//
//            this.cost = (double) Math.abs(startNode.x - this.x)/  + (double) Math.abs(startNode.y - this.y)  +h_val;
//        }
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
                    tab_pos[i][j] = 2;
                    startNode = new Node(j, i, null);
                }
                else if( gridSquares[i][j].getState() == DESTINATION){
                    tab_pos[i][j] = 10;
                    lastNode = new Node(j, i, null);
                }
                else if (gridSquares[i][j].getState() == OBSTACLE){
                    tab_pos[i][j] = 2;
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
                    lastNode.prev = startNode;
                    foundPath = true;
                }
                else if(tab_pos[tochk.get(i).y][tochk.get(i).x] == 0){
                    gridSquares[tochk.get(i).y][tochk.get(i).x].setState(FRONTIER);
                    tab_pos[tochk.get(i).y][tochk.get(i).x] = 1;
                    Execution.get().Wait();
                    tochk.get(i).setCost(startNode,lastNode);
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


        //System.out.println(lastNode);
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
//                    tochk.getLast().setCost(startNode,
//                            Math.pow(tochk.getLast().x - lastNode.x,2) + Math.pow(tochk.getLast().y- lastNode.y,2)
//                    );
                    gridSquares[y][x-1].setState(FRONTIER);
                    tab_pos[y][x-1] = 1;
                    Execution.get().Wait();
                }
                else if (tab_pos[y][x-1] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;

                }
            if( x != cols-1 )
                if( tab_pos[y][x+1] == 0){
                    tochk.add(new Node(x+1,y,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);
//                    tochk.getLast().setCost(startNode,Math.pow(tochk.getLast().x - lastNode.x,2)
//                            + Math.pow(tochk.getLast().y- lastNode.y,2));
                    gridSquares[y][x+1].setState(FRONTIER);
                    tab_pos[y][x+1] = 1;
                    Execution.get().Wait();
                }else if (tab_pos[y][x+1] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;

                }
            if( y != 0 )
                if(tab_pos[y-1][x] == 0){
                    tochk.add(new Node(x,y-1,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);
//                    tochk.getLast().setCost(startNode,Math.pow(tochk.getLast().x - lastNode.x,2)
//                            + Math.pow(tochk.getLast().y- lastNode.y,2));
                    gridSquares[y-1][x].setState(FRONTIER);
                    tab_pos[y-1][x]= 1;
                    Execution.get().Wait();
                } else if (tab_pos[y-1][x] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;

                }
            if( y !=rows-1 )
                if(tab_pos[y+1][x] == 0){
                    tochk.add(new Node(x,y+1,tochk.getFirst()));
                    tochk.getLast().setCost(startNode,lastNode);
//                    tochk.getLast().setCost(startNode,Math.pow(tochk.getLast().x - lastNode.x,2)
//                            + Math.pow(tochk.getLast().y- lastNode.y,2));
                    gridSquares[y+1][x].setState(FRONTIER);
                    tab_pos[y+1][x] = 1;
                    Execution.get().Wait();
                }

                else if (tab_pos[y+1][x] == 10)
                {
                    lastNode.setPrev(tochk.getFirst());
                    foundPath = true;

                }
            tochk.removeFirst();

        }
        int i = 0;
        Node tmp = lastNode;
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
        gridSquares[tmp.y][tmp.x].setState(DESTINATION);
        gridSquares[startNode.y][startNode.x].setState(ORIGIN);
        Execution.get().stopPoint(i);
    }
}
