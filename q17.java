import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q17 {

    public static enum Direction{
        UP, DOWN, LEFT, RIGHT, WILDCARD;
    }

    public static int heuristic( int r, int c, int goalR, int goalC){
        return Math.abs(goalR - r) + Math.abs(goalC - c); // Manhattan heuristic
    }

    public static boolean isInBounds(int r, int c, int goalR, int goalC){
        return r >= 0 && c >= 0 && r <= goalR && c <= goalC;
    }

    public static List<GridSpace> getNeighbors(GridSpace node, int bestCostSoFarNode, int[][] grid, int minStep, int maxStep, int goalR, int goalC){
        List<GridSpace> neighbors = new ArrayList<>();

        int cost1 = bestCostSoFarNode;
        int cost2 = bestCostSoFarNode;
        
        for(int i=1; i<=maxStep; ++i){
            if(node.dir == Direction.DOWN || node.dir == Direction.UP){
                if(isInBounds(node.r, node.c + i, goalR, goalC)){
                    cost1 += grid[node.r][node.c+i];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r, node.c + i, cost1, Direction.RIGHT));
                } if(isInBounds(node.r, node.c - i, goalR, goalC)){
                    cost2 += grid[node.r][node.c-i];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r, node.c - i, cost2, Direction.LEFT));
                }
            } else if(node.dir == Direction.LEFT || node.dir == Direction.RIGHT) {
                if(isInBounds(node.r+i, node.c, goalR, goalC)){
                    cost1 += grid[node.r+i][node.c];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r+i, node.c, cost1, Direction.DOWN));
                } if(isInBounds(node.r-i, node.c, goalR, goalC)){
                    cost2 += grid[node.r-i][node.c];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r-i, node.c, cost2, Direction.UP));
                }
            } else { // Start node
                if(isInBounds(node.r+i, node.c, goalR, goalC)){
                    cost1 += grid[node.r+i][node.c];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r+i, node.c, cost1, Direction.DOWN));
                } if(isInBounds(node.r, node.c + i, goalR, goalC)){
                    cost2 += grid[node.r][node.c+i];
                    if(i >= minStep) neighbors.add(new GridSpace(node.r, node.c + i, cost2, Direction.RIGHT));
                }
            }
        }
        

        return neighbors.stream().filter(e -> isInBounds(e.r, e.c, goalR, goalC)).toList();
    }

    public static int astar(int[][] grid, int goalR, int goalC, int minStep, int maxStep){
        HashMap<GridSpace, GridSpace> cameFrom = new HashMap<>();
        HashMap<GridSpace, Integer> bestCostSoFar = new HashMap<>();
        Set<GridSpace> visited = new HashSet<>();
        PriorityQueue<PriorityGridSpace> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.priority));
        
        GridSpace startNode = new GridSpace(0, 0, 0, null);
        GridSpace goalNode = new GridSpace(goalR, goalC, 0, Direction.WILDCARD);
        GridSpace finalNode = null;
        int totalPathCost = 0;

        bestCostSoFar.put(startNode, 0);
        cameFrom.put(startNode, null);

        pq.add(new PriorityGridSpace(0, startNode));

        while(!pq.isEmpty()){
            GridSpace node = pq.poll().gs;

            if(node.equals(goalNode)){
                finalNode = node;
                totalPathCost = finalNode.cost;
                break;
            }   

            if(visited.contains(node)) continue;
            visited.add(node);
            
            List<GridSpace> neighbors = getNeighbors(node, bestCostSoFar.get(node), grid, minStep, maxStep, goalR, goalC);
            for(GridSpace nextNode : neighbors){
                int newCost = nextNode.cost;

                if(!cameFrom.containsKey(nextNode) || newCost < bestCostSoFar.get(nextNode)){
                    cameFrom.put(nextNode, node);
                    bestCostSoFar.put(nextNode, newCost);
                    pq.add(new PriorityGridSpace(newCost + heuristic(nextNode.r, nextNode.c, goalR, goalC), nextNode));
                }
            }
        }

        if(finalNode == null) return -1;
        return totalPathCost;
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        int[][] grid = new int[lines.size()][lines.get(0).length()];
        for(int i=0; i<grid.length; ++i) for(int j=0; j<grid[0].length; ++j) grid[i][j] = (int)(lines.get(i).charAt(j) - 48);

        int goalR = grid.length-1;
        int goalC = grid[0].length-1;

        System.out.println("Part 1: " + astar(grid, goalR, goalC, 1, 3));
        System.out.println("Part 2: " +  astar(grid, goalR, goalC, 4, 10));
    }

    private static class GridSpace{
        int r, c, cost;
        Direction dir;

        public GridSpace(int r, int c, int cost, Direction dir){
            this.r = r;
            this.c = c;
            this.cost = cost;
            this.dir = dir;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof GridSpace)) return false;
            GridSpace gs = (GridSpace)o;
            return this.r == gs.r && this.c == gs.c && (this.dir == gs.dir || this.dir == Direction.WILDCARD || gs.dir == Direction.WILDCARD);
        }

        @Override
        public String toString(){
            return "{R: " + this.r + ", C: "+this.c+", "+this.dir+"}" ;
        }

        @Override
        public int hashCode(){
            return this.toString().hashCode();
        }
    }

    private static class PriorityGridSpace{
        int priority;
        GridSpace gs;
        public PriorityGridSpace(int priority, GridSpace gs){
            this.priority = priority;
            this.gs = gs;
        }
    }
}