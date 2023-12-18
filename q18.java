import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q18 {

    public static boolean shouldExpand(int r, int c, char[][] grid){
        return r >= 0 && c >= 0 && r < grid.length && c < grid[0].length && grid[r][c] == '.';
    }

    public static int exapandInside(char[][] grid){
        int numExpanded = 0;

        for(int i=0; i<grid.length; ++i){
            for(int j=0; j<grid[0].length; j++){
                if(grid[i][j] == '$'){
                    for(int r=i-1; r<=i+1; ++r){
                        for(int c=j-1; c<=j+1; ++c){
                            if(shouldExpand(r, c, grid)){
                                grid[r][c] = '$';
                                numExpanded++;
                            }
                        }
                    }
                }
            }
        }

        return numExpanded;
    }

    public static long shoelace(List<Vertex> vertices){
        long area = 0L;
        
        int n = vertices.size();

        for(int i=0; i<n; ++i){
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get((i+1) % n);
            area += (v1.x*v2.y - v2.x*v1.y);
        }

        return area / 2;
    }
    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        // Hashmap of my directional changes according to letter
        HashMap<String, Direction> dirs = new HashMap<>();
        dirs.put("U", new Direction(-1, 0, new Direction(0, 1, null)));
        dirs.put("D", new Direction(1, 0, new Direction(0, -1, null)));
        dirs.put("L", new Direction(0, -1, new Direction(-1, 0, null)));
        dirs.put("R", new Direction(0, 1, new Direction(1, 0, null)));

        HashMap<Character, String> hexToDir = new HashMap<>();
        hexToDir.put('0', "R");        
        hexToDir.put('1', "D");
        hexToDir.put('2', "L");
        hexToDir.put('3', "U");

        // --------------------------------
        // Ah, how naive I was with this...
        // --------------------------------

        // Traverse once to get dimensions of my grid
        long minRow = 0, maxRow = 0, minCol = 0, maxCol = 0;
        long curRow = 0, curCol = 0;
        for(String line : lines){
            String[] data = line.split(" ");
            Direction dir = dirs.get(data[0]);
            int meters = Integer.parseInt(data[1]);
            
            curRow += dir.dr * meters;
            curCol += dir.dc * meters;

            maxRow = Math.max(maxRow, curRow);
            maxCol = Math.max(maxCol, curCol);

            minRow = Math.min(minRow, curRow);
            minCol = Math.min(minCol, curCol);
        }

        long rows = maxRow - minRow + 1;
        long cols = maxCol - minCol + 1;
        long startRow = minRow * -1, startCol = minCol * -1;

        char[][] grid = new char[(int)rows][(int)cols];
        for(int i=0; i<rows; ++i) for(int j=0; j<cols; ++j) grid[i][j] = '.';
        
        // Traverse for real now
        curRow = startRow; curCol = startCol;
        grid[(int)startRow][(int)startCol] = '#';
        for(String line : lines){
            String[] data = line.split(" ");
            Direction dir = dirs.get(data[0]);
            int meters = Integer.parseInt(data[1]);
            
            for(int i=0; i<meters; ++i){
                grid[(int)curRow][(int)curCol] = '#';
                curRow += dir.dr;
                curCol += dir.dc;

                Direction inside = dir.inside;
                if(grid[(int)curRow + inside.dr][(int)curCol + inside.dc] != '#') grid[(int)curRow + inside.dr][(int)curCol + inside.dc] = '$';
            }
        }

        while(exapandInside(grid) > 0);

        // Count non-outside
        int partOneSum = 0;
        for(int i=0; i<rows; ++i) for(int j=0; j<cols; ++j) if(grid[i][j] != '.') partOneSum++;


        // --------------------------------
        //            Part two.
        // --------------------------------

        // Actually traverse and get all vertices
        List<Vertex> vertices = new ArrayList<>();
        curRow = 0; curCol = 0;
        vertices.add(new Vertex(curRow, curCol));

        int lineIdx = 0;
        for(String line : lines){
            String[] data = line.split(" ");

            Direction dir = dirs.get(hexToDir.get(data[2].charAt(7)));
            String prevDirStr = hexToDir.get(lines.get(lineIdx == 0 ? lines.size()-1 : lineIdx-1).split(" ")[2].charAt(7));
            String nextDirStr = hexToDir.get(lines.get((lineIdx+1) % lines.size()).split(" ")[2].charAt(7));
            Direction prevDir = dirs.get(prevDirStr);
            Direction nextDir = dirs.get(nextDirStr);

            int sideAdjustment = 0;
            if(!prevDir.equals(nextDir)){
                if(dir.inside.equals(nextDir)) sideAdjustment = 1;
                else sideAdjustment = -1;
            }

            int meters = Integer.parseInt(data[2].substring(2, 7), 16) + sideAdjustment;

            curRow += dir.dr * (meters);
            curCol += dir.dc * (meters);

            vertices.add(new Vertex(curCol, curRow)); // X and Y instead of row and col

            lineIdx++;
        }

        long partTwoSum = shoelace(vertices);

        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: " + partTwoSum );
    }

    private static class Direction{
        int dr, dc;
        Direction inside;
        public Direction(int dr, int dc, Direction inside){
            this.dr = dr;
            this.dc = dc;
            this.inside = inside;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Direction)) return false;
            Direction dir = (Direction)o;
            return dir.dr == this.dr && dir.dc == this.dc;
        }
    }

    private static class Vertex{
        long x, y;
        public Vertex(long x, long y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString(){
            return "("+x+", "+y+")";
        }
    }
}