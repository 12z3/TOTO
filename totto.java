package dynamicsStructhure;

import java.util.*;

public class totto {
    public static void main(String[] args) {
        int[] a = {1, 7, 11, 33, 48, 49};
        System.out.println("   " + Arrays.toString(a));
        List<List<Integer>> result = generateRandomList();

        printResult(result);
        //System.out.println(result);
        System.out.println();
        checkMatches(result, a);
        //System.out.println("   " + checkMatches(result, a));
    }

    private static void checkMatches(List<List<Integer>> res, int[] arr) {
        List<List<Integer>> matches = new ArrayList<>();
        List<Integer> tmp = null;
        int idx = 1;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < res.size(); j++) {
                tmp = new ArrayList<>();
                for (int k = 0; k < res.get(j).size(); k++) {
                    if (arr[i] == res.get(j).get(k)) {
                        tmp.add(res.get(j).get(k));
                        idx = k;
                    }
                }
               if(tmp.size() != 0) System.out.println(++j +": "+tmp);
            }
                matches.add(tmp);
        }
        System.out.println();
//        for (int i = 0; i < matches.size(); i++) {
//            System.out.print(" " + matches.get(i));
//        }
    }

    private static void printResult(List<List<Integer>> res) {
        int idx = 1;
        for (List<Integer> item : res) {
            System.out.println(idx++ + ": " + item);
        }
    }

    private static List<List<Integer>> generateRandomList() {
        List<List<Integer>> list = new ArrayList<>();
        Set<Integer> addedDigit = new HashSet<>();
        List<Integer> result = null;

        Random rnd = new Random();
        int choices = 8;
        int counting = 6;


        for (int i = 0; i < choices; i++) {
            Set<Integer> tmp = new HashSet<>();
            for (int j = 0; j < counting; j++) {
                int newEl = rnd.nextInt(1, 50);
                if (!addedDigit.contains(newEl)) {
                    tmp.add(newEl);
                    addedDigit.add(newEl);
                } else {
                    j--;
                }
                result = new ArrayList<>(tmp);
                Collections.sort(result);
            }
            if (!list.contains(result)) list.add(result);
        }
        return list;
    }
}
