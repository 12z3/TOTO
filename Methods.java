package training;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;




/*
 * -> The String.valueOf() method converts int to String.
 * -> int i=Integer.parseInt("200") : code to convert a string to an int.
 */

/**
 * БРОЯ на ЗАЕТИТЕ места в масива е TOЧНО равен на ИНДЕКСА на
 * следващият СВОБОДЕН елемент от масива.
 * <p>
 * Class K -> methods: addUser @ removeUser са примери за това.
 * <p>
 * Resize:
 * "if count > arr.length" -> създава нова матрица,
 * за която "brr.length = 2 * (arr.length)"
 * <p>
 * "for(int i = 0; i < a.length; i++){ brr[i] = a[i] };"
 * -> копира елементите от arr в brr, елемент по елемент.
 * <p>
 * и извън for-а  "arr = brr" израза е ясен.
 * "arr" вече е преоразмерена и със старите елементи.
 * <p>
 * "number+=5" (number = number + 5) -> увеличава стойноста на number с 5. Аналогично "number*=5".
 */


public class Methods {

    /**
     * d = см
     * h = см
     * т = мин
     * G=V/t;  1 m3/h -> 1666.67 cm3/мин
     * G= 40 л/сек
     * Височината е в сантиметри.
     */
    public static void volumeOfWell(double higth) {

        double debit = 40.00;                                        // литра / сек -> 2400 л/мин
        double volume = ((3.14 * (1.5 * 1.5 / 4)) * higth * 1000);   // литри
        double time = volume / debit;                                // мин

        System.out.printf("V = %.2f l ->", (3.14 * (Math.pow(1.5, 2) / 4)) * higth * 1000);   // литри
        System.out.printf(" %.2f mˆ3.%n", (3.14 * (Math.pow(1.5, 2) / 4)) * higth);           //  м^3
        System.out.printf("Водата ще стигне за %.0f мин. %n", time / 60);                     //  мин
    }

    public static void heightOfWell(double time) {

        double debit = 40.00;  // литра / сек -> 2400 л/мин
        double volume = (time * debit) * 60;  // мин
        System.out.printf("Обем на водата -> %.0f литра или %.2f м^3. %n", volume, volume / 1000);
    }

    /**
     * - Създава Обект от класа "LocalDateTime" с име "localTime", който използва метода "now()" на класа.
     * Създава втори обект "formaterDate" от класа "DateTimeFormatter" .
     * На променливата "formatDateTime" присвоява стойност "localTime.format(formaterDate)".
     * <p>
     * - Date dt = new Date();
     * System.out.println(dt); -> Sun Jun 13 16:13:23 EEST 2021.
     */

    public static String timeAndData() {
        /*
         Date time =new Date();
         */
        LocalDateTime localTime = LocalDateTime.now();
        //  TODO:    String result = (localTime < 18) ? "Good DAY. " : "Good NIGHT.";
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч ");
        String formattedDateTime = localTime.format(formatDate);

        System.out.println(formattedDateTime);
        return formattedDateTime;
    }

    /*
     * - Трябва да се дооправят и двата метода: "readFile" и "writeFile".
     *
     * @param filePath - не намира файла ако е в друг клас.
     *                 <p>
     *                 / - Scanner myReader = new Scanner(Path.of(String.valueOf(filePath)));
     *                 Scanner myReader = new Scanner(reader); /
     */

    public static void readFile(File filePath) {
        try {
            FileReader reader = new FileReader("text.txt");
            Scanner myReader = new Scanner(reader);

            while (myReader.hasNextLine()) {
                String dataToRead = myReader.nextLine();
                System.out.println(dataToRead);
                reader.close();
            }
        } catch (IOException bx) {
            System.out.println("File Not Found Here");
        }

    }

    /**
     * - Създава Обект "FileWriter writer = new FileWriter("text.txt")" от класа "FileWriter".
     * Използва метода на класа "writer.write" за да чете от даденият файл.
     */

    public static void writeFile(String pathOnFile, String dataToWrite) {
        try {
            FileWriter writer = new FileWriter(String.valueOf(pathOnFile));
            writer.write(dataToWrite);
            writer.close();
        } catch (IOException ex) {
            System.out.println("File Not Found Here");
        }

    }

    /**
     * -  На първата итерация text = "" + *;
     * На втората text = * и text = * + * и т.н.
     */

    public static void repeatStringText(String symbol, int count) {
        String text = "";
        for (int i = 0; i < count; i++) {
            text = text + symbol;
        }
        System.out.println(text);
        ;
    }

    public static void repeatStringBuilder(String symbol, int count) {
        StringBuilder strb = new StringBuilder();
        // TODO: strb.append(String.valueOf(symbol).repeat(Math.max(0, count))); <- замества for
        for (int i = 0; i < count; i++) {
            strb.append(symbol);
        }
        System.out.println(strb);
    }

    /*
     * - public static String repeatString(String symbol, int count) {
     * StringBuilder text = new StringBuilder();
     * for (int i = 0; i < count; i++) {
     * text.append(" ").append(symbol);
     * }
     * return text.toString();
     * }
     */

    public static void fibonacciRow(int numberOfElements) {
        /*
         Fibonacci row is: 0 1 1 2 3 5 8 13 21 34 55 89 144 233...
         */
        try {
            long[] a = new long[numberOfElements * 3];
            System.out.print("Fibonacci row is: ");

            for (int i = 2; i <= numberOfElements + 2; i++) {
                a[0] = a[1] = 1;
                a[i + 1] = a[i - 1] + a[i];
                System.out.print(" " + a[i]);
            }
            System.out.println();
            System.out.printf("%d - elements of Fb rows is = %d ",
                    numberOfElements, a[numberOfElements + 2]);

        } catch (ArrayIndexOutOfBoundsException exception) {
            System.out.println(": -> OutOfBoundsException. ");
        } catch (NullPointerException exception) {
            System.out.println(": -> NullPointerException. ");
        }
    }

    public static int aFibonacci(int numberOfElements) {
        int f0, f1, f2, i;

        f0 = 0;
        f1 = 1;
        f2 = 1;
        i = 0;

        System.out.print("Row is = 0 1 1 ");
        while (i <= numberOfElements - 3) {
            // TODO: За всяка една интерация прави размяната на стойностите на f :
            f2 = f1 + f0;                               // Помисли дали трябва да бъде на този ред или най-отдалу ;)
            f0 = f1;
            f1 = f2;
            i++;
            System.out.printf("%d ", f2);
        }
        return f2;
    }

    public static int aFibonacci1(int n) {
        int f = 0, f1 = 1, f2 = 1;
        System.out.printf("%d %d ", f1, f2);
        for (int i = 0; i < n - 2; i++) {
            f = f1 + f2;
            f1 = f2;
            f2 = f;
            System.out.printf("%d ", f);
        }
        return f;
    }
    public static void printFibonacciSequence(int count) {
        int a = 0;
        int b = 1;
        int c = 1;

        for (int i = 1; i <= count; i++) {
            System.out.print(a + ", ");

            a = b;
            b = c;
            c = a + b;
        }
    }

    public static void main(String[] args) {
        printFibonacciSequence(10);
    }

    /**
     * -  resizeMassive(int[] arr, int lengthPlusNumber):
     * Създава нов масив с нова дължина. Копира елементите, елемент по-елемент,
     * от старият в новият масив. Стария масив го приравнява на новият.
     */

    public static void resizeMassive(int[] arr, int lengthPlusNumber) {

        if (lengthPlusNumber == 0) {
            System.out.println("Breakfast UNDENIORIA ->" +
                    " lengthPlusNumber = " + lengthPlusNumber);
            return;
        }

        int[] temp = new int[arr.length + lengthPlusNumber];
        for (int i = 0; i < arr.length; i++) {
            temp[i] = (int) arr[i];
        }
        arr = temp;
        printIntArray(arr);
    }

    public static void addElement(int[] arr, int oNindex, int elementONIndex) {
        int size = arr.length;
        int[] temp = new int[size + 1];

        try {
            if (validateIndex(arr, oNindex, size)) {
                return;
            }

            copyElementOfArrays(arr, temp);            // TODO: <- 1. Копиране.
            arr = temp;                                // TODO: Освобождава от памета стария масия  arr <-
            shiftLeftElementOfArray(arr, oNindex);     // TODO: <- 2. Преместаване.
            arr[oNindex] = elementONIndex;             // TODO: <- 3. Поставяне.

            System.out.print("Add element - " + elementONIndex
                    + ", oNindex " + oNindex + " -> ");

            printIntArray(arr);

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println(" -> Index Out Of Bounds Exception.");
        }
    }

    /**
     * Премества всички елементо от oNindex на дясно с една позиция.
     * ТуЙ тънкият момент тук при пренареждането на елементите е в обхождането на масива.
     * < i++ или i-- >.
     */

    public static void shiftLeftElementOfArray(int[] arr, int oNindex) {

        int[] temp = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            temp[i] = arr[i];
        }
        arr = temp;                                       // TODO: Освобождава от памета стария масия  arr <-

        for (int i = arr.length - 1; i <= oNindex; i--) { // TODO: губя елемента в/у който замествам.
            arr[i + 1] = arr[i];  // <-
            // TODO: size++;  // <- Много често се забравя.
        }

//        for (int i = oNindex; i < size-1 ; i++) {
//            arr[i+1] = arr[i];  // <-
//        }
    }

    /**
     * Не изпълнява дявола този ред: "arr = brr".
     * Идея си нямам защо.
     */

    // TODO: Копира елементите на даден масив в друг.
    public static void copyElementOfArrays(int[] arr, int[] brr) {

        for (int i = 0; i < arr.length; i++) {
            brr[i] = arr[i];                           // TODO: <- 1 Копира Елементите.
        }
        arr = brr;
    }

    private static boolean validateIndex(int[] arr, int oNindex, int size) {
        if (oNindex < 0 || oNindex > size) {
            System.out.print(
                    "Index-> " + oNindex +
                            " Cannot be > " + arr.length + " = arr.length.");
        }
        return false;
    }

    /**
     * // TODO: Проверява дали индекса е валиден.
     *
     * @param arr -> Проверявания масив
     * @return -> резултата от проверката.
     */
    public static boolean checkPosition(int[] arr) {
        int size = arr.length - 1;
        boolean isNoValidIndex = false;
        try {
            if (size > arr.length + 1) {
                isNoValidIndex = true;
                throw new ArrayIndexOutOfBoundsException();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.print("Index Out Of Bounds");
        }
        return isNoValidIndex;
    }


    // TODO: Добавя елеемнт на определена позиция

    public static void addElementIntoArray(int newElement, int[] arr, int index) {
        int size = arr.length;
        int countZeroElement = 0;

        if (checkPosition(arr)) {
            System.out.println("Index is OUT Of BOUNDS!");
            return;
        }
        for (int i = 0; i < size; i++) {
            if (arr[i] == 0) {
                countZeroElement++;
            }
        }

        if (countZeroElement <= 3) {
            int[] temp = new int[size + size / 2];
            for (int i = 0; i < size; i++) {             // {1, 2, 5, 3, 67, 8, 9, 0, 0, 0, 0};
                temp[i] = arr[i];                        //  0  1  2  3  4   5  6 - indexes.
            }
            arr = temp;                                  // Изчиства от памета старият масив "arr"  <-- !!!
        }
        // Отзад напред до "index" включително <--
        for (int i = size; i > index; i--) {            // Премества елементи на ДЯСНО от "index" с една позиция.
            arr[i] = arr[i - 1];                         // Последният става = на предпоследният и т.н. до index вкл.
        }

        try {
            arr[index] = newElement;                     // На позиция "index" слага новият елемент.
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("ERROR -> Index is OUT Of BOUNDS!");
        }

        printArrayWithOutZeroElements(arr);
    }


    // TODO: Премахва елемент от дадена позиция.

    public static void removeElementFromArray(int index, int[] arr) {
        int size = arr.length;

        if (checkPosition(arr)) {
            System.out.println("Index is OUT Of BOUNDS!");
            return;
        }
        int removedEl = arr[index];
        // От следващият след "index" елем. до последният.
        for (int i = index + 1; i < arr.length; i++) {  // Тъй като е "arr[i - 1] = arr[i]" то трябва "i = index + 1".
            arr[i - 1] = arr[i];                        // Премества елементи на ЛЯВО от "index" с една позиция.
        }
        //size--;
        arr[arr.length - 1] = 0;
        printArrayWithOutZeroElements(arr);
    }


    // TODO: Намира факториала на дадено число -> Рекурсия.

    public static long factoriall(int n) {
        if (n == 0) {
            return 1;

        } else {
            return (n * factoriall(n - 1));
        }
    }

    // TODO: Намира факториала на дадено число -> for - цикъл.

    public static void factorial(int n) {
        int temp = 1;
        for (int i = 1; i <= n; i++) {
            temp *= i;
        }
        System.out.print(n + "!" + " = " + temp);
        System.out.printf("%d!\n", temp);
    }

    // TODO: Намира факториала на дадено число -> for - цикъл.

    public static void factorialOfForCycle(int n) {
        int factorial = 1;
        for (int i = 1; i <= n; i++) {
            factorial *= i;
        }
        System.out.printf("%d ", factorial);
    }

    // TODO: Печата масива на Обратно.

    public static void printReversedArray(int[] arr) {
        System.out.print("[");
        System.out.print(arr[arr.length - 1]);

        for (int i = arr.length - 2; i >= 0; i--) {
            System.out.print(" ," + arr[i]);
        }
        System.out.print("]");
        System.out.println();
    }

    // TODO: Печата масив от "int".

    public static void printIntArray(int[] arr) {
        System.out.print("[");
        System.out.print(arr[0]);

        for (int i = 1; i < arr.length; i++) {
            System.out.print(", " + arr[i]);
        }
        System.out.print("]");
        System.out.println();
    }

    // TODO: Печата масив от "String".

    public static void printStringArray(String[] arr) {
        System.out.print("[");
        System.out.print(arr[0]);

        for (int i = 1; i < arr.length; i++) {
            System.out.print(", " + arr[i]);
        }
        System.out.print("]");
        System.out.println();
    }


    //  TODO: Сравнява дали елементите на два масива са еднакви
    //   без значение на местата на елeментите в масива.

    public static void identicalMassive(int[] arr, int[] brr) {
        int countA = 0, countB = 0, count = 0;
        int[] resultAinA = new int[arr.length];                    // arr = {0, 0, 3, 0, 33, 3};
        int[] resultAinB = new int[brr.length];                    // brr = {0, 0, 3, 3, 33, 0};

        int countOfElementsInArray = arr.length;
        if (arr.length != brr.length) {
            System.out.print("Array is Not IDENTICAL \n");
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            countA = 0;
            countB = 0;                       // За всеки елемент брояча се нулира.
            for (int j = 0; j < arr.length; j++) {
                if (arr[i] == arr[j]) {
                    countA++;                             // Колко пъти даден елемент се съдържа в 1-я масив.
                    resultAinA[i] = countA;               // = [4, 4, 1, 4, 1, 4]
                }
            }
            for (int j = 0; j < brr.length; j++) {
                if (arr[i] == brr[j]) {
                    countB++;                            // Колко пъти същият елемент се съдържа в 2-я масив.
                    resultAinB[i] = countB;              // = [4, 4, 1, 4, 1, 4]
                }
            }
        }
//        if (countA == countB) {                         // count помни бройката съвпадения само за последният елемент.
//            System.out.print("Array is Identical \n");  // ... Не върши работа в този си вид.
//        } else {                                               // {0, 0, 3, 0, 133, 3}
//            System.out.print("Array is Not IDENTICAL \n");     // {0, 0, 3, 3, 33, 0}; -> Гърми
//        }
        for (int i = 0; i < countOfElementsInArray; i++) {
            if (resultAinA[i] == resultAinB[i]) count++;
        }
        if (count == countOfElementsInArray) {
            System.out.print("Array is Identical \n");
        } else {
            System.out.print("Array is Not IDENTICAL \n");
        }
    }


    //  TODO: Сравнява дали елементите на два масива са еднакви
    //   без значение на местата на елeментите  в масива.
    //   Не работи:
    public static boolean identicalArray(int[] a, int[] b) {
        boolean[] isMacH = new boolean[a.length];
        boolean isIdentical = false;
        int count = 0;

        if (a.length != b.length) {
            System.out.print("Array is Not IDENTICAL \n");
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            isMacH[i] = false;
            for (int j = 0; j < a.length; j++) {
                if (a[i] == b[j]) {
                    isMacH[i] = true;
                }
            }
        }
        for (boolean el : isMacH) {
            if (el) {
                count++;
            }
            if (count == 12) {                                         // ?... някакво Магическо Чисълце
                isIdentical = true;
            } else {
                isIdentical = false;
            }
        }
        if (isIdentical) {
            System.out.print("Array is Identical \n");
        } else {
            System.out.print("Array is Not IDENTICAL \n");
        }
        return isIdentical;
    }

    // TODO: Намира Минимална стойност в масив.

    public static void findMin(int[] arr) {
        long min = Integer.MAX_VALUE;
        System.out.print("MinValue element =");

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        System.out.print(" " + min + ";" + "\n");
    }

    // TODO: Намира Максимална стойност в масив.

    public static void findMax(int[] arr) {
        long max = Integer.MIN_VALUE;
        System.out.print("MaxValue element =");

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        System.out.print(" " + max + ";" + "\n");
    }

    // TODO: Премахва Нулевите елементи от даден масив

    // TODO: -> Много ме кефи: - От ред 384 до 397-и = 4 дни мислене.!!! <-
    public static void printArrayWithOutZeroElements(int[] array) {
        System.out.print("New array is: ");
        int countNoZeroElement = 0, index = -1;

        for (int arrayElements : array) {
            if (!(arrayElements == 0)) {                            // <- 1.
                countNoZeroElement++;
            }
        }
        int[] noZeroMassive = new int[countNoZeroElement];          // TODO: Тук е Магията. Ред: 389 -> 400.
        for (int arrayElements : array) {
            if ((arrayElements == 0)) {                             // <- 2.
                continue;                                           // <- 3.
            } else {
                index++;                                            // <- 4.
            }
            noZeroMassive[index] = arrayElements;                   // <- 5.
        }

        array = noZeroMassive;                 // TODO: <- Референцията на arr вече сочи към адреса на temp в Heap-a.
        printIntArray(array);
    }


    // TODO: Печата не-нулевите елементо от даден масив

    public static void printNoZeroIntDArray(int[] arr) {
        boolean isMatch = false;
        int count = 0, countZeroElement = 0;
        System.out.print("[");

        // Проверява нулев ли е елемента. Ако е Да, брой и премини на следващия Индекс.
        // Ако ли не изпринтирай го. Като провериш И условието:
        // "if (index > countZeroElement)" -> условието за печатане на запетайте.

        // ВАЖНО:
        // Печата ", " ако е изпълнено условието -> (index > countZeroElement).
        // И едва След това печата елемента ако е различен от нула независимо от условието за запетаята.

        for (int index = 0; index < arr.length; index++) {
            if ((arr[index] == 0)) {
                countZeroElement++;                // Брои нулевите елементи. {0,0,3,0,33,0}
            } else {
                if (index > countZeroElement) {   // Ако индекса на текущия > Броя на нулевите елементи то.... Печатай.
                    System.out.print(", ");       // ... Първо печата ", "... при изпълнено условие...
                }
                System.out.print(arr[index]);     // ... след това числото независимо от ", ". Важно е.
            }
        }

        System.out.print("]");
        System.out.println();
    }


    // TODO: Сравнява две думи за съвпадение.

    public static boolean compareWords(String aWord, String bWord) {
        boolean isItDifferenceChar = false, isItMatch = false;
        int countDifferenceChar = 0, countSameChar = 0, indexI = 0, tempIndex = -1;

        char[] differenceCharS = new char[aWord.length()];
        char[] temp = new char[aWord.length()];

        List<String> listArray = new ArrayList<>();

        // TODO: Да се обработят Изключенията за Цифри и "Други" символи от ASCII таблицата.
        // TODO:  <- Какъв трябва да е Размера на масивите: "char[], anotherCharS" и "indeX" ?

        if (!(aWord.length() == bWord.length())) {
            System.out.printf("%s %n", "These Are Different Words!");
            return isItMatch;
        } else {

            //  TODO: <- Дебъгни го. Интересно Е!
            for (int chartIndex = 0; chartIndex < aWord.length(); chartIndex++) {
                // isItDifferenceChar = false;                       // Поне един char да е различен.....
                if (!(aWord.charAt(chartIndex) == bWord.charAt(chartIndex))) {
                    countDifferenceChar++;
                    isItDifferenceChar = true;                        // Никъде не си "нулира" стойноста ?

                    tempIndex++;
                    indexI = chartIndex;
                    differenceCharS[indexI] = bWord.charAt(indexI);   //Пази Чара който е различен на същият Индекс.
                    // temp[chartIndex] = bWord.charAt(indexI);          // СПАГЕТИ НА ПОРАЗИЯ...
                    temp[indexI] = bWord.charAt(chartIndex);

                    //if (some == 1){
                    // TODO: -> System.out.printf("There are %d different char. %n", countAnotherChar);
                    //}
                    System.out.printf("(%c) on index ", bWord.charAt(indexI));
                    System.out.printf("%d; ", indexI);
                } else {
                    countSameChar++;
                }
            }

            if (isItDifferenceChar && countDifferenceChar >= 1) {        // Поне един char да е различен.....
                System.out.println();

                System.out.printf("%s %n", "The words do not match!");
                System.out.printf("There are %d different char. %n", countDifferenceChar);

                String differenceCharsMassive = Arrays.toString(differenceCharS);
//                String temP = Arrays.toString(temp);

                System.out.printf("Difference massive of chars is: -> %s %n", differenceCharsMassive);

            } else if (!isItDifferenceChar && countSameChar == aWord.length()) {
                isItMatch = true;
                System.out.printf("It's Ok. These Words are the Same! %n");
            }
        }
        return isItMatch;
    }

    public static boolean compareTwoWords(String wordA, String wordB) {
        boolean isNoMatch = true;


        Map<Integer, Character> different = new LinkedHashMap<>();

        Stack<Character> aWord = new Stack<>();
        Stack<Character> bWord = new Stack<>();

        if (wordA.length() != wordB.length()) {
            return false;
        }

        for (int i = 0; i < wordA.length(); i++) {
            aWord.add(wordA.charAt(i));
            bWord.add(wordB.charAt(i));
        }

        for (int i = 0; i < wordA.length(); i++) {
            isNoMatch = true;
            if (aWord.get(i) != bWord.get(i)) {
                isNoMatch = false;
                different.put(i, aWord.get(i));
            }
        }

        if (!isNoMatch) System.out.println("Different are: ");
        for (Map.Entry<Integer, Character> el : different.entrySet()) {
            System.out.printf("Index: %d, Char: %c%n", el.getKey(), el.getValue());
        }

        return isNoMatch;
    }

    public static void wordSearchInText(String text, String word) {
        int count = 0, index = 0, fromIndex = 0, j = 0;

        while (j < text.length()) {
            index = text.indexOf(word, fromIndex);
            if (index != -1) {
                fromIndex = index + 1;
                count++;
            }
            j++;
        }
        System.out.printf
                ("Word '%s' is matched %d times in text: %s%n",
                        word, count, text);
    }

    public static void charSearchInText(String text, char x) {
        int count = 0, index = 0, fromIndex = 0, j = 0;
        while (j < text.length()) {
            index = text.indexOf(x, fromIndex);
            if (index != -1) {
                fromIndex = index + 1;
                count++;
            }
            j++;
        }
        System.out.printf("Char '%c' is matched %d times in text: %s%n",
                x, count, text);
    }

    public static boolean compareTwoIntArray(int[] a, int[] b) {
        boolean isMatch = false;
        if ((a.length != b.length)) return false;
        for (int i = 0; i < a.length; i++) {
            isMatch = false;
            for (int j = 0; j < b.length; j++) {
                if (a[i] == b[j]) {
                    isMatch = true;
                    break;
                }
            }
        }
        return isMatch;
    }

    public static <T> boolean isASameElementsOfLists(List<T> a, List<T> b) {
        boolean isMatch = false;
        T element = null;
        if ((a.size() != b.size())) return false;
        for (int i = 0; i < a.size(); i++) {
            isMatch = false;
            for (int j = 0; j < b.size(); j++) {
                if (a.get(i).equals(b.get(j))) {
                    isMatch = true;
                    element = a.get(i);
                    break;
                }
            }
            if (isMatch) break;
        }
        System.out.print(element + " ");
        return isMatch;
    }

    private static void searchingForRepetitiveElementsInArray(int[][] arr) {
        int countElement = 0, elementToChek = 0,
                element = 0, maxCount = Integer.MIN_VALUE,
                elementToChekIndex = 0, elementIndex = 0, allCount = 0, countPrint = 0;

        String index = "", elementToChekStringIndex = "", elementStringIndex = "";
        List<Integer> list = new ArrayList<>();

        //  TODO: {{33, 33, 33}, {3, 33, 3}, {3, 33, 33}};
        System.out.println("-------------------");                           //  00  01  02,  10  11 12,   20  21  22
        for (int thisRow = 0; thisRow < arr.length; thisRow++) {
            for (int thisColl = 0; thisColl < arr[thisRow].length; thisColl++) {

                // За Всеки Елемент от Всяка Колона на дедения Ред:
                // Дефинира Реперният елемент.

                elementToChek = arr[thisRow][thisColl];
                elementToChekStringIndex = "" + thisRow + thisColl;                 // 1 + 2 = 12 as String
                elementToChekIndex = Integer.parseInt(elementToChekStringIndex);

                if (!(countElement == 0)) {
                    System.out.printf("%d = %d checks. %n%n", elementToChek, countElement);

//                    if (!(list.isEmpty())){
//                        System.out.print(Arrays.toString(list.toArray()));
//                        System.out.printf(", size = %d. %n", list.size());
//                    }
//                    list.add(elementToChek);
                }
                if (!(countElement == 0)) {
                    System.out.println("-------------------");
                }                                                                   //TODO: Брои - Печата - Нулира !
                countElement = 0;                     // Нулира "countElement" при избор на нов елемент "elementToChek".
                for (int findRow = 0; findRow < arr.length; findRow++) {
                    for (int findColl = 0; findColl < arr[findRow].length; findColl++) {

                        // За Всеки Елемент от Всяка Колона на дедения Ред:
                        // Дефинира Елемента който ще се сравнява с Реперният.

                        element = arr[findRow][findColl];
                        elementStringIndex = "" + findRow + findColl;
                        elementIndex = Integer.parseInt(elementStringIndex);

                        // Изключва текущия(thisRow, thisColl) от търсенето.
                        if (thisRow == findRow && thisColl == findColl) {
                            continue;
                        }                                         // ??? Ako i-я е проверен да не се сравнява с текущия.
                        if (elementToChekIndex > elementIndex) {
                            continue;
                        }
                        if (elementToChek == element) {

                            countElement++;
                            allCount += countElement;

                            System.out.printf("%d[%d%d] -> ", elementToChek, thisRow, thisColl);
                            System.out.printf("%d(%d%d) = ", element, findRow, findColl);
                            System.out.printf("%d;    ", countElement);
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

//  TODO: Генерира произволни числа.
//        Числата се генерират от числата на елементите на масив(String-a) с уникални индекси.
//        Уникални са неповтарящите се индекси.
//        int index = (int) ((Math.random() * (Max - Min)) + Min);

    public static long generateRandomNumberFromElementOfArray(int[] massive) {
        long thisDigit = 0;
        StringBuilder stb = new StringBuilder();

        int randomIndex = 0, i = 0, j = massive.length, countsDuplicate = 0, l = 0;
        int[] arrayOfIndexes = new int[massive.length];
        int[] arrayOfElements = new int[massive.length];

        ArrayList<Integer> tmpIndexes = new ArrayList<>();
        ArrayList<Integer> tmpElement = new ArrayList<>();

        while (j > 0) {
            boolean isMach = false;
            // При всяка итерация Избира произволен индекс от 0 до massive.length
            randomIndex = (int) ((Math.random() * (massive.length)));
            arrayOfIndexes[i] = randomIndex;                 // Запълва (+ Ред: 100) масива с произволни индекси
            if (i != 0) {                                    // За всеки индекс "i" различен от 0-ят прави следното:
                for (int k = 0; k < i; k++) {                // За Всеки от индексите "i" генерирани до момента провери:
                    if (arrayOfIndexes[i] == arrayOfIndexes[k]) {   // Ако текущият "i" = на някой от предишните "k" то:
                        countsDuplicate++;                           // Излез от if - a и отиди на Ред: 51
                        isMach = true;
                        break;
                    }
                }
            }
            if (isMach && (countsDuplicate != 0)) {
                tmpIndexes.add(arrayOfIndexes[i]);
                tmpElement.add(massive[randomIndex]);
            }
            if (!isMach) {                              // Ако няма съвпадение на индексите то:
                randomIndex = arrayOfIndexes[i];  // Това ще се изпълни тогава и само тогава когато "!isMach"
                arrayOfElements[i] = massive[randomIndex]; // Ще вземе стойността от "randomIndex" -
                // - при условие, че "!isMach"
                stb.append(arrayOfElements[i]);
                thisDigit = Long.parseLong(String.valueOf(stb));
                i++;                                  // "i" се променя само тогава когато isMatch = false.
                j--;                                      // Ако няма съвпадение на индексите то намали брояча "j"
            }                             // При наличие на съвпадение на индексите "isMatch" j не променя стойността си
        }
        printAllInfo("From Array: ",
                arrayOfIndexes, arrayOfElements, countsDuplicate, tmpIndexes, tmpElement);
        return thisDigit;
    }

    public static int generateRandomNumberFromElementOfString(String text) {
        StringBuilder result = new StringBuilder();
        int randomElement = 0;
        int randomIndex = 0, thisDigit = 0, i = 0,
                j = text.length(), countsDuplicate = 0, l = 0;

        int[] arrayOfIndexes = new int[text.length()];
        int[] arrayOfElements = new int[text.length()];
        int[] array = new int[text.length()];

        ArrayList<Integer> tmpIndex = new ArrayList<>();
        ArrayList<Integer> tmpElement = new ArrayList<>();

        while (j > 0) {
            boolean isMach = false;
            randomIndex = (int) ((Math.random() * (text.length())));
            arrayOfIndexes[i] = randomIndex;          // Ако е имало съвпадение "i, j" няма да си променят стойносттите
            if (i != 0) {                             // На следващата итерация, arrayOfIndexes[i] = на нов Рандом.
                for (int k = 0; k < i; k++) {         // ... и логиката се повтаря.
                    if (arrayOfIndexes[i] == arrayOfIndexes[k]) {    // Индексите, а не елемрнтите проверява
//                        arrayOfElements[i] == arrayOfElements[k]   // Проверката дали елементите съвпадат -
                        countsDuplicate++;                           // - препълва Стека на Паметта ;( "Java heap space"
                        isMach = true;
                        break;
                    }
                }
            }
            if (isMach) {
                long element = Long.parseLong(String.valueOf(text.charAt(arrayOfIndexes[i])));
                tmpIndex.add(randomIndex);
                tmpElement.add(Math.toIntExact(element));
                l++;
            }
            if (!isMach) {
                long element = Long.parseLong(String.valueOf(text.charAt(arrayOfIndexes[i])));    // Вземи елемента
                randomElement = Math.toIntExact(element);                                         // Направи го на long
                randomIndex = arrayOfIndexes[i];
                arrayOfElements[i] = randomElement;
                result.append(randomElement);
                thisDigit = Integer.parseInt(String.valueOf(result));
                i++;
                j--;
            }
        }
        printAllInfo("From String: ",
                arrayOfIndexes, arrayOfElements, countsDuplicate, tmpIndex, tmpElement);
        return thisDigit;
    }

    public static void printAllInfo(String x, int[] arrayOfIndexes,
                                    int[] arrayOfElements, int countsDuplicate,
                                    ArrayList<Integer> tmpIndexes,
                                    ArrayList<Integer> tmpElement) {
        System.out.println();
        System.out.println(x);
        System.out.print("Indexes is: ");
        printIntArray(arrayOfIndexes);
        System.out.print("Array is: ");
        printIntArray(arrayOfElements);
        System.out.println();
        System.out.println("Count of Duplicate indexes = " + countsDuplicate);

        System.out.print("Duplicate indexes is: ");
        // printArrayList(tmpIndexes);
        //System.out.println(Arrays.toString(tmpIndexes.toArray()));                // How to print ArrayList <-
        System.out.print("Duplicate element on these indexes: ");
        //printArrayList(tmpElement);
        System.out.print("Number is = ");
    }

//   TODO: Валидира дали въвеждания литерал е число (int)
//                      ... Оправи "indexAtInt". Не ми харесва - Ред: 885
//        111 && 1 -> true
//        1a1 && a11 && a && aaa -> false

    public static int validateInputIndexFromArray(int[] array) {
        boolean isItInt = true;
        Scanner scanner = new Scanner(System.in);
        StringBuilder stb = new StringBuilder();
        String inputString = "";
        int count = 0, l = 0, digit = 0, indexAtInt = 0;
        System.out.println("Enter index number");

        while (true) {
            isItInt = true;
            System.out.print("Index must be in " + 0 + " to " + (array.length - 1) + ": ");
            inputString = (scanner.nextLine());
            stb.append(inputString);

            while (inputString.isEmpty()) {
                System.out.print("Index must be in " + 0 + " to " + (array.length - 1) + ": ");
                inputString = (scanner.nextLine());
            }
            for (int j = 0; j < inputString.length(); j++) {
                for (int i = 58; i <= 127; i++) {
                    if (i == inputString.charAt(j)) {
                        isItInt = false;
                        break;
                    }
                }
                for (int i = 0; i <= 47; i++) {
                    if (i == inputString.charAt(j)) {
                        isItInt = false;
                        break;
                    }
                }
            }
            printInfo(isItInt, stb, inputString);
            if (isItInt) {
                indexAtInt = Integer.parseInt(inputString);
            }
            if (isItInt && indexAtInt < array.length) {           // Индекса да е Число && Индекса < размера на масива
                digit = Integer.parseInt(inputString);
                break;                                            // Брейква "while"
            }
        }
        System.out.print("Index = ");
        return digit;
    }

    public static void printInfo(boolean isItInt, StringBuilder stb, String inputString) {
        System.out.println("isItInt = " + isItInt);
        System.out.println("inputString = " + inputString);
        //System.out.println("String builder is: " + stb);
    }

    // TODO: Проверява дали даден литерал е Текст или Число.

    public static boolean isItANumber() {
        Scanner scanner = new Scanner(System.in);
        int countLiteral = 0, countSymbol = 0, countDigit = 0;
        boolean isItANumber = false, isIt = false;
        boolean isItALiteral = false;
        boolean isItASymbol = false;

        StringBuilder literal = new StringBuilder();
        StringBuilder symbol = new StringBuilder();
        StringBuilder digit = new StringBuilder();

        System.out.print("Write somethings: ");
        String input = scanner.nextLine();

        for (int i = 0; i < input.length(); i++) {
            for (int j = 0; j <= 47; j++) {                        // Проверка за Литерали
                if (input.charAt(i) == j) {
                    countSymbol++;
                    isItASymbol = true;
                    symbol.append(input.charAt(i));
                }
            }
            for (int j = 58; j <= 127; j++) {                      // Проверка за Символи
                if (input.charAt(i) == j) {
                    countLiteral++;
                    isItALiteral = true;
                    literal.append(input.charAt(i));
                }
            }
            for (int j = 48; j <= 57; j++) {                      // Проверка за Числа
                if (input.charAt(i) == j) {
                    countDigit++;
                    isItANumber = true;
                    isIt = true;
                    digit.append(input.charAt(i));
                }
            }
        }
        // Отпадна. проверката за числа и Символи я прави заедно с литералите. няма смисъл да се викат два еднакви фор-а

//        for (int i = 0; i < input.length(); i++) {
//            for (int j = 58; j <= 127; j++) {                       // Проверка за Символи
//                if (input.charAt(i) == j) {
//                    countSymbol++;
//                    isItASymbol = true;
//                    symbol.append(input.charAt(i));
//                } else isItANumber = false;                         // !!! Не Го Разбирам
//            }
//            for (int j = 48; j <= 57; j++) {                        // и отново Проверка за Числа
//                if (input.charAt(i) == j) {
//                    countDigit++;
//                    isItANumber = true;                            // Това е в сила за стария случай с четерите фор-а
//                    if (!isIt) digit.append(input.charAt(i));       // Ако Ред; 38 е изпълнен и се изпълни и Ред: 54
//                }                                                   // това Ще добави едно и също число към "stbInt"
//            }                                                       // ... за това е цялата тази сложнотия с "isIt"
//        }

        if (isItALiteral || isItASymbol) {
            isItANumber = false;
        }

        if (((countLiteral != 0) && (countSymbol != 0) && (countDigit != 0))) {
            System.out.print(
                    "This is a TEXT!\nLetter is: " + literal + ";\nSymbol is: " + symbol + ";\nDigit is: " + digit + ";\n");
        } else if ((countLiteral != 0) && (countDigit != 0)) {
            System.out.print(
                    "This is a TEXT!\nletter is : " + literal + ";\nDigit is: " + digit + ";\n");
        } else if (((countSymbol != 0) && (countDigit != 0))) {
            System.out.print(
                    "This is a TEXT!\nSymbol is: " + symbol + ";\nDigit is: " + digit + ";\n");
        } else if (countLiteral != 0 && countSymbol != 0) {
            System.out.print(
                    "This is a TEXT!\nSymbol is: " + symbol + ";\nLetter is: " + literal + ";\n");
        } else if (countSymbol != 0) {
            System.out.print(
                    "This is a TEXT!\nSymbol is: " + symbol + ";\n");
        } else if (countLiteral != 0) {
            System.out.print(
                    "This is a TEXT!\nLetter is: " + literal + ";\n");
        } else if (countDigit != 0) {
            System.out.println("This is a Number: " + digit);
        }
        return isItANumber;
    }

    // TODO: Премахва Нулевите елементи от даден масив - Едномерен.
    public static int[] removeZeroElementsFromArray(int[] array) {
        int countNoZeroElement = 0, index = 0;

        for (int k : array) {
            if (k != 0) countNoZeroElement++;
        }
        int[] tmp = new int[countNoZeroElement];
        for (int j : array) {
            if ((j != 0) && (index < tmp.length)) {
                tmp[index] = j;
                index++;
            }
        }
        array = tmp;
        return array;
    }

    // TODO: Премехва дублиращите се елементи в масива - направи го с Map.
    private static int[] removeDuplicateElementsInArray(int[] arr) {
        int countDuplicate = 0, index = 0;
        boolean isChecked = false;
        int[] checkedElements = new int[arr.length];

        for (int i = 0; i < arr.length; i++) {
            isChecked = false;

            if (i != 0) {
                for (int j = 0; j < checkedElements.length; j++) {
                    if (arr[i] == checkedElements[j]) {                            // Търси съвпадения
                        isChecked = true;
                        break;
                    }
                }
            }
            if (isChecked) continue;                                      // arr = {1, 2, 1, 1, 2, 2, 3, 4}
            checkedElements[i] = arr[i];                                  // checkedElements = {1, 2, 0, 0, 0, 0, 3, 4}

            for (int j = i + 1; j < arr.length; j++) {             // Не стига до тук ако е проверяван елемента
                if (arr[i] == arr[j]) {                    // и има съвпадение -> if (arr[i] == checkedElements[j])
                    countDuplicate++;
                }
            }
        }
        int mach = 0;
        int[] tmp = new int[arr.length - countDuplicate];

        for (int i = 0; i < checkedElements.length; i++) {
            if (checkedElements[i] != 0) {
                tmp[index++] = checkedElements[i];
            }
//            for (int i = 0; i < arr.length; i++) {
//                mach = 1;
//                for (int j = i + 1; j < arr.length; j++) {
//                    if (arr[i] == arr[j]) {
//                        mach++;
//                    }
//                }
//                if (mach == 1) {
//                    tmp[index++] = arr[i];                 // Записва елементи с Индекси -> 1, 4, 5
//                }                                          // Първо добавя елемента в tmp[l], след това увеличава л с 1.
        }
        arr = tmp;
        return arr;
    }

    // TODO: String to char[]
    public static char[] stringToCharMassive(String text) {
        char[] charsMassive = new char[text.length()];

        for (int i = 0; i < charsMassive.length; i++) {
            charsMassive[i] = text.charAt(i);
        }
        return charsMassive;
    }

    public static <T> void print1DMassive(T[] massive) {
        for (int i = 0; i < massive.length; i++) {
            System.out.print(massive[i] + " ");
        }
    }

    public static <T> void print2DMassive(T[][] massive) {
        for (int rows = 0; rows < massive.length; rows++) {
            for (int cols = 0; cols < massive[rows].length; cols++) {
                System.out.print(massive[rows][cols] + " ");
            }
            System.out.println();
        }
    }

    // TODO: "searchDuplicates" използва: "findDuplicated" и "isChecked".
    public static void searchDuplicates(int[] arr) {
        int[] matchesArr = new int[arr.length];

        LOOP:
        for (int i = 0; i < arr.length; i++) {
            int matches, el1 = arr[i], index = i + 1;

            if (i == 0) {
                matches = findDuplicated(arr, el1, index);
            } else {
                if (!isChecked(arr, arr[i], i)) {
                    matches = findDuplicated(arr, el1, index);
                } else continue LOOP;
            }
            matchesArr[i] = matches;
            System.out.printf("Element %d = finds %d times %n", el1, matches);
        }

        for (int el : matchesArr) System.out.print(el + " ");
    }

    private static int findDuplicated(int[] arr, int el1, int index) {
        int matches = 1;
        for (int i = index; i < arr.length; i++) {
            int el2 = arr[i];
            if (el1 == el2) {
                matches++;
            }
            index++;
        }
        return matches;
    }

    private static boolean isChecked(int[] arr, int el, int index) {
        for (int j = index - 1; j >= 0; j--) {
            if (el == arr[j]) return true;
        }
        return false;
    }

    public static long getStartTime() {
        Date dateStart = new Date();
        return dateStart.getTime();
    }

    public static long getEndTime(long start) {
        Date dateEnd = new Date();
        long end = dateEnd.getTime();
        System.out.println();
        //System.out.println((end - start) + " ms");
        return end;
    }

    public static String whenTotoTimeIs1(LocalDateTime timeOfToto) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч ");
        ;
        LocalDateTime now = LocalDateTime.now();

        int count = 0;
        int days = timeOfToto.getDayOfMonth() - now.getDayOfMonth();
        while (days != 0) {
            days--;
            count++;
        }
        long time1 = now.getHour();
        long time2 = timeOfToto.getHour();

        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();

        return ("The Day is: " + timeOfToto.format(formatDate) + "\n"
                + "Reminders: "
                + count + " days (" + now.getDayOfWeek() + ") "
                + (Math.abs((time1 - time2))) + " hours " + "and "
                + (Math.abs(min1 - min2)) + " minutes");
    }
}



