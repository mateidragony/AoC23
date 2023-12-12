import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q12 {

    public static HashMap<String, Long> memo = new HashMap<>();

    public static boolean validStr(String str, int[] groups){
        int curGroupLen = 0;
        int curGroupIdx = 0;

        // First, go through and count up all the #
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);

            if(c=='#') {

                if(curGroupIdx == groups.length) return false;

                curGroupLen++;

                if(curGroupLen == groups[curGroupIdx]){
                    if(i == str.length()-1 || str.charAt(i+1) == '.'){
                        curGroupIdx++;
                        curGroupLen = 0;
                    } else return false;
                }
            } else if(c == '.'){
                if(curGroupLen > 0) return false;
            }
        }

        return curGroupIdx == groups.length;
    }

    public static int expandUnknowns(String str, int i, int[] groups){
        if(i == str.length()){
            boolean isValid = validStr(str, groups);
            return isValid ? 1 : 0;
        }
        else if(str.charAt(i) != '?') return expandUnknowns(str, i+1, groups);
        else{
            char[] strCharsPeriod = str.toCharArray();
            strCharsPeriod[i] = '.';
            char[] strCharsHash = str.toCharArray();
            strCharsHash[i] = '#';

            return expandUnknowns(new String(strCharsPeriod), i+1, groups) + expandUnknowns(new String(strCharsHash), i+1, groups);
        }
    }

    // Inclusive end
    public static long numWaysCanFit(String str, int[] groupNums, int groupStartIdx, int groupEndIdx, int curStrIdx){

        int strlen = str.length();

        if(groupStartIdx == groupEndIdx) { // I've made all of my groups already

            if(curStrIdx >= strlen) return 1; // I'm outside bounds, but i made all groups (could be 0)

            if(str.substring(curStrIdx).contains("#")) return 0; // wrong
            else return 1;
        }

        int curGroupSz = groupNums[groupStartIdx];

        if(curStrIdx >= strlen)  return 0; // I'm outside the bounds of my string

        if(curStrIdx + curGroupSz > strlen) return 0; // Looking outside bounds


        if(memo.containsKey(str + "|" + Arrays.toString(groupNums) + "|" + groupStartIdx + "|" + groupEndIdx + "|" + curStrIdx)) 
            return memo.get(str + "|" + Arrays.toString(groupNums) + "|" + groupStartIdx + "|" + groupEndIdx + "|" + curStrIdx);

        char charAfterGroup;        
        if(curStrIdx + curGroupSz == strlen) charAfterGroup = '?';
        else charAfterGroup = str.charAt(curStrIdx + curGroupSz);


        long ret;

        String groupTrynaMake = str.substring(curStrIdx, curStrIdx + curGroupSz);
        if(groupTrynaMake.contains(".")){ // Can't make so move directly after period
            if(str.charAt(curStrIdx) == '#') return 0;
            ret = numWaysCanFit(str, groupNums, groupStartIdx, groupEndIdx, curStrIdx +1);

        }

        else if(charAfterGroup == '?' || charAfterGroup == '.'){ // Good, I can create the group
            if(str.charAt(curStrIdx) == '#'){ // I have to create the group and i can't not make it
                ret = numWaysCanFit(str, groupNums, groupStartIdx + 1, groupEndIdx, curStrIdx + curGroupSz + 1);
            } else { // I can either make it, or not make it
                long madeGroup = numWaysCanFit(str, groupNums, groupStartIdx + 1, groupEndIdx, curStrIdx + curGroupSz + 1);
                long notMadeGroup = numWaysCanFit(str, groupNums, groupStartIdx, groupEndIdx, curStrIdx + 1);
                ret = madeGroup + notMadeGroup;
            }
        } else { // Can't create the group
            if (str.charAt(curStrIdx) == '#') return 0;
            else ret = numWaysCanFit(str, groupNums, groupStartIdx, groupEndIdx, curStrIdx + 1);
        }

        memo.put(str + "|" + Arrays.toString(groupNums) + "|" + groupStartIdx + "|" + groupEndIdx + "|" + curStrIdx, ret);

        return ret;
    }


    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        int partOneSum = 0;
        long partTwoSum = 0;

        for(String line : lines){
            String[] data = line.split(" ");
            int[] groups = Arrays.stream(data[1].split(",")).mapToInt(Integer::parseInt).toArray();
            String springs = data[0];

            partOneSum += expandUnknowns(springs, 0, groups);


            String newSprings = (springs+"?").repeat(5);
            newSprings = newSprings.substring(0, newSprings.length()-1);
            int[] newGroups = new int[groups.length * 5];
            for(int i=0; i<newGroups.length; ++i) newGroups[i] = groups[i % groups.length];

            System.out.println(newSprings);
            System.out.println(Arrays.toString(newGroups));

            partTwoSum += numWaysCanFit(newSprings, newGroups, 0, newGroups.length, 0);

        }




        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: " + partTwoSum);

        // String str = "??#?.?#?#???#?#??";
        // int[] groups = {1,11};
        // System.out.println(str);
        // System.out.println(numWaysCanFit(str, groups, 0, groups.length, 0));

        // str = "??????##????";
        // groups = new int[]{2,1,3};
        // System.out.println(str);
        // System.out.println(numWaysCanFit(str, groups, 0, groups.length, 0));

        // str = "???.###";
        // groups = new int[]{1,1,3};
        // System.out.println(str);
        // System.out.println(numWaysCanFit(str, groups, 0, groups.length, 0));

        // str = "?###????????";
        // groups = new int[]{3,2,1};
        // System.out.println(str);
        // System.out.println(numWaysCanFit(str, groups, 0, groups.length, 0));

    }
}


// 415861673995012
// 399009278701389