package task.TOTO;

import training.Methods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TotoPoint extends Methods {
    public static void main(String[] args) {

        //matchCheckers(getFinalListOfNumbers(), 11);
        //writeToFile(matchCheckers(memo,11));

        System.out.println(getFinalListOfNumbers());
    }

    public static List<List<Integer>> getFinalListOfNumbers() {
        List<List<Integer>> tmp = new ArrayList<>();
        int i = 50;
        while (i >= 0) {
            tmp = getListsOfDigits();
            i--;
        }
        for (List<Integer> el: tmp) System.out.print(el + " ");
        System.out.println();
        return tmp;
    }

    private static List<List<Integer>> getListsOfDigits() {
        List<List<Integer>> memo = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            memo.add(generateNumbers());
        }
         //for (List<Integer> el : memo) System.out.print(el + " ");
        for (List<Integer> memoEl: memo){
            Collections.sort(memoEl);
        }
        return memo;
    }

    // Генерира резултат 1 @ count
    private static List<Integer> generateNumbers() {
        List<Integer> numbers = new ArrayList<>();
        int count = 10, step = 0;
        while (count >= 0) {
            numbers = generateDigits();
            count--;
        }
        return numbers;
    }

    // Генерира масив от 6 числа по логика 1 от 49, след което срвнява генерираното с предишните числа.
    // Бягаме от съвпадения.
    private static List<Integer> generateDigits() {                     // Тези трябва да са уникални едни спрямо други.
        Random rnd = new Random();
        List<Integer> listOfDigits = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            int digit = rnd.nextInt(1, 50);
            listOfDigits.add(digit);
            if (i > 0) {
                for (int j = 0; j < listOfDigits.size() - 1; j++) {     // Последно е добавен текущият.
                    int el = listOfDigits.get(j);                       // от тук и "j < listOfDigits.size()".
                    if (digit == el) {
                        digit = rnd.nextInt(1, 50);
                        listOfDigits.add(j, digit);                    // Добави новият digit на поз.j -
                        listOfDigits.remove(j + 1);              // и премахни старият от поз. j + 1
                    }
                }
            }
        }
        //for (int el: listOfDigits) System.out.print(el + " ");
        return listOfDigits;
    }









    //-----------------------------------------------------------------------------------------------------------------
    // [32, 17, 32, 27, 22, 9] [34, 47, 37, 11, 12, 36] [32, 36, 5, 34, 14, 46] -> 11
    // [0, 0, 0, 0, 0, 0]      [0, 0, 0, 1, 0, 0]       [0, 0, 0, 0, 0, 0]
    // [32, 17, 32, 27, 22, 9] [34, 47, 37, 22, 12, 36] [32, 36, 5, 34, 14, 46]
    protected static List<List<Integer>> matchCheckers(List<List<Integer>> list, int el) throws IOException {
        List<List<Integer>> matchers = new ArrayList<>();
        List<List<Integer>> tmp = new ArrayList<>(list);
        Random rnd = new Random();

        for (int i = 0; i < list.size(); i++) {
            List<Integer> matcher = new ArrayList<>();
            for (int j = 0; j < list.get(i).size(); j++) {
                int digitA = list.get(i).get(j);
                if (digitA != el) {
                    matcher.add(j, 0);
                } else {
                    digitA = rnd.nextInt(1, 50);            // Добави на поз. j и премахни от поз. j + 1
                    list.get(i).add(j, digitA);                                    // Добавя digit на позиция j
                    list.get(i).remove(j + 1);                               // Премахва 11 от позиция j + 1
                    for (int k = 0; k < list.get(i).size(); k++) {
                        if (list.get(i).get(k) == digitA) {
                            digitA = rnd.nextInt(1, 50);    // k е позоцията на която има съвпадение
                            list.get(i).add(k, digitA);                            // Добавя digit на позиция k
                            list.get(i).remove(k + 1);                       // Премахва 11 от позиция k + 1

                            // list.get(i).remove(k);
                            // list.get(i).add(k, digitA);              // Има същият ефект върху списъка
                        }
                    }
                    matcher.add(j, 1);
                }
            }

            matchers.add(i, matcher);
        }

//        System.out.println();
//        for (List<Integer> e : matchers) System.out.print(e + " ");
//        System.out.println();
//        for (List<Integer> e : list) System.out.print(e + " ");

       // writeToFile(list, matchers, tmp);
        return matchers;
    }

    private static void writeToFile(List<List<Integer>> list, List<List<Integer>> matchers, List<List<Integer>> tmp)
            throws IOException {
        BufferedWriter writer =
                new BufferedWriter(new FileWriter("totoPoint.txt", true));

        writer.newLine();
        for (List<Integer> el : tmp) {
            writer.write(el.toString());
        }
        writer.newLine();
        for (List<Integer> el : matchers) {
            writer.write(el.toString());
        }
        writer.newLine();
        for (List<Integer> el : list) {
            writer.write(el.toString());
        }
        writer.newLine();
        writer.close();
    }


}
