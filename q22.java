import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Stream;

public class q22 {

    public static final String filename = "in.txt";
    public static final int X = 0, Y = 1, Z = 2;


    public static void printGrids(char[][] xGrid, char[][] yGrid){ // debugging
        System.out.println(" X\t Y");
        System.out.println("-----------");
        for(int i=xGrid.length-1; i>=0; --i){
            char[] xRow = xGrid[i];
            for(char c : xRow) System.out.print(c);
            System.out.print("\t");
            char[] yRow = yGrid[i];
            for(char c : yRow) System.out.print(c);
            
            System.out.println();
        }
    }

    public static void initGrids(char[][] xGrid, char[][] yGrid, int maxZ, List<Brick> bricks){ // debugging
        for(int i=0; i<maxZ; ++i){
            Arrays.fill(xGrid[i], '.');
            Arrays.fill(yGrid[i], '.');
        }

        for(Brick b : bricks){
            // X grid
            for(int i=b.coords1[Z]; i<=b.coords2[Z]; ++i){
                for(int j=b.coords1[X]; j<=b.coords2[X]; ++j){
                    if(xGrid[i][j] == '.') xGrid[i][j] = (char)b.name;
                    else xGrid[i][j] = '?';
                    
                }
            }

            // Y grid
            for(int i=b.coords1[Z]; i<=b.coords2[Z]; ++i){
                for(int j=b.coords1[Y]; j<=b.coords2[Y]; ++j){
                    if(yGrid[i][j] == '.') yGrid[i][j] = (char)b.name;
                    else yGrid[i][j] = '?';
                }
            }
        }
    }

    public static int whatIfIACheekyLittleBuggerDisintegratedThisCheekyLittleBrick(
                                        Brick startBrick, 
                                        HashMap<Brick, List<Brick>> supportBricks,  
                                        HashMap<Brick, List<Brick>> bricksSupportedBy,
                                        HashMap<HashSet<Brick>, Integer> memo){

        PriorityQueue<Brick> toRemove = new PriorityQueue<>(Comparator.comparingInt(e -> e.coords1[Z]));
        toRemove.add(startBrick);

        HashSet<Brick> bricksRemovedSoFar = new HashSet<>();

        int count = 0;

        while(!toRemove.isEmpty()){

            Brick b = toRemove.poll();
            bricksRemovedSoFar.add(b);

            for(Brick supported : bricksSupportedBy.get(b)){
                if(supportBricks.get(supported).stream().filter(e -> !bricksRemovedSoFar.contains(e)).count() == 0){
                    toRemove.add(supported);
                    count++;
                }
            }
        }

        return count;
    }

    public static List<Brick> getSupportBricks(Brick b, List<Brick> bricksBelow){
        // I think that X and Y here doesn't matter (Source: copium) (Edit: I was wrong) (Edit 2: I was wrong everywhere holy shit)
        return bricksBelow.stream().filter(e -> bricksIntersect(e, b, X) && bricksIntersect(e, b, Y)).toList();
    }

    public static List<Brick> getBricksSupportedBy(Brick b, List<Brick> bricksAbove){
        return bricksAbove.stream().filter(e -> bricksIntersect(e, b, X) && bricksIntersect(e, b, Y)).toList();
    }

    public static HashMap<Integer, List<Brick>> getBricksAtAllZ(List<Brick> bricks, boolean bottomZ){
        HashMap<Integer, List<Brick>> bricksHM = new HashMap<>();

        for(Brick b : bricks){
            int z = bottomZ ? b.coords1[Z] : b.coords2[Z];
            bricksHM.putIfAbsent(z, new ArrayList<>());
            bricksHM.get(z).add(b);
        }

        return bricksHM;
    }

    public static boolean bricksIntersect(Brick b1, Brick b2, int coordIndex){
        // If one rectangle is on left side of other

        int b1p1 = b1.coords1[coordIndex];
        int b1p2 = b1.coords2[coordIndex];
        int b2p1 = b2.coords1[coordIndex];
        int b2p2 = b2.coords2[coordIndex];

        if (b1p1 > b2p2 || b2p1 > b1p2)
            return false;

        return true;
    }

    public static void moveBrickDown(Brick  b, HashMap<Integer, List<Brick>> bricksTopZ, int coordIndex, List<Brick> ignoredBricks){        
        // coord index decides x or y directions move down
        int zLookingAt = b.coords1[Z]-1;
        // z is determined by coords1. If the brick is angeled up to down, coords2 z will be updated accordingly
        int coordsZDiff = b.coords2[Z] - b.coords1[Z];

        Brick brickBelow = null;

        while(zLookingAt >= 1){
            List<Brick> bricks = bricksTopZ.getOrDefault(zLookingAt, new ArrayList<>());
            if(bricks == null) continue;
            for(Brick bBelow : bricks){
                if(bricksIntersect(b, bBelow, X) && bricksIntersect(b, bBelow, Y) && !ignoredBricks.contains(bBelow)){
                    brickBelow = bBelow;
                    // brick goes right above the bottomOne
                    b.coords1[Z] = brickBelow.coords2[Z] + 1;
                    b.coords2[Z] = b.coords1[Z] + coordsZDiff;
                    return;
                }
            }
            zLookingAt--;
        }

        // No block below me
        b.coords1[Z] = 1;
        b.coords2[Z] = b.coords1[Z] + coordsZDiff;
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

        int maxX = 0, maxY = 0, maxZ = 0;
        List<Brick> bricks = new ArrayList<>(lines.size());
        HashSet<Brick> removableBricks = new HashSet<>();
        HashSet<Brick> killerBricks = new HashSet<>();
        
        char curName = '\0';
        for(String line : lines){
            int[] coords1 = Arrays.stream(line.split("~")[0].split(",")).mapToInt(e -> Integer.parseInt(e)).toArray();
            int[] coords2 = Arrays.stream(line.split("~")[1].split(",")).mapToInt(e -> Integer.parseInt(e)).toArray();
            Brick b = new Brick(curName++, coords1, coords2);
            bricks.add(b);
            removableBricks.add(b);
            killerBricks.add(b);

            maxX = Math.max(maxX, Math.max(coords1[X], coords2[X]));
            maxY = Math.max(maxY, Math.max(coords1[Y], coords2[Y]));
            maxZ = Math.max(maxZ, Math.max(coords1[Z], coords2[Z]));
        }

        maxX++; maxY++; maxZ++;

        char[][] xGrid = new char[maxZ][maxX];
        char[][] yGrid = new char[maxZ][maxY];

        // Collections.sort(bricks, Comparator.comparingInt(e -> e.coords1[Z]));

        // initGrids(xGrid, yGrid, maxZ, bricks);
        // printGrids(xGrid, yGrid);

        HashMap<Integer, List<Brick>> bricksZ1;
        HashMap<Integer, List<Brick>> bricksZ2;
        
        // Drop them in the X direction
        bricksZ1 = getBricksAtAllZ(bricks, true);
        bricksZ2 = getBricksAtAllZ(bricks, false);
        for(Brick b : bricks){
            bricksZ1.get(b.coords1[Z]).remove(b);
            bricksZ2.get(b.coords2[Z]).remove(b);
            moveBrickDown(b, bricksZ2, X, bricksZ1.getOrDefault(b.coords1[Z], new ArrayList<>()));
            bricksZ1.putIfAbsent(b.coords1[Z], new ArrayList<>());
            bricksZ2.putIfAbsent(b.coords2[Z], new ArrayList<>());
            bricksZ1.get(b.coords1[Z]).add(b);
            bricksZ2.get(b.coords2[Z]).add(b);
        }
        Collections.sort(bricks, Comparator.comparingInt(e -> e.coords1[Z]));

        // Drop them in the Y direction
        bricksZ1 = getBricksAtAllZ(bricks, true);
        bricksZ2 = getBricksAtAllZ(bricks, false);
        for(Brick b : bricks){
            bricksZ1.get(b.coords1[Z]).remove(b);
            bricksZ2.get(b.coords2[Z]).remove(b);
            moveBrickDown(b, bricksZ2, Y, bricksZ1.getOrDefault(b.coords1[Z], new ArrayList<>()));
            bricksZ1.putIfAbsent(b.coords1[Z], new ArrayList<>());
            bricksZ2.putIfAbsent(b.coords2[Z], new ArrayList<>());
            bricksZ1.get(b.coords1[Z]).add(b);
            bricksZ2.get(b.coords2[Z]).add(b);
        }
        Collections.sort(bricks, Comparator.comparingInt(e -> e.coords1[Z]));


        // System.out.println("----DROPPED BOTH----");
        // initGrids(xGrid, yGrid, maxZ, bricks);
        // System.out.println();
        // printGrids(xGrid, yGrid);
        // System.out.println();

        int partOneSum = 0;
        bricksZ1 = getBricksAtAllZ(bricks, true);
        bricksZ2 = getBricksAtAllZ(bricks, false);

        HashMap<Brick, List<Brick>> supportBricks = new HashMap<>();
        HashMap<Brick, List<Brick>> bricksSupportedBy = new HashMap<>();

        for(Brick b : bricks){
            List<Brick> supportBricksList = getSupportBricks(b, bricksZ2.getOrDefault(b.coords1[Z]-1, new ArrayList<>()));
            // System.out.println((char)b.name + " " + supportBricks);
            if(supportBricksList.size() == 1) removableBricks.remove(supportBricksList.get(0)); // I can't remove it
            else if(supportBricksList.size() == 0) supportBricksList = Stream.of(new Brick(-1, new int[]{0}, new int[]{0})).toList(); // supported by the ground
            supportBricks.put(b, supportBricksList);
            bricksSupportedBy.put(b, getBricksSupportedBy(b, bricksZ1.getOrDefault(b.coords2[Z]+1, new ArrayList<>())));
        }
        partOneSum = removableBricks.size();
     
        // --------------------
        //      Part Two
        // --------------------

        killerBricks.removeAll(removableBricks); // all the bricks that will cause some damage

        HashMap<HashSet<Brick>, Integer> removedBricksMemo = new HashMap<>();
        
        long partTwoSum = 0;
        for(Brick b : killerBricks){
            partTwoSum += whatIfIACheekyLittleBuggerDisintegratedThisCheekyLittleBrick(b, supportBricks, bricksSupportedBy, removedBricksMemo);
        }


        System.out.println("Part 1: "+partOneSum);
        System.out.println("Part 2: "+partTwoSum);
    }

    private static class Brick{
        int name; // for testing
        int[] coords1;
        int[] coords2;

        public Brick(int name, int[] coords1, int[] coords2) {
            this.name = name;
            
            // coords1 is the smaller one
            for(int i=0; i<coords1.length; ++i){
                if(coords1[i] != coords2[i]){
                    if(coords1[i] < coords2[i]){
                        this.coords1 = coords1;
                        this.coords2 = coords2;
                    } else {
                        this.coords2 = coords1;
                        this.coords1 = coords2;
                    }
                }
            }

            if(this.coords1 == null){ // 1x1 brick
                this.coords1 = coords1;
                this.coords2 = coords2;
            }
            

        }

        

        @Override
        public String toString() {
            return "[" + name + "]";
        }



        @Override // generated
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + name;
            return result;
        }

        @Override // generated
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Brick other = (Brick) obj;
            return name == other.name;
        }
    }
}

// 777
// 794
// 807

// 471!

/*
 * 
 * 
 * 

0,0,2~0,0,3
1,0,2~1,0,4
2,0,2~2,0,2
0,0,5~2,0,5

0,0,2~0,0,2
3,0,2~3,0,2
4,0,2~5,0,2
0,0,4~4,0,4
5,0,5~6,0,5
4,0,6~5,0,6
6,1,4~6,1,4

 */