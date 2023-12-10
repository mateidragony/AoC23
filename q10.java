import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class q10 {

    public enum Direction{
        LEFT, RIGHT, UP, DOWN;
    }

    public static final int EMPTY = -1, IN1 = -2, IN2 = -3;

    public static void setIns(int[][] dists, int row, int col, Direction dir, int rows, int cols, int pathIdx, char curChar){


        int in1 = pathIdx == 0 ? IN1 : IN2;
        int in2 = pathIdx == 0 ? IN2 : IN1;

        if(dir == Direction.UP){

            if(col != cols-1) dists[row][col+1] = dists[row][col+1] == EMPTY ? in1 : dists[row][col+1];
            if(col != 0) dists[row][col-1] = dists[row][col-1] == EMPTY ? in2 : dists[row][col-1];

            //behin
            if(curChar == 'J' && row != rows-1) dists[row+1][col] = dists[row+1][col] == EMPTY ? in1 : dists[row+1][col];
            if(curChar == 'L' && row != rows-1) dists[row+1][col] = dists[row+1][col] == EMPTY ? in2 : dists[row+1][col];
        } else if(dir == Direction.DOWN){
            if(col != 0) dists[row][col-1] = dists[row][col-1] == EMPTY ? in1 : dists[row][col-1];
            if(col != cols-1) dists[row][col+1] = dists[row][col+1] == EMPTY ? in2 : dists[row][col+1];

            //behind
            if(curChar == '7' && row != 0) dists[row-1][col] = dists[row-1][col] == EMPTY ? in2 : dists[row-1][col];
            if(curChar == 'F' && row != 0) dists[row-1][col] = dists[row-1][col] == EMPTY ? in1 : dists[row-1][col];
        } else if(dir == Direction.RIGHT){
            if(row != rows-1) dists[row+1][col] = dists[row+1][col] == EMPTY ? in1 : dists[row+1][col];
            if(row != 0) dists[row-1][col] = dists[row-1][col] == EMPTY ? in2 : dists[row-1][col];

            //behind
            if(curChar == 'L' && col != 0) dists[row][col-1] = dists[row][col-1] == EMPTY ? in1 : dists[row][col-1];
            if(curChar == 'F' && col != 0) dists[row][col-1] = dists[row][col-1] == EMPTY ? in2 : dists[row][col-1];
        } else if(dir == Direction.LEFT){
            if(row != 0) dists[row-1][col] = dists[row-1][col] == EMPTY ? in1 : dists[row-1][col];
            if(row != rows-1) dists[row+1][col] = dists[row+1][col] == EMPTY ? in2 : dists[row+1][col];
            
            // behind
            if(curChar == '7' && col != cols-1) dists[row][col+1] = dists[row][col+1] == EMPTY ? in1 : dists[row][col+1];
            if(curChar == 'J' && col != cols-1) dists[row][col+1] = dists[row][col+1] == EMPTY ? in2 : dists[row][col+1];
        }
    }

    public static int expandIns(int [][] dists, int rows, int cols){
        int numChanged = 0;

        for(int i=0; i<rows; ++i){
            for(int j=0; j<cols; ++j){
                if(dists[i][j] == IN1 || dists[i][j] == IN2){
                    int toSet = dists[i][j];
                    for(int r=Math.max(0, i-1); r<=Math.min(rows-1, i+1); ++r){
                        for(int c=Math.max(0, j-1); c<=Math.min(cols-1, j+1); ++c){
                            if(dists[r][c] == EMPTY){
                                dists[r][c] = toSet;
                                numChanged++;
                            }
                        }
                    }

                }
            }
        }

        return numChanged;
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] map = new char[rows][cols];
        int[][] dists = new int[rows][cols];

        for(int[] dist : dists) Arrays.fill(dist, EMPTY);

        int startRow = -1;
        int startCol = -1;

        for(int i=0; i<rows; ++i){
            for(int j=0; j<cols; ++j){
                char c = lines.get(i).charAt(j);
                map[i][j] = c;
                if(c == 'S'){
                    startRow = i;
                    startCol = j;
                }
            }
        }

        dists[startRow][startCol] = 0;

        int[] pathRows = new int[2];
        int[] pathCols = new int[2];
        Direction[] pathDirs = new Direction[2];

        // Set the initial paths
        int startPathIdx = 0;
        // Above
        if(startRow != 0){
            char nextChar = map[startRow-1][startCol];
            if(nextChar == '|'){
                pathRows[startPathIdx] = startRow - 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.UP;
                startPathIdx++;
            } else if (nextChar == 'F'){
                pathRows[startPathIdx] = startRow - 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.RIGHT;
                startPathIdx++;
            } else if(nextChar == '7'){
                pathRows[startPathIdx] = startRow - 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.LEFT;
                startPathIdx++;
            }
        }
        // Below
        if(startRow != rows-1){
            char nextChar = map[startRow+1][startCol];
            if(nextChar == '|'){
                pathRows[startPathIdx] = startRow + 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.DOWN;
                startPathIdx++;
            } else if (nextChar == 'L'){
                pathRows[startPathIdx] = startRow + 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.RIGHT;
                startPathIdx++;
            } else if(nextChar == 'J'){
                pathRows[startPathIdx] = startRow + 1;
                pathCols[startPathIdx] = startCol;
                pathDirs[startPathIdx] = Direction.LEFT;
                startPathIdx++;
            }
        }
        // Left
        if(startCol != 0){
            char nextChar = map[startRow][startCol-1];
            if(nextChar == '-'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol -1;
                pathDirs[startPathIdx] = Direction.LEFT;
                startPathIdx++;
            } else if (nextChar == 'L'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol - 1;
                pathDirs[startPathIdx] = Direction.UP;
                startPathIdx++;
            } else if(nextChar == 'F'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol - 1;
                pathDirs[startPathIdx] = Direction.DOWN;
                startPathIdx++;
            }
        }
        // Right
        if(startCol != cols-1){
            char nextChar = map[startRow][startCol+1];
            if(nextChar == '-'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol +1;
                pathDirs[startPathIdx] = Direction.RIGHT;
                startPathIdx++;
            } else if (nextChar == 'J'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol + 1;
                pathDirs[startPathIdx] = Direction.UP;
                startPathIdx++;
            } else if(nextChar == '7'){
                pathRows[startPathIdx] = startRow;
                pathCols[startPathIdx] = startCol + 1;
                pathDirs[startPathIdx] = Direction.DOWN;
                startPathIdx++;
            }
        }


        int pathCost = 1;

        while(!(pathRows[0] == pathRows[1] && pathCols[0] == pathCols[1])){
            for(int path = 0; path<2; ++path){

                // Update path cost
                dists[pathRows[path]][pathCols[path]] = pathCost;
                if(pathCost == 4339) System.out.println(pathDirs[path] + " -Bef- "+dists[pathRows[path]+1][pathCols[path]]);
                setIns(dists, pathRows[path], pathCols[path], pathDirs[path], rows, cols, path, map[pathRows[path]][pathCols[path]]);

                // Move along path
                if(pathDirs[path] == Direction.UP){
                    
                    pathRows[path]--;
                    char nextChar = map[pathRows[path]][pathCols[path]];

                    if(nextChar == '|'){
                    } else if(nextChar == '7'){
                        pathDirs[path] = Direction.LEFT;
                    } else if(nextChar == 'F'){
                        pathDirs[path] = Direction.RIGHT;
                    } else {
                        System.out.println("Something broke UP");
                        System.exit(-1);
                    }

                } else if(pathDirs[path] == Direction.DOWN){
                    pathRows[path]++;
                    char nextChar = map[pathRows[path]][pathCols[path]];

                    if(nextChar == '|'){
                    } else if(nextChar == 'J'){
                        pathDirs[path] = Direction.LEFT;
                    } else if(nextChar == 'L'){
                        pathDirs[path] = Direction.RIGHT;
                    } else {
                        System.out.println("Something broke DOWN");
                        System.exit(-1);
                    }

                } else if(pathDirs[path] == Direction.LEFT){
                    pathCols[path]--;
                    char nextChar = map[pathRows[path]][pathCols[path]];

                    if(nextChar == '-'){
                    } else if(nextChar == 'F'){
                        pathDirs[path] = Direction.DOWN;
                    } else if(nextChar == 'L'){
                        pathDirs[path] = Direction.UP;
                    } else {
                        System.out.println("Something broke LEFT");
                        System.exit(-1);
                    }

                } else if(pathDirs[path] == Direction.RIGHT){
                    pathCols[path]++;
                    char nextChar = map[pathRows[path]][pathCols[path]];

                    if(nextChar == '-'){
                    } else if(nextChar == 'J'){
                        pathDirs[path] = Direction.UP;
                    } else if(nextChar == '7'){
                        pathDirs[path] = Direction.DOWN;
                    } else {
                        System.out.println("Something broke RIGHT");
                        System.exit(-1);
                    }

                } else {
                    System.out.println("Something broke");
                    System.exit(-1);
                }
            }
            pathCost++;
        }
        dists[pathRows[0]][pathCols[0]] = pathCost;

        int numChanged;
        do{
            numChanged = expandIns(dists, rows, cols);
        } while(numChanged > 0);

        int toCount = dists[0][0] == IN1 ? IN2 : IN1;
        int numIns = 0;
        for(int i=0; i<rows; ++i) for(int j=0; j<cols; ++j) if(dists[i][j] == toCount) numIns++;


        System.out.println("Part 1: " + pathCost);
        System.out.println("Part 2: " + numIns); 
    }
}
