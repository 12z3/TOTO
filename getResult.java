package Jackpot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class getResult {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String data = readFromFile();
        if (!data.equalsIgnoreCase("-1") || data != null) {
            System.out.println(parsToInt(getLastLine(data)));
        }
    }

    static String readFromFile() throws FileNotFoundException {
        File input = new File(
                "/Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/archive.txt");
        if (!input.exists() || input.length() == 0) {
            System.out.println("No such file or directory");
            return "-1";
        }

        System.out.println("Прочетен е файл: " + input.getAbsolutePath());

        Scanner scanner = new Scanner(input);
        String data = null;
        while (scanner.hasNext()) {
            data = scanner.nextLine();
        }
        return data;
    }

    static List<List<String>> getLastLine(String input) {
        List<List<String>> resToStr = new ArrayList<>();
        int fromIdx, toIdx, cnt = 0;
        String line;

        String data = input.substring(input.indexOf("[") + 1, input.lastIndexOf("]"));
        fromIdx = data.indexOf("[") + 1;
        toIdx = data.indexOf("]");

        while (cnt < 3) {
            line = data.substring(fromIdx, toIdx);
            List<String> tmp = new ArrayList<>(List.of(line));
            resToStr.add(tmp);

            fromIdx = (data.indexOf("[", (toIdx))) + 1;
            toIdx = data.indexOf("]", fromIdx);
            cnt++;
        }
        return resToStr;
    }

    static List<List<Integer>> parsToInt(List<List<String>> input) {
        List<List<Integer>> resToInt = new ArrayList<>();
        String[] arr = new String[0];
        List<Integer> tmpInt;

        for (List<String> strings : input) {
            //for (String string : strings) arr = string.trim().split(", ");
            arr = strings.get(0).trim().split(", ");
            tmpInt = new ArrayList<>();
            for (String literal : arr) tmpInt.add(Integer.parseInt(literal));
            resToInt.add(tmpInt);
        }
        return resToInt;
    }
}
