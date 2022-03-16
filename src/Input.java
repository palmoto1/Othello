import java.util.HashMap;
import java.util.Scanner;

public class Input {

    Scanner scanner;
    HashMap<String, Integer> columnID;

    public Input(){
        scanner = new Scanner(System.in);
        columnID = new HashMap<>();

        columnID.put("A", 0);
        columnID.put("B", 1);
        columnID.put("C", 2);
        columnID.put("D", 3);
        columnID.put("E", 4);
        columnID.put("F", 5);
        columnID.put("G", 6);
        columnID.put("H", 7);

    }

    public int nextInt() {
        String in = scanner.nextLine();
        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int nextChar() {
        String in = scanner.nextLine().toUpperCase();
        if (isColumnID(in))
            return columnID.get(in);
        return -1;

    }



    private boolean isColumnID(String in){
        return columnID.containsKey(in);
    }

    public void close(){
        scanner.close();
    }

}
