import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q8 {

    // Reason for sourcing: I was lazy and didn't want to write this math code myself...
    private static long gcd(long a, long b){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        while (b > 0){
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static long gcd(long[] input){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = gcd(result, input[i]);
        return result;
    }

    private static long lcm(long a, long b){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        return a * b / gcd(a, b);
    }

    private static long lcm(long[] input){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }



    public static boolean allEndInZ(List<String> nodes){
        for(String node : nodes) if(node.charAt(2) != 'Z') return false;
        return true;
    }

    public static boolean allTrue(boolean[] bools){
        for(boolean bool: bools) if(!bool) return false;
        return true;
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        String instructions = lines.get(0);

        HashMap<String, String> rightPath = new HashMap<>();
        HashMap<String, String> leftPath = new HashMap<>();

        List<String> curNodes = new ArrayList<>();

        for(int i=2; i<lines.size(); ++i){
            String line = lines.get(i);
            String[] nodeAndDest = line.split(" = ");
            String[] leftRightDest = nodeAndDest[1].substring(1, nodeAndDest[1].length()-1).split(", ");

            leftPath.put(nodeAndDest[0], leftRightDest[0]);
            rightPath.put(nodeAndDest[0], leftRightDest[1]);

            if(nodeAndDest[0].charAt(2) == 'A') curNodes.add(nodeAndDest[0]);
        }

        // Part 1
        String curNode = "AAA";
        int curInstIdx = 0;
        int numMoves = 0;
        while(!curNode.equals("ZZZ")){
            if(instructions.charAt(curInstIdx++) == 'R'){
                curNode = rightPath.get(curNode);
            } else {
                curNode = leftPath.get(curNode);
            }

            if(curInstIdx >= instructions.length()) curInstIdx = 0;

            numMoves++;
        }


        List<Set<String>> curPaths = new ArrayList<>();
        long[] curPathCosts = new long[curNodes.size()];
        boolean[] finishedPath = new boolean[curNodes.size()];
        for(String node : curNodes){
            Set<String> path = new HashSet<>();
            path.add(node);
            curPaths.add(path);
        }

        // Part 2
        curInstIdx = 0;
        while(!allTrue(finishedPath)){
            for(int i=0; i<curNodes.size(); ++i){
                HashMap<String, String> mapPathToTake;

                if(instructions.charAt(curInstIdx) == 'R'){
                    mapPathToTake = rightPath;
                } else {
                    mapPathToTake = leftPath;
                }

                curNode = curNodes.remove(i);
                String nextNode = mapPathToTake.get(curNode);  
                curNodes.add(i, nextNode);

                curPaths.get(i).add(curNode);

                if(curNode.charAt(2) == 'Z'){ // Path loop
                    finishedPath[i] = true;
                }

                if(!finishedPath[i]) curPathCosts[i]++;

            }

            curInstIdx++;

            if(curInstIdx >= instructions.length()) curInstIdx = 0;
        }

        System.out.println("Part 1: "+numMoves);
        System.out.println("Part 2: " + lcm(curPathCosts));
    }
}
//16187743689077