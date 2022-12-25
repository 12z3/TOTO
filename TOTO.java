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

    private final String YOUR_LAST_SUPPOSE = " 1, 7, 22, 23, 37, 43 ";
    private final String LAST_OFFICIAL_RESULT = " 15, 7, 29, 23, 47, 33 ";
    private final String LastDateOfLottery = "2022 12 25 18 45 ";
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
        if (!setYourSuppose()) return;
        printToto();
        writeResult(this.variants, this.result);
    }

    public void checkResult() {
        printCheckedResult(checkResults(this.result, this.yourSuppose), this.counter);
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
        String[] input;
        boolean verification;

        System.out.print("-> Валидни числа са всички положителни Двуцифрени (12) числа " +
                "от 1 до 49 разделени с запетая и интервал (', ') - (6, 15, 18, 23, 25, 39). \n" +
                "-> Комбинации от сорта: (1а,b3, г6  ааа, -98, ЗЯхF, 654, -1) се приемат за невалидни!\n");

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print("Трябва ми резултата от предишният тираж. " +
                "Ще въведеш резултата или да си го търся?: ( i / s ) ");

        String answer = scanner.nextLine().trim();
        while (!answer.equalsIgnoreCase("i")
                && !answer.equalsIgnoreCase("s")) {
            System.out.print("Виж. Трябва да избереш между i и c. Дай пак: ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("i")) {
            System.out.printf("%s (%d-и) %s", "Въведи резултата от последният", this.CIRCULATION, "тираж: ");
            input = scanner.nextLine().trim().split(", ");
            verification = inputVerification(input);
            while (!verification) {
                System.out.print("ЗаПри се Вихъре. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                input = scanner.nextLine().trim().split(", ");
                verification = inputVerification(input);
            }
        } else {
            input = this.LAST_OFFICIAL_RESULT.trim().split(", ");
            verification = inputVerification(input);
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
        return (input.length == 6) && isNotAString(input);
    }

    private boolean isNotAString(String[] input) {                       // alskjdlaks, sa - 22, a
        boolean isNotAString = true;                                     // 12, 7, re, 15, 34, -44
        for (int i = 0; i < input.length; i++) {                         // asd,as, 12, wewewew, -98, d
            if ((input[i].length() <= 2)) {                              // 12, 7, 13, 15, 34, 44
                if (isDoubleDigits(input[i])) {    // Ако това не мине ще имаш Exception при Integer.parseInt(input[i]);
                    int el = Integer.parseInt(input[i]);                 // next: 2022 12 25 18 45
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

    private boolean isDoubleDigits(String input) {                      // 12, ad
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

    public boolean setYourSuppose() throws IOException {
        List<List<Integer>> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nИграем ли?: (y / n) ");
        String answer = scanner.nextLine().trim();

        while (!answer.equalsIgnoreCase("y")
                && !answer.equalsIgnoreCase("n")) {
            System.out.print("Дай ми Коректен отговор: (y / n) ?: ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("n")) return false;

        System.out.printf("%s", "Вариантите са три: ");
        tmp = getFinalListOfNumbers();

        // TODO: Има Още какво да се желае.
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
        return true;
    }

    public static List<List<Integer>> generateUniqueList(List<List<Integer>> list) {
        Random rnd = new Random();
        int el1 = 0;

        for (int i = 1; i < list.size(); i++) {
            for (int j = 0, p = 0; j < list.get(0).size(); j++, p++) {
                for (int d = 0; d < 6; d++) {

                    el1 = list.get(0).get(p);
                    int el2 = list.get(i).get(d);
                    if (el1 == el2) {
                        list.get(i).remove(d);
                        list.get(i).add(d, rnd.nextInt(1, 50));
                        el2 = list.get(i).get(d);
                        for (int k = 0; k < list.get(i).size(); k++) {        // Търси дали новото се среща в j-я лист.
                            int el3 = list.get(i).get(k);
                            if (d != k && (Objects.equals(list.get(i).get(d), list.get(i).get(k)))) {
                                list.get(i).remove(k);
                                list.get(i).add(k, rnd.nextInt(1, 50));
                                el3 = list.get(i).get(k);
                            }
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
                "Залогът, който си избрал е вариант %s: %s ", this.result, this.yourVariantChoice, this.yourSuppose);
    }

    protected List<Integer> checkResults(List<Integer> result, List<Integer> suppose) {
        String[] resInput;
        String[] suppInput;
        boolean verificationA;
        boolean verificationB;
        Scanner scanner = new Scanner(System.in);

        //TODO: Валидирай Input!

        System.out.print("\nТрябват ми резултата от последният тираж и твоя предишен залог залог. " +
                "\nЩе въведеш резултата или да го търся?: ( i / s ): ");

        String answer = scanner.nextLine().trim();
        while (!answer.equalsIgnoreCase("i")
                && !answer.equalsIgnoreCase("s")) {
            System.out.print("Виж Братле, трябва да избереш между i и s. Дай пак: ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("i")) {
            System.out.print("Въведи последният резултат от тиража: ");
            resInput = scanner.nextLine().trim().split(", ");                       // 112, 12, aa,
            verificationA = inputVerification(resInput);
            while (!verificationA) {
                System.out.print("ЗаПри се Вихъре. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                resInput = scanner.nextLine().trim().split(", ");                       // 112, 12, aa,
                verificationA = inputVerification(resInput);
            }

            result = getDigitFromInput(resInput);

            System.out.print("Въведи твоят залог: ");
            suppInput = scanner.nextLine().trim().split(", ");
            verificationB = inputVerification(suppInput);
            while (!verificationB) {
                System.out.print("ЗаПри се Вихъре. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                suppInput = scanner.nextLine().trim().split(", ");
                verificationB = inputVerification(suppInput);
            }
            suppose = getDigitFromInput(suppInput);
        } else {
            resInput = this.LAST_OFFICIAL_RESULT.trim().split(", ");
            result = getDigitFromInput(resInput);
            suppInput = this.YOUR_LAST_SUPPOSE.trim().split(", ");
            suppose = getDigitFromInput(suppInput);
        }


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
            System.out.print("\nДа се запазят ли предишните резултати? (y / n): ");

            String thisAnswer = scanner.nextLine().trim();
            while (!thisAnswer.equalsIgnoreCase("y")
                    && !thisAnswer.equalsIgnoreCase("n")) {
                System.out.print("Май - май продължаваме с магариите...?: (y / n)?: ");
                thisAnswer = scanner.nextLine().trim();
            }
            if (thisAnswer.equalsIgnoreCase("y")) choice = true;

            BufferedWriter writer =
                    new BufferedWriter(new java.io.FileWriter("newTotoResult.txt", choice));

            File file = new File("newTotoResult.txt");
            if (file.exists()) path = file.getAbsolutePath();

            // writer.write(String.valueOf(timeAndData()));
            writer.newLine();
            writer.write("Предложения за залог - " + this.CIRCULATION + " тираж: \n");
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

//            writer.newLine();
            writer.close();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        if (choice) {
            System.out.print("\nРезултатите са записани в: " + path);
        } else {
            System.out.print("\nРезултатът е записан в: " + path);
        }
    }

    private LocalDateTime getLocalDateTime() {
        String[] dataTimeFormatAnswer;
        Scanner scanner = new Scanner(System.in);

        //TODO: Трябва да валидираш въвеждането на датата.
        dataTimeFormatAnswer = LastDateOfLottery.trim().split(" ");

//        System.out.print("Кога е следващият Тираж? " +
//                "\nВъведи (година месец ден час минути) разделени с интервал (2022 12 25 18 45): ");
//
//        //TODO: Трябва да валидираш въвеждането на датата.
//        dataTimeFormatAnswer = scanner.nextLine()
//                .trim()
//                .split(" ");

        int year = Integer.parseInt(dataTimeFormatAnswer[0]);
        int month = Integer.parseInt(dataTimeFormatAnswer[1]);
        int dayOfMonth = Integer.parseInt(dataTimeFormatAnswer[2]);
        int hour = Integer.parseInt(dataTimeFormatAnswer[3]);
        int minute = Integer.parseInt(dataTimeFormatAnswer[4]);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public String whenTotoTimeIs(LocalDateTime timeOfToto) {
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
