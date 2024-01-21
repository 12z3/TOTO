package Jackpot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Objects;

import static java.time.DayOfWeek.*;

public class Jackpot extends getResult {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        int choices = 3, counting = 6;
        List<Integer> officialResult = Arrays.asList(1, 5, 21, 34, 40, 42);
        String validatedAnswer = choice(scanner);

        if (validatedAnswer.equalsIgnoreCase("P")) {
            play(choices, counting, officialResult);
        } else if (validatedAnswer.equalsIgnoreCase("C")) {
            checkingResults(parsToInt(getLastLine(readFromFile())), officialResult);
        } else if (validatedAnswer.equalsIgnoreCase("E")) {
            return;
        }
        scanner.close();
    }

    private static String choice(Scanner scanner) {
        System.out.print("Играем, Проверяваме (P / C) или (Е) за изход: ");
        String answer = scanner.nextLine();
        return validatedAnswers(answer, "P", "C", "E");
    }

    private static void play(int choices, int counting, List<Integer> officialResult) throws IOException {
        int idx = 1;
        List<List<Integer>> newDraw = generateRandomResults(choices, counting, officialResult);
        if (newDraw != null) {
            System.out.println("Последно теглене: " + officialResult + "\n");
            for (List<Integer> item : newDraw) System.out.println((idx++) + ". " + item);
        } else return;
        String check = (checkResults(newDraw, officialResult) == 1) ? "Дублира се. " : "Няма дублирани елементи.";
        System.out.println(check + "\n");
        if (saveInFile(newDraw, officialResult)) return;
    }

    private static void getYourHistorySuppose() {

    }

    private static void checkingResults(List<List<Integer>> newDraw, List<Integer> officialResult) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Ще проверяваме ли резултатите (Y / N) или (E) за изход: ");
//        String answer = scanner.nextLine();
//        if (validatedAnswers(answer, "Y", "N", "E").equalsIgnoreCase("Y")) {
//            checkYoursResults(newDraw, officialResult);
//        } else return;
        checkYoursResults(newDraw, officialResult);
    }

    private static void checkYoursResults(List<List<Integer>> newDraw, List<Integer> officialResult) {
        System.out.print("\nРезултати от последното теглене: " + officialResult);
        System.out.println("\n" + "И Положението Е: ");
        isJackpot(newDraw, officialResult);
    }

    private static List<List<Integer>> generateRandomResults(int choices, int counting, List<Integer> official) {
        Scanner scanner = new Scanner(System.in);
        Random rnd = new Random();

        List<List<Integer>> resList = new ArrayList<>();
        Set<Integer> addedDigit = new HashSet<>();
        List<Integer> result;

        System.out.print("Да се отчита ли предишното теглене?  (Y / N) или (E) за изход: ");
        String answer = scanner.nextLine();
        String validatedAnswer = validatedAnswers(answer, "Y", "N", "E");
        boolean draw = validatedAnswer.equalsIgnoreCase("Y");
        if (validatedAnswer.equalsIgnoreCase("E")) {
            return null;
        }

        for (int i = 0; i < choices; i++) {
            Set<Integer> tmp = new HashSet<>();
            for (int j = 0; j < counting; j++) {
                int newEl = rnd.nextInt(1, 50);
                if (draw) {
                    if (!addedDigit.contains(newEl) && !tmp.contains(newEl)) {
                        tmp.add(newEl);
                        addedDigit.add(newEl);
                    } else {
                        j--;
                    }
                } else {
                    if (!addedDigit.contains(newEl) && !tmp.contains(newEl) && !official.contains(newEl)) {
                        tmp.add(newEl);
                        addedDigit.add(newEl);
                    } else {
                        j--;
                    }
                }
            }
            result = new ArrayList<>(tmp);
            Collections.sort(result);
            resList.add(result);
        }
        return resList;
    }

    private static String validatedAnswers(String answer, String str1, String str2, String str3) {
        Scanner scanner = new Scanner(System.in);
        while (!answer.equalsIgnoreCase(str1)
                && !answer.equalsIgnoreCase(str2) && !answer.equalsIgnoreCase(str3)) {
            System.out.printf("%s или %s или %s: ", str1, str2, str3);
            answer = scanner.nextLine();
        }
        return answer;
    }

    private static boolean saveInFile(List<List<Integer>> res, List<Integer> official) throws IOException {
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        File file = new File("/Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/tResN.txt");
        boolean doDelete;

        System.out.print("Да се запазят ли предишните резултати (Y / N) или (E) за изход: ");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("E")) {
            exit = true;
            return exit;  // последен метод е - 23 ред.
        } else {
            doDelete = validatedAnswers(answer, "Y", "N", "E").equalsIgnoreCase("Y");
        }

        //doDelete = validatedAnswers(answer).equalsIgnoreCase("Y") ? true : false;
        writer(res, official, file, doDelete);
        return exit;
    }

    private static void writer(List<List<Integer>> res, List<Integer> official, File file, boolean doDelete)
            throws IOException {

        File fileArc = new File(
                "/Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/archive.txt");
        FileWriter writer = new FileWriter(file, doDelete);
        FileWriter writerArc = new FileWriter(fileArc, doDelete);

        writer.write("\n" + "Последно тегелене: " + official.toString() + "\n");
        writer.write("---------------------------" + "\n");
        writer.write("Предложения: " + "\n");
        for (int i = 0; i < res.size(); i++) {
            writer.write((i + 1) + ". " + res.get(i).toString() + "\n");
        }
        System.out.println("Резултата е записан в: " + file.getAbsolutePath());
        writer.write("---------------------------" + "\n");
        writer.write(getDateAndTime() + "\n");
        writer.write(dateFromNextDraw() + "\n");
        writerArc.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy (HH:mm:ss):     ")) +
                res.toString() + "\n");

        writerArc.close();
        writer.close();
    }

    private static String getDateAndTime() {
        DateTimeFormatter formatDate =
                DateTimeFormatter.ofPattern("dd MMM yyyy, E - a- c 'ден:' HH:mm:ss ч ");

        LocalDateTime now = LocalDateTime.now();
        return now.format(formatDate);
    }

    private static String dateFromNextDraw() {
        LocalDateTime now = LocalDateTime.now();

        Integer[] drawDays = {3, 6};
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        String thisDayStr = String.valueOf(now.getDayOfWeek());
        String drawDay = "", printDay, resStr, nextDrawDay = "";
        int thisDay = -1, leftDays = -1, leftDaysToNext = -1;

        //long daysDifference = ChronoUnit.DAYS.between();
        //System.out.println(String.valueOf(now.getDayOfWeek()));

        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(thisDayStr)) thisDay = i;
        }

        if (thisDay == drawDays[0]) {
            leftDays = 0;
            leftDaysToNext = 3;
            drawDay = "Четвъртък";
            nextDrawDay = "Неделя";
        } else if (thisDay < drawDays[0]) {
            leftDays = drawDays[0] - thisDay;
            drawDay = "Четвъртък";
            leftDaysToNext = leftDays;
        } else if (thisDay > drawDays[0] && thisDay < drawDays[1]) {
            leftDays = drawDays[1] - thisDay;
            drawDay = "Неделя";
            leftDaysToNext = leftDays;
        } else if (thisDay == drawDays[1]) {
            leftDays = 0;
            leftDaysToNext = 3;
            drawDay = "Неделя";
            nextDrawDay = "Четвъртък";
        }

        thisDayStr = getCurrentDay(thisDayStr);

        printDay = (Math.abs(leftDays) == 1) ? " ден." : " дни.";

        String resStrA = ("Днес е " + thisDayStr + ", следващият тираж е в " + drawDay +
                ". Остават " + Math.abs(leftDays) + printDay);

        String resStrB = (thisDayStr + " e." + " Ден за ПЕЧАЛБИ!  ;) \n" + "До следващият тираж остават: "
                + leftDaysToNext + " дни (" + nextDrawDay + ").");

        resStr = (leftDays == 0) ? resStrB : resStrA;
        return resStr;
    }

    private static String getDayToNextDraw() {
        String thisDay = " ", result = "", resultA = "", resultB = "", day = "", nextDrawDay = "";
        int cnt = 0, days = 0, nextDaysToDraw = -1;

        LocalDateTime now = LocalDateTime.now();
        while (!now.getDayOfWeek().equals(SATURDAY) && !now.getDayOfWeek().equals(SUNDAY)) {
            now = now.plusDays(1);
            days++;
        }


        switch (now.getDayOfWeek()) {
            case MONDAY -> {
                thisDay = "Понеделник";
                nextDrawDay = "Четвъртък";
            }
            case TUESDAY -> {
                thisDay = "Вторник";
                nextDrawDay = "Четвъртък";
            }
            case WEDNESDAY -> {
                thisDay = "Сряда";
                nextDrawDay = "Четвъртък";
            }
            case THURSDAY -> {
                thisDay = "Четвъртък";
                nextDrawDay = "Неделя";
                nextDaysToDraw = 3;
            }
            case FRIDAY -> {
                thisDay = "Петък";
                nextDrawDay = "Неделя";
            }
            case SATURDAY -> {
                thisDay = "Събота";
                nextDrawDay = "Неделя";
            }
            case SUNDAY -> {
                thisDay = "Неделя";
                nextDrawDay = "Четвъртък";
                nextDaysToDraw = 4;
            }
        }

        day = (days == 1) ? " ден." : " дни.";

        resultA = (thisDay + " e." + " Ден за ПЕЧАЛБИ!  ;) \n" + "До следващият тираж остават: "
                + days + " дни (" + nextDrawDay + ").");

        resultB = ("Днес е " + thisDay + ", следващият тираж е в " + nextDrawDay +
                ". Остават " + days + day);

        result = (days == 0) ? resultA : resultB;
        return result;
    }


    private static String getCurrentDay(String thisDayStr) {
        switch (thisDayStr) {
            case "MONDAY" -> thisDayStr = "Понеделник";
            case "TUESDAY" -> thisDayStr = "Вторник";
            case "WEDNESDAY" -> thisDayStr = "Сряда";
            case "THURSDAY" -> thisDayStr = "Четвъртък";
            case "FRIDAY" -> thisDayStr = "Петък";
            case "SATURDAY" -> thisDayStr = "Събота";
            case "SUNDAY" -> thisDayStr = "Неделя";
        }
        return thisDayStr;
    }

    private static int checkResults(List<List<Integer>> res, List<Integer> official) {
        Set<Integer> officialSet = new HashSet<>(official);
        Map<Integer, List<Integer>> rowCol;
        List<Integer> matches;
        int isMatch = -1;

        for (int i = 0; i < res.size(); i++) {
            rowCol = new TreeMap<>();
            for (int j = 0; j < res.get(i).size(); j++) {
                matches = new ArrayList<>();
                if (officialSet.contains(res.get(i).get(j))) {
                    matches.add(res.get(i).get(j));
                    //rowCol.put(i, matches);
                    System.out.println(("Ред: " + (i + 1)) + ", Колона: " + j + " - " + matches + " ");
                    //printMap(rowCol, res.get(i).get(j));
                    isMatch = 1;
                }
            }
        }
        return isMatch;
    }

    private static void isJackpot(List<List<Integer>> yoursSuppose, List<Integer> currentDraw) {
        List<Integer> column;
        List<Integer> matches;
        Set<Integer> tmp = new HashSet<>(currentDraw);

        for (int i = 0; i < yoursSuppose.size(); i++) {
            System.out.println("---------------------------");
            System.out.println((i + 1) + ". " + yoursSuppose.get(i));
            matches = new ArrayList<>();
            column = new ArrayList<>();
            for (int j = 0; j < yoursSuppose.get(i).size(); j++) {
                if (tmp.contains(yoursSuppose.get(i).get(j))) {
                    matches.add(yoursSuppose.get(i).get(j));
                    column.add(j);
                }
            }
            printMatches(matches, column, i);
        }
    }

    private static void printMatches(List<Integer> matches, List<Integer> column, int i) {
        if (!column.isEmpty()) {
            System.out.print(("Ред: " + (i + 1)) + ", Колона: " + column + " -> " + matches + " " + "\n");
            System.out.println("---------------------------" + "\n");
        } else {
            System.out.println("Няма никакви съвпадения...");
            System.out.println("---------------------------" + "\n");
        }
    }

    private static void printMap(Map<Integer, List<Integer>> map, int digit) {
        for (Map.Entry<Integer, List<Integer>> el : map.entrySet()) {
            System.out.printf("Ред: %d; -> %d%n", el.getKey(), digit);
        }
    }

    private static int checkResultsOld(List<List<Integer>> res, List<Integer> official) {
        for (List<Integer> item : res) {
            for (int i = 0; i < item.size(); i++) {
                for (int j = 0; j < official.size(); j++) {
                    if (item.get(i).equals(official.get(j))) {
                        System.out.printf("ERROR :( -> %d ", official.get(j));
                        return 1;
                    }
                }
            }
        }
        return -1;
    }
}
