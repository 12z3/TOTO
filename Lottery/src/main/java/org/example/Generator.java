package org.example;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.Frequency.countMatches;

/**
 * Клас {@code Statistic} предоставя основна изчислителна логика, използвана за анализ и обработка
 * на данни от тото тегления. Той съдържа методи за:
 * <ul>
 *   <li>Пресмятане на стандартно отклонение на изтеглени числа</li>
 *   <li>Филтриране и групиране на тегления по дата</li>
 *   <li>Прогнозиране на числа въз основа на честота</li>
 *   <li>Извличане на резултати от последно теглене и изчисляване на статистики върху тях</li>
 * </ul>
 *
 * <h2>Какво прави?</h2>
 * Основната роля на класа е да обобщава статистическа информация за предишни тегления,
 * като предоставя полезна информация за визуализация, проверка или прогнозиране на бъдещи комбинации.
 *
 * <h2>Как го прави?</h2>
 * <ul>
 *   <li>Използва {@code Stream API}, {@code Map<Integer, Integer>} и {@code List<Integer>} за агрегиране и анализ</li>
 *   <li>Работи с текстови файлове чрез други помощни класове като {@code ParseURL}, {@code Checks}, {@code Manipulate}</li>
 *   <li>Използва регулярни изрази за валидиране и филтриране на съдържание</li>
 *   <li>Разчита на времеви компоненти чрез {@code LocalDate}, {@code DateTimeFormatter} и др.</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * Целта на класа е:
 * <ul>
 *   <li>Да предоставя обобщена статистика (като STD, AVG, последно теглене и честота на срещане)</li>
 *   <li>Да подпомогне потребителя в процеса на избор на числа за бъдещи тегления</li>
 *   <li>Да филтрира или преобразува сурови данни от архиви и сайтове до удобни структури за анализ</li>
 * </ul>
 *
 * <h2>Основни методи:</h2>
 * <ul>
 *   <li>{@code calculateStd(List<Integer> numbers)} – пресмята стандартно отклонение</li>
 *   <li>{@code printStat()} – извежда обща статистика за честота на срещане на числа</li>
 *   <li>{@code allDrawsToMap()} – агрегира всички срещания на числа по позиции</li>
 *   <li>{@code filterByDate(List<String> data, String date)} – връща тегления само за конкретна дата</li>
 *   <li>{@code getStatLines()} – зарежда тегления от архив</li>
 *   <li>{@code mostFrequentNumbers(...)} – връща най-често срещаните числа по зададен брой</li>
 * </ul>
 *
 * <h2>Взаимодействия:</h2>
 * <ul>
 *   <li>С {@code ParseURL} – за зареждане на draw резултати от файл или линк</li>
 *   <li>С {@code Checks} – за филтриране на вече познати тегления</li>
 *   <li>С {@code Manipulate} – за преобразуване на текст в масиви от цели числа</li>
 * </ul>
 *
 * <p>Този клас е критичен за статистическия модул на системата и подпомага всички действия,
 * свързани с анализ на тенденции и извличане на най-често срещаните комбинации от числа.</p>
 *
 * @author bNN
 * @version 1.0
 */
public class Generator {
    public static void main(String[] args) {
        List<int[]> data = Frequency.filteringDrawsByWeekday("sunday", false);
        System.out.println(countMatches(data) + "\n" + data.size());

        Date.allDrawDateAndDayForYear();

        for (List<Integer> line : ofNewProposalListOfFrequencyMatches()) {
            System.out.println(line);
        }
    }

    /**
     * Валидира числова стойност от потребителски вход спрямо подаден регулярен израз
     * и връща резултата като цяло или реално число в зависимост от флага.
     * <p>
     * Методът използва {@link Scanner}, за да чете вход от потребителя, проверява дали
     * въведената стойност отговаря на подадения регулярен израз и при невалиден вход
     * подканя за повторно въвеждане, докато не получи валидна стойност.
     *
     * <p>Полезен е за гъвкава валидираща логика, като например:
     * <ul>
     *   <li>Цяло число от 1 до 6: {@code ^[1-6]$}</li>
     *   <li>Число от 0 до 30 с или без десетични: {@code ^(?:[0-9](?:\\.\\d+)?|[1-9][0-9]?(?:\\.\\d+)?|30(?:\\.0+)?)$}</li>
     * </ul>
     *
     * @param regEx   Регулярен израз, който описва допустимия числов формат.
     * @param scanner Обект от тип {@link Scanner} за четене от потребителя.
     * @param text    Първоначалният вход, който да бъде валидиран.
     * @param isStd   Флаг, указващ дали да се върне стойността като {@code double} (ако е {@code true})
     *                или като {@code int} (ако е {@code false}).
     * @return Въведената стойност, конвертирана към {@code double} или {@code int}, в зависимост от флага.
     */
    static double digitsValidator(String regEx, Scanner scanner, String text, boolean isStd) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);

        while (!matcher.matches()) {
            if (isStd) {
                System.out.print("0 < Числото трябва да бъде във формат 3, 3.1 или 3,1 < 30: ");
            } else {
                System.out.print("0 <= Съвпаденията трябва да бъдат цели числа < 6: ");
            }
            text = scanner.nextLine();
            matcher = pattern.matcher(text);
        }
        return isStd ?
                Double.parseDouble(text.replace(",", ".")) :
                Integer.parseInt(text);
    }


    /**
     * Стартира интерактивен режим за генериране на нови предложения за залози
     * на база на последния официален тираж и подадени от потребителя параметри.
     *
     * <p><b>Входни данни от потребителя:</b></p>
     * <ul>
     *   <li><b>std</b> — стандартно отклонение (double), използвано при нормалното разпределение</li>
     *   <li><b>matches</b> — брой съвпадащи числа от предходния тираж (int, между 0 и 6 включително)</li>
     * </ul>
     *
     * <p><b>Функционалност:</b></p>
     * <ol>
     *   <li>Извежда деня на следващото теглене и честотата на срещане на числата за този ден</li>
     *   <li>Позволява въвеждане на параметрите std и matches от конзолата</li>
     *   <li>Извлича последния официален тираж от <i>officialToto.txt</i></li>
     *   <li>Генерира 3 уникални предложения чрез {@code gaussianNumbersGenerator(...)}</li>
     *   <li>Отпечатва предложенията и сравнението им с последния официален тираж</li>
     *   <li>Позволява запис на предложенията в текстов файл, ако потребителят потвърди</li>
     * </ol>
     *
     * <p><b>Използвани файлове:</b></p>
     * <ul>
     *   <li><b>officialToto.txt</b> — последен официален тираж (прочит)</li>
     *   <li><b>bgTotoFromSite.txt</b> — временен файл за индексиране на записите (прочит/запис)</li>
     *   <li><b>tmpRes/*.txt</b> — файл за запис на генерираните предложения (запис)</li>
     * </ul>
     *
     * <p><b>Свързани методи:</b></p>
     * <ul>
     *   <li>{@link Date#findNextDrawDay()} — определя следващия ден за теглене</li>
     *   <li>{@link Frequency#ofMatchesForDayOfWeek(String)} — показва честота на числата по ден</li>
     *   <li>{@link Generator#newGaussianProposalList(List, double, int)} — генерира предложения</li>
     *   <li>{@link Generator#printProposalList(Set, double, int)} (Set<List<Integer>>, double, int)} — отпечатва и сравнява резултати</li>
     *   <li>{@link WebData#getWebIdxFromFileData(String)} — определя индекс за запис на база броя записи</li>
     *   <li>{@link Write#toFile(Set, int, double, int)} — записва предложенията във файл</li>
     *   <li>{@link ParseURL#readFromFile(File)} — зарежда редове от текстов файл</li>
     *   <li>{@link Manipulate#strToListOfInt(String)} — преобразува числова редица от String към List</li>
     *   <li>{@link Manipulate#extractData(String, String)} — извлича числова част от ред</li>
     *   <li>{@link Manipulate#answer()} — получава вход от потребителя</li>
     * </ul>
     */
    protected static long newProposal() {
        double std = -1.1;
        int matches = -1;
        boolean wasThisProposalWritten = false;
        Set<List<Integer>> proposal = null;

        String stdRegEx = "^(?:[0-9]|[1-9][0-9]?|30)([.,]\\d+)?$";
        String matchesRegEx = "^[0-5]$";

        String officialTotoPath = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";
        String bgTotoFilePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Да генерирам ли предложения за залози?: ");
        String ans = scanner.nextLine();
        //Трябва да върне или Y или N:
        ans = Generator.validateAns(ans, scanner, "Y", "N");

        long time = 0;
        if (ans.equalsIgnoreCase("Y")) {
            System.out.print("Гаус (G) или честота на срещане на числата (F) избираш за генератор на залог?: ");
            String approachAns = Manipulate.answerRgx("G", "F");
            boolean isGaussian = approachAns.equalsIgnoreCase("G");

            String today = Date.findNextDrawDay();
            System.out.print("\nСтатистика за ");
            Map<Integer, List<Integer>> map = Frequency.ofMatching(today, true);
            Print.printMap(map);

            while (ans.equalsIgnoreCase("Y")) {
                if (isGaussian) {
                    System.out.print("\nstd: ");
                    String line = scanner.nextLine();
                    std = digitsValidator(stdRegEx, scanner, line, true);

                    System.out.print("matches: ");
                    line = scanner.nextLine();
                    if (line.isEmpty()) {
                        matches = 0;
                    } else {
                        matches = (int) digitsValidator(matchesRegEx, scanner, line, false);
                    }

                    long sTime = System.nanoTime();
                    List<String> data = ParseURL.readFromFile(new File(officialTotoPath));
                    String tmp = Manipulate.extractData(data.getFirst(), "   ");
                    List<Integer> oldDraw = Manipulate.strToListOfInt(tmp);

                    proposal = Generator.newGaussianProposalList(oldDraw, std, matches);
                    System.out.println("\nGaussian approach: ");
                    Generator.printProposalList(proposal, std, matches);
                    long eTime = System.nanoTime();
                    time = eTime - sTime;
                } else {
                    long sTime = System.nanoTime();
                    proposal = Generator.ofNewProposalListOfFrequencyMatches();
                    System.out.println("\nFrequency of matched approach: ");
                    Generator.printProposalList(proposal, std, matches);
                    long eTime = System.nanoTime();
                    time = eTime - sTime;
                }

                // todo: 26.08.25 16:08 - Обмисли метода с записването на междинните тиражи.
                //                      - Трябва да пазиш тези междинни тиражи  някъде.

                printThisProposal(bgTotoFilePath, proposal, matches, std, "↪︎404: purposeSuppose(): ");

                System.out.print("Нов?: ");
                String newAns = scanner.nextLine();
                ans = Generator.validateAns(newAns, scanner, "Y", "N");

                if (ans.equalsIgnoreCase("Y")) {
                    System.out.print("Гаус (G) или честота на срещане на числата (F) избираш за генератор на залог?: ");
                    approachAns = Manipulate.answerRgx("G", "F");
                    isGaussian = approachAns.equalsIgnoreCase("G");
                }
            }

            // todo: 26.08.25 16:08 - Обмисли метода с записването на междинните тиражи.
            //                      - Тук евентуално трябва да подадаеш на printThisProposal листа с Сета

        }
        return time;
    }

    private static void printThisProposal(String bgTotoFilePath, Set<List<Integer>> proposal, int matches,
                                          double std, String errorMsg) {
        System.out.print("Да ги запиша ли?: ");
        String answer = Manipulate.answer();

        if (answer.equals("Y")) {
            Integer webIdx = WebData.getWebIdxFromFileData(bgTotoFilePath).getLast();
            try {
                Write.toFile(proposal, matches, std, ++webIdx);
            } catch (IOException e) {
                System.out.println(errorMsg +
                        "Не мога да запиша файла");
            }
        }
    }


    /**
     * Генерира множество от 6 уникални числа на база тяхната честота
     * на срещане в предишни тегления.
     *
     * <p>Алгоритъмът избира всички числа, които са се срещали поне (frequency + 1) пъти
     * в тегленията, събира ги в списък и го разбърква. След това взема първите
     * 6 уникални числа от този списък, като гарантира разнообразие чрез
     * повторно разбъркване при всяко добавяне.</p>
     *
     * <p>Използва се за генериране на предложения базирани на
     * статистическа активност.</p>
     *
     * @return множество от 6 уникални числа, по-често срещани в тегленията
     * @see Frequency#ofMatching(String, boolean)
     * @see Date#findNextDrawDay()
     */
    static List<Integer> ofFrequencyDigits() {
        final int frequency = 4;
        List<Integer> digits = new ArrayList<>();
        String today = Date.findNextDrawDay();
        Map<Integer, List<Integer>> frequencyDigits = Frequency.ofMatching(today, false);
        List<Integer> data = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> el : frequencyDigits.entrySet()) {
            if (el.getKey() > frequency) data.addAll(el.getValue());
        }
        Collections.shuffle(data);
        for (int i = 0; i < 6; i++) {
            int el = data.get(i);
            if (!digits.contains(el))
                digits.add(el);
        }
        Collections.sort(digits);
        return digits;
    }

    static Set<List<Integer>> ofNewProposalListOfFrequencyMatches() {
        Set<List<Integer>> res = new HashSet<>();
        while (res.size() < 3) {
            List<Integer> x = ofFrequencyDigits();
            res.add(x);
        }
        return res;
    }

    /**
     * Валидира вход от потребителя до въвеждане на "Y" или "N" (без значение от главни/малки букви).
     *
     * <p>Използва {@link Scanner} за четене от стандартния вход и извежда съобщение за повторен опит,
     * докато не бъде въведена валидна стойност.</p>
     *
     * <p>Използва се за потвърждение на действия, като: "Да генерираме нови предложения?",
     * "Да ги запиша ли?" и др.</p>
     *
     * <p>Приема първоначална стойност (newAns), за да позволи повторна проверка
     * при предишно въведен вход.</p>
     *
     * @param newAns  началният отговор, който се валидира
     * @param scanner обект за четене от стандартния вход
     * @return {@code "Y"} или {@code "N"} — валиден отговор
     */
    static String validateAns(String newAns, Scanner scanner, String str1, String str2) {
        while (!newAns.equalsIgnoreCase(str1) && !newAns.equalsIgnoreCase(str2)) {
            System.out.print(str1 + "||" + str2 + ": ");
            newAns = scanner.nextLine();
        }
        return newAns;
    }


    protected static void printProposalList(Set<List<Integer>> proposals, double std, int matches) {
        //List<String> data = ParseURL.readDB("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt");
        //String lastDrawInArchiveData = Checks.getNewDrawFromSite(data);
        //String officialResult = Manipulate.extractData(lastDrawInArchiveData, "-");
        //List<Integer> olDRaw = Manipulate.strToListOfInt(officialResult);

        System.out.println();
        for (List<Integer> list : proposals) {
            List<String> p = parseIntListToStrList(list);
            double stdOfThisRow = Std.stdOfRowD(list);
            double mean = Std.mean(list);
            //List<Integer> variance = Std.variance(list);
            System.out.printf("std: %.2f; avr: %.2f;%n", stdOfThisRow, mean);
            //System.out.println(variance);
            System.out.println(p);
            Print.printThis7x7Draw(p, true);
            System.out.println();
        }
    }

    /**
     * Генерира множество от три уникални лотарийни предложения (редове от числа),
     * базирани на предишни тегления, нормално разпределение и зададен брой съвпадения.
     *
     * <p>Всяко предложение представлява списък от 6 уникални числа, съставени чрез
     * метода {@code gaussianNumbersGenerator()}, който използва нормално разпределение
     * около средната стойност и стандартното отклонение на старите тегления.
     *
     * <p>Целта е да се генерират 3 уникални предложения (редове), като се гарантира,
     * че всеки от тях има точно {@code matches} съвпадения със старите тегления.
     *
     * <p>Използва се {@code HashSet} за да се избегнат дубликати.
     *
     * @param oldDraws списък от числа от предишно теглене, които служат като база
     * @param std      стандартно отклонение, което контролира разсейването на новите числа
     * @param matches  брой числа, които трябва да съвпадат с реда {@code oldDraws}
     * @return множество от три уникални предложения (всяко е {@code List<Integer>})
     * @see Generator#gaussianNumbersGenerator(List, double, int)
     */
    static Set<List<Integer>> newGaussianProposalList(List<Integer> oldDraws, double std, int matches) {
        Set<List<Integer>> res = new HashSet<>();
        while (res.size() < 3) {
            List<Integer> x = gaussianNumbersGenerator(oldDraws, std, matches);
            res.add(x);
        }
        return res;
    }

    /**
     * Генерира комбинация от 6 числа, която частично съвпада с предишни тегления
     * и се доближава по стандартно отклонение до зададена целева стойност.
     *
     * <p><b>Какво прави:</b></p>
     * <ul>
     *     <li>Създава нова лото комбинация от точно 6 числа.</li>
     *     <li>Включва точно {@code matches} числа от предишните тегления {@code oldDraws}.</li>
     *     <li>Останалите числа се генерират по нормално разпределение.</li>
     *     <li>Целта е резултатната комбинация да има стандартно отклонение максимално близко до {@code targetStd}.</li>
     * </ul>
     *
     * <p><b>Защо го прави:</b></p>
     * <ul>
     *     <li>Да създаде реалистични и статистически контролирани комбинации от числа,</li>
     *     <li>които могат да имитират поведение на реални лото тегления.</li>
     *     <li>Възможност за симулация и анализ на вероятни изходи спрямо исторически данни.</li>
     * </ul>
     *
     * <p><b>Как го прави:</b></p>
     * <ol>
     *     <li>Ако {@code matches} е извън интервала [0,6], извежда съобщение и връща {@code null}.</li>
     *     <li>Изчислява средната стойност на {@code oldDraws}, която служи като център за нормалното разпределение.</li>
     *     <li>Извършва до ATTEMPTS опита за генериране на комбинация:
     *         <ul>
     *             <li>Във всеки опит:
     *                 <ul>
     *                     <li>Избира на случаен принцип {@code matches} числа от {@code oldDraws}.</li>
     *                     <li>Генерира останалите числа чрез {@code random.nextGaussian()}, центрирано около средното и мащабирано със {@code targetStd}.</li>
     *                     <li>Изчислява стандартното отклонение на получената комбинация.</li>
     *                     <li>Ако то е най-близко до {@code targetStd} досега, запазва комбинацията като „най-добра“.</li>
     *                 </ul>
     *             </li>
     *         </ul>
     *     </li>
     * </ol>
     * Генерира ATTEMPTS на брой комбинации (например 1000 пъти).
     * За всяка комбинация:
     * Изчислява реалното ѝ std (стандартно отклонение).
     * Сравнява това std с целевото (targetStd).
     * Ако е по-близо от всички досега → я запомня като най-добра.
     * След всички опити връща само една комбинация – тази с най-близкото реално std до зададеното.
     *
     * @param oldDraws  Списък с числа от предишно теглене, от които да се изберат съвпадения.
     * @param targetStd Целевото стандартно отклонение, към което да се доближи новата комбинация.
     * @param matches   Брой числа (0–6), които трябва да присъстват от {@code oldDraws}.
     * @return Най-добрата намерена комбинация от 6 числа, сортирани във възходящ ред, или {@code null}, ако няма валиден резултат.
     * @throws IllegalArgumentException ако {@code matches} е извън обхвата [0, 6].
     *
     *                                  <h4>Пример:</h4>
     *                                  <pre>{@code
     *                                                                                                    List<Integer> предишно = List.of(3, 17, 25, 33, 41, 45);
     *                                                                                                    List<Integer> ново = gaussianNumbersGenerator(предишно, 6.0, 2);
     *                                                                                                    }</pre>
     */
    static List<Integer> gaussianNumbersGenerator(List<Integer> oldDraws, double targetStd, int matches) {
        final int LOTTO_SIZE = 6;
        final int MAX_NUMBER = 49;
        final int ATTEMPTS = 3000;

        Random random = new Random();

        if (matches < 0 || matches > LOTTO_SIZE) {
            System.out.println("Съвпаденията трябва да бъдат между 0 и 6");
            return null;
        }

        double mean = Statistic.genericMean(oldDraws);
        List<Integer> bestCombination = null;
        double closestStdDiff = Double.MAX_VALUE;

        for (int i = 0; i < ATTEMPTS; i++) {
            Set<Integer> result = new HashSet<>();

            // Включва съвпадения от предишни тегления:
            Collections.shuffle(oldDraws);
            List<Integer> matchingNumbers = oldDraws.subList(0, matches);
            result.addAll(matchingNumbers);

            // Генерираме нови числа до размер 6:
            while (result.size() < LOTTO_SIZE) {
                int number = (int) Math.round(random.nextGaussian() * targetStd + mean);
                if (number >= 1 && number <= MAX_NUMBER) {
                    result.add(number);
                }
            }

            List<Integer> combination = result.stream().sorted().toList();
            double actualStd = Std.stdOfRowD(combination);
            double diff = Math.abs(actualStd - targetStd);

            // Запомня комбинацията с наи-близкото std до сега:
            if (diff < closestStdDiff) {
                closestStdDiff = diff;
                bestCombination = combination;
            }
        }

        return bestCombination;
    }


    /**
     * Преобразува всеки елемент от подаден списък с цели числа в неговия стрингов еквивалент.
     *
     * @param data Списък от цели числа ({@code List<Integer>})
     * @return Списък от низове ({@code List<String>}), всеки елемент е {@code String.valueOf(Integer)}
     *
     * <b>Пример:</b><br>
     * {@code [3, 7, 21]} → {@code ["3", "7", "21"]}
     *
     * <p>Полезно при сериализация, запис във файл или визуализиране на числови списъци.</p>
     */
    static List<String> parseIntListToStrList(List<Integer> data) {
        List<String> res = new ArrayList<>();
        for (int el : data) {
            String x = String.valueOf(el);
            res.add(x);
        }
        return res;
    }


    /**
     * Стартира интерактивен режим за генериране на нови предложения за залози
     * на база на последния официален тираж и подадени от потребителя параметри.
     *
     * <p><b>Входни данни от потребителя:</b></p>
     * <ul>
     *   <li><b>std</b> — стандартно отклонение (double), използвано при нормалното разпределение</li>
     *   <li><b>matches</b> — брой съвпадащи числа от предходния тираж (int, между 0 и 6 включително)</li>
     * </ul>
     *
     * <p><b>Функционалност:</b></p>
     * <ol>
     *   <li>Извежда деня на следващото теглене и честотата на срещане на числата за този ден</li>
     *   <li>Позволява въвеждане на параметрите std и matches от конзолата</li>
     *   <li>Извлича последния официален тираж от <i>officialToto.txt</i></li>
     *   <li>Генерира 3 уникални предложения чрез {@code gaussianNumbersGenerator(...)}</li>
     *   <li>Отпечатва предложенията и сравнението им с последния официален тираж</li>
     *   <li>Позволява запис на предложенията в текстов файл, ако потребителят потвърди</li>
     * </ol>
     *
     * <p><b>Използвани файлове:</b></p>
     * <ul>
     *   <li><b>officialToto.txt</b> — последен официален тираж (прочит)</li>
     *   <li><b>bgTotoFromSite.txt</b> — временен файл за индексиране на записите (прочит/запис)</li>
     *   <li><b>tmpRes/*.txt</b> — файл за запис на генерираните предложения (запис)</li>
     * </ul>
     *
     * <p><b>Свързани методи:</b></p>
     * <ul>
     *   <li>{@link Date#findNextDrawDay()} — определя следващия ден за теглене</li>
     *   <li>{@link Frequency#ofMatchesForDayOfWeek(String)} — показва честота на числата по ден</li>
     *   <li>{@link Generator#newGaussianProposalList(List, double, int)} — генерира предложения</li>
     *   <li>{@link Generator#printProposalList(Set, double, int)} (Set<List<Integer>>, double, int)} — отпечатва и сравнява резултати</li>
     *   <li>{@link WebData#getWebIdxFromFileData(String)} — определя индекс за запис на база броя записи</li>
     *   <li>{@link Write#toFile(Set, int, double, int)} — записва предложенията във файл</li>
     *   <li>{@link ParseURL#readFromFile(File)} — зарежда редове от текстов файл</li>
     *   <li>{@link Manipulate#strToListOfInt(String)} — преобразува числова редица от String към List</li>
     *   <li>{@link Manipulate#extractData(String, String)} — извлича числова част от ред</li>
     *   <li>{@link Manipulate#answer()} — получава вход от потребителя</li>
     * </ul>
     */
    protected static void newSupposeOld(boolean isGaussian) {
        double std = -1.1;
        int matches = -1;
        boolean wasThisProposalWritten = false;
        Set<List<Integer>> suppose = null;

        String stdRegEx = "^(?:[0-9](?:[.,]\\d+)?|[1-9][0-9]?(?:[.,]\\d+)?|30(?:[.,]0+)?)$";
        String matchesRegEx = "^[0-5]$";

        String officialTotoPath = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";
        String bgTotoFilePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Да генерирам ли предложения за залози?: ");
        String ans = scanner.nextLine();
        ans = validateAns(ans, scanner, "Y", "N");

        if (ans.equalsIgnoreCase("Y")) {
            String today = Date.findNextDrawDay();
            //System.out.println(today.toUpperCase());
            //Frequency.ofMatchForDayOfWeek(today);

            //int numberOdDays = Manipulate.allSpecificDayCntInArchive(today);
            System.out.print("\nСтатистика за ");
            Map<Integer, List<Integer>> map = Frequency.ofMatching(today, true);
            Print.printMap(map);

            List<String> data = ParseURL.readFromFile(new File(officialTotoPath));
            String tmp = Manipulate.extractData(data.getFirst(), "-");
            List<Integer> oldDraw = Manipulate.strToListOfInt(tmp);

            if (isGaussian) {
                suppose = Generator.newGaussianProposalList(oldDraw, std, matches);
            } else suppose = Generator.ofNewProposalListOfFrequencyMatches();

            while (ans.equalsIgnoreCase("Y")) {
                System.out.print("\nstd: ");
                String line = scanner.nextLine();
                std = digitsValidator(stdRegEx, scanner, line, true);

                System.out.print("matches: ");
                line = scanner.nextLine();
                matches = (int) digitsValidator(matchesRegEx, scanner, line, false);

                //suppose = newSupposeList(oldDraw, std, matches);
                suppose = ofNewProposalListOfFrequencyMatches();
                printProposalList(suppose, std, matches);

                System.out.print("Нов?: ");
                String newAns = scanner.nextLine();
                ans = validateAns(newAns, scanner, "Y", "N");
            }

            printThisProposal(bgTotoFilePath, suppose, matches, std,
                    "404: purposeSuppose(): ");
        }
    }

    /**
     * Генерира ново предположение за тото комбинация, базирана частично на предишни тиражи
     * и частично на случайно генерирани числа с нормално разпределение.
     *
     * <p>Комбинацията винаги съдържа точно 6 уникални числа от 1 до 49 включително.</p>
     *
     * <p>Методът:
     * <ol>
     *   <li>Избира {@code matches} на брой числа от старите тиражи (след разбъркване)</li>
     *   <li>Изчислява средната стойност {@code mean} на старите тиражи</li>
     *   <li>Генерира нови числа около {@code mean}, използвайки нормално разпределение и подаденото {@code std}</li>
     *   <li>Добавя новите числа към резултата, ако не са дублиращи и са в позволения диапазон</li>
     * </ol>
     *
     * @param oldDraws Списък от всички числа, участвали в предишни тиражи
     * @param std      Стандартното отклонение за нормалното разпределение (примерно: 5.2)
     * @param matches  Брой числа от предишните тиражи, които да се включат директно в новата комбинация (между 0 и 6)
     * @return Списък с 6 уникални, сортирани числа (от тип {@code List<Integer>}). Връща {@code null}, ако {@code matches} е извън обхвата [0, 6]
     *
     * <p><b>Пример:</b><br>
     * Ако подадеш {@code oldDraws = [3, 5, 17, 28, 36, 42, 49]} и {@code matches = 2}, методът ще избере 2 от тях
     * (случайни), а останалите 4 ще ги генерира чрез нормално разпределение около средната стойност на списъка.</p>
     */
    static List<Integer> gaussianNumbersGeneratorOld(List<Integer> oldDraws, double std, int matches) {
        final int LOTTO_SIZE = 6;
        final int MAX_NUMBER = 49;
        Random random = new Random();

        if (matches < 0 || matches > LOTTO_SIZE) {
            System.out.println("Съвпаденията трябва да бъдат между 0 и 6");
            return null;
        }

        Set<Integer> result = new HashSet<>();
        Collections.shuffle(oldDraws);
        List<Integer> matchingNumbers = oldDraws.subList(0, matches);
        result.addAll(matchingNumbers);

        double mean = Statistic.genericMean(oldDraws);
        while (result.size() < LOTTO_SIZE) {
            int number = (int) (Math.round(random.nextGaussian() * std) + mean);
            if (number >= 1 && number <= MAX_NUMBER) {
                result.add(number);
            }
        }

        return result.stream().sorted().toList();
    }

}
