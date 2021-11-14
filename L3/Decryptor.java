import java.util.ArrayList;
import java.util.HashMap;

public class Decryptor {

    private static final String ASCII_SPACE = "00100000";

    public static String charToASCII (char c) {
        StringBuilder ascii = new StringBuilder(Integer.toBinaryString(c));
        while (ascii.length() < 8)
            ascii.insert(0, '0');
        return ascii.toString();
    }

    public static String integerToASCII (int number) {
        StringBuilder ascii = new StringBuilder(Integer.toString(number));
        while (ascii.length() < 8)
            ascii.insert(0, '0');
        return ascii.toString();
    }

    // absolutnie nie jestem z tego dumny xDDD
    public static char asciiToChar (String ascii) {
        int n = ascii.length();
        int value = 0;
        for (int i = 0; i < n; i++) {
            int factor = ascii.charAt(i) == '0' ? 0 : 1;
            value += (int)Math.pow(2.0, n - i - 1) * factor;
        }
        return (char)value;
    }

    public static int calculateMaxSize (ArrayList<ArrayList<String>> cryptograms) {
        int max = 0;
        for (ArrayList<String> cryptogram : cryptograms) {
            if (max < cryptogram.size())
                max = cryptogram.size();
        }
        return max;
    }

    public static String xor (String s1, String s2) {
        StringBuilder xor = new StringBuilder();
        int length = Math.min(s1.length(), s2.length());
        for (int i = 0; i < length; i++)
            xor.append(s1.charAt(i) == s2.charAt(i) ? '0' : '1');
        return xor.toString();
    }

    public static ArrayList<ArrayList<String>> decryptKey(ArrayList<ArrayList<String>> cryptograms) {
        int n = cryptograms.size(), m = calculateMaxSize(cryptograms);
        ArrayList<ArrayList<String>> keys = new ArrayList<>();
        for (int i = 0; i < m; i++)
            keys.add(new ArrayList<>());

        // SPACEBAR EXPLOIT
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int minSize = Math.min(cryptograms.get(i).size(), cryptograms.get(j).size());
                for (int k = 0; k < minSize; k++) {
                    // czy to sprawdzenie w ogole jest potrzebne??
                    String c1 = cryptograms.get(i).get(k);
                    String c2 = cryptograms.get(j).get(k);
                    String messXor = xor(c1, c2);
                    if (messXor.charAt(1) == '1') {
                        String spaceXor = xor(messXor, ASCII_SPACE);
                        String key1 = xor(spaceXor, c1);
                        String key2 = xor(spaceXor, c2);
                        // mozna tutaj zaimplementowac dodatkowe sprawdzenia, zeby udoskonalic algorytm
                        if (keys.get(k).size() == 1) {
                            //
                        }
                        else {
                            keys.get(k).add(key1);
                            keys.get(k).add(key2);
                            for (int p = j + 1; p < n; p++) {
                                if (cryptograms.get(p).size() >= k+1) {
                                    for (int q = p + 1; q < n; q++) {
                                        if (cryptograms.get(q).size() >= k+1) {
                                            String c3 = cryptograms.get(p).get(k);
                                            String c4 = cryptograms.get(q).get(k);
                                            String messXor34 = xor(c3, c4);
                                            if (messXor34.charAt(1) == '1') {
                                                String spaceXor34 = xor(messXor34, ASCII_SPACE);
                                                String key3 = xor(spaceXor34, c3);
                                                String key4 = xor(spaceXor34, c4);
                                                if (keys.get(k).contains(key3)) {
                                                    keys.get(k).clear();
                                                    keys.get(k).add(key3);
//                                                    String key = keys.get(k).get(0);
//                                                    System.out.println("k=" + k + " ._" + asciiToChar(xor(c1, key)) + "_" + asciiToChar(xor(c2, key)) + "_" + asciiToChar(xor(c3, key)) + "_" + asciiToChar(xor(c4, key)) + "_.");
                                                    break;
                                                } else if (keys.get(k).contains(key4)) {
                                                    keys.get(k).clear();
                                                    keys.get(k).add(key4);
//                                                    String key = keys.get(k).get(0);
//                                                    System.out.println("k=" + k + " ._" + asciiToChar(xor(c1, key)) + "_" + asciiToChar(xor(c2, key)) + "_" + asciiToChar(xor(c3, key)) + "_" + asciiToChar(xor(c4, key)) + "_.");
                                                    break;
                                                } else {
                                                    keys.get(k).add(key3);
                                                    keys.get(k).add(key4);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (keys.get(k).size() == 1)
                                    break;
                            }
                        }
                    }
                }
            }
        }

        return keys;
    }

    public static char getMostLikelyLetter (ArrayList<String> cryptogram, ArrayList<ArrayList<String>> keys, int k) {
        HashMap<Character, Integer> letters = new HashMap<>();
        for (int i = 0; i < keys.get(k).size(); i++) {
            char l = asciiToChar(xor(cryptogram.get(k), keys.get(k).get(i)));
            if (!letters.containsKey(l))
                letters.put(l, 0);
            letters.replace(l, letters.get(l) + 1);
        }
        int maxValue = -1;
        char maxKey = '?';
        for (char letter : letters.keySet()) {
            if (maxValue < letters.get(letter)) {
                maxValue = letters.get(letter);
                maxKey = letter;
            }
        }
        return maxKey;
    }

    public static String decryptMessage(ArrayList<String> cryptogram, ArrayList<ArrayList<String>> keys) {
        StringBuilder message = new StringBuilder();
        int minSize = Math.min(cryptogram.size(), keys.size());
        for (int k = 0; k < minSize; k++) {
            if (keys.get(k).size() == 1)
                message.append(asciiToChar(xor(cryptogram.get(k), keys.get(k).get(0))));
            else if (keys.get(k).size() > 1)
                message.append(getMostLikelyLetter(cryptogram, keys, k));
            else message.append('?');
        }
        return message.toString();
    }

    public static void printKeys(ArrayList<ArrayList<String>> keysArray) {
        System.out.println("-----DRUKOWANIE KLUCZY-----");
        for (ArrayList<String> keys : keysArray) {
            System.out.print("[");
            if (keys.size() > 0) {
                for (int i = 0; i < keys.size() - 1; i++)
                    System.out.print(keys.get(i) + ", ");
                System.out.print(keys.get(keys.size() - 1));
            }
            System.out.println("]");
        }
    }

}
