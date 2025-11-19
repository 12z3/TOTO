package org.example;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Клас {@code Date} съдържа методи, свързани с обработка на дати и времена
 * във връзка с тиражите на тото 6/49. Основната му цел е да управлява времевата
 * логика относно:
 * <ul>
 *   <li>Изчисляване на оставащото време до следващия тираж</li>
 *   <li>Извеждане на дните, в които има тегления (например четвъртък и неделя)</li>
 *   <li>Парсване на дати и времена от текстови формати</li>
 *   <li>Генериране на списъци с дати, които са подходящи за анализ</li>
 * </ul>
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *   <li>Пресмята времето до следващия тираж, според текущата дата и час</li>
 *   <li>Групира дните на тегления по години</li>
 *   <li>Филтрира тегленията спрямо ден от седмицата</li>
 *   <li>Форматира дати в удобен текстов вид (например "30 APR 2025")</li>
 *   <li>Проверява колко тегления са се провели на определен ден от седмицата</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * <ul>
 *   <li>Използва {@link java.time.LocalDateTime}, {@link java.time.DayOfWeek} и {@link java.time.Duration} за пресмятания</li>
 *   <li>Използва {@link java.time.format.DateTimeFormatter} за форматиране на дати</li>
 *   <li>Чете данни от файлове чрез {@link java.util.Scanner}</li>
 *   <li>Методи като {@code countDrawOnDay} анализират честотата на тегления за даден ден</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * <ul>
 *   <li>За да се подготвят предварителни или архивни анализи на тегления по време</li>
 *   <li>За да се установи дали текущата дата е близка до предстоящо теглене</li>
 *   <li>За да се улесни форматирането на изхода във файловете с резултати</li>
 * </ul>
 *
 * <h2>Ключови методи:</h2>
 * <ul>
 *   <li>{@code timeToDraw()} – изчислява времето до следващия четвъртък/неделя в 19:30</li>
 *   <li>{@code countDrawOnDay(String)} – брои тегленията за определен ден</li>
 *   <li>{@code formatDrawDate(LocalDateTime)} – преобразува дата до вид "dd MMM yyyy"</li>
 *   <li>{@code parseInfoDrawByString()} – генерира информация от ред със записан тираж</li>
 *   <li>{@code allowDateAndDayForYear()} – връща списък с валидни дати за тегления за анализ</li>
 *   <li>{@code getCurrentDate()} – връща текуща дата/час</li>
 * </ul>
 *
 * <h2>Входни/Изходни данни:</h2>
 * <ul>
 *   <li>Изходните резултати често са {@code List<String>} или {@code Map<String, Integer>}</li>
 *   <li>Данните се зареждат от файлове като {@code fromSite.txt} чрез {@code Scanner}</li>
 * </ul>
 *
 * @author bNN
 * @version 1.0
 */
public class Date {
    public static void main(String[] args) throws IOException {
        createdDayDrawString("   ").forEach(System.out::println);
        allDrawDateAndDayForYear().forEach(System.out::println);

        for (Map.Entry<List<Integer>, String> el : dayOfWeekDraws().entrySet()) {
            System.out.println(el.getKey() + " " + el.getValue());
        }

        for (Map.Entry<String, Integer> el : countDrawDay("THURSDAY").entrySet()) {
            System.out.println(el.getKey() + " " + el.getValue());
        }

        allDrawDateAndDayForYear().forEach(System.out::println);

    }

    /**
     * Изчислява следващия момент на теглене в четвъртък или неделя в 19:30 часа спрямо текущия ден и час.
     *
     * <p><b>Какво прави:</b><br>
     * Определя кога ще е следващото теглене на база правилата:
     * - В четвъртък и неделя има теглене в 19:30 часа.
     * - Ако днес е четвъртък или неделя и е преди 19:30 — тегленето е днес в 19:30.
     * - Ако е след 19:30 — следващото теглене е в другия ден (неделя или четвъртък).
     * - Ако е друг ден от седмицата — тегленето е в най-близкия четвъртък или неделя, според деня.
     *
     * <p><b>Как го прави:</b><br>
     * 1. Задава двата специални дни: четвъртък и неделя.<br>
     * 2. Взима текущата дата и час.<br>
     * 3. Създава фиксиран целеви час — 19:30.<br>
     * 4. Изчислява:
     * - Кога е следващият четвъртък (включително днес, ако е четвъртък).
     * - Кога е следващата неделя (включително днес, ако е неделя).
     * 5. Създава два варианта на пълна дата и час за теглене:
     * - `nextThursdayTime` (следващ четвъртък в 19:30)
     * - `nextSundayTime` (следваща неделя в 19:30)
     * 6. Проверява какъв е днешният ден:
     * <p>
     * - **Ако днес е четвъртък:**
     * - Ако текущото време е **преди 19:30** → тегленето е днес в 19:30.
     * - Иначе → тегленето е в неделя в 19:30.
     * <p>
     * - **Ако днес е неделя:**
     * - Ако текущото време е **преди 19:30** → тегленето е днес в 19:30.
     * - Иначе → тегленето е в четвъртък в 19:30.
     * <p>
     * - **Ако днес е друг ден:**
     * - Ако денят е **преди четвъртък** → тегленето е в четвъртък.
     * - Ако е между четвъртък и неделя → тегленето е в неделя.
     * - Ако е понеделник след неделя → тегленето е чак в следващия четвъртък.
     *
     * <p><b>Ключови особености на логиката:</b><br>
     * - Приоритет е текущият ден ако е четвъртък или неделя преди 19:30.<br>
     * - Ако е след 19:30, винаги се премества към следващия валиден ден.<br>
     * - Използва {@code TemporalAdjusters.nextOrSame()} за намиране на следващ четвъртък/неделя.<br>
     * - Използва {@code LocalDateTime.of()} за съчетаване на дата и целеви час.
     *
     * <p><b>Пример за използване:</b>
     * <pre>{@code
     * DateUtils.timeDuration();
     * }</pre>
     */
    static void timeDuration() {
        // Специални дни:
        DayOfWeek thursday = DayOfWeek.THURSDAY;
        DayOfWeek sunday = DayOfWeek.SUNDAY;
        LocalDateTime now = LocalDateTime.now();
        LocalTime targetTime = LocalTime.of(19, 30);

        // Намира текущия ден и час:
        LocalDate todayDate = now.toLocalDate();
        DayOfWeek today = todayDate.getDayOfWeek();
        LocalTime nowTime = now.toLocalTime();

        // Намира следващия четвъртък и неделя:
        LocalDate nextThursday = todayDate.with(TemporalAdjusters.nextOrSame(thursday));
        LocalDate nextSunday = todayDate.with(TemporalAdjusters.nextOrSame(sunday));

        // Създаване на дата и час за теглене:
        LocalDateTime nextThursdayTime = LocalDateTime.of(nextThursday, targetTime);
        LocalDateTime nextSundayTime = LocalDateTime.of(nextSunday, targetTime);
        LocalDateTime drawDateTime = null;

        if (today == thursday) {
            if (nowTime.isBefore(targetTime)) {
                drawDateTime = LocalDateTime.of(todayDate, targetTime);
            } else {
                drawDateTime = nextSundayTime;
            }
        } else if (today == sunday) {
            if (nowTime.isBefore(targetTime)) {
                drawDateTime = LocalDateTime.of(todayDate, targetTime);
            } else {
                drawDateTime = nextThursdayTime;
            }
        } else {
            // Нито е четвъртък, нито неделя
            if (today.getValue() < thursday.getValue()) {
                drawDateTime = nextThursdayTime;
            } else if (today.getValue() < sunday.getValue()) {
                drawDateTime = nextSundayTime;
            } else {
                // Ако е понеделник след неделя — отива към следващия четвъртък
                drawDateTime = nextThursdayTime;
            }
        }

        // Изчислява разликата:
        Duration duration = Duration.between(now, drawDateTime);
        long totalSeconds = duration.getSeconds();
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String day = days == 1 ? " ден " : " дни ";
        String txt = days == 1 ? " остава: " : " остават: ";
        String hour = hours == 1 ? " час " : " часа ";
        String min = minutes == 1 ? " минута " : " минути ";
        String sec = seconds == 1 ? " секунда " : " секунди";

        // Окончателен текст
        System.out.println("До следващият тираж" + txt
                + days + day + hours + hour + minutes + min + "и " + seconds + sec + " - "
                + (drawDateTime.getDayOfWeek().toString().equals("SUNDAY") ? "НЕДЕЛЯ." : "ЧЕТВЪРТЪК."));
    }


    /**
     * Генерира списък от форматирани редове, съдържащи информация за тегленията на Тото 2 (6 от 49),
     * като комбинира номера на тиража, датата и числата от тегленето.
     *
     * <p>Методът използва два източника:
     * <ul>
     *   <li>{@code allDrawDateAndDayForYear()} — връща списък с редове във формат:
     *       <b>"142-02 MAR 2025-THURSDAY"</b>, където:
     *       <ul>
     *         <li>142 е номер на тиража</li>
     *         <li>02 MAR 2025 е дата</li>
     *         <li>THURSDAY е денят от седмицата</li>
     *       </ul>
     *   </li>
     *   <li>{@code ParseURL.parseBgTotoURL()} — връща списък с редове като:
     *       <b>"142-1 5 13 22 28 47"</b>, където:
     *       <ul>
     *         <li>142 е номер на тиража</li>
     *         <li>"1 5 13 22 28 47" са изтеглените числа</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <p>Комбинира двете по индекс на тиража и връща резултати във формат:
     * <p> return new ArrayList<>(List.of("❌ Грешка: Неуспешно свързване със сайта!")): е необходим за да се
     * 	 * обработи коректно грешката в getThreeOfficialDB()</p>
     * <pre>
     * 142 <regex> 02 MAR 2025 <regex> 1 5 13 22 28 47
     * </pre>
     *
     * <p><b>Пример:</b>
     * <pre>{@code
     * List<String> formatted = createdDayDrawString(" - ");
     * formatted.get(0);
     * // Изход: "142 - 02 MAR 2025 - 1 5 13 22 28 47"
     * }</pre>
     *
     * @param regex разделител, който се поставя между номер на тиража, дата и числата (напр. " - ")
     * @return списък от низове, всеки от които представя едно теглене в пълен формат
     */
    static List<String> createdDayDrawString(String regex) {
        List<String> dates = allDrawDateAndDayForYear();                    // напр. "142-02 MAR 2025-THURSDAY"
        List<String> draws = ParseURL.parseBgTotoURL();                     // напр. "142-1 5 13 22 28 47"

        if (draws.contains("❌ Грешка: Неуспешно свързване със сайта!")) {
            String prevDraw = ParseURL.readDB(
                    "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt").getLast();
            List<String> connectionFalseList = new ArrayList<>();
            for (int i = 0; i < 1; i++) connectionFalseList.add(prevDraw); // ? for (int i = 0; i < 1; i++)
            return connectionFalseList;
        }

        Map<String, String> indexToDateMap = new HashMap<>();
        for (String dateEntry : dates) {
            String[] parts = dateEntry.split("-");     // parts[0] = index, parts[1] = date, parts[2] = weekday
            indexToDateMap.put(parts[0], parts[1]);
        }

        List<String> res = new ArrayList<>();
        for (int j = draws.size() - 1; j >= 0; j--) {
            String[] drawParts = draws.get(j).split("-");
            String index = drawParts[0];
            String numbers = drawParts[1].replace(",", ", ");

            if (indexToDateMap.containsKey(index)) {
                String date = indexToDateMap.get(index);
                res.add(index + regex + date + regex + numbers);
            }
        }
        return res;
    }

    /**
     * Генерира списък с всички дати на тегления за текущата година, групирани по двойки:
     * Тегленията са единственно във Неделя и Четвъртък
     * <ul>
     *   <li>Четвъртък → Неделя</li>
     *   <li>Неделя → Четвъртък</li>
     * </ul>
     *
     * <p>Методът намира първата дата от текущата година, която е или четвъртък, или неделя,
     * и я използва като начална (pivotDate). След това:
     *
     * <ul>
     *   <li>Ако първият ден е <strong>неделя</strong>, цикълът създава двойки:
     *     <ol>
     *       <li>Неделя → Четвъртък (плюс 4 дни)</li>
     *       <li>Четвъртък → Неделя (плюс 3 дни)</li>
     *     </ol>
     *   </li>
     *   <li>Ако първият ден е <strong>четвъртък</strong>, цикълът започва от Четвъртък → Неделя и обратно.</li>
     * </ul>
     *
     * <p>Всяка дата се форматира като:
     * <pre>
     * <индекс>-<дата във формат dd MMM yyyy>-<ден от седмицата>
     * </pre>
     *
     * <p>Примерен резултат:
     * <pre>
     * 1-05 Jan 2025-SUNDAY
     * 2-09 Jan 2025-THURSDAY
     * 3-12 Jan 2025-SUNDAY
     * 4-16 Jan 2025-THURSDAY
     * ...
     * </pre>
     *
     * <p>Генерирането приключва при достигане на 1 януари следващата година.
     *
     * <p><strong>Бележка:</strong> Методът обработва грешки при парсване на датите и
     * логва съобщение на български, ако възникне изключение.
     *
     * @return Списък с всички двойки дати на тегления за текущата година и съответните дни от седмицата.
     */
    static List<String> allDrawDateAndDayForYear() {
        List<String> res = new ArrayList<>();
        StringBuilder stbThur;
        StringBuilder stbSun;
        LocalDate pivotDate;

        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String firstDay = "01 Jan " + year;

        int pvtIdx = 0;
        // 01.01.2026
        String endDate = "01 Jan " + (now.getYear() + 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        try {
            // 31.12.2024
            LocalDate thisDay = LocalDate.parse(firstDay, formatter).minusDays(1);
            while (true) {
                // 01.01.2025
                thisDay = thisDay.plusDays(1);
                // Намира първият Четвъртък или Неделя от текущата година
                if (thisDay.getDayOfWeek() == DayOfWeek.THURSDAY || thisDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    pivotDate = thisDay;
                    break;
                }
            }

            //
            LocalDate endDay = LocalDate.parse(endDate, formatter);
            if (pivotDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                LocalDate sundayFromThursday = pivotDate;                                      // неделя
                while (true) {
                    stbThur = new StringBuilder();
                    stbSun = new StringBuilder();
                    LocalDate thursdayFromSunDay = sundayFromThursday.plusDays(4);  // четвъртък

                    stbThur.append(++pvtIdx).append("-")
                            .append(sundayFromThursday.format(formatter)).append("-")
                            .append(sundayFromThursday.getDayOfWeek());
                    stbSun.append(++pvtIdx).append("-")
                            .append(thursdayFromSunDay.format(formatter)).append("-")
                            .append(thursdayFromSunDay.getDayOfWeek());

                    sundayFromThursday = thursdayFromSunDay.plusDays(3);             // неделя
                    if (sundayFromThursday.isAfter(endDay)) break;

                    res.add(stbThur.toString());
                    res.add(stbSun.toString());

                }
            } else if (pivotDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                LocalDate thursdayFromSunDay = pivotDate;                                       // четвъртък
                while (true) {
                    stbThur = new StringBuilder();
                    stbSun = new StringBuilder();

                    LocalDate sundayFromThursday = thursdayFromSunDay.plusDays(3);    // неделя

                    stbThur.append(++pvtIdx).append("-")
                            .append(thursdayFromSunDay.format(formatter)).append("-")
                            .append(thursdayFromSunDay.getDayOfWeek());
                    stbSun.append(++pvtIdx).append("-")
                            .append(sundayFromThursday.format(formatter)).append("-")
                            .append(sundayFromThursday.getDayOfWeek());

                    thursdayFromSunDay = sundayFromThursday.plusDays(4);              // четвъртък
                    if (thursdayFromSunDay.isAfter(endDay)) break;

                    res.add(stbThur.toString());
                    res.add(stbSun.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("404: allDrawDateAndDayForYear(): " +
                    "Не мога да обработя коректно датите...");
        }
        return res;
    }

    static String timeNow() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime timeNow = LocalDateTime.now();
        return timeNow.format(timeFormatter);
    }

    /**
     * Чете редове от локален файл с изтегляния от тотото и създава асоциативна структура
     * между списък с изтеглени числа и деня от седмицата, в който са били изтеглени.
     *
     * <p>Очакваният формат на файла е:
     * <pre>
     *   <номер>          <дата>              <числа>
     *     123    02 APR 2025-WEDNESDAY   3,15,18,24,31,47
     * </pre>
     * като полетата са разделени с ТРИ интервала ("   "), не табулация или един интервал.</p>
     *
     * <p>Методът използва:
     * <ul>
     *   <li><b>index 1</b> (второ поле) за датата, която се конвертира до ден от седмицата</li>
     *   <li><b>index 2</b> (трето поле) за числата, които се парсват в {@code List<Integer>}</li>
     * </ul>
     * </p>
     *
     * @return `Map`, където:
     * <ul>
     *   <li>ключовете са списъци от изтеглени числа ({@code List<Integer>})</li>
     *   <li>стойностите са текстови представяния на ден от седмицата (например "WEDNESDAY")</li>
     * </ul>
     * @see Manipulate#parseToInt(String) – за парсване на числата
     * @see Date#dayOfWeekName(String) – за извличане на деня от седмицата от дата
     */
    static Map<List<Integer>, String> dayOfWeekDraws() {
        String path = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
        Map<List<Integer>, String> drawsDayOfWeek = new LinkedHashMap<>();
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String date = line.split(" {3}")[1].trim();
                String numbers = line.split(" {3}")[2].trim();
                List<Integer> draws = Manipulate.parseToInt(numbers);
                String thisDay = dayOfWeekName(date);
                drawsDayOfWeek.put(draws, thisDay);
            }
        } catch (Exception e) {
            System.out.println("404: DayOfWeekDraws(): " +
                    "Файла \"bgTotoFromSite.txt\" не може да се отвори.");
        }
        return drawsDayOfWeek;
    }

    /**
     * Преобразува дадена дата във формат "dd MMM yyyy" (напр. "02 APR 2025")
     * към съответния ден от седмицата като низ.
     *
     * <p>Използва {@link DateTimeFormatter} с локал {@code Locale.ENGLISH},
     * за да интерпретира датата правилно.
     *
     * @param date дата като низ, напр. "02 APR 2025"
     * @return ден от седмицата в {@code String} формат – например "WEDNESDAY"
     */
    static String dayOfWeekName(String date) {
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate thisDay = LocalDate.parse(date, formater);
        return thisDay.getDayOfWeek().toString();
    }

    /**
     * Определя следващия ден за теглене на база текущия ден от седмицата и час.
     * <p>
     * Методът връща името на следващия ден за теглене – "THURSDAY" или "SUNDAY", в зависимост от текущия ден
     * и час. Часът на теглене е фиксиран на 19:30:00.
     * <ul>
     *   <li>Ако текущият ден е понеделник, вторник, сряда или четвъртък преди 19:30, се връща "THURSDAY".</li>
     *   <li>Ако текущият ден е четвъртък (след 19:30), петък, събота или неделя преди 19:30, се връща "SUNDAY".</li>
     *   <li>Ако е неделя след 19:30, се счита, че чакаме новия четвъртък и връща "THURSDAY".</li>
     * </ul>
     *
     * @return Името на деня от седмицата, в който ще се проведе следващото теглене – "THURSDAY" или "SUNDAY".
     */
    static String findNextDrawDay() {
        LocalTime drawTime = LocalTime.parse("19:30:00");
        LocalTime nowTime = LocalTime.now();
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        if (today == DayOfWeek.THURSDAY) {
            return nowTime.isBefore(drawTime) ? "THURSDAY" : "SUNDAY";
        } else if (today == DayOfWeek.SUNDAY) {
            return nowTime.isBefore(drawTime) ? "SUNDAY" : "THURSDAY";
        } else if (today.getValue() < DayOfWeek.THURSDAY.getValue()) {
            return "THURSDAY";
        } else if (today.getValue() < DayOfWeek.SUNDAY.getValue()) {
            return "SUNDAY";
        } else {
            // Това ще се изпълни само ако днес е СЪБОТА (или неделя след 19:30, но вече е покрито по-горе)
            return "THURSDAY";
        }
    }

    /**
     * Преброява всички тегления на конкретен ден от седмицата за текущата година.
     *
     * <p>Методът обхожда всички налични тегления за годината, като за всяко проверява
     * дали съвпада с търсения ден от седмицата (например "THURSDAY" или "SUNDAY").
     * Ако съвпадне, броячът се увеличава с едно.
     * Накрая връща асоциативна структура (Map) с едно единствено съответствие:
     * търсения ден ➔ брой на намерените тегления за него.</p>
     *
     * <h3>Формат на данните:</h3>
     * Данните за всички тегления са взети от метода {@code dayOfWeekDraws()},
     * който връща карта от списък от числа към ден от седмицата.
     *
     * <h3>Пример за работа:</h3>
     *
     * <pre>
     * Вход:
     *   thisDay = "THURSDAY"
     *   тегления (ден от седмицата):
     *     [3,15,18,24,31,47] -> "THURSDAY"
     *     [5,10,22,30,40,49] -> "SUNDAY"
     *     [6,12,19,23,35,48] -> "THURSDAY"
     *
     * Изпълнение:
     *   - първото теглене е "THURSDAY" ➔ брояч +1
     *   - второто теглене е "SUNDAY" ➔ пропуск
     *   - третото теглене е "THURSDAY" ➔ брояч +1
     *
     * Изход:
     *   {"THURSDAY" = 2}
     * </pre>
     *
     * @param thisDay търсения ден от седмицата като текст (напр. "THURSDAY", "SUNDAY")
     * @return {@code Map<String, Integer>} — карта с търсения ден като ключ и
     * брой на намерените тегления като стойност.
     */
    static Map<String, Integer> countDrawDay(String thisDay) {
        Map<String, Integer> dayCnt = new TreeMap<>();
        Map<List<Integer>, String> drawDay = dayOfWeekDraws();
        int cnt = 0;
        for (Map.Entry<List<Integer>, String> el : drawDay.entrySet()) {
            String day = el.getValue();
            if (thisDay.equalsIgnoreCase(day)) cnt++;
        }
        dayCnt.put(thisDay, cnt);
        return dayCnt;
    }

    static LocalDateTime getCurrentDay() {
        return LocalDateTime.now();
    }

    /**
     * Генерира списък от обединени записи, съдържащи информация за индекс на теглене,
     * дата и самото теглене, като съпоставя записи от:
     * <ul>
     *   <li>{@code allDrawDateAndDayForYear()} — списък с дати и дни на тегления</li>
     *   <li>{@code ParseURL.parseBgTotoURL()} — списък с тегления от сайта</li>
     * </ul>
     *
     * <p>Всяко съвпадение се открива чрез сравнение на индексите (първият елемент от записите),
     * и при съвпадение се съставя ред във формат:
     *
     * <pre>
     * &lt;индекс&gt; &lt;дата&gt; &lt;теглени числа&gt;
     * </pre>
     *
     * <p>Например:
     * <pre>
     * 142 02 MAR 2025 1,5,13,22,28,47
     * </pre>
     *
     * <p>Резултатът се връща в низов списък {@code List<String>} и може да бъде записан във файл.
     *
     * @return списък със съпоставени записи между тегления и дати
     */
    static List<String> createdDayDrawStringOld() {
        List<String> dates = allDrawDateAndDayForYear();
        List<String> draws = ParseURL.parseBgTotoURL();
        Set<String> ds = new TreeSet<>(dates);

        List<String> res = new ArrayList<>();

        for (int j = draws.size() - 1; j >= 0; j--) {
            for (int i = 0; i < ds.size(); i++) {
                if (draws.get(j).split("-")[0].equals(dates.get(i).split("-")[0])) {
                    res.add(draws.get(j).split("-")[0] + "   " + dates.get(i).split("-")[1] + "   " +
                            draws.get(j).split("-")[1]);
                }
            }
        }
        return res;
    }
}
