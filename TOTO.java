package task.TOTO.Projects;

import task.TOTO.TotoPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TOTO extends TotoPoint {
    /**
     * Днешна дата:
     * Номер на тираж: 102
     * Резултат от тиража:
     * Твоят залог: 1, 7, 22, 23, 37, 43 / 23.12.2022 06:14
     */

    private final int TODAY_CIRCULATION = 101;
    private int CIRCULATION = TODAY_CIRCULATION;
    private List<Integer> result = new ArrayList<>();
    private List<Integer> yourSuppose = new ArrayList<>();
    private List<Integer> variantResult = new ArrayList<>();
    private List<List<Integer>> variants = new ArrayList<>();

    protected int counter = 0;
    private final String MESSAGE1 = "Нема да се плашиш само... Продължавай.";
    private final String MESSAGE2 = "Имаш %d съвпадения: ";
    private final String MESSAGE3 = "Имаш %d съвпадение: ";
    String yourVariantChoice = "";

    public TOTO() {
    }

    protected void play() throws IOException {
        TOTO toto = new TOTO();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Залагаме или проверяваме резултат? (p / c): ");
        String answer = scanner.nextLine().trim();

        while (!answer.equalsIgnoreCase("p")
                && !answer.equalsIgnoreCase("c")) {
            System.out.print("Айде пак се почна....  (p / c) ?: ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("p")) {
            toto.playToto();
        } else if (answer.equalsIgnoreCase("c")) {
            toto.checkResult();
        }
    }

    public void playToto() throws IOException {
        this.CIRCULATION++;
        setResult();
        setYourSuppose();
        writeResult(this.variants, this.result);
        printToto();
    }

    public void checkResult() {
        printCheckedResult(cherResults(this.result, this.yourSuppose), this.counter);
    }

    public List<Integer> getResult() {
        while (this.result == null) {
            System.out.println("Въведи резултата. Не се Ослушвай!");
            this.result = this.setResult();
        }
        return this.result;
    }

    public List<Integer> getYourSuppose() {
        return this.yourSuppose;
    }

    public List<Integer> setResult() {

        System.out.print("-> Валидни числа са всички положителни Двуцифрени (12) числа " +
                "от 1 до 49 разделени с запетая и интервал (', ') - (6, 15, 18, 23, 25, 39). \n" +
                "-> Комбинации от сорта: (1а,b3, г6  ааа, -98, ЗЯхF, 654, -1) се приемат за невалидни!\n");

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.printf("%s (%d-и) %s", "Какъв e резултата от последният", this.CIRCULATION, "тираж: ");

        String[] input = scanner.nextLine().trim().split(", ");
        boolean isIt = inputVerification(input);

        while (!isIt) {
            System.out.print("ЗаПри се Вихъре. " +
                    "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                    "\n" + "Дай пак: ");
            input = scanner.nextLine().trim().split(", ");
            if (inputVerification(input)) isIt = true;
        }
        this.result = getDigitFromInput(input);
        return this.result;
    }

    private List<Integer> getDigitFromInput(String[] input) {                 // TODO: Обмисло го все пак.
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            digits.add(Integer.parseInt(input[i]));
        }
        return digits;
    }

    private boolean inputVerification(String[] input) {
        return isNotAString(input) && (input.length == 6);
    }

    private boolean isNotAString(String[] input) {                       // alskjdlaks, sa - 22, a
        boolean isNotAString = true;                                     // 12, 7, re, 15, 34, -44
        for (int i = 0; i < input.length; i++) {                         // asd,as, 12, wewewew, -98, d
            if ((input[i].length() <= 2)) {                              // 12, 7, 13, 15, 34, 44
                if (isADoubleDigits(input[i])) {                         // next: 2022 12 25 18 45
                    int el = Integer.parseInt(input[i]);
                    for (int j = 58; j <= 126; j++) {
                        if (el == j) {                               // TODO: Стопира ако намери буква в "el".
                            isNotAString = false;                    //       Идеята е да не минава през целия цикъл
                            break;
                        }
                    }
                } else return false;
            } else return false;
        }
        return isNotAString;
    }

    private boolean isADoubleDigits(String input) {                      // 12, ad
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            for (int j = 49; j <= 57; j++) {
                int el = (int) input.charAt(i);
                if (el == j) {
                    count++;
                    break;
                }
            }
        }
        return count == input.length();
    }

    public void setYourSuppose() throws IOException {
        List<List<Integer>> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nИграем ли?: (y / n) ");
        String answer = scanner.nextLine().trim();

        while (!answer.equalsIgnoreCase("y")
                && !answer.equalsIgnoreCase("n")) {
            System.out.print("Дай ми Коректен отговор: (y / n) ?: ");
            answer = scanner.nextLine().trim();
        }

        System.out.printf("%s", "Вариантите са три: ");
        tmp = getFinalListOfNumbers();
        this.variants = generateUniqueList(tmp);

        for (List<Integer> el : this.variants) {
            Collections.sort(el);
        }
        System.out.print("Избери между 1, 2, 3: ");

        this.yourVariantChoice = scanner.nextLine();
        while (!this.yourVariantChoice.equalsIgnoreCase("1") &&      // -> false == на 1, 2 или 3
                !this.yourVariantChoice.equalsIgnoreCase("2") &&     // -> true != от 1, 2 или 3
                !this.yourVariantChoice.equalsIgnoreCase("3")) {     // while търси true;
            System.out.print("Избери между 1, 2 и 3 вариант: ");     // true && true = true; true && false = false;
            this.yourVariantChoice = scanner.nextLine();
        }

        switch (yourVariantChoice) {
            case "1" -> this.variantResult = this.variants.get(0);
            case "2" -> this.variantResult = this.variants.get(1);
            case "3" -> this.variantResult = this.variants.get(2);
        }
        Collections.sort(this.variantResult);
        System.out.println("Избрал си: " + variantResult.toString() + "\n");
        this.yourSuppose = variantResult;
    }

    public static List<List<Integer>> generateUniqueList(List<List<Integer>> list) {
        Random rnd = new Random();

        int el1 = 0;
        for (int i = 1; i < list.size(); i++) {
            for (int j = 0; j < list.get(0).size(); j++) {
                el1 = list.get(0).get(j);
                int el2 = list.get(0).get(j);
                if (el1 == el2) {
                    list.get(i).remove(j);
                    list.get(i).add(j, rnd.nextInt(1, 50));
                    for (int k = 0; k < list.get(i).size(); k++) {
                        if (j != k && (el2 != list.get(i).get(j))) {
                            list.get(i).remove(j);
                            list.get(i).add(j, rnd.nextInt(1, 50));
                        }
                    }
                }
            }
        }
        return list;
    }

    public void printToto() {
        Collections.sort(this.result);
        Collections.sort(this.yourSuppose);
        System.out.printf("Резултата от последният тираж е:  %s. \n" +
                "Залогът, който си избрал е вариант: %s %s ", this.result, this.yourVariantChoice, this.yourSuppose);
    }


    protected List<Integer> cherResults(List<Integer> result, List<Integer> suppose) {
        Scanner scanner = new Scanner(System.in);

        //TODO: Валидирай Input!
        System.out.print("Въведи последният резултат от тиража: ");
        String[] resInput = scanner.nextLine().trim().split(", ");   // 112, 12, aa,
        result = getDigitFromInput(resInput);

        System.out.print("Въведи твоят залог: ");
        String[] suppInput = scanner.nextLine().trim().split(", ");
        suppose = getDigitFromInput(suppInput);

        List<Integer> tmp = new ArrayList<>();
        int count = 0;
        boolean isMatch = false;
        for (int i = 0; i < result.size(); i++) {
            isMatch = false;
            count = 0;
            int el1 = result.get(i);
            for (int j = 0; j < suppose.size(); j++) {
                int el2 = suppose.get(j);
                if (el1 == el2) {
                    count++;
                    isMatch = true;
                }
            }
            if (count > 1) {
                System.out.println("Не може да има повече от едно съвпадение....");
                return null;
            }
            this.counter += count;
            if (isMatch) tmp.add(el1);
        }
        return tmp;
    }

    //TODO: Оправи си Логиката....
    protected void printCheckedResult(List<Integer> tmp, int counter) {
        if (counter < 0) {
            System.out.println("ERROR < 0");                // MESSAGE1 = "Нема да се плашиш само... Продължавай.";
            return;                                         // MESSAGE2 = "Имаш %d съвпадения: ";
        }
        if (counter == 1) {
            System.out.println("\n" + this.MESSAGE1);
            System.out.printf(this.MESSAGE3, counter);
            printListResult(tmp);
            return;
        }

        if (counter > 1 && counter < 4) {
            System.out.println("\n" + this.MESSAGE1);
            System.out.printf(this.MESSAGE2, counter);
            printListResult(tmp);
        } else if (counter == 0) {
            System.out.println(this.MESSAGE1);
        } else {
            System.out.printf("\n" + this.MESSAGE2, counter);
            printListResult(tmp);
        }

    }

    private void printListResult(List<Integer> tmp) {
        for (int i = 0; i < tmp.size(); i++) {
            if (tmp.get(i) != 0 && i < tmp.size() - 1) {
                System.out.print(tmp.get(i) + ", ");
            } else if (i == tmp.size() - 1) {
                System.out.println(tmp.get(i) + ".");
            }
        }
    }

    private void writeResult(List<List<Integer>> variants, List<Integer> lastResult) {
        Scanner scanner = new Scanner(System.in);
        String path = "";
        boolean choice = false;
        try {
            System.out.print("Да се запазят ли предишните резултати? (y / n): ");

            String thisAnswer = scanner.nextLine().trim();
            while (!thisAnswer.equalsIgnoreCase("y")
                    && !thisAnswer.equalsIgnoreCase("n")) {
                System.out.print("Май - май продължаваме с магариите...?: (y / n)?: ");
                thisAnswer = scanner.nextLine().trim();
            }
            if (thisAnswer.equalsIgnoreCase("y")) choice = true;

            BufferedWriter writer =
                    new BufferedWriter(new java.io.FileWriter("newTotoResult", choice));

            File file = new File("newTotoResult");
            if (file.exists()) path = file.getAbsolutePath();

            // writer.write(String.valueOf(timeAndData()));
            writer.newLine();
            writer.write("Вариантите са три: \n");
            for (List<Integer> el : variants) {
                writer.write(el.toString() + "\n");
            }
            writer.append("----------------------------------------------" + "\n");
            writer.write("Избрал си вариант " + this.yourVariantChoice + ": " +
                    this.yourSuppose.toString() + "\n"
                    + "Последен тираж: " + lastResult.toString() + "\n");
            writer.append("----------------------------------------------" + "\n");
//            writer.newLine();

            LocalDateTime resultLDT = getLocalDateTime();
            writer.write(
                    Objects.requireNonNull(whenTotoTimeIs(resultLDT)));

            writer.newLine();
            writer.close();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        if (choice) System.out.println("\nРезултатът е записан в: " + path);
    }

    private static LocalDateTime getLocalDateTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Кога е следващият Тираж? " +
                "\nВъведи (година месец ден час минути) разделени с интервал (2022 12 25 18 45): ");

        //TODO: Трябва да валидираш въвеждането на датата.
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
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч. ");

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

        //TODO: Оправи името на деня в файла да бъде на Кирилица.
//        LocalDateTime day = null;
//        switch (timeOfToto.getDayOfWeek()){
//            case MONDAY -> day.;
//        }

        String sDay = " дни ";
        if (count == 1) sDay = " ден ";

        return ("Днес е: " + now.format(formatDate) + "\n"
                + "До следващият тираж остават: "
                + count + sDay + "(денят е: " + timeOfToto.getDayOfWeek() + ") - "
                + (dHours + " часа " + "и "
                + (dMins + " минути."))
                + "\n");
    }
}
