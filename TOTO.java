package task.TOTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TOTO extends TotoPoint {

    // Анти SOLID class.....
    // Необходими са: MainTOTO, TOTO, Calendar и TotoPoint.

    // Нов начин за валидиране на входните масиви: isValidInputList() гетValidInputList();
    // Валидират се и стойносттите на "YOUR_SUPPOSE" и "OFFICIAL_RESULT"

    /**
     * Днешна дата:25.12.2022
     * Номер на тираж: 103
     * Резултат от тиража:
     * Твоят залог: 1, 7, 22, 23, 37, 43 / 23.12.2022 06:14
     */

    private final String YOUR_SUPPOSE = " 4, 7, 15, 39, 47, 48 ";                 //  <-                          *
    private final String OFFICIAL_RESULT = "  ";             //   <-       *
    private final String DATE_OF_LOTTERY = " 2023 04 6 18 45 ";                   //   <-                         *
    private final int TODAY_CIRCULATION = 28;                                      //   <- Промени тук++:          *
    private int CIRCULATION = TODAY_CIRCULATION;
    private List<Integer> result = new ArrayList<>();
    private List<Integer> yourSuppose = new ArrayList<>();
    private List<Integer> variantResult = new ArrayList<>();
    private List<List<Integer>> variants = new ArrayList<>();

    protected int counter = 0;
    private final String MESSAGE1 = "Идеално... Продължавай така.";
    private final String MESSAGE2 = "Имаш %d съвпадения: ";
    private final String MESSAGE3 = "Имаш %d съвпадение: ";
    String yourVariantChoice = "";

    public TOTO() {
    }

    public void play() throws IOException {
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
        //this.CIRCULATION++;
        setResult();
        if (!setYourSuppose()) return;
        printToto();
        writeResult(this.variants, this.result);
    }

    public void checkResult() {
        printCheckedResult(checkResults(), this.counter);
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
        String input;
        boolean verification;

        System.out.print("-> Валидни числа са всички положителни Двуцифрени (12) числа " +
                "от 1 до 49 разделени с запетая и интервал (', ') - (6, 15, 18, 23, 25, 39) \n" +
                "-> Комбинации от сорта: (1а,b3, г6  ааа, -98, ЗЯхF, 654, -1) се приемат за невалидни!\n");

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print("- Трябва ми резултата от последният тираж. " +
                "Ще въведеш резултата или да си го търся?: ( i / s ): ");

        String answer = scanner.nextLine().trim();
        while (!answer.equalsIgnoreCase("i")
                && !answer.equalsIgnoreCase("s")) {
            System.out.print("Виж. Трябва да избереш между i и s. Дай пак: ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("i")) {
            System.out.printf("%s (%d-и) %s", "Въведи резултата от последният", this.CIRCULATION, "тираж: ");
            input = scanner.nextLine();
            verification = isValidInputList(input);
            while (!verification) {
                System.out.print("ЗаПри се Вихъре. -> " + "\"" + input + "\"" + " е грешно. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                input = scanner.nextLine();
                verification = isValidInputList(input);
            }
        } else {
            input = this.OFFICIAL_RESULT;
            verification = isValidInputList(input);
            while (!verification) {
                System.out.print("- Стойността по подразбиране на \"OFFICIAL_RESULT\" ->" +
                        " " + "\"" + input + "\"" + " е грешна." +
                        " Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39 " +
                        "\n- Въведи нова: ");
                input = scanner.nextLine();
                verification = isValidInputList(input);
            }
        }
        this.result = getValidInputList(input);        // Докато не въведеш валиден вход няма да стигнеш до тук....
        return this.result;                            // ??? Защо пак проверяваш с този метод входа, а не го взимаш...
    }

    //TODO: Замества методите: "isNotAString{isNotAString, isDoubleDigits}"
    private static boolean isValidInputList(String inputList) {
        int countDigit = 0;
        String regex = "^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$";
        String[] tmp = inputList.trim().split(", ");

        for (String s : tmp) {
            if (s.matches(regex)) countDigit++;
        }
        return countDigit == tmp.length && tmp.length == 6;
    }

    private static List<Integer> getValidInputList(String inputList) {
        List<Integer> digits = new ArrayList<>();
        String regex = "^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$";
        String[] tmp = inputList.trim().split(", ");

        for (String s : tmp) {
            if (s.matches(regex)) {
                digits.add(Integer.parseInt(s));
            }
        }
        return digits;
    }

//    private List<Integer> getDigitFromInput(String[] input) {                 // TODO: Обмисло го все пак.
//        List<Integer> digits = new ArrayList<>();
//        for (int i = 0; i < input.length; i++) {
//            digits.add(Integer.parseInt(input[i]));
//        }
//        return digits;
//    }

//    private boolean inputVerification(String[] input) {
//        return (input.length == 6) && isNotAString(input);
//    }
//
//    private boolean isNotAString(String[] input) {                       // alskjdlaks, sa - 22, a
//        boolean isNotAString = true;                                     // 12, 7, re, 15, 34, -44
//        for (int i = 0; i < input.length; i++) {                         // asd,as, 12, wewewew, -98, d
//            if ((input[i].length() <= 2)) {                              // 12, 7, 13, 15, 34, 44
//                if (isDoubleDigits(input[i])) {    // Ако това не мине ще имаш Exception при Integer.parseInt(input[i]);
//                    int el = Integer.parseInt(input[i]);                 // next: 2022 12 25 18 45
//                    for (int j = 58; j <= 126; j++) {
//                        if (el == j) {                               // TODO: Стопира ако намери буква в "el".
//                            isNotAString = false;                    //       Идеята е да не минава през целия цикъл
//                            break;
//                        }
//                    }
//                } else return false;
//            } else return false;
//        }
//        return isNotAString;
//    }
//
//    private boolean isDoubleDigits(String input) {                      // 12, ad
//        int count = 0;
//        for (int i = 0; i < input.length(); i++) {
//            for (int j = 49; j <= 57; j++) {
//                int el = (int) input.charAt(i);
//                if (el == j) {
//                    count++;
//                    break;
//                }
//            }
//        }
//        return count == input.length();
//    }

    public boolean setYourSuppose() throws IOException {
        List<List<Integer>> tmp;
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
        tmp = getFinalListOfNumbers();                                           // от TotoPoint.java
        this.variants = generateUniqueList(tmp);

        // TODO: Има Още какво да се желае.
//        this.variants = generateUniqueList(tmp);

//        for (List<Integer> el : this.variants) {
//            Collections.sort(el);
//        }
        System.out.print("Избери между 1, 2, 3: ");

        this.yourVariantChoice = scanner.nextLine();
        while (!this.yourVariantChoice.equalsIgnoreCase("1") &&      // -> false == на 1, 2 или 3
                !this.yourVariantChoice.equalsIgnoreCase("2") &&     // -> true != от 1, 2 или 3
                !this.yourVariantChoice.equalsIgnoreCase("3")) {     // while търси true;
            System.out.print("Избери между 1, 2 и 3 вариант: ");     // true && true = true; true && false = false;
            this.yourVariantChoice = scanner.nextLine();
        }

        switch (yourVariantChoice) {
            case "1" -> this.variantResult = tmp.get(0);
            case "2" -> this.variantResult = tmp.get(1);
            case "3" -> this.variantResult = tmp.get(2);
        }

        Collections.sort(this.variantResult);
        System.out.println("Избрал си: " + this.variantResult.toString() + "\n");
        this.yourSuppose = this.variantResult;
        //this.variants = tmp;
        return true;
    }

    // Сравнява всеки el1 с всеки el2. При съвпадение генерира ново число и проверява дали то се среща в същия масив.
    // Ако се среща го заменя с ново произволно число без да търси повторно съвпадение.
    // [7, 8, 28, 34, 42, 47] [1, 10, 11, 25, 32, 34] [19, 20, 27, 33, 35, 41]
    //  el1                    el2:
    // [7, 8, 28, 34, 42, 47] [1, 10, 11, 25, 32, 34]        -> 1 с 2
    // [7, 8, 28, 34, 42, 47] [19, 20, 27, 33, 35, 41]       -> 1 с 3
    // [1, 10, 11, 25, 32, 34] [19, 20, 27, 33, 35, 41]      -> 2 с 3
    public static List<List<Integer>> generateUniqueList(List<List<Integer>> list) {
        Random rnd = new Random();
        int el1 = 0;
        // TODO: Има ли смисъл от t и p при положение, че имат същото поведение като i и j. i++, j++;
        for (int i = 1, t = 0; i < list.size(); i++, t++) {
            for (int j = 0, p = 0; j < list.get(0).size(); j++, p++) {
                for (int d = 0; d < list.get(0).size(); d++) {

                    el1 = list.get(t).get(p);                      // t -> реперният масив; p -> масива в който търси.
                    int el2 = list.get(i).get(d);
                    if (el1 == el2) {
                        list.get(i).remove(d);
                        list.get(i).add(d, rnd.nextInt(1, 50));
                        el2 = list.get(i).get(d);
                        for (int k = 0; k < list.get(i).size(); k++) {      // Търси дали новото се среща в j-я лист.
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
        for (List<Integer> el : list) {
            Collections.sort(el);
        }
        return list;
    }

    public void printToto() {
        Collections.sort(this.result);
        Collections.sort(this.yourSuppose);
        System.out.printf("Резултата от последният тираж (%d) е:  %s \n" +
                        "Залогът, който си избрал е вариант %s:  %s  ",
                this.CIRCULATION - 1, this.result, this.yourVariantChoice, this.yourSuppose);
    }

    protected List<Integer> checkResults() {
        String resInput;
        String suppInput;
        boolean verificationA;
        boolean verificationB;
        boolean verificationC;
        boolean verificationD;
        Scanner scanner = new Scanner(System.in);

        //TODO: Валидирай Input!

        System.out.print("\nТрябва ми резултата от последният тираж и твоя последен залог. " +
                "\nЩе въведеш резултата или да го търся?: ( i / s ): ");

        String answer = scanner.nextLine().trim();
        while (!answer.equalsIgnoreCase("i")
                && !answer.equalsIgnoreCase("s")) {
            System.out.print("Виж Братле, трябва да избереш между i и s. Дай пак: ");
            answer = scanner.nextLine().trim();
        }

        List<Integer> result;
        List<Integer> suppose;
        if (answer.equalsIgnoreCase("i")) {
            System.out.print("Въведи последният резултат от тиража: ");
            resInput = scanner.nextLine().trim();
//            if (resInput.equalsIgnoreCase("")) resInput = " Това което си въвел ";
            verificationA = isValidInputList(resInput);
            while (!verificationA) {
//                if (resInput.equalsIgnoreCase(" ")) resInput = " Това което си въвел ";
                System.out.print("ЗаПри се Вихъре. -> " + "\"" + resInput + "\"" + " е грешно. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                resInput = scanner.nextLine();
                verificationA = isValidInputList(resInput);
            }

            result = getValidInputList(resInput);

            System.out.print("Въведи твоят залог: ");
            suppInput = scanner.nextLine().trim();
            verificationB = isValidInputList(suppInput);
            while (!verificationB) {
                System.out.print("ЗаПри се Вихъре. -> " + "\"" + suppInput + "\"" + " е грешно. " +
                        "Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39" +
                        "\n" + "Дай пак: ");
                suppInput = scanner.nextLine();
                verificationB = isValidInputList(suppInput);
            }
            suppose = getValidInputList(suppInput);
        } else {
            resInput = this.OFFICIAL_RESULT;
            verificationC = isValidInputList(resInput);
            while (!verificationC) {
                System.out.print("- Стойността по подразбиране на \"OFFICIAL_RESULT\" -> "
                        + "\"" + resInput + "\"" + " е некоректна." +
                        " Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39 " +
                        "\n- Въведи нова: ");
                resInput = scanner.nextLine();
                verificationC = isValidInputList(resInput);
            }

            result = getValidInputList(resInput);
            suppInput = this.YOUR_SUPPOSE;
            verificationD = isValidInputList(suppInput);
            while (!verificationD) {
                System.out.print("- Стойността по подразбиране на \"YOUR_SUPPOSE\" ->" + "\""
                        + suppInput + "\"" + "е некоректна." +
                        " Трябва да бъде нещо от сорта: 6, 15, 18, 23, 25, 39 " +
                        "\n- Въведи нова: ");
                suppInput = scanner.nextLine();
                verificationD = isValidInputList(suppInput);
            }
            suppose = getValidInputList(suppInput);
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
            System.out.println("ERROR < 0");
            return;
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
                System.out.println(tmp.get(i));
            }
        }
    }

    //TODO: Проверявай за актуална дата.
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

            if (thisAnswer.equalsIgnoreCase("n")) {
                System.out.println("\n- Предишната хронология ще бъде ИЗТРИТА.");
                System.out.print("\nДа се запазят ли предишните резултати? (y / n): ");
                thisAnswer = scanner.nextLine().trim();
                while (!thisAnswer.equalsIgnoreCase("y")
                        && !thisAnswer.equalsIgnoreCase("n")) {
                    System.out.print("Май - май продължаваме с магариите...?: (y / n)?: ");
                    thisAnswer = scanner.nextLine().trim();
                }
            }

            BufferedWriter writer =
                    new BufferedWriter(new java.io.FileWriter("TMPResult.txt", choice));         // <-

            File file = new File("TMPResult.txt");                                              // <-
            if (file.exists()) path = file.getAbsolutePath();

            // writer.write(String.valueOf(timeAndData()));
            writer.newLine();
            writer.write("Предложения за залог за " + this.CIRCULATION +"-и"+" тираж: \n");
            for (List<Integer> el : variants) {
                writer.write(el.toString() + "\n");
            }
            writer.append("----------------------------------------------" + "\n");
            writer.write("Избрал си вариант " + this.yourVariantChoice + ": " +
                    this.yourSuppose.toString() + "\n"
                    + "Последен тираж " + (this.CIRCULATION - 1) + "-и" + ": " + lastResult.toString() + "\n");
            writer.append("----------------------------------------------" + "\n");
//            writer.newLine();

            LocalDateTime resultLDT = getLocalDateTime();
            //LocalDateTime resultLDT = getLocalDateTime();
            writer.write(
                    Objects.requireNonNull(whenIsTotoTime(resultLDT)));

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

        //TODO: Трябва да валидираш въвеждането на датата.
        dataTimeFormatAnswer = DATE_OF_LOTTERY.trim().split(" ");

        int year = Integer.parseInt(dataTimeFormatAnswer[0]);
        int month = Integer.parseInt(dataTimeFormatAnswer[1]);
        int dayOfMonth = Integer.parseInt(dataTimeFormatAnswer[2]);
        int hour = Integer.parseInt(dataTimeFormatAnswer[3]);
        int minute = Integer.parseInt(dataTimeFormatAnswer[4]);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public String whenIsTotoTime(LocalDateTime timeOfToto) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a- c 'ден:' HH:mm:ss ч ");

        LocalDateTime now = LocalDateTime.now();

        long days = ChronoUnit.DAYS.between(now, timeOfToto);
        long hours = ChronoUnit.HOURS.between(now, timeOfToto) % 24;
        long minutes = ChronoUnit.MINUTES.between(now, timeOfToto) % 60;


        //TODO: Оправи името на деня в файла да бъде на Кирилица.
        String sDay = " дни ";
        if (days == 1) sDay = " ден ";

        String sHour = " часа ";
        if (hours == 1) sHour = " час ";

        String sMin = " минути ";
        if (minutes == 1) sMin = " минута ";

        Calendar thisDay = null;
        switch (timeOfToto.getDayOfWeek()) {
            case MONDAY -> {
                thisDay = Calendar.ПОНЕДЕЛНИК;
            }
            case TUESDAY -> {
                thisDay = Calendar.ВТОРНИК;
            }
            case WEDNESDAY -> {
                thisDay = Calendar.СРЯДА;
            }
            case THURSDAY -> {
                thisDay = Calendar.ЧЕТВЪРТЪК;
            }
            case FRIDAY -> {
                thisDay = Calendar.ПЕТЪК;
            }
            case SATURDAY -> {
                thisDay = Calendar.СЪБОТА;
            }
            case SUNDAY -> {
                thisDay = Calendar.НЕДЕЛЯ;
            }
        }

        return ("Денят е: " + timeOfToto.format(formatDate) + "\n" + "Днес е:  " + now.format(formatDate) + "\n"
                + "До следващият тираж остават: "
                + days + sDay + "(денят е: " + thisDay + ") "
                + (hours + sHour + "и "
                + (minutes + sMin))
                + "\n");
    }
}

