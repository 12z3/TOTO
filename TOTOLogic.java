package task.TOTO;

import training.Methods;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TOTOLogic extends Methods {
    /**
     * Генерира числата от 1 до 49 "variants" пъти и записва резултатите в "List<List<Integer>> allNumbers".
     * Търси колко пъти се намира числото "thisNumber" в резултата.
     * Избира дали да се включат числата от предишният тираж в новото теглене.
     * Проверява за еднаквост на числата от всяко ново теглене (allNumbers) и старият тираж (last).
     */

    public static void main(String[] args) {
        List<Integer> last = new ArrayList<>(List.of(8, 37, 48, 28, 26, 41));
        timeAndData();
        generateNum(last, 3, 5);
        //allMatchesForThisNumber();
    }

    private static void allMatchesForThisNumber() {
        List<Integer> count = new ArrayList<>();
        int i = 0;
        while (i < 100) {
            count.add(allMatches(6, 39));
            i++;
        }
        System.out.println(count);
    }

    private static int allMatches(int variants, int thisNumber) {
        Random rnd = new Random();
        List<Integer> list;
        List<List<Integer>> allNumbers = new ArrayList<>();

        int counter = 0;
        while (counter < variants) {
            list = new ArrayList<>();
            for (int k = 0; k < 6; k++) {
                list.add(rnd.nextInt(50));
            }
            allNumbers.add(list);
            counter++;
        }
        return allThisContains(allNumbers, thisNumber);
    }

    static List<List<Integer>> generateNum(List<Integer> last, int variants, int thisNumber) {
        Scanner scanner = new Scanner(System.in);
        Random rnd = new Random();

        List<Integer> list;
        List<List<Integer>> allNumbers = new ArrayList<>();

        System.out.print("Да се отчита ли предишният тираж? (y / n): ");
        char answer = scanner.nextLine().charAt(0);

        int counter = 0;
        while (counter < variants) {
            list = new ArrayList<>();
            for (int k = 0; k < 6; k++) {
                int el = rnd.nextInt(50);                                   // ... до 50!
                switch (answer) {
                    case 'n' -> {
                        if (!check(list, el) && el != 0) {                        // Да не се съдържа в "last" и "List"
                            list.add(el);                                         // ... и да е != 0.
                        } else {
                            if (k != 0 && k > -1) {            // k e от -0.99 до - 0.001 и от 0.001 до...безкрайност)
                                k--;
                            }
                        }
                    }
                    case 'y' -> {
                        if (!check(list, el) && !check(last, el) && el != 0) {
                            list.add(el);
                        } else {
                            if (k != 0 && k > -1) {
                                k--;
                            }
                        }
                    }
                }
            }
            allNumbers.add(list);
            counter++;
        }
        System.out.println("Предишно теглене - резултат: " + last);
        System.out.print("Предложения за залог: ");
        printResult(allNumbers, thisNumber, last);
        return allNumbers;
    }

    private static void printResult(List<List<Integer>> allNumbers, int thisNumber, List<Integer> last) {
        System.out.println(allNumbers + "\n");
        System.out.println(
                "Numbers " + thisNumber + " contains " + allThisContains(allNumbers, thisNumber) + " times.");
        System.out.print("is 'allNumbers' - elements match with 'last' - element? : ");
        for (List<Integer> el : allNumbers) System.out.print(isASameElementsOfLists(last, el) + " / ");
    }

    private static int isThisContains(List<Integer> list, int thisNumber) {
        int contains = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(thisNumber)) {
                contains++;
                // boolean a = list.remove(Integer.valueOf(i));
            }
        }
        if (contains != 0) {
            return contains;
        } else return -1;
    }

    private static int allThisContains(List<List<Integer>> allNumbers, int thisNumber) {
        int allContains = 0;
        for (int i = 0; i < allNumbers.size(); i++) {
            if ((isThisContains(allNumbers.get(i), thisNumber)) != -1) {
                allContains += (isThisContains(allNumbers.get(i), thisNumber));
                System.out.println(allNumbers.get(i));
            }
        }
        return allContains;
    }

    // ----------------------------------------------------------------------------------------------------------
    // Методчетата по-долу се използват в TOTO.java. Тези от горе са друга бира.....

    private static boolean check(List<Integer> l1, int l2) {
        boolean isMatch = false;
        for (int i = 0; i < l1.size(); i++) {
            if (l1.get(i) == l2) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }

    static List<List<Integer>> generateTotoNum(List<Integer> lastN, int variants) {
        Scanner scanner = new Scanner(System.in);
        Random rnd = new Random();

        List<Integer> list;
        List<List<Integer>> allNumbers = new ArrayList<>();

        System.out.print("Да се отчита ли предишният тираж? (y / n): ");
        char thisAnswer = scanner.nextLine().charAt(0);

        // TODO: Генерирай 10 числа и избери от тях 3-и.
        int counter = 0, el = 0;
        while (counter < variants) {
            list = new ArrayList<>();
            for (int k = 0; k < 6; k++) {
                int n = 10;
                while (n > 0) {
                    el = rnd.nextInt(50);                                   // ... до 50!
                    n--;
                }
                switch (thisAnswer) {
                    case 'n' -> {
                        if (!check(list, el) && el != 0) {                        // Да не се съдържа в "last" и "List"
                            list.add(el);                                         // ... и да е != 0.
                        } else {
                            // if (k != 0 && k > - 1)            // k e от -0.99 до - 0.001 и от 0.001 до...безкрайност)
//                            if (k > 0) {
                            k--;
                            //}
                        }
                    }
                    case 'y' -> {
                        if (!check(list, el) && !check(lastN, el) && el != 0) {
                            list.add(el);
                        } else {
                            if (k != 0 && k > -1) {
                                k--;
                            }
                        }
                    }
                }
            }
            allNumbers.add(list);
            counter++;
        }
        System.out.println("Предишно теглене - резултат: " + lastN);
        System.out.print("Предложения за залог: ");
        printTotoResult(allNumbers, lastN);
        return allNumbers;
    }

    private static void printTotoResult(List<List<Integer>> allNumbers, List<Integer> lastN) {
        System.out.println(allNumbers + "\n");
    }
}

