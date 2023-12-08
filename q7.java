import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q7 {

    public static void insertOrdered(List<Integer> idxLst, int idx, List<String> lines, HashMap<Character, Integer> cardVals){
        String toInsert = lines.get(idx).trim().split(" ")[0];

        if(idxLst.isEmpty()){
            idxLst.add(idx);
            return;
        }

        for(int i=0; i<idxLst.size(); ++i){
            String lookingAt = lines.get(idxLst.get(i)).trim().split(" ")[0];
            for(int card=0; card<toInsert.length(); ++card){
                if(cardVals.get(lookingAt.charAt(card)) < cardVals.get(toInsert.charAt(card))){
                    idxLst.add(i, idx);
                    return;
                } else if(cardVals.get(lookingAt.charAt(card)) == cardVals.get(toInsert.charAt(card))) continue;
                else break;
            }
        }

        idxLst.add(idx);
    }

    public static void partOne(String fileName) throws FileNotFoundException{
        Scanner s = new Scanner(new File(fileName));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        final HashMap<Character, Integer> cardVals = new HashMap<>();
        cardVals.put('2', 0);
        cardVals.put('3', 1);
        cardVals.put('4', 2);
        cardVals.put('5', 3);
        cardVals.put('6', 4);
        cardVals.put('7', 5);
        cardVals.put('8', 6);
        cardVals.put('9', 7);
        cardVals.put('T', 8);
        cardVals.put('J', 9);
        cardVals.put('Q', 10);
        cardVals.put('K', 11);
        cardVals.put('A', 12);

        ArrayList<ArrayList<Integer>> handsRankedIdx = new ArrayList<>(7);
        for(int i=0; i<7; ++i) handsRankedIdx.add(new ArrayList<>());

        int idx = 0;
        for(String line : lines){
            HashMap<Character, Integer> charNum = new HashMap<>();

            String cards = line.trim().split(" ")[0];

            for(char c : cards.toCharArray()){
                charNum.put(c, charNum.getOrDefault(c, 0) + 1);
            }

            List<Integer> cardNumsOrdered = new ArrayList<>(charNum.values());
            Collections.sort(cardNumsOrdered, Collections.reverseOrder());

            if(cardNumsOrdered.get(0) == 5) insertOrdered(handsRankedIdx.get(0), idx, lines, cardVals); // 5 of a kind
            else if(cardNumsOrdered.get(0) == 4) insertOrdered(handsRankedIdx.get(1), idx, lines, cardVals); // 4 of a kind
            else if(cardNumsOrdered.get(0) == 3){
                if(cardNumsOrdered.get(1) == 2){
                    insertOrdered(handsRankedIdx.get(2), idx, lines, cardVals); // full house
                } else {
                    insertOrdered(handsRankedIdx.get(3), idx, lines, cardVals); // 3 of a kind
                }
            } else if(cardNumsOrdered.get(0) == 2){
                if(cardNumsOrdered.get(1) == 2){
                    insertOrdered(handsRankedIdx.get(4), idx, lines, cardVals); // 2 pair
                } else {
                    insertOrdered(handsRankedIdx.get(5), idx, lines, cardVals); // 2 of a kind
                }
            } else insertOrdered(handsRankedIdx.get(6), idx, lines, cardVals);

            idx++;
        }

        int curNum = lines.size();
        long totalPts = 0;

        for(List<Integer> handIdxs : handsRankedIdx){
            for(int i : handIdxs){
                totalPts += Integer.parseInt(lines.get(i).trim().split(" ")[1]) * curNum--;
            }
        }

        System.out.println("Part 1: "+totalPts); // 251806792  
    }

    public static void partTwo(String fileName) throws FileNotFoundException{
        Scanner s = new Scanner(new File(fileName));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        final HashMap<Character, Integer> cardVals = new HashMap<>();
        cardVals.put('2', 0);
        cardVals.put('3', 1);
        cardVals.put('4', 2);
        cardVals.put('5', 3);
        cardVals.put('6', 4);
        cardVals.put('7', 5);
        cardVals.put('8', 6);
        cardVals.put('9', 7);
        cardVals.put('T', 8);
        cardVals.put('J', -1);
        cardVals.put('Q', 10);
        cardVals.put('K', 11);
        cardVals.put('A', 12);

        ArrayList<ArrayList<Integer>> handsRankedIdx = new ArrayList<>(7);
        for(int i=0; i<7; ++i) handsRankedIdx.add(new ArrayList<>());

        int idx = 0;
        for(String line : lines){
            HashMap<Character, Integer> charNum = new HashMap<>();

            String cards = line.trim().split(" ")[0];

            for(char c : cards.toCharArray()){
                charNum.put(c, charNum.getOrDefault(c, 0) + 1);
            }

            char bestChar = (char)charNum.keySet().toArray()[0];
            for(char c : charNum.keySet()) if(c != 'J' && charNum.get(c) > charNum.get(bestChar)) bestChar = c;

            if(!cards.equals("JJJJJ")){
                charNum.put(bestChar, charNum.get(bestChar) + charNum.getOrDefault('J', 0));
                charNum.remove('J');
            }

            List<Integer> cardNumsOrdered = new ArrayList<>(charNum.values());
            Collections.sort(cardNumsOrdered, Collections.reverseOrder());

            if(cardNumsOrdered.get(0) == 5) insertOrdered(handsRankedIdx.get(0), idx, lines, cardVals); // 5 of a kind
            else if(cardNumsOrdered.get(0) == 4) insertOrdered(handsRankedIdx.get(1), idx, lines, cardVals); // 4 of a kind
            else if(cardNumsOrdered.get(0) == 3){
                if(cardNumsOrdered.get(1) == 2){
                    insertOrdered(handsRankedIdx.get(2), idx, lines, cardVals); // full house
                } else {
                    insertOrdered(handsRankedIdx.get(3), idx, lines, cardVals); // 3 of a kind
                }
            } else if(cardNumsOrdered.get(0) == 2){
                if(cardNumsOrdered.get(1) == 2){
                    insertOrdered(handsRankedIdx.get(4), idx, lines, cardVals); // 2 pair
                } else {
                    insertOrdered(handsRankedIdx.get(5), idx, lines, cardVals); // 2 of a kind
                }
            } else insertOrdered(handsRankedIdx.get(6), idx, lines, cardVals);

            idx++;
        }

        int curNum = lines.size();
        long totalPts = 0;

        for(List<Integer> handIdxs : handsRankedIdx){
            for(int i : handIdxs){
                totalPts += Integer.parseInt(lines.get(i).trim().split(" ")[1]) * curNum--;
            }
        }

        System.out.println("Part 2: " + totalPts);    
    }
    public static void main(String[] args) throws FileNotFoundException{
        partOne("in.txt");
        partTwo("in.txt");
    }
}
