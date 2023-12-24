import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class q21 {

    public static final String filename = "in.txt";

    public static boolean isValidPosition(int r, int c, int rows, int cols, char[][] grid){
        return grid[r%rows >= 0 ? r%rows : (r%rows) + rows][c%cols >= 0 ? c%cols : (c%cols) + cols] != '#'; 
    }

    public static Set<Position> step(Position p, char[][] grid){
        Set<Position> newPositions = new HashSet<>();
        Position[] dPositons = {new Position(-1, 0), new Position(1, 0), new Position(0, -1), new Position(0, 1)};

        for(Position dP : dPositons)
            if(isValidPosition(p.r + dP.r, p.c + dP.c, grid.length, grid[0].length, grid))
                newPositions.add(new Position(p.r + dP.r, p.c + dP.c));
        
        return newPositions;
    }

    public static long runNSteps(long numSteps, List<String> lines){
        int rows = lines.size();
        int cols = lines.get(0).length();

        char[][] grid = new char[rows][cols];
        Set<Position> positions = new HashSet<>();

        for(int i=0; i<rows; ++i){
            for(int j=0; j<cols; ++j){
                grid[i][j] = lines.get(i).charAt(j);
                if(grid[i][j] == 'S') positions.add(new Position(i, j));
            } 
        }

        for(int i=0; i<numSteps; ++i){
            Set<Position> newPositons = new HashSet<>();
            for(Position p : positions) newPositons.addAll(step(p, grid));
            positions = newPositons;
        }

        return positions.size();
    }


    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        // --------------------
        //      Part One
        // --------------------

        long partOneSum = runNSteps(50, lines);

        // --------------------
        //      Part Two
        // --------------------

        int width = lines.get(0).length();

        long partTwoSteps = 26501365;
        long cycles = partTwoSteps / width;
		long remainder = partTwoSteps % width;

        long x1 = remainder;
        long x2 = width + remainder;
        long x3 = 2*width + remainder;
        long y1 = runNSteps(x1, lines);
        long y2 = runNSteps(x2, lines);
        long y3 = runNSteps(x3, lines);
        x1 = 0;
        x2 = 1; 
        x3 = 2;

        long x = cycles;

        long partTwoSum = (long) (((x-x2) * (x-x3)) / ((x1-x2) * (x1-x3)) * y1 +
                                    ((x-x1) * (x-x3)) / ((x2-x1) * (x2-x3)) * y2 +
                                    ((x-x1) * (x-x2)) / ((x3-x1) * (x3-x2)) * y3);


        System.out.println("Part 1: "+partOneSum);
        System.out.println("Part 2: "+partTwoSum);
    }

    private static class Position{
        int r, c;

        public Position(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public int hashCode(){
            return this.r + 100000*this.c;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Position)) return false;
            Position p = (Position)o;
            return p.r == this.r && p.c == this.c;
        }

        @Override
        public String toString() {
            return "Position [r=" + r + ", c=" + c + "]";
        }
    }
}
