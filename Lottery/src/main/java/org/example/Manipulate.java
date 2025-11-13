package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Клас {@code Manipulate} изпълнява ключова роля при обработката и
 * трансформацията на текстови данни, свързани с тото тегления.
 * Той отговаря за:
 * <ul>
 *   <li>Извличане на числа и дати от сурови редове от файлове</li>
 *   <li>Парсване на данни в подходящи структури (списъци от числа или стрингове)</li>
 *   <li>Филтриране и сортиране на числови редове</li>
 *   <li>Извличане на конкретни стойности (като дата, максимум, минимум)</li>
 * </ul>
 *
 * <p><b>Какво прави?</b></p>
 * <ul>
 *   <li>Разделя сурови входни редове в числа (parseToIntFromRawFile)</li>
 *   <li>Извлича конкретни елементи (дата, числов ред, ден от седмицата и др.)</li>
 *   <li>Генерира двумерни масиви 7x7 за визуално представяне на draw</li>
 *   <li>Поддържа търсене на максимум/минимум стойности</li>
 *   <li>Извлича архивни стойности или позиции по конкретни критерии</li>
 * </ul>
 *
 * <p><b>Как го прави?</b></p>
 * <ul>
 *   <li>Работи основно със списъци от редове (List&lt;String&gt;) или сурови draw данни</li>
 *   <li>Използва {@code split()}, {@code trim()}, {@code Integer.parseInt()} и регулярни изрази</li>
 *   <li>Прилага филтрация и нормализация на текст (replace, equalsIgnoreCase)</li>
 *   <li>Генерира нови списъци или двумерни масиви, използвани в статистически анализи</li>
 * </ul>
 *
 * <p><b>Защо го прави?</b></p>
 * Този клас служи като “трансформатор” на данните, извлечени от файловете.
 * Без него данните биха останали в неструктуриран, текстов и неуправляем вид.
 * {@code Manipulate} е необходима междинна стъпка между:
 * <ul>
 *   <li>входа – текстови файлове от тото сайтове</li>
 *   <li>и анализа – чрез {@code Statistic}, {@code Checks} и {@code Print}</li>
 * </ul>
 *
 * <p><b>Основни методи:</b></p>
 * <ul>
 *   <li>{@code extractElementsFromDrawsLine(String line)} – извлича drawID, дата и числа от един текстов ред</li>
 *   <li>{@code parseToIntFromRawFile(List<String> rawData)} – превръща списък от текстови редове в списък от списъци от цели числа</li>
 *   <li>{@code extractStructFromRawData(String rawData)} – връща структура от реда като List&lt;String&gt;</li>
 *   <li>{@code fill49x7()} – генерира 7x7 матрица с числата от 1 до 49</li>
 *   <li>{@code findMax()/findMin()} – намира най-голямата/най-малката стойност в списък</li>
 *   <li>{@code getDrawWithMaxValIndex()} – намира индекса на реда с най-голяма стойност</li>
 *   <li>{@code getDrawWithMaxVal()} – връща самия ред с максимална стойност</li>
 *   <li>{@code extractDate(String data)} – извлича дата от draw идентификатор</li>
 *   <li>{@code isClassifiedByDateInArchive()} – брои колко пъти дадена дата фигурира в архив</li>
 * </ul>
 *
 * <p><b>Зависимости:</b></p>
 * <ul>
 *   <li>{@code java.util.List}, {@code java.util.Scanner}, {@code java.util.stream.Collectors}</li>
 *   <li>{@code java.time.LocalDate}, {@code java.time.format.DateTimeFormatter}</li>
 *   <li>{@code ParseURL} – за четене на файлове и извличане на последния ред</li>
 * </ul>
 *
 * <p><b>Използване:</b></p>
 * Класът се използва от други компоненти като {@code Write}, {@code Print}, {@code Statistic}, {@code Checks}
 * за да подготвят и структурират числови данни преди по-нататъшна обработка или анализ.
 *
 * @author bNN
 * @version 1.0
 */
public class Manipulate {
    public static void main(String[] args) {
        List<String> data = ParseURL.readDB(
                "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt");
        System.out.println(Checks.getNewDrawFromSite(data));
        System.out.println(allSpecificDayCntInArchive("sunday"));
    }

    /**
     * Извлича индекс, дата и стойност на тираж от подаден текстов ред, използвайки
     * троен интервал (" {3}") като разделител.
     *
     * <p>Очаква се редът да съдържа информация във формат:
     * <pre>
     * <номер>   <дата>   <теглене>
     * </pre>
     * като между отделните елементи има точно три интервала.
     *
     * <p>Извлечените елементи се връщат в масив от тип {@code String[]} с дължина 3:
     * <ul>
     *   <li>{@code res[0]} – индекс на тегленето (напр. "134")</li>
     *   <li>{@code res[1]} – дата (напр. "02 MAR 2025")</li>
     *   <li>{@code res[2]} – теглени числа или друг текст (напр. "1 12 17 22 34 45")</li>
     * </ul>
     *
     * <p>Примерен вход:
     * <pre>
     * 134   02 MAR 2025   1 12 17 22 34 45
     * </pre>
     * <p>Примерен изход:
     * <pre>
     * ["134", "02 MAR 2025", "1 12 17 22 34 45"]
     * </pre>
     *
     * @param line ред от текстов файл със съдържание на теглене, разделен с троен интервал
     * @return масив от 3 елемента: [индекс, дата, теглене]
     */
    static String[] extractElementsFromDrawsLine(String line) {
        String[] res = new String[4];
        String date = line.split(" {3}")[1].trim();
        String draws = line.split(" {3}")[2];
        String idx = line.split(" {3}")[0];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        LocalDate dateX = LocalDate.parse(date, formatter);
        String dateName = dateX.getDayOfWeek().getDisplayName
                (TextStyle.FULL, Locale.forLanguageTag("bg")).toUpperCase();

        res[0] = (idx);
        res[1] = (date);
        res[2] = (draws);
        res[3] = (dateName);
        return res;
    }

    /**
     * Изисква от потребителя въвеждане на отговор от конзолата и го валидира, докато
     * не бъде въведен валиден символ <strong>"Y"</strong> (Yes) или <strong>"N"</strong> (No).
     *
     * <p>Въвеждането е нечувствително към малки/главни букви – всички входни стойности
     * се преобразуват автоматично в главни чрез {@code toUpperCase()}.
     *
     * <p>Ако потребителят въведе невалидна стойност, методът извежда повторно
     * съобщение <code>Y||N:</code> и очаква нов опит.
     *
     * <p>Примерен диалог:
     * <pre>
     * Въведено: maybe
     * Изход: Y||N:
     * Въведено: y
     * Изход: връща "Y"
     * </pre>
     *
     * @return Стринг, съдържащ валиден отговор – "Y" или "N"
     */
    static String answer() {
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toUpperCase();

        while (!answer.equals("N") && !answer.equals("Y")) {
            System.out.print("Y||N: ");
            answer = scanner.nextLine().toUpperCase();
        }
        return answer;
    }

    /**
     * Изисква от потребителя въвеждане на отговор от конзолата и го валидира, докато
     * не бъде въведен валиден символ <strong>"Y"</strong> (Yes) или <strong>"N"</strong> (No).
     *
     * <p>Въвеждането е нечувствително към малки/главни букви – всички входни стойности
     * се преобразуват автоматично в главни чрез {@code toUpperCase()}.
     *
     * <p>Ако потребителят въведе невалидна стойност, методът извежда повторно
     * съобщение <code>Y||N:</code> и очаква нов опит.
     *
     * <p>Примерен диалог:
     * <pre>
     * Въведено: maybe
     * Изход: Y||N:
     * Въведено: y
     * Изход: връща "Y"
     * </pre>
     *
     * @return Стринг, съдържащ валиден отговор – "Y" или "N"
     */
    static String answerRgx(String s1, String s2) {
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toUpperCase();

        while (!answer.equals(s1) && !answer.equals(s2)) {
            System.out.print(s1 + "||" + s2 + ": ");
            answer = scanner.nextLine().toUpperCase();
        }
        return answer;
    }

    /**
     * Генерира и връща двумерен масив (матрица) с размер 7x7, съдържащ числата от 1 до 49,
     * подредени последователно по редове.
     *
     * <p>Използва се например за представяне на всички числа от 6/49 лотария
     * в таблична форма.
     *
     * <p>Примерен изход:
     * <pre>
     *  1  2  3  4  5  6  7
     *  8  9 10 11 12 13 14
     * ...
     * 43 44 45 46 47 48 49
     * </pre>
     *
     * @return двумерен масив {@code int[7][7]} съдържащ числата от 1 до 49
     */
    protected static int[][] fill649Arr() {
        int[][] arr = new int[7][7];
        int cnt = 1;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                arr[i][j] = cnt;
                cnt++;
            }
        }
        return arr;
    }

    /**
     * Търси даден елемент {@code el} във всеки ред на подадена 2D матрица {@code matrix}.
     * Ако елементът бъде намерен в даден ред, той се записва на същата позиция в помощна матрица {@code tmp}.
     *
     * <p>За търсене се използва двоично търсене, което означава, че редовете на {@code matrix}
     * трябва да бъдат сортирани.
     *
     * <p>Пример: Ако {@code el = 17} и се намира на позиция (2,3) в {@code matrix},
     * то след изпълнение {@code tmp[2][3] = 17}
     *
     * @param el     числото, което търсим
     * @param matrix оригиналната матрица, в която се търси
     * @param tmp    матрица, в която ще бъде отбелязано местоположението на намерените елементи
     */
    protected static void searchThisElIn649Matrix(int el, int[][] matrix, int[][] tmp) {
        for (int i = 0; i < matrix.length; i++) {
            int idx = bs(matrix[i], el);                // За всеки ред(i) от matrix Търси el във всяка колона
            if (idx != -1) {
                tmp[i][idx] = el;
            }
        }
    }

    /**
     * Извършва двоично (бинарно) търсене на стойност {@code target} в сортиран едномерен масив {@code arr}.
     *
     * <p>Връща индекса на намерения елемент или {@code -1}, ако елементът не присъства в масива.
     *
     * <p>Предполага, че масивът {@code arr} е предварително сортиран във възходящ ред.
     *
     * @param arr    сортиран едномерен масив, в който се търси
     * @param target стойността, която се търси
     * @return индексът на елемента, ако е намерен, или -1, ако не съществува в масива
     */
    static int bs(int[] arr, int target) {
        int s = 0, e = arr.length - 1, m;
        while (s <= e) {
            m = s + (e - s) / 2;
            if (arr[m] == target) {
                return m;
            } else if (arr[m] < target) {
                s = m + 1;
            } else e = m - 1;
        }
        return -1;
    }

    /**
     * Парсва списък от текстови редове, съдържащи цели числа, в двоен списък от цели числа.
     * <p>
     * Всеки ред в {@code rawData} трябва да съдържа цели числа, разделени с интервали.
     * Методът ще преобразува всеки ред в списък от {@code Integer} стойности и ще ги
     * добави в главен списък, връщайки така структура от типа {@code List<List<Integer>>}.
     *
     * <p>Ако някой от елементите в ред не е валидно число, грешката се улавя и се
     * извежда съобщение в конзолата без прекъсване на изпълнението. В този случай
     * числото се пропуска и обработката продължава с останалите.
     *
     * <p><b>Пример за вход:</b></p>
     * <pre>{@code
     * List<String> rawData = List.of(
     *     "3 15 22 31 37 44",
     *     "1 6 17 28 35 49"
     * );
     *
     * List<List<Integer>> parsed = parseToIntFromFile(rawData);
     * // parsed = [[3, 15, 22, 31, 37, 44], [1, 6, 17, 28, 35, 49]]
     * }</pre>
     *
     * @param rawData Списък от редове с цели числа, разделени с интервали.
     * @return Списък от списъци с парсираните {@code Integer} стойности.
     */
    protected static List<List<Integer>> parseToIntDrawsFromFile(List<String> rawData) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> tmp;
        for (String rawDatum : rawData) {
            tmp = new ArrayList<>();
            String[] arr = rawDatum.trim().split(" ");
            for (String el2 : arr) {
                try {
                    tmp.add(Integer.parseInt(el2.trim()));
                } catch (NumberFormatException nf) {
                    System.out.println("↪︎404: parse: " +
                            "Формата на числата в \"results.txt\" е криминален");
                }
            }
            res.add(tmp);
        }
        return res;
    }

    /**
     * Парсва входен низ, съдържащ числа, разделени с интервали,
     * и връща списък от цели числа.
     *
     * <p>Премахва излишните интервали и замества запетайки с интервали,
     * след което се опитва да конвертира всяка стойност в {@code int}.
     * В случай на невалидна стойност извежда съобщение за грешка,
     * но не прекратява изпълнението на програмата.</p>
     *
     * <p>Пример за вход: {@code "1 2 3 4"} ⟶ {@code [1, 2, 3, 4]}</p>
     *
     * @param line текстов ред със стойности, които трябва да бъдат преобразувани
     * @return списък от цели числа, които са успешно парснати от входа
     * @see Integer#parseInt(String)
     * @see NumberFormatException
     */
    protected static List<Integer> parseToInt(String line) {
        List<Integer> res = new ArrayList<>();
        String[] s = line.trim().replace(", ", " ").split(" ");
        try {
            for (String el : s) {
                int x = Integer.parseInt(el.trim());
                res.add(x);
            }
        } catch (NumberFormatException e) {
            System.out.println("404: parseToInt(): " +
                    "Не мога да парсна към цяло число");
        }
        return res;
    }

    protected static List<Double> parseToDouble(String line) {
        List<Double> res = new ArrayList<>();
        String[] s = line.trim().replace(", ", " ").split(" ");
        try {
            for (String el : s) {
                double x = Double.parseDouble(el.trim());
                res.add(x);
            }
        } catch (NumberFormatException e) {
            System.out.println("404: parseToInt(): " +
                    "Не мога да парсна към цяло число");
        }
        return res;
    }

    /**
     * Извлича най-новите тегления на тото 6/49 от три различни източника и записва резултатите в officialToto.txt,
     * като връща списък с три реда, всички във формат:
     *
     * <li>При грешка в свързването със сайта се връща предишният записан резултат от архива</li>
     *
     * <pre>{@code
     * "26-30 MAR 2025 - 3,15,18,24,31,47"
     * }</pre>
     *
     * <p>Методът събира данни от следните източници:</p>
     * <ul>
     *   <li><b>bgToto</b> – локално генериран ред чрез {@code createdDayDrawString()}</li>
     *   <li><b>officialToto</b> – от официалния сайт {@code toto.bg}</li>
     *   <li><b>toto49</b> – от архивния сайт {@code toto49.com}</li>
     * </ul>
     *
     * <p>Тези стойности се използват за проверка на съвпадение на последните резултати от трите източника.</p>
     *
     * @return Списък от три елемента (String), всеки съдържащ:
     * {@code "номер-тираж ДАТА - ЧИСЛА"}, напр.
     * {@code "26-30 MAR 2025 - 3,15,18,24,31,47"}
     *
     * <p><b>Пример:</b></p>
     * <pre>{@code
     * [
     *   "26-30 MAR 2025 - 3,15,18,24,31,47",  // от bgToto
     *   "26-30 MAR 2025 - 3,15,18,24,31,47",  // от officialToto
     *   "26-30 MAR 2025 - 3,15,18,24,31,47"   // от toto49.com
     * ]
     * }</pre>
     */
    static List<String> getThreeOfficialDB() {
        List<String> res = new ArrayList<>();

        try {
            String bgToto = Date.createdDayDrawString("-").getLast();
            String officialToto = ParseURL.parseOfficialURL().getLast();
            String toto49 = ParseURL.parseToto49URL().getLast();

            res.add(bgToto);
            res.add(toto49);
            res.add(officialToto);
            //Write.writeDrawDB(res, false);
        } catch (NoSuchElementException e) {
            System.out.println("↪︎NeT@:\ngetThreeOfficialDB(): Връзката я няма никаква ...");
        }
        return res;
    }

    static int findMax(List<Integer> numbers) {
        int max = Integer.MIN_VALUE;
        for (int el : numbers) {
            if (el > max) max = el;
        }
        return max;
    }

    static int findMin(List<Integer> numbers) {
        int min = Integer.MAX_VALUE;
        for (int el : numbers) {
            if (el < min) min = el;
        }
        return min;
    }

    /**
     * @param data 27-03 Apr 2025-25, 29, 31, 34, 38, 44
     * @return 25 29 31 34 38 44
     */
    static String extractData(String data, String regEx) {
        return data.split(regEx)[2]
                .replace(",", "")
                .trim();
    }

    /**
     * Преобразува низ от цели числа, разделени с интервали, в списък от {@code Integer}.
     * <p>
     * Всеки елемент в низа се изрязва чрез {@code trim()}, след което се преобразува в цяло число
     * с помощта на {@code Integer.parseInt()}, и се събира в списък.
     * <p>
     * Методът е удобен за извличане на числови данни от текстов вход, като например:
     * <pre>{@code
     *   String input = " 3  15  42  8 ";
     *   List<Integer> numbers = strToListOfInt(input);
     *   // numbers = [3, 15, 42, 8]
     * }</pre>
     *
     * @param s входен низ, съдържащ цели числа, разделени с един или повече интервали
     * @return списък от цели числа, извлечени от подадения низ
     * @throws NumberFormatException ако някой от елементите не може да бъде преобразуван в {@code int}
     * @throws NullPointerException  ако входният низ {@code s} е {@code null}
     */
    static List<Integer> strToListOfInt(String s) {
        return Arrays.stream(s.split(" "))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Преброява колко тегления в архива са се случили в даден ден от седмицата,
     * след като валидира входа от потребителя през конзолата.
     *
     * <p>Методът първо валидира стойността на подадения ден (`thisDay`)
     * чрез {@code FormatTxt.textValidator(Scanner, String)}. Ако не е валидна,
     * потребителят ще бъде подканян да въведе нова стойност чрез {@code Scanner(System.in)}.</p>
     *
     * <p>След успешна валидация, се извличат всички дати от текущата година чрез
     * {@code Date.allDrawDateAndDayForYear()}, и всяка дата се анализира, за да се изолира
     * денят от седмицата, записан на позиция [2] след разделяне по символа "-".</p>
     *
     * @param thisDay низ, който представя ден от седмицата (например "THURSDAY", "Monday")
     * @return броят на тегленията, които са се провели в зададения ден
     *
     * <p><b>Формат на ред в архива:</b></p>
     * <pre>{@code
     * 123-03 APR 2025-WEDNESDAY - 3,15,18,24,31,47
     * }</pre>
     * <p>Т.е. разделяне по "-" дава: [0] ID, [1] дата, [2] ден</p>
     *
     * <p><b>Валидация на входа:</b></p>
     * Потребителят въвежда стойност през конзолата и тя се валидира чрез:
     * <ul>
     *   <li>{@code FormatTxt.textValidator(scanner, thisDay)}</li>
     *   <li>{@code FormatTxt.textValidator(scanner, line)} — ако е невалидна, се повтаря въвеждането</li>
     * </ul>
     *
     * <p><b>Пример:</b></p>
     * <pre>{@code
     * int count = allSpecificDayCntInArchive("saturday");
     * System.out.println("Тегления в събота: " + count);
     * }</pre>
     */
    static int allSpecificDayCntInArchive(String thisDay) {
        int cnt = 0;
        Scanner scanner = new Scanner(System.in);
        while (!FormatTxt.textValidator(scanner, thisDay)) {
            String line = scanner.nextLine();
            FormatTxt.textValidator(scanner, line);
        }

        List<String> data = Date.allDrawDateAndDayForYear();
        for (String row : data) {
            String day = row.split("-")[2].trim();
            if (thisDay.equalsIgnoreCase(day)) cnt++;
        }
        return cnt;
    }

    /**
     * Намира и връща реда (draw) с най-голям индекс от подаден списък от текстови редове.
     *
     * <p>Всеки ред от списъка {@code data} се предполага, че започва с числов индекс, отделен с тире {@code "-"}.
     * Пример за ред: {@code "36-04 May 2025-15, 18, 19, 22, 24, 38"}.</p>
     *
     * <p>Методът:
     * <ol>
     *     <li>Парсва първия елемент преди тирето като цяло число (index).</li>
     *     <li>Събира всички индекси.</li>
     *     <li>Открива максималния индекс.</li>
     *     <li>Връща оригиналния ред, който съдържа този максимален индекс.</li>
     * </ol>
     * Ако няма валиден ред или списъкът е празен, методът връща {@code null}.</p>
     *
     * <p>В случай на невалиден формат на числото (например ако частта преди тирето не е цяло число),
     * грешката се прихваща и се извежда диагностично съобщение в конзолата.</p>
     *
     * @param data Списък от редове, съдържащи индекс и друга информация, разделени с тире.
     * @return Редът, съдържащ най-големия индекс; или {@code null}, ако не е намерен такъв.
     * @throws NullPointerException ако {@code data} е {@code null}.
     *
     *                              <h4>Пример:</h4>
     *                              <pre>{@code
     *                                                                                                                     List<String> редове = List.of("36-04 May 2025-15, 18, 19, 22, 24, 38", "35-04 May 2025-15, 18, 19, 22, 24, 38");
     *                                                                                                                     String последен = getDrawWithMaxIdx(редове); // връща "36-04 May 2025-15, 18, 19, 22, 24, 38"
     *                                                                                                                     }</pre>
     */
    static String getDrawWithMaxIdx(List<String> data) {
        List<Integer> indexes = new ArrayList<>();
        for (String line : data) {
            String tmpIdx = line.split("-")[0];
            try {
                int idx = Integer.parseInt(tmpIdx);
                indexes.add(idx);
            } catch (NumberFormatException e) {
                System.out.println("↪︎404: findingNewDraw(): " +
                        "Не мога да парсна стринга до число. Формата явно е криминален...");
            }
        }
        int max = findMax(indexes);
        for (String line : data) {
            String tmpIdx = line.split("-")[0].trim();
            if (Integer.parseInt(tmpIdx) == max) {
                return line;
            }
        }
        return null;
    }

    /**
     * @param rawData String: 27-03 Apr 2025-25, 29, 31, 34, 38, 44
     * @return String: 25, 29, 31, 34, 38, 44
     */
    static String extractNumDataFromDraw(String rawData) {
        return rawData.split("-")[2];
    }

    /**
     * Преобразува списък от низове, представляващи резултати от тегления,
     * в списък от списъци с цели числа.
     *
     * <p>Очаква се всеки входен ред да е във формат:
     * <pre>
     * &lt;индекс&gt;-&lt;числа, разделени със запетая&gt;
     * </pre>
     * Например:
     * <pre>
     * 142-1,12,23,28,32,47
     * </pre>
     *
     * <p>Методът:
     * <ul>
     *   <li>Извлича числата след тирето ("-")</li>
     *   <li>Разделя ги по запетая</li>
     *   <li>Парсва всяка стойност до {@code int}</li>
     *   <li>Добавя резултата като списък от цели числа във финалния списък</li>
     * </ul>
     *
     * <p>При невалиден числов формат (напр. букви вместо числа), извежда съобщение
     * в конзолата, но не прекъсва обработката.
     *
     * @param rawData списък от редове с резултати от тегления във формат {@code <индекс>-<числа>}
     * @return списък от списъци с цели числа, представящи изтеглените комбинации
     */
    protected static List<List<Integer>> parseToIntFromWeb(List<String> rawData) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> tmp;
        for (int i = rawData.size() - 1; i >= 0; i--) {
            tmp = new ArrayList<>();
            String[] arr = rawData.get(i).trim().split("-")[1].trim().split(",");
            for (String el2 : arr) {
                try {
                    tmp.add(Integer.parseInt(el2.trim()));
                } catch (NumberFormatException nf) {
                    System.out.println("404: parse: " +
                            "Формата на числата в \"results.txt\" е криминален");
                }
            }
            res.add(tmp);
        }
        return res;
    }

    protected static List<Integer> getLastDrawIdxFromSiteData(List<String> siteData) {
        List<Integer> idxList = new ArrayList<>();
        for (int i = 0; i < siteData.size(); i++) {
            try {
                int index = Integer.parseInt(siteData.get(i).split("-")[0]);
                idxList.add(index);
            } catch (NumberFormatException e) {
                System.out.println("404: getLastDrawIdxFromSiteData:" +
                        "Не мога да достъпя \"siteData\"");
            }
        }
        return idxList;
    }

    /**
     * <li> 17   27 Feb 2025   23, 24, 25, 37, 42, 45   ->   23 24 25 37 42 45 </li>
     * @param fileData 17   27 Feb 2025   23, 24, 25, 37, 42, 45
     * @param rgx String
     * @return 23 24 25 37 42 45
     */
    protected static List<String> getOnlyDigitsDataFromArchFile(List<String> fileData, String rgx) {
        List<String> drawArcData = new ArrayList<>();
        for (String fileDatum : fileData) {
            try {
                String line = fileDatum.split(rgx)[2];
                line = line.replace(",", "");
                drawArcData.add(line);
            } catch (NumberFormatException e) {
                System.out.println("404: getLastDrawIdxFromSiteData:" +
                        "Не мога да достъпя \"siteData\"");
            }
        }
        return drawArcData;
    }
}
