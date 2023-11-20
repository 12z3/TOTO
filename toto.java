package dynamicsStructhure;

import java.util.*;

public class toto {
    public static void main(String[] args) {
        List<Integer> officialResult = new ArrayList<>(List.of(1, 7, 11, 33, 48, 49));
        System.out.println();

        System.out.println("Lottery draw:   \n" + officialResult);
        System.out.println("\nYour Choices:");
        List<List<Integer>> result = generateRandomLists(8, 6);

        printResult(result);
        System.out.println("\nMatches:");
        checkMatches(result, officialResult);
    }

    private static void checkMatches(List<List<Integer>> res, List<Integer> arr) {
        for (List<Integer> re : res) checkResults(arr, re);
    }

    private static void checkResults(List<Integer> a, List<Integer> b) {
        int cnt = 0;
        List<Integer> tmp = new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (Objects.equals(a.get(i), b.get(j))) {
                    tmp.add(a.get(i));
                    cnt++;
                }
            }
        }
        System.out.println("cnt = " + cnt + ": " + tmp);
    }

    private static void printResult(List<List<Integer>> res) {
        int idx = 1;
        for (List<Integer> re : res) System.out.println(idx++ + ": " + re);
    }

    private static List<List<Integer>> generateRandomLists(int choices, int counting) {
        List<List<Integer>> resList = new ArrayList<>();
        Set<Integer> addedDigit = new HashSet<>();
        List<Integer> result = null;

        Random rnd = new Random();
        for (int i = 0; i < choices; i++) {
            Set<Integer> tmp = new HashSet<>();
            for (int j = 0; j < counting; j++) {
                int newEl = rnd.nextInt(1, 50);
                if (!addedDigit.contains(newEl) && !tmp.contains(newEl)) {
                    tmp.add(newEl);
                    addedDigit.add(newEl);                   // Всички генерирани до тук числа.
                } else {
                    j--;
                }
            }
            result = new ArrayList<>(tmp);
            Collections.sort(result);
            resList.add(result);
        }
        return resList;
    }
}
