import java.util.*;
import java.io.*;

public class q6{


    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }


        String timesStr = lines.get(0);
        String recordDistsStr = lines.get(1);

        List<Integer> times = new ArrayList<>();
        List<Integer> recordDists = new ArrayList<>();

        for(String time : timesStr.trim().substring(timesStr.indexOf(":")+1).trim().split(" ")){
            if(time.equals("")) continue;
            times.add(Integer.parseInt(time.trim()));
        }

        for(String dist : recordDistsStr.trim().substring(recordDistsStr.indexOf(":")+1).trim().split(" ")){
            if(dist.equals("")) continue;
            recordDists.add(Integer.parseInt(dist.trim()));
        }


        // Part 1
        int totalProd = 1;
        for(int i=0; i<times.size(); ++i){
            int num = 0;
            for(int j=0; j<times.get(i); ++j){
                if(j*(times.get(i) - j) > recordDists.get(i)) num++;
            }
            totalProd *= num;
        }

        // Part 2
        double totalTime = Long.parseLong(times.stream().map(e -> Integer.toString(e)).reduce((e1, e2) -> e1+e2).orElse("0"));
        double totalDist = Long.parseLong(recordDists.stream().map(e -> Integer.toString(e)).reduce((e1, e2) -> e1+e2).orElse("0"));

        double x1 = (-totalTime + Math.sqrt(totalTime * totalTime - 4 * totalDist))/(-2); 
        double x2 = (-totalTime - Math.sqrt(totalTime * totalTime - 4 * totalDist))/(-2); 

        System.out.println("Part 1: "+totalProd);
        System.out.println("Part 2: " + (int)(Math.floor(x2) - Math.floor(x1)));
    }
}