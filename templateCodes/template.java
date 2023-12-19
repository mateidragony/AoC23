import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.*;


public class template {
    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        System.out.println("Part 1: ");
        System.out.println("Part 2: ");
    }
}
