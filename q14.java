import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q14 {

    public static enum Direction{
        NORTH, SOUTH, EAST, WEST;
    }

    public static final int NO_SWAP = -1;

    public static final HashMap<String, Long> memo = new HashMap<>();

    public static String stringify(char[][] grid){
        StringBuilder str = new StringBuilder();
        for(char[] row : grid) str.append(Arrays.toString(row));
        return str.toString();
    }

    public static void swapGrid(char[][] grid, Direction dir, int gridIdx, int i, int j){
        if(dir == Direction.NORTH || dir == Direction.SOUTH){
            grid[i][gridIdx] = (char)(grid[i][gridIdx] ^ grid[j][gridIdx]);
            grid[j][gridIdx] = (char)(grid[i][gridIdx] ^ grid[j][gridIdx]);
            grid[i][gridIdx] = (char)(grid[i][gridIdx] ^ grid[j][gridIdx]);
        } else {
            grid[gridIdx][i] = (char)(grid[gridIdx][i] ^ grid[gridIdx][j]);
            grid[gridIdx][j] = (char)(grid[gridIdx][i] ^ grid[gridIdx][j]);
            grid[gridIdx][i] = (char)(grid[gridIdx][i] ^ grid[gridIdx][j]);
        }
    }

    public static void tilt(char[][] grid, Direction dir, int gridIdx, int swapIdx, int idx){
        if((idx == grid.length && dir == Direction.NORTH) 
                    || (idx == -1 && dir == Direction.SOUTH)
                    || (idx == grid[0].length && dir == Direction.WEST) 
                    || (idx == -1 && dir == Direction.EAST)) 
            return;
            
        int row = dir == Direction.NORTH || dir == Direction.SOUTH ? idx : gridIdx;
        int col = row == idx ? gridIdx : idx;

        if(grid[row][col] == '#') tilt(grid, dir, gridIdx, NO_SWAP, dir == Direction.NORTH || dir == Direction.WEST ? idx+1 : idx-1);
        else if(grid[row][col] == '.') tilt(grid, dir, gridIdx, swapIdx == NO_SWAP ? idx : swapIdx, dir == Direction.NORTH || dir == Direction.WEST ? idx+1 : idx-1);
        else { // found a rock
            if(swapIdx != NO_SWAP){
                swapGrid(grid, dir, gridIdx, swapIdx, idx);
                tilt(grid, dir, gridIdx, NO_SWAP, dir == Direction.NORTH || dir == Direction.WEST ? swapIdx+1 : swapIdx-1);
            } else tilt(grid, dir, gridIdx, NO_SWAP, dir == Direction.NORTH || dir == Direction.WEST ? idx+1 : idx-1);
        }
    }

    public static void spinCycle(char[][] grid){
        for(int i=0; i<grid[0].length; ++i) tilt(grid, Direction.NORTH, i, NO_SWAP, 0);
        for(int i=0; i<grid[0].length; ++i) tilt(grid, Direction.WEST, i, NO_SWAP, 0);
        for(int i=0; i<grid[0].length; ++i) tilt(grid, Direction.SOUTH, i, NO_SWAP, grid.length-1);
        for(int i=0; i<grid[0].length; ++i) tilt(grid, Direction.EAST, i, NO_SWAP, grid[0].length-1);
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        char[][] gridp1 = new char[lines.size()][lines.get(0).length()];
        char[][] grid = new char[lines.size()][lines.get(0).length()];
        for(int i=0; i<lines.size(); ++i){
            for(int j=0; j<lines.get(i).length(); ++j){
                grid[i][j] = lines.get(i).charAt(j);
                gridp1[i][j] = lines.get(i).charAt(j);
            } 
        }

        int partOneSum = 0;
        int partTwoSum = 0;

        long loopStart = -1, loopEnd = -1;
        long numSpins = 1000000000L;

        for(long i=0; i<numSpins; ++i){
            memo.put(stringify(grid), i);
            spinCycle(grid);
            String str = stringify(grid);
            if(memo.containsKey(str)){
                loopStart = memo.get(str);
                loopEnd = i;
                break;
            }
        }

        long numSpinsLeft = (numSpins - loopStart) % (loopEnd - loopStart + 1);

        for(long i=0; i<numSpinsLeft; ++i) spinCycle(grid);

        for(int i=0; i<gridp1[0].length; ++i) tilt(gridp1, Direction.NORTH, i, NO_SWAP, 0);
        for(int i=0; i<gridp1.length; ++i) for(int j=0; j<gridp1[0].length; ++j) if(gridp1[i][j] == 'O') partOneSum += grid.length - i;
        for(int i=0; i<grid.length; ++i) for(int j=0; j<grid[0].length; ++j) if(grid[i][j] == 'O') partTwoSum += grid.length - i;

        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: " + partTwoSum);
    }
}
