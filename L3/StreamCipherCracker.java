import java.util.ArrayList;
import java.util.InputMismatchException;

public class StreamCipherCracker {

    public static void main (String[] args) {
        try {
            if (args.length < 1) throw new InputMismatchException();
            String filename = args[0];
            ArrayList<ArrayList<String>> cryptograms = FileParser.readFile(filename);
            ArrayList<String> goalMessage = cryptograms.get(cryptograms.size() - 1);

            int l = cryptograms.size();
            if (args.length > 1) {
                try {
                    l = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (cryptograms.size() > l)
                cryptograms.subList(l, cryptograms.size()).clear();

            ArrayList<ArrayList<String>> key = Decryptor.decryptKey(cryptograms);
            String message = Decryptor.decryptMessage(goalMessage, key);
            System.out.println(message);
//            for (ArrayList<String> cryptogram : cryptograms) {
//                message = Decryptor.decryptMessage(cryptogram, key);
//                System.out.println(message);
//            }
        }
        catch (InputMismatchException e) {
            System.out.println("BLAD: Nie podano argumentow.");
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            System.out.println("BLAD: Niewlasciwy format danej.");
            e.printStackTrace();
        }
    }
}
