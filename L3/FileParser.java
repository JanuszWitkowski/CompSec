import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileParser {

    public static ArrayList<ArrayList<String>> readFile (String filename) {
        ArrayList<ArrayList<String>> cryptograms = null;
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            cryptograms = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                if (data.length() > 0) {
                    if (data.charAt(0) == '0' || data.charAt(0) == '1') {
                        cryptograms.add(cryptogramToStringArray(data));
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Nie znaleziono pliku " + filename);
            e.printStackTrace();
        }
        return cryptograms;
    }

    public static ArrayList<String> cryptogramToStringArray (String cryptogram) {
        ArrayList<String> array = new ArrayList<>();
        Scanner scanner = new Scanner(cryptogram);
        while (scanner.hasNext()) {
            array.add(scanner.next());
        }
//        System.out.println(array);
        return array;
    }
}
