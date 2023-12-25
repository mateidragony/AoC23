import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class q24 {

    public static final String filename = "in.txt";


    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        // --------------------
        //      Part One
        // --------------------

        long minXY = 200000000000000L;
        long maxXY = 400000000000000L;

        // minXY = 7;
        // maxXY = 27;

        List<Hailstone> hailstones = new ArrayList<>();
        for(String line : lines){
            String[] data = line.split(" @ ");
            long[] pos = Arrays.stream(data[0].split(", ")).mapToLong(e -> Long.parseLong(e.trim())).toArray(); 
            long[] vel = Arrays.stream(data[1].split(", ")).mapToLong(e -> Long.parseLong(e.trim())).toArray(); 

            hailstones.add(new Hailstone(pos, vel));
        }

        int partOneSum = 0;

        for(int i=0; i<hailstones.size()-1; ++i){
            for(int j=i+1; j<hailstones.size(); ++j){
                Hailstone h1 = hailstones.get(i);
                Hailstone h2 = hailstones.get(j);

                double timeCrash = h1.getCrashTimeXY(h2);
                double timeCrash2 = h2.getCrashTimeXY(h1);
                

                if(timeCrash >= 0 && timeCrash2 >= 0){
                    double[] crashSite = h1.getCrashXY(timeCrash);
                    boolean inBounds = crashSite[0] >= minXY && crashSite[0] <= maxXY && crashSite[1] >= minXY && crashSite[1] <= maxXY;

                    if(inBounds){
                        partOneSum++;
                    }
                } 
            }
        }

        // --------------------
        //      Part Two
        // --------------------

        // couldn't be bothered tbh
        // this part is literally just math
        // kinda lame ngl

        // --------------------
        //       Answers
        // --------------------        

        System.out.println("Part 1: "+ partOneSum);
        System.out.println("Part 2: ");
    }

    private static class Hailstone{
        long[] pos, vel;

        public Hailstone(long[] pos, long[] vel) {
            this.pos = pos;
            this.vel = vel;
        }

        @Override
        public String toString() {
            return "Hailstone [pos=" + Arrays.toString(pos) + ", vel=" + Arrays.toString(vel) +"]";
        }

        public double getCrashTimeXY(Hailstone h){
            long x1 = pos[0];
            long y1 = pos[1];
            long dx1 = vel[0];
            long dy1 = vel[1];
            
            long x2 = h.pos[0];
            long y2 = h.pos[1];
            long dx2 = h.vel[0];
            long dy2 = h.vel[1];

            double mult = -1.0 * dy2 / dx2;

            double constant = x1 * mult + y1;
            double coeff = dx1 * mult + dy1;

            return (y2 + mult * x2 - constant) / coeff;
        }

        public double[] getCrashXY(double time){
            return new double[]{pos[0] + vel[0] * time, pos[1] + vel[1] * time};
        }
    }
}
