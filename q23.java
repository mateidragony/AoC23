import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.*;

public class q23 {

    public static enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    public static final String filename = "in.txt";


    public static final Map<Direction, int[]> directions = Stream.of(Map.entry(Direction.UP, new int[] { -1, 0 }),
            Map.entry(Direction.DOWN, new int[] { 1, 0 }),
            Map.entry(Direction.LEFT, new int[] { 0, -1 }),
            Map.entry(Direction.RIGHT, new int[] { 0, 1 }))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    public static final Map<Character, Direction> slopeDirections = Stream.of(Map.entry('^', Direction.UP),
            Map.entry('v', Direction.DOWN),
            Map.entry('<', Direction.LEFT),
            Map.entry('>', Direction.RIGHT))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));



    public static boolean isValidSpace(Position p, char[][] grid, Set<Position> placesWalked){
        return !placesWalked.contains(p) && p.r >= 0 && p.c >= 0 && p.r < grid.length && p.c < grid[0].length && grid[p.r][p.c] != '#';
    }

    public static boolean isSlope(Position p, char[][] grid){
        char c = grid[p.r][p.c];
        return c == 'v' || c=='^' || c=='<' || c=='>';
    }

    public static void printGrid(char[][] grid){
        for(char[] row : grid){
            for(char c : row) System.out.print(c);
            System.out.println();
        }
    }

    public static void blockOffDeadEnd(char[][] grid, Position p, Position start, Position goal){
        while(isDeadEnd(grid, p.r, p.c, start, goal)){
            grid[p.r][p.c] = '#';
            for(int[] dir : directions.values()){
                Position newP = new Position(p.r+dir[0], p.c+dir[1]);
                if(isValidSpace(newP, grid, new HashSet<>())){
                    p = newP;
                    break;
                }
            }
        }
    }

    public static boolean isDeadEnd(char[][] grid, int r, int c, Position start, Position goal){
        int numWalls = 0;

        if(grid[r][c] == '#') return false;
        Position p = new Position(r, c);
        if(p.equals(start) || p.equals(goal)) return false; 

        for(int[] dir : directions.values()){
            if(!isValidSpace(new Position(r+dir[0], c+dir[1]), grid, new HashSet<>())) numWalls++;
        }

        return numWalls == 3;
    }

    public static void detectDeadEnds(char[][] grid, Position start, Position goal){
        for(int r=0; r<grid.length; r++){
            for(int c=0; c<grid[0].length; c++){
                if(isDeadEnd(grid, r, c, start, goal)){
                    blockOffDeadEnd(grid, new Position(r, c), start, goal);
                }
            }
        }
    }

    public static Set<Position> getAllSplits(char[][] grid){
        Set<Position> splitPos = new HashSet<>();
        for(int i=0; i<grid.length; i++) for(int j=0; j<grid[0].length; j++) if(isSplit(grid, i, j)) splitPos.add(new Position(i, j));
        return splitPos;
    }


    public static boolean isSplit(char[][] grid, int r, int c){
        int numArrows = 0;

        if(grid[r][c] == '#') return false;

        for(int[] dir : directions.values()){
            if(isValidSpace(new Position(r+dir[0], c+dir[1]), grid, new HashSet<>()) && slopeDirections.keySet().stream().anyMatch(e -> e==grid[r+dir[0]][c+dir[1]])) numArrows++;
        }

        return numArrows >= 3;
    }


    public static HashMap<Position, List<Path>> generateGraph(char[][] grid, Set<Position> splitPos){
        HashMap<Position, List<Path>> splits = new HashMap<>();

        for(Position split : splitPos){
            List<Path> paths = expandNode(grid, split, splits, splitPos);
            splits.put(split, paths);
        }

        return splits;
    } 

    public static List<Path> expandNode(char[][] grid, Position cur, HashMap<Position, List<Path>> splits, Set<Position> splitPos){

        List<Position> walkablePositions = new ArrayList<>();
        for(int[] dir : directions.values()){
            Position p = new Position(cur.r + dir[0], cur.c + dir[1]);
            if(isValidSpace(p, grid, new HashSet<>())) walkablePositions.add(p);
        }

        List<Path> paths = new ArrayList<>();

        for(Position p : walkablePositions) paths.add(runDownPath(grid, cur, p, splits, splitPos));
        return paths;
    }

    public static Path runDownPath(char[][] grid, Position cameFrom, Position cur, HashMap<Position, List<Path>> splits, Set<Position> splitPos){
        int len = 0;

        Set<Position> pathsTreaded = new HashSet<>();
        pathsTreaded.add(cameFrom);

        while(!splitPos.contains(cur)){ // while i haven't reached the next split
            pathsTreaded.add(cur);

            len++;
            for(int[] dir : directions.values()){
                Position newP = new Position(cur.r+dir[0], cur.c+dir[1]);
                if(isValidSpace(newP, grid, pathsTreaded)){
                    cur = newP;
                    break;
                }
            }
        }

        return new Path(cameFrom, cur, len+1);
    }

    // dijkstras on -G (doesn't work somehow...)
    public static int getLongestPath(HashMap<Position, List<Path>> splits, Position start, Position goal){
        Set<Position> unvisited = new HashSet<>(splits.keySet());
        HashMap<Position, Integer> costs = new HashMap<>();
        for(Position p : unvisited) costs.put(p, Integer.MAX_VALUE);

        costs.put(start, 0);
        PriorityQueue<Position> q = new PriorityQueue<>(Comparator.comparingInt(e -> costs.get(e)));
        q.add(start);

        while(!q.isEmpty()){
            Position cur = q.poll();

            if(!unvisited.contains(cur)) continue; // revisiting

            for(Path path : splits.get(cur)){
                if(costs.get(path.end) > costs.get(cur) - path.length) costs.put(path.end, costs.get(cur) - path.length);
                q.add(path.end);
            }
            unvisited.remove(cur);
        }

        return -costs.get(goal);
    }

    public static int getLongestPath2(HashMap<Position, List<Path>> splits, Position start, Position goal, Set<Position> visited){
        if(start.equals(goal)) return 0;

        visited.add(start);
        int res = 0;

        List<Path> paths = splits.get(start);
        for(Path path : paths){
            if(visited.contains(path.end)) continue;

            int r = getLongestPath2(splits, path.end, goal, visited);

            if(r != -1){ // no dead end
                r += path.length;

                res = Math.max(res, r);
            }
        }

        visited.remove(start);

        if(res == 0) return -1;

        return res;
    }

    // greedy no backtrack
    public static int walk(char[][] grid, Position curPosition, Set<Position> placesWalked, Position goal) {

        if(!isValidSpace(curPosition, grid, placesWalked)) return Integer.MIN_VALUE; // slid into bad spot
        
        placesWalked.add(curPosition);

        if(curPosition.equals(goal)) return 1;

        else if(isSlope(curPosition, grid)){
            int[] dir = directions.get(slopeDirections.get(grid[curPosition.r][curPosition.c]));
            return 1 + walk(grid, new Position(curPosition.r + dir[0], curPosition.c + dir[1]), placesWalked, goal);
        } 

        List<Position> walkablePositions = new ArrayList<>();
        for(int[] dir : directions.values()){
            Position p = new Position(curPosition.r + dir[0], curPosition.c + dir[1]);
            if(isValidSpace(p, grid, placesWalked)) walkablePositions.add(p);
        }

        if(walkablePositions.isEmpty()) return Integer.MIN_VALUE; // didn't make it to goal and can no longer walk
        else if(walkablePositions.size() == 1){ // I don't need to copy set
            return 1 + walk(grid, walkablePositions.get(0), placesWalked, goal);
        } else { // I will have to copy set :(
            int max = Integer.MIN_VALUE;
            for(Position p : walkablePositions){
                max = Math.max(max, walk(grid, p, new HashSet<>(placesWalked), goal));
            }
            return max+1;
        }
    }

    public static int walkLoop(char[][] grid, Position start, Position goal){ // cuz my stack overflows... and now i run out of heapspace...

        List<Set<Position>> possiblePaths = new ArrayList<>();
        List<Position> allLookingAt = new ArrayList<>();
        List<Boolean> donePaths = new ArrayList<>();

        possiblePaths.add(Stream.of(start).collect(Collectors.toSet()));
        allLookingAt.add(start);
        donePaths.add(false);

        while(!donePaths.stream().allMatch(e -> e)){
            for(int i=0; i<possiblePaths.size(); ++i){
                if(donePaths.get(i)) continue; // path is done (made it to goal or impossible to reach goal)

                Set<Position> path = possiblePaths.get(i);
                Position curPosition = allLookingAt.get(i);

                if(curPosition.equals(goal) || path.contains(goal)) { // arrived at goal (first condition should be it)
                    donePaths.set(i, true);
                    continue; 
                }

                List<Position> walkablePositions = new ArrayList<>();
                for(int[] dir : directions.values()){
                    Position p = new Position(curPosition.r + dir[0], curPosition.c + dir[1]);
                    if(isValidSpace(p, grid, path)) walkablePositions.add(p);
                }

                if(walkablePositions.isEmpty()) { // didn't make it to goal and can no longer walk
                    possiblePaths.set(i, new HashSet<>());
                    donePaths.set(i, true);
                }
                else if(walkablePositions.size() == 1){ // I don't need to copy set
                    path.add(walkablePositions.get(0));
                    allLookingAt.set(i, walkablePositions.get(0));
                } else { // I will have to copy set :(

                    List<Integer> splitIndex = new ArrayList<>();
                    splitIndex.add(i);

                    for(int j=0; j<walkablePositions.size(); j++){

                        Position newPos = walkablePositions.get(j);
                        if(j == walkablePositions.size() - 1){ // last walkable position
                            path.add(newPos);
                            allLookingAt.set(i, newPos);
                        } else {
                            Set<Position> newPath = new HashSet<>(path);
                            newPath.add(newPos);
                            allLookingAt.add(newPos);
                            donePaths.add(false);
                            possiblePaths.add(newPath);
                        }
                    }
                }
            }
        }

        return possiblePaths.stream().mapToInt(e -> e.size()).max().getAsInt();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<>();
        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }

        // --------------------
        // Part One
        // --------------------

        char[][] grid = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < grid.length; ++i) for (int j = 0; j < grid[0].length; ++j) grid[i][j] = lines.get(i).charAt(j);
        grid[0][1] = 'v';

        Position start = new Position(0, 1);
        Position goal = new Position(grid.length-1, grid[0].length-2);
        int partOneSum = walk(grid, start, new HashSet<>(), goal) - 1; // don't count start apparently...

        // --------------------
        // Part Two
        // --------------------


        Set<Position> splitPos = getAllSplits(grid);
        splitPos.add(start);
        splitPos.add(goal);

        HashMap<Position, List<Path>> graph = generateGraph(grid, splitPos);
        int partTwoSum = getLongestPath2(graph, start, goal, new HashSet<>());

        // --------------------
        // Answers
        // --------------------


        System.out.println("Part 1: "+partOneSum);
        System.out.println("Part 2: "+partTwoSum);
    }

    private static class Position {
        int r, c;

    
        public Position(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public int hashCode() { // generated
            final int prime = 31;
            int result = 1;
            result = prime * result + r;
            result = prime * result + c;
            return result;
        }

        @Override
        public boolean equals(Object obj) { // generated
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Position other = (Position) obj;
            if (r != other.r)
                return false;
            if (c != other.c)
                return false;
            return true;
        }

        @Override
        public String toString() { // generated
            return "#r=" + r + ", c=" + c + "#";
        }
    }

    private static class Path{
        Position start, end;
        int length;

        public Path(q23.Position start, q23.Position end, int length) {
            this.start = start;
            this.end = end;
            this.length = length;
        }

        @Override
        public String toString() {
            return "$end=" + end + ", length=" + length + "$";
        }

        @Override
        public int hashCode() { // generated
            final int prime = 31;
            int result = 1;
            result = prime * result + ((start == null) ? 0 : start.hashCode());
            result = prime * result + ((end == null) ? 0 : end.hashCode());
            result = prime * result + length;
            return result;
        }

        @Override
        public boolean equals(Object obj) { // generated
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            Path other = (Path) obj;
            if (start == null) {
                if (other.start != null)
                    return false;
            } else if (!start.equals(other.start))
                return false;
            if (end == null) {
                if (other.end != null)
                    return false;
            } else if (!end.equals(other.end))
                return false;
            if (length != other.length)
                return false;
            return true;
        }
    }
}
