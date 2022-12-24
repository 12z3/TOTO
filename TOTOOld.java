package task.TOTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TOTOOld extends TotoPoint {  // extends TOTOLogic.

    // TODO: Пренапиши го грамотно използвайки ООП

    public static void main(String[] args) {
        //timeAndData();

        //TODO: Резултат от тиража:
        List<Integer> lastLotteryResult = new ArrayList<>(List.of(15, 18, 23, 33, 38, 39));
        //TODO: 102-и тираж - Твоя залог: -> current: 1, 7, 22, 33, 37, 43 / 2022 12 22 18 45 - next: 2022 12 24 18 45

        letsGo(lastLotteryResult);
    }

    private static void letsGo(List<Integer> last) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Проверяваме или играем? (c / p): ");
        String thisAnswer = scanner.nextLine().trim();
        String string = checkAnswer(thisAnswer);

//        while (!thisAnswer.equalsIgnoreCase("c")
//                && !thisAnswer.equalsIgnoreCase("p")) {
//            System.out.println("Айде сега.... 'c' или 'p'?");
//            thisAnswer = scanner.nextLine().trim();
//        }
        if ("p".equalsIgnoreCase(string)) {
            play(last);
        } else if ("c".equalsIgnoreCase(string)) {
            checkResults(last);
        }
    }

    private static String checkAnswer(String answer) {
        Scanner scanner = new Scanner(System.in);
        while (!answer.equalsIgnoreCase("c") &&
                !answer.equalsIgnoreCase("p")) {
            System.out.println("Айде сега.... 'c' или 'p'?");
            answer = scanner.nextLine();
        }
        return answer;
    }

    private static void play(List<Integer> last) {
        Scanner scanner = new Scanner(System.in);
        List<List<Integer>> listResult = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        System.out.print("Ще залагаме ли? (y / n): ");
        String answer = checkAnswerAgain();

        if (answer.equalsIgnoreCase("y")) {
            // listResult = generateTotoNum(last, 3);
            listResult = getFinalListOfNumbers();

            System.out.print("Избери между 1, 2 и 3 вариант: ");
            String yourChoice = scanner.nextLine();
            while (!yourChoice.equalsIgnoreCase("1") &&      // -> false == на 1, 2 или 3
                    !yourChoice.equalsIgnoreCase("2") &&     // -> true != от 1, 2 или 3
                    !yourChoice.equalsIgnoreCase("3")) {     // while търси true;
                System.out.print("Избери между 1, 2 и 3 вариант: ");     // true && true = true; true && false = false;
                yourChoice = scanner.nextLine();
            }
            switch (yourChoice) {
                case "1" -> result = listResult.get(0);
                case "2" -> result = listResult.get(1);
                case "3" -> result = listResult.get(2);
            }
            System.out.println("Избрал си: " + result.toString());
        }

        System.out.print("Приключваш ли?... (y / n): ");
        String thisAnswer = scanner.nextLine().trim();
        if (thisAnswer.equalsIgnoreCase("n")) {
            System.out.println("... добре");
            checkResults(last);
        } else if (thisAnswer.equalsIgnoreCase("y"))
            System.out.println("... Всичко добро Брат.");

        int resultCounter = 1;
        Collections.sort(result);
        writeResult(listResult, result, last);
    }

    private static String checkAnswerAgain() {
        Scanner scanner = new Scanner(System.in);
        String thisAnswer = scanner.nextLine();

        while (!thisAnswer.equalsIgnoreCase("y") &&
                !thisAnswer.equalsIgnoreCase("n")) {
            System.out.println("Айде сега.... 'y' или 'n'?");
            thisAnswer = scanner.nextLine();
        }
        return thisAnswer;
    }

    private static void checkResults(List<Integer> last) {
        System.out.println("-----------------------------------------------");
        int[] a = officialResult("Резултат от тиража: ");
        int[] b = yourSuppose("Твоят залог: ");
        System.out.println("-----------------------------------------------");
        printResult(a, b);
        System.out.println("-----------------------------------------------");

        System.out.print("Край?... (y / n) : ");
        String thisAnswer = checkAnswerAgain();
        if (thisAnswer.equalsIgnoreCase("n")) {
            play(last);
        }
    }

    private static int[] yourSuppose(String s) {
        System.out.print(s);
        return result();
    }

    private static int[] officialResult(String s) {
        System.out.print(s);
        return result();
    }

    private static int[] result() {
        Scanner scanner = new Scanner(System.in);

        String[] input = scanner.nextLine()
                .trim()
                .split(", ");

        int[] result = new int[input.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(input[i]);
        }
        return result;
    }

    private static void printResult(int[] a, int[] b) {

        System.out.print("Имаш " + getCounts(a, b) + " от " + a.length + " попадения: ");

        int[] tmp = Objects.requireNonNull(compareResults(a, b));
        int counter = getCounts(a, b);
        boolean isZero = false;

        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != 0 && counter >= 1) {
               if (counter > 1) {
                   System.out.print(tmp[i] + ", ");
               } else {
                   System.out.print(tmp[i] + " ");
               }
                counter--;
            } else if (tmp[i] != 0) {
                System.out.print(tmp[i]);
                counter--;
            } else if (tmp[i] == 0) {
                if (!isZero && counter <= 3) System.out.print("... Нема Се Отказваш Само ;)" + " ");
                isZero = true;
            }
            if (counter == 0) break;
        }
        System.out.println();   // 4, 7, 13, 35, 38, 45
    }                           // 4, 10, 13, 35, 38, 45

    private static int[] compareResults(int[] a, int[] b) {
        if (a.length != b.length) {
            System.out.println("ERROR");
            return null;
        }
        return getResult(a, b);
    }

    private static int[] getResult(int[] a, int[] b) {
        int[] tmp = new int[a.length];
        boolean isMatch;
        for (int i = 0; i < a.length; i++) {
            isMatch = false;
            for (int j = 0; j < b.length; j++) {
                if (a[i] == b[j]) {
                    isMatch = true;
                    break;
                }
            }
            if (isMatch) tmp[i] = a[i];

        }
        return tmp;
    }

    private static int getCounts(int[] a, int[] b) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (a[i] == b[j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void writeResult(List<List<Integer>> input, List<Integer> result, List<Integer> last) {
        String path = "";
        boolean choice = false;
        try {
            System.out.print("Да се запазят ли предишните резултати? (y / n): ");

            String thisAnswer = checkAnswerAgain();
            if (thisAnswer.equalsIgnoreCase("y")) choice = true;

            BufferedWriter writer =
                    new BufferedWriter(new java.io.FileWriter("totoNew.txt", choice));

            File file = new File("totoNew.txt");
            if (file.exists()) path = file.getAbsolutePath();

            // writer.write(String.valueOf(timeAndData()));
            writer.newLine();
            for (List<Integer> el : input) {
                writer.write(el.toString() + "\n");
            }
            writer.append("------------------------------------" + "\n");
            writer.write("Избрал си: " + result.toString() + "\n"
                    + "Изтеглени: " + last.toString() + "\n");
            writer.append("------------------------------------" + "\n");
//            writer.newLine();

//          TODO: Оправи си времената.... че е Мазало.
            LocalDateTime resultLDT = getLocalDateTime();
            writer.write(
                    Objects.requireNonNull(whenTotoTimeIs(resultLDT)));

            writer.newLine();
            writer.newLine();
            writer.close();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        if (choice) System.out.println("Резултатът е записан в: " + path);
    }

    private static LocalDateTime getLocalDateTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Кога е следващият Тираж? " +
                "\nВъведи (година месец ден час минути) разделени с интервал: ");           // 2022 12 8 18 45

        String[] dataTimeFormatAnswer = scanner.nextLine()
                .trim()
                .split(" ");
        int year = Integer.parseInt(dataTimeFormatAnswer[0]);
        int month = Integer.parseInt(dataTimeFormatAnswer[1]);
        int dayOfMonth = Integer.parseInt(dataTimeFormatAnswer[2]);
        int hour = Integer.parseInt(dataTimeFormatAnswer[3]);
        int minute = Integer.parseInt(dataTimeFormatAnswer[4]);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public static String whenTotoTimeIs(LocalDateTime timeOfToto) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч ");

        LocalDateTime now = LocalDateTime.now();

        int count = 0;
        int dDays = timeOfToto.getDayOfMonth() - now.getDayOfMonth();

        while (dDays < 0) {
            System.out.println("... Я си оправи времената");
            dDays = getLocalDateTime().getDayOfMonth() - now.getDayOfMonth();
        }

        while (dDays != 0) {
            dDays--;
            count++;
        }
        long hour1 = now.getHour();
        long hour2 = timeOfToto.getHour();
        long dHours = Math.abs(hour1 - hour2);

        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();
        long dMins = Math.abs(min1 - min2);

        // if (dDays == 1 && dHours == 0 && dMins == 0) count = 0;

        //System.out.println("The Day is: " + timeOfToto.format(formatDate));

        return ("ToDay is: " + now.format(formatDate) + "\n"
                + "Reminders: "
                + count + " days (in " + timeOfToto.getDayOfWeek() + ") - "
                + (dHours + " hours " + "and "
                + (dMins + " minutes"))
                + "\n");
    }

//    public static String totoTimeAndData() {
//        LocalDateTime localTime = LocalDateTime.now();
//        DateTimeFormatter formatDate =
//                DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч ");
//        return localTime.format(formatDate);
//    }
}

/* ----------------------------------------------------------------------------------------------------------

/*
   First variant:
 * public static void main(String[] args) {
 * Scanner scanner = new Scanner(System.in);
 * timeAndData();
 * <p>
 * //TODO: 92-и тираж:  12, 24, 26, 28, 37, 48  // 9, 24, 26, 28, 37, 46
 * <p>
 * System.out.print("Проверяваме или играем (c / p): ");
 * String answerOne = scanner.nextLine().trim();
 * <p>
 * <p>
 * if (answerOne.equalsIgnoreCase("p")){
 * question();
 * } else if (answerOne.equalsIgnoreCase("c")){
 * <p>
 * System.out.println("-----------------------------------------------");
 * int[] a = officialResult("Резултат от тиража: ", scanner);
 * int[] b = yourSuppose("Твоят залог: ", scanner);
 * System.out.println("-----------------------------------------------");
 * printResult(a, b);
 * System.out.println("-----------------------------------------------");
 * }
 * //question();
 * }
 * <p>
 * private static void question() {
 * List<Integer> last = new ArrayList<>(List.of(12, 14, 17, 21, 39, 48));
 * Scanner scanner = new Scanner(System.in);
 * System.out.print("Ще залагаме ли? (y / n) : ");
 * <p>
 * // while търси true;  // true && true = true; true && false = false;
 * String answer = scanner.nextLine();
 * while (!answer.equalsIgnoreCase("y") &&
 * !answer.equalsIgnoreCase("n")) {
 * System.out.println("Айде сега.... 'y' или 'n'?");
 * answer = scanner.nextLine();
 * }
 * <p>
 * if (answer.equalsIgnoreCase("y")) generateTotoNum(last, 3, 5);
 * }
 * <p>
 * private static int[] yourSuppose(String s, Scanner scanner) {
 * System.out.print(s);
 * return result(scanner);
 * }
 * <p>
 * private static int[] officialResult(String s, Scanner scanner) {
 * System.out.print(s);
 * return result(scanner);
 * }
 * <p>
 * private static int[] result(Scanner scanner) {
 * String[] input = scanner.nextLine().
 * trim().
 * split(", ");
 * <p>
 * int[] result = new int[input.length];
 * for (int i = 0; i < result.length; i++) {
 * result[i] = Integer.parseInt(input[i]);
 * }
 * return result;
 * }
 * <p>
 * private static void printResult(int[] a, int[] b) {
 * <p>
 * System.out.print("Имаш " + getCounts(a, b) + " от " + a.length + " попадения: ");
 * <p>
 * int[] tmp = Objects.requireNonNull(compareResults(a, b));   // 12, 14, 17, 21, 39, 48
 * int counter = getCounts(a,b);
 * for (int i = 0; i < tmp.length; i++) {
 * if (tmp[i] != 0 && counter > 1) {
 * System.out.print(tmp[i] + ", ");
 * counter--;
 * } else if(tmp[i] != 0)  {
 * System.out.print(tmp[i]);
 * counter--;
 * }
 * if (counter == 0) break;
 * }
 * System.out.println();
 * }
 * <p>
 * private static int[] compareResults(int[] a, int[] b) {
 * if (a.length != b.length) {
 * System.out.println("ERROR");
 * return null;
 * }
 * return getResult(a, b);
 * }
 * <p>
 * private static int[] getResult(int[] a, int[] b) {
 * int[] tmp = new int[a.length];
 * boolean isMatch = false;
 * for (int i = 0; i < a.length; i++) {
 * isMatch = false;
 * for (int j = 0; j < b.length; j++) {
 * if (a[i] == b[j]) {
 * isMatch = true;
 * }
 * }
 * if (isMatch) tmp[i] = a[i];
 * }
 * return tmp;
 * }
 * <p>
 * private static int getCounts(int[] a, int[] b) {
 * int count = 0;
 * for (int i = 0; i < a.length; i++) {
 * for (int j = 0; j < b.length; j++) {
 * if (a[i] == b[j]) {
 * count++;
 * }
 * }
 * }
 * return count;
 * }
 */