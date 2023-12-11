import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q11 {
    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        List<Long> colIdxNoGalaxy = new ArrayList<>();
        List<Long> rowIdxNoGalaxy = new ArrayList<>();
        List<Position> galaxyPositions = new ArrayList<>();

        for(int i=0; i<lines.size(); ++i){
            String line = lines.get(i);
            for(int j=0; j<line.length(); ++j){
                char c = line.charAt(j);
                if(i == 0 && c == '.') colIdxNoGalaxy.add((long)j);
                if(c == '#' && colIdxNoGalaxy.contains((long)j)) colIdxNoGalaxy.remove(colIdxNoGalaxy.indexOf((long)j));
                if(c == '#') galaxyPositions.add(new Position(i, j));
            }

            if(line.chars().noneMatch(e -> e == '#')) rowIdxNoGalaxy.add((long)i);
        }


        long partOneSum = 0;

        for(int i=0; i<galaxyPositions.size()-1; ++i){
            Position g1 = galaxyPositions.get(i);
            for(int j=i+1; j<galaxyPositions.size(); ++j){
                Position g2 = galaxyPositions.get(j);

                long dist = Math.abs(g1.r - g2.r) + Math.abs(g1.c - g2.c); // Manhattan

                long minCol = Math.min(g1.c, g2.c);
                long maxCol = Math.max(g1.c, g2.c);

                long minRow = Math.min(g1.r, g2.r);
                long maxRow = Math.max(g1.r, g2.r);
                
                // Account for expansion
                dist += colIdxNoGalaxy.stream().filter(e -> e > minCol && e < maxCol).count() * 999999;
                dist += rowIdxNoGalaxy.stream().filter(e -> e > minRow && e < maxRow).count() * 999999;

                partOneSum += dist;
            }
        }

        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: ");
    }


    private static class Position{
        public int r, c;
        public Position(int r, int c){
            this.r = r;
            this.c = c;
        }
    }
}
