import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q13 {

    public static int getMirrorRow(List<String> grid, int curRow){
        int row = -1;

        for(int i=curRow; i<grid.size()-1; ++i){
            if(grid.get(i).equals(grid.get(i+1))){
                row = i;
                break;
            }
        }

        if(row == -1) return -1;

        for(int i=0; i<=row && row+1+i < grid.size(); ++i){
            if(!grid.get(row-i).equals(grid.get(row+i+1))) return getMirrorRow(grid, row + 1);
        }

        return row;
    }

    public static int countDifferences(String s1, String s2){
        int count = 0;
        for(int i=0; i<s1.length(); ++i) if(s1.charAt(i) != s2.charAt(i)) count++;
        return count;
    }

    public static int getMirrorRowP2(List<String> grid, int curRow){
        boolean smudgeFixed = false;
        int row = -1;

        for(int i=curRow; i<grid.size()-1; ++i){

            if(grid.get(i).equals(grid.get(i+1))){
                row = i;
                break;
            } else if(countDifferences(grid.get(i), grid.get(i+1)) == 1){
                row = i;
                smudgeFixed = true;
                break;
            }
        }

        if(row == -1){
            return -1;
        }

        for(int i=1; i<=row && row+1+i < grid.size(); ++i){
            if((!grid.get(row-i).equals(grid.get(row+i+1)) && smudgeFixed) || countDifferences(grid.get(row-i), grid.get(row+i+1)) > 1) 
                return getMirrorRowP2(grid, row + 1);
            else if(countDifferences(grid.get(row-i), grid.get(row+i+1)) == 1 && !smudgeFixed){
                smudgeFixed = true;
                continue;
            }
        }

        if(!smudgeFixed){
            return getMirrorRowP2(grid, row + 1);
        }
        return row;
    }
    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        List<List<String>> gridRows = new ArrayList<>();
        List<String> grid = new ArrayList<>();
        for(String line : lines){
            if(!line.trim().isEmpty()) grid.add(line);
            else{
                gridRows.add(grid);
                grid = new ArrayList<>();
            }
        }
        gridRows.add(grid);

        List<List<String>> gridCols = new ArrayList<>();
        for(List<String> gridRow : gridRows){        
            grid = new ArrayList<>();
            StringBuilder col = new StringBuilder();
            int w = gridRow.get(0).length();
            int h = gridRow.size();

            for(int i=0; i<w; ++i){
                for(int j=0; j<h; ++j){
                    col.append(gridRow.get(j).charAt(i));
                }
                grid.add(col.toString());
                col = new StringBuilder();
            }
            gridCols.add(grid);
        }

        int numGrids = gridRows.size();
        int partOneSum = 0;
        int partTwoSum = 0;

        for(int i=0; i<numGrids; ++i){
            int mirrorRow = getMirrorRow(gridRows.get(i), 0);
            if(mirrorRow == -1){
                mirrorRow = getMirrorRow(gridCols.get(i), 0);
                partOneSum += (mirrorRow + 1);
            } else {
                partOneSum += (mirrorRow + 1) * 100;
            }

            mirrorRow = getMirrorRowP2(gridRows.get(i), 0);
            if(mirrorRow == -1){
                mirrorRow = getMirrorRowP2(gridCols.get(i), 0);
                partTwoSum += (mirrorRow + 1);
            } else {
                partTwoSum += (mirrorRow + 1) * 100;
            }
            
        }

        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: " + partTwoSum);
    }
}