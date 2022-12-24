package task.TOTO.Projects;

import task.TOTO.TotoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GenerateUniqueTotoList extends TotoPoint {
    public static void main(String[] args) {

        List<List<Integer>> aList = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        List<Integer> a = new ArrayList<>(List.of(32, 17, 32, 27, 22, 9));
        List<Integer> b = new ArrayList<>(List.of(32, 47, 37, 11, 12, 36));
        List<Integer> c = new ArrayList<>(List.of(32, 36, 5, 34, 14, 46));


        aList.add(a);
        aList.add(b);
        aList.add(c);

        //aList = getFinalListOfNumbers();

        System.out.println();
        for (List<Integer> e : aList) System.out.print(e + " ");

        //System.out.println(matchCheckers(aList, 32));
        System.out.println();
        System.out.println(generateUniqueList(aList));


    }

    // [32, 17, 32, 27, 22, 9] [34, 47, 37, 11, 12, 36] [32, 36, 5, 34, 14, 46] -> 11
    // [0, 0, 0, 0, 0, 0]      [0, 0, 0, 1, 0, 0]       [0, 0, 0, 0, 0, 0]
    // [32, 17, 32, 27, 22, 9] [34, 47, 37, 22, 12, 36] [32, 36, 5, 34, 14, 46]
//    protected static List<List<Integer>> matchCheckers(List<List<Integer>> list, int el) throws IOException {
//        List<List<Integer>> matchers = new ArrayList<>();
//        List<List<Integer>> tmp = new ArrayList<>(list);
//        Random rnd = new Random();
//
//        for (int i = 0; i < list.size(); i++) {
//            List<Integer> matcher = new ArrayList<>();
//            for (int j = 0; j < list.get(i).size(); j++) {
//                int digitA = list.get(i).get(j);
//                if (digitA != el) {
//                    matcher.add(j, 0);
//                } else {
//                    digitA = rnd.nextInt(1, 50);            // Добави на поз. j и премахни от поз. j + 1
//                    list.get(i).add(j, digitA);                                    // Добавя digit на позиция j
//                    list.get(i).remove(j + 1);                               // Премахва 11 от позиция j + 1
//                    for (int k = 0; k < list.get(i).size(); k++) {
//                        if (list.get(i).get(k) == digitA) {
//                            digitA = rnd.nextInt(1, 50);    // k е позоцията на която има съвпадение
//                            list.get(i).add(k, digitA);                            // Добавя digit на позиция k
//                            list.get(i).remove(k + 1);                       // Премахва 11 от позиция k + 1
//
//                            // list.get(i).remove(k);
//                            // list.get(i).add(k, digitA);              // Има същият ефект върху списъка
//                        }
//                    }
//                    matcher.add(j, 1);
//                }
//            }
//
//            matchers.add(i, matcher);
//        }
//
//        System.out.println();
//        for (List<Integer> e : matchers) System.out.print(e + " ");
//        System.out.println();
////        System.out.println();
////        for (List<Integer> e : list) System.out.print(e + " ");
//
//        return list;
//    }

    // [32, 17, 32, 27, 22, 9]  [32, 47, 37, 11, 12, 36]  [32, 36, 5, 34, 14, 46]
    //[[32, 17, 32, 27, 22, 9], [42, 40, 24, 32, 15, 31], [7, 48, 29, 13, 22, 13]]

    // Сравнява всеки елемент от 1-я масив със всеки елемент от 2-я - 3-я. В тази последователност.
    // Ако намери съвпадение генерира ново случайно число за този елемент.
    public static List<List<Integer>> generateUniqueList(List<List<Integer>> list) {
        Random rnd = new Random();

        int el1 = 0;
        for (int i = 1; i < list.size(); i++) {
            for (int j = 0; j < list.get(0).size(); j++) {
                el1 = list.get(0).get(j);
                int el2 = list.get(i).get(j);
                if (el1 == el2) {
                    list.get(i).remove(j);
                    list.get(i).add(j, rnd.nextInt(1, 50));
                    for (int k = 0; k < list.get(i).size(); k++) {          // Търси дали новото се среща в j-я лист.
                        int el3 = list.get(i).get(k);
                        if (j != k && (Objects.equals(list.get(i).get(j), list.get(i).get(k)))) {
                            list.get(i).remove(j);
                            list.get(i).add(j, rnd.nextInt(1, 50));
                        }
                    }
                }
            }
        }
        return list;
    }
}
