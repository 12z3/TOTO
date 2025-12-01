package org.example;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Класът {@code Checks} представлява помощен инструмент за извършване на базови проверки,
 * използвани при работа с файлове и потребителски вход в текстов интерфейс.
 *
 * <p><b>Основна логика на класа:</b></p>
 * Класът централизира логиката по валидация на входни стойности и съществуване на ресурси
 * преди да бъдат използвани от други части на програмата. Това включва:
 * <ul>
 *     <li>проверка за съществуване на файл в директория чрез частично съвпадение на името;</li>
 *     <li>проверка дали даден текстов низ съответства на шаблон за дата/час (форматна валидация);</li>
 *     <li>интерактивна валидация на потребителски избор от предварително зададени опции;</li>
 *     <li>печат на съобщения при липсващи файлове или невалидни стойности.</li>
 * </ul>
 *
 * <p><b>Използвани методи и тяхната роля:</b></p>
 * <ul>
 *     <li>{@code fileExists(String dirPath, String fileName)} — обхожда директория и проверява
 *         дали има файл, чието име съдържа подадения низ.</li>
 *
 *     <li>{@code validFormat(String input, String pattern)} — валидира дали даден низ е съвместим
 *         с шаблон за дата и час чрез {@code DateTimeFormatter} и {@code LocalDateTime}.</li>
 *
 *     <li>{@code getAnswer(String... options)} — изисква отговор от потребителя, повтаря подканата,
 *         докато не бъде въведен валиден отговор, съвпадащ с един от разрешените.</li>
 *
 *     <li>{@code fileNotFoundMessage(String name)} — помощен метод за отпечатване на съобщение
 *         при липса на файл, което подсказва на потребителя какво липсва.</li>
 * </ul>
 *
 * <p><b>Защо съществува този клас:</b></p>
 * Целта му е да събере на едно място всички повтарящи се проверки, свързани с:
 * <ul>
 *     <li>валидиране на имена;</li>
 *     <li>валидиране на входен формат;</li>
 *     <li>сигурно четене от конзолата;</li>
 *     <li>контрол над поведението при липса на очакван файл.</li>
 * </ul>
 * Така се избягва дублиране на код и се улеснява повторната употреба и поддръжка.
 *
 * <p><b>Типична употреба:</b></p>
 * Класът се използва в класове, които работят с файлове (например криптиране/декриптиране),
 * за да проверят дали даден файл реално съществува и дали потребителят е въвел допустима команда.
 *
 * @author bNN
 * @version 1.0
 */
public class Checks {
    public static void main(String[] args) {
    }

	/*
		И трите метода isNewDrawFromSite(), getNewDrawFromSite() и forNewDraw() разчитат, че siteData ще върне:
		масив с редове - 33-24 Apr 2025-8, 16, 33, 41, 45, 46 -
		За това в ParseURL при грешка на връзката със сайта методите връщат старите тиражи архивирани във
		файловете.
	 */

    /**
     * Основна логика за проверка и обработка на нов тираж от сайта.
     *
     * <li>Наличие на ново теглене се установява като се вземе новият тираж с най-голям индекс и се
     * сарвни с последният записан в: bgTotoFromSite.txt тираж.</li>
     *
     * <p>Ако се установи, че в подадените редове има ново теглене,
     * методът:
     * <ol>
     *     <li>Извлича най-новия ред чрез {@link #getNewDrawFromSite(List)}</li>
     *     <li>Извършва запис във всички съответни файлове чрез {@link Write#writeThisDrawInAllFiles(String, List, boolean[])}</li>
     *     <li>Извежда резултат чрез {@code Print.messageX()}</li>
     * </ol>
     *
     * <p>Очакваният вход е списък от редове, прочетени от сайта, всеки във формат:
     * <pre>
     * индекс-дата-ден - числа
     * например:
     * 32-20 Apr 2025-1, 23, 28, 29, 38, 44
     * 32-20 Apr 2025-1, 23, 28, 29, 38, 44
     * 32-20 Apr 2025-1, 23, 28, 29, 38, 44
     * </pre>
     * <p>Методът хваща потенциален {@link IndexOutOfBoundsException}, ако някой ред е с грешен формат.</p>
     *
     * @param siteData редове с тегления от сайта
     *                 <li>И трите метода isNewDrawFromSite(), getNewDrawFromSite() и forNewDraw() разчитат, че siteData ще върне:
     *                 масив с редове - 33-24 Apr 2025-8, 16, 33, 41, 45, 46 -
     *                 За това в ParseURL при грешка на връзката със сайта, методите:
     *                 parseBgTotoURL(), parseToto49URL(), parseOfficialTotoURL()
     *                 връщат старите тиражи архивирани във файловете.
     *                 </li>
     */
    static void forNewDraw(List<String> siteData) {
        boolean isNew = false;
        boolean[] urls = new boolean[3];
        try {
            if (isNewDrawOnSite(siteData)) {
                isNew = true;
                urls = urlArr(urls, siteData);

                // От трите резултата взима най-новият:
                String newDraw = getNewDrawFromSite(siteData);
                //... И го записва във всички файлове. Затова ти пропада проверката за липсващи тиражи!
                Write.writeThisDrawInAllFiles(newDraw, siteData, urls);
            }
            Print.messageX(isNew, urls, siteData);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("↪︎404: Провери си мрежата...");
        } catch (FileNotFoundException fe) {
            System.out.println("↪︎404: Файла в който записваш е криминален...");
        }
    }

    /**
     * <li>Сравнява датата на всеки един ред от siteData с датата на последният архив във файла
     * bgTotoFromSite.txt. Връща този ред от намерените тиражи от сайтовете, който е най близък до деня на
     * извикване на метода.</li>
     * <p>
     * Връща ред от списъка с тегления чиято дата е най-близка до текущата.
     *
     * <p>Всеки ред съдържа дата на теглене във формат:
     * <pre>
     *     индекс - dd MMM yyyy - ден - числа
     * </pre>
     * Пример:
     * <pre>
     *     32-20 Apr 2025-1, 23, 28, 29, 38, 44
     *     31-19 Apr 2025-1, 3, 18, 39, 38, 47
     *     30-12 Apr 2025-1, 13, 21, 27, 36, 49
     * </pre>
     *
     * <p>Методът сравнява всяка дата с референтна дата (точно преди 1 година)
     * и връща най-новия ред, ако такъв бъде намерен.</p>
     * <p>Дублира проверката за нов тираж от isNewDrawFromSite()</p>
     *
     * @param siteData списък от редове, съдържащи тегления от сайта:<p> 32-20 Apr 2025-1, 23, 28, 29, 38, 44</p>
     * @return редът с най-новата дата или {@code ""} ако няма такъв: <p>32-20 Apr 2025-1, 23, 28, 29, 38, 44</p>
     *
     * <li>И трите метода isNewDrawFromSite(), getNewDrawFromSite() и forNewDraw() разчитат, че siteData ще върне:
     * масив с редове - 33-24 Apr 2025-8, 16, 33, 41, 45, 46 -
     * За това в ParseURL при грешка на връзката със сайта, методите:
     * parseBgTotoURL(), parseToto49URL(), parseOfficialTotoURL()
     * връщат старите тиражи архивирани във файловете.
     * </li>
     */
    static String getNewDrawFromSite(List<String> siteData) {
        String draw = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate maxDate = LocalDate.now().minusYears(1);

        for (String siteDatum : siteData) {
            try {
                if (!siteDatum.equalsIgnoreCase(
                        "❌ Грешка: Неуспешно свързване със сайта!")
                        && !siteDatum.equalsIgnoreCase("")) {
                    String date = siteDatum.trim().split("-")[1];
                    LocalDate thisDate = LocalDate.parse(date, formatter);

                    if (thisDate.isAfter(maxDate)) {
                        maxDate = thisDate;
                        draw = siteDatum;
                    }
                }
            } catch (Exception e) {
                System.out.println("404: getNewDrawFromSite(): " +
                        "❌ Грешка: Неуспешно свързване със сайта!");
            }
        }
        return draw;
    }

    /**
     * <p>Проверява дали в подадените редове от сайта има нов тираж,
     * като сравнява индексите на записите.</p>
     *
     * <p>Очаква се всеки ред да е във формат:
     * <pre>
     *     124-04 APR 2025-SATURDAY - 3,15,18,24,31,47
     * </pre>
     * Числото преди първото тире представлява поредния индекс.</p>
     *
     * <p>Извлича всички индекси, намира най-големия и най-малкия.
     * Ако са различни — значи има нов запис, върща {@code true}.</p>
     *
     * <p>Сравнява дали залога с най-голям индекс от сайта съвпада с последният запис
     * във файла {@code bgTotoFromSite.txt}
     * * Ако са различни — значи има нов запис, върща {@code true}.</p>
     *
     * @param siteData списък с текстови редове, прочетени от източник (сайт)
     * @return {@code true} ако има нов запис, {@code false} ако всички са еднакви
     *
     * <li>И трите метода isNewDrawFromSite(), getNewDrawFromSite() и forNewDraw() разчитат, че siteData ще върне:
     * масив с редове - 33-24 Apr 2025-8, 16, 33, 41, 45, 46 -
     * За това в ParseURL при грешка на връзката със сайта, методите:
     * parseBgTotoURL(), parseToto49URL(), parseOfficialTotoURL()
     * връщат старите тиражи архивирани във файловете.
     * </li>
     */
    static boolean isNewDrawOnSite(List<String> siteData) {
        String lastWrote;

        List<String> lastWroteDrawInFile = ParseURL.readDB
                ("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt");
        lastWrote = lastWroteDrawInFile.getLast();

        if (siteData != null) {
            String drawWithMaxIdxFromSite = Manipulate.getDrawWithMaxIdx(siteData);
            return (!lastWrote.equalsIgnoreCase(drawWithMaxIdxFromSite));
        }
        return false;
    }

    /**
     * Проверява дали в дадените уеб източници има ново теглене спрямо локалните архивни файлове.
     * <p>
     * За всеки от трите източника:
     * <ul>
     *   <li>{@code bgTotoURL} – <a href="https://bgtoto.com/6ot49_arhiv.php">bgtoto.com</a></li>
     *   <li>{@code toto49URL} – <a href="https://www.toto49.com/arhiv/toto_49/2025">toto49.com</a></li>
     *   <li>{@code officialTotoURL} – <a href="https://toto.bg/index.php?lang=1&pid=playerhistory">toto.bg</a></li>
     * </ul>
     * се извършва сравнение между последното теглене в локалния архивен файл и последното теглене в сайта.
     * Използва се методът {@link Checks#checkTotoData(String, String, String)}.
     *
     * <p>Резултатите се връщат като масив от булеви стойности в същия ред:
     * {@code [isNewFromBgToto, isNewFromToto49, isNewFromOfficialToto]}.
     *
     * <p><strong>Пример:</strong></p>
     * <pre>{@code
     * Вход:
     *   checkRes = new boolean[3]; // всички false
     *   siteData = List.of(
     *     "132-21 APR 2025-MONDAY - 1,4,18,23,38,42",  // bgToto
     *     "132-21 APR 2025-MONDAY - 1,4,18,23,38,42",  // toto49
     *     "132-21 APR 2025-MONDAY - 1,4,18,23,38,42"   // officialToto
     *   );
     *
     * Изход:
     *   връща [true, false, true]
     *   обновява checkRes → [true, false, true]
     * }</pre>
     *
     * <p><strong>Изключения:</strong></p>
     * Ако някой от URL-ите е недостъпен или възникне вътрешна грешка – методът хвърля {@code RuntimeException}.
     *
     * @param checkRes масив, който ще бъде модифициран с булеви стойности за всеки източник
     * @param siteData списък с последни тегления, по един от всеки сайт, в оригиналния им формат
     * @return нов булев масив от състояния за всяка проверка – указва за кой източник има ново теглене
     */
    static boolean[] urlArr(boolean[] checkRes, List<String> siteData) {
        String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
        String archivePathFor49 = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
        //String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
        //String archivePathFor49 = "/Users/blagojnikolov/Desktop/@tmp/results.txt";

        String bgTotoURL = "https://bgtoto.com/6ot49_arhiv.php";
        String toto49URL = "https://www.toto49.com/arhiv/toto_49/2025";
        String officialTotoURL = "https://toto.bg/index.php?lang=1&pid=playerhistory";
        try {
            boolean isNewFromBgToto =
                    Checks.checkTotoData(bgTotoURL, bgTotoArchivePath, siteData.get(0).trim());
            boolean isNewFromToto49 =
                    Checks.checkTotoData(toto49URL, archivePathFor49, siteData.get(1).trim());
            boolean isNewFromOfficialToto =
                    Checks.checkTotoData(officialTotoURL, bgTotoArchivePath, siteData.get(2).trim());

            checkRes[0] = isNewFromBgToto;
            checkRes[1] = isNewFromToto49;
            checkRes[2] = isNewFromOfficialToto;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new boolean[]{checkRes[0], checkRes[1], checkRes[2]};
    }

    /**
     * Проверява дали има нов тираж в сайта, като го сравнява с последния записан в локалния архив.
     * <p>
     * Извлича реда с тиража от подадената сайтова информация {@code siteData},
     * и го сравнява с последния ред от архивния файл на локалната система.
     * Ако редовете са различни (case-insensitive), се приема, че има нов тираж.
     * <p>
     * При възникване на грешка при достъпа до архивния файл или при парсване,
     * методът извежда съобщение в конзолата, но не прекъсва изпълнението.
     *
     * @param totoURL             URL адресът на сайта, използван само за съобщения при грешка.
     * @param archivePath         Път до локалния файл с архив на предишни тиражи.
     * @param newDrawFromSiteData Данни за текущия (последния) тираж, извлечени от сайта.
     * @return {@code true} ако има нов тираж (редовете са различни), {@code false} в противен случай или при грешка.
     * @see ParseURL#readFromFile(File)
     * @see Manipulate#extractData(String, String)
     */
    static boolean checkTotoData(String totoURL, String archivePath, String newDrawFromSiteData) {
        boolean isNewDraw = false;
        try {
            String fromToto = Manipulate.extractData(newDrawFromSiteData, "-");
            List<String> rawArchiveData = ParseURL.readFromFile(new File(archivePath));
            List<String> archiveData = Manipulate.getOnlyDigitsDataFromArchFile(rawArchiveData, "   ");
            String lastArchiveDraw = archiveData.getLast();

            if (!fromToto.equalsIgnoreCase(lastArchiveDraw)) {
                isNewDraw = true;
            }
        } catch (Exception e) {
            System.out.println("❌ Грешка: Неуспешно свързване със сайта:" + totoURL + e.getMessage());
        }
        return isNewDraw;
    }

    /**
     * Открива липсващите индекси на тиражи в даден диапазон.
     * <p>
     * Методът приема списък с вече налични индекси на тиражи и връща списък от липсващите индекси
     * в обхвата от {@code 0} до най-голямото число в списъка (включително).
     *
     * <p>Използва се методът {@code Manipulate.findMax(...)} за определяне на горната граница на обхвата.</p>
     *
     * <p><b>Пример:</b></p>
     * <pre>{@code
     * List<Integer> налични = List.of(0, 1, 3, 5, 6);
     * List<Integer> липсващи = missingDrawIdx(налични);
     * // findMax = 6, ще се провери от 0 до 5 включително
     * // липсващи = [2, 4]
     * }</pre>
     *
     * @param drawIndexes списък от цели числа, представляващи налични индекси на тиражи
     * @return списък с всички липсващи индекси между {@code 0} и {@code max - 1},
     * където {@code max} е най-голямото число в {@code drawIndexes}
     */
    static List<Integer> missingDrawIdx(List<Integer> drawIndexes) {
        List<Integer> missing = new ArrayList<>();
        int maxDrawIdx = Manipulate.findMax(drawIndexes);
        for (int i = 1; i <= maxDrawIdx; i++) {
            if (!drawIndexes.contains(i)) missing.add(i);
        }
        return missing;
    }

    // allDrawIndexesInFile == drawIndexes; lastDrawIdxFromSiteData от getLastDrawIdxFromSiteData
    static List<Integer> missingDrawIdxNew(List<Integer> allDrawIndexesInFile, int lastDrawIdxFromSiteData) {
        List<Integer> missing = new ArrayList<>();
        if (lastDrawIdxFromSiteData > 0) {
            for (int i = 1; i <= lastDrawIdxFromSiteData; i++) {
                if (!allDrawIndexesInFile.contains(i)) missing.add(i);
            }
            Collections.sort(missing);
        }
        return missing;
    }

    /**
     * Изчислява и връща списък с липсващите индекси на тиражи,
     * които се очакват, но не присъстват във файла с резултати от bgTotoFromSite (bgtoto.com).
     *
     * <p>Файлът съдържа редове с информация за тегления, например:
     * <pre>
     *     1   02 Jan 2025   3, 15, 18, 24, 31, 47
     *     3   04 Jan 2025   4, 10, 21, 25, 33, 44
     * </pre>
     * Това означава, че липсва индекс 2 (т.е. 2-... не е в списъка).
     *
     * <p>Методът намира максималния индекс и връща всички пропуснати между 0 и този максимум.
     *
     * @return Списък с липсващите индекси на тиражи от bgTotoFromSite.txt
     */
    static List<Integer> bgTotoMissing(List<String> siteData) {
//        return missingDrawIdx(
//                WebData.getWebIdx("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt"));
        List<Integer> allDrawIdxFromFile = WebData
                .getWebIdxFromFileData("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt");
        int lastDrawIdxFromSiteData = Manipulate.getLastDrawIdxFromSiteData(siteData).getLast();
        return missingDrawIdxNew(allDrawIdxFromFile, lastDrawIdxFromSiteData);
    }

    /**
     * Изчислява и връща списък с липсващите индекси на тиражи,
     * които се очакват, но не присъстват във файла с резултати от fromSite (toto49.com).
     *
     * <p>Файлът съдържа редове с информация за тегления, например:
     * <pre>
     *     1   02 Jan 2025   3, 15, 18, 24, 31, 47
     *     3   04 Jan 2025   4, 10, 21, 25, 33, 44
     * </pre>
     * Това означава, че липсва индекс 2 (т.е. 2-... не е в списъка).
     *
     * <p>Методът намира максималния индекс и връща всички пропуснати между 0 и този максимум.
     *
     * @return Списък с липсващите индекси на тиражи от fromSite.txt
     */
    static List<Integer> toto49Missing(List<String> siteData) {
//        return missingDrawIdx(
//                WebData.getWebIdx("/Users/blagojnikolov/Desktop/@tmp/fromSite.txt"));
        // fromSite49 трябва да го преименуваш на fromSite. В Write класа добави и оценка по сайт - boolean[] checkRes
        List<Integer> allDrawIdxFromFile = WebData
                .getWebIdxFromFileData("/Users/blagojnikolov/Desktop/@tmp/fromSite.txt");
        int lastDrawIdxFromSiteData = Manipulate.getLastDrawIdxFromSiteData(siteData).get(2);
        return missingDrawIdxNew(allDrawIdxFromFile, lastDrawIdxFromSiteData);
    }

    /**
     * Проверява съвпадения между официалните изтеглени числа от тираж и две групи залози –
     * едната подготвена чрез Java, другата – чрез LabVIEW.
     *
     * <p>Методът извършва:
     * <ol>
     *   <li>Зареждане на директория с Java-залози {@code tmpRes/}</li>
     *   <li>Зареждане на LabView файл {@code toto.txt} от {@code @tmp/}</li>
     *   <li>Създаване на две мап структури:
     *     <ul>
     *       <li>{@code Map<Integer, List<List<Integer>>> idxSupposesJava} — Java залози</li>
     *       <li>{@code Map<Integer, List<List<Integer>>> idxSupposesLv} — LabVIEW залози</li>
     *     </ul>
     *   </li>
     *   <li>Извличане на текущия мап от {@code webIdxDraw} по подадения {@code webIdx} (номер на тиража)</li>
     *   <li>Извличане на съответните залози (списък от редове с числа), съответстващи на тиража:
     *     <ul>
     *       <li>Java залози чрез {@code doTheseIdxMatch(...)} — връща {@code jSuppose}</li>
     *       <li>LabView залози чрез {@code doTheseIdxMatch(...)} — връща {@code lvSuppose}</li>
     *     </ul>
     *   </li>
     *   <li>За всеки от двата случая, ако има съвпадения:
     *     <ul>
     *       <li>Извежда име на файл и съвпадения</li>
     *       <li>Извиква {@code check(...)} за проверка спрямо официалните числа</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Изход:</b> в конзолата ще се изпишат:
     * <ul>
     *   <li>Информация за липсващи файлове или несъответстващи индекси</li>
     *   <li>Резултат от съвпадения за всеки ред от залози</li>
     * </ul>
     *
     * <p><b>Примерен изход:</b>
     * <pre>
     * Java suppose:
     * [3, 6, 12, 25, 34, 42]
     * [1, 7, 8, 19, 28, 41]
     * --------------------
     * - 3 съвпадения
     * {3=0, 6=1, 12=3}
     *
     * LabView Suppose:
     * [3, 6, 12, 25, 34, 42]
     * ---------------------
     * - 3 съвпадения
     * {3=0, 6=1, 12=3}
     * </pre>
     *
     * <p><b>Грешкообработка:</b>
     * <ul>
     *   <li>Ако {@code officialRes == null} → съобщение за липсващ резултат</li>
     *   <li>Ако директорията с Java файлове или {@code toto.txt} липсва → съобщение за липсващ файл</li>
     *   <li>Ако не се открие съвпадение между индекс и тираж → съобщение за несъответствие</li>
     * </ul>
     *
     * @param officialRes списък от 6 официално изтеглени числа: 1 2 3 4 5 6.
     * @param webIdx      индекс/номер на тиража, по който се търси съвпадение в залозите: 23.
     */
    static void checksByIndexes(List<Integer> officialRes, int webIdx) {
        String javaResFilePath = "/Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/tmpRes";
        String labViewResFilePath = "/Users/blagojnikolov/Desktop/@tmp";

        File jDir = new File(javaResFilePath);
        File lvFile = FileProcesses.lastLabViewResFile(new File(labViewResFilePath), "toto.txt");

        if (officialRes == null) {
            System.out.println("""
                    \n4↪︎04: resSupposeCheck(): \
                    1. Или тиража който проверяваш не е записан правилно \
                    
                    2. Или Ти се разминават Номерата на Тиражите. Този за който проверяваш и този \
                    който съответства на залога ти....""");
            return;
        }

        // System.out.println();
        if ((jDir.length() == 0 || lvFile == null)) {
            System.out.println("↪︎Някой от файловете със залозите \"toto.txt\" " +
                    "или \"tmpRes\" не съдържат нищо или не съществуват");
        } else {
            // Създава Маповете: Индекс - залог и Индекс - залози
            Map<Integer, List<Integer>> webIdxDraw = WebData.getLastWebIdxDrawMap(officialRes, webIdx);

            Map<Integer, List<List<Integer>>> idxSupposesJava = FileProcesses
                    .createPairIdxListMapFromJFiles(jDir);

            Map<Integer, List<List<Integer>>> idxSupposesLv = FileProcesses
                    .createPairIdxListMapFromLVFile();

            //Търси има ли твой залог с номер съотвестващ на номера на текущият тираж.
            // Връща трите масиви от залозите.
            List<List<Integer>> jSuppose = doTheseIdxMatch(idxSupposesJava, webIdxDraw);
            List<List<Integer>> lvSuppose = doTheseIdxMatch(idxSupposesLv, webIdxDraw);

            boolean isNotJFile = false, isNotLvFiles = false;
            if (jSuppose == null) {
                isNotJFile = true;
                //System.out.println("JFile:  - Няма намерени залози за този тираж ...");
            } else {
                System.out.print("Намерен е залог за този тираж. Да го принтирам ли?: ");
                if (Manipulate.answer().equalsIgnoreCase("Y")) {
                    String jFileName = FileProcesses.getJFilePath(webIdx);
                    //todo: ⛔ 29.03.25 - Това трябва да се пипне:
                    System.out.println("\n" + jFileName + " - " + webIdx + " тираж");
                    System.out.println("Java suppose:");
                    // търси съвпадения на officialRes в jSuppose:
                    checkAndPrint(jSuppose, officialRes);
                    System.out.println("----------------");
                }
            }

            if (lvSuppose == null) {
                isNotLvFiles = true;
                //System.out.println("LvFile: - Няма намерени залози за този тираж ...");
            } else {
                System.out.print("Намерен е залог за този тираж. Да го принтирам ли?: ");
                if (Manipulate.answer().equalsIgnoreCase("Y")) {
                    System.out.println("\n" + lvFile.getName());
                    System.out.println("LabView Suppose: ");
                    checkAndPrint(lvSuppose, officialRes);
                }
            }

            if (isNotJFile && isNotLvFiles) {
                System.out.println("- Не са намерени залози за този тираж ...");
                isNotLvFiles = false;
                isNotJFile = false;
            }
            //if (isNotJFile) System.out.println("\n" + "JFile:  - Няма намерени залози за този тираж ...");
            //if (isNotLvFiles) System.out.println("\n" + "LvFile:  - Няма намерени залози за този тираж ...");
        }
    }

    /**
     * Сравнява всяка комбинация от предполагаеми числа с официалния тираж
     * и извежда колко съвпадения има между тях.
     *
     * <p>Методът обхожда всяка комбинация в {@code suppose}, сравнява я с {@code officialRes}
     * и открива кои числа съвпадат.
     *
     * <p>За всяка редица:
     * <ul>
     *   <li>Извежда официалната комбинация</li>
     *   <li>Извежда текущата редица</li>
     *   <li>Показва броя съвпадения</li>
     *   <li>Извежда map с всяко съвпаднало число и неговия индекс в текущата редица</li>
     * </ul>
     *
     * <p>Примерен изход:
     * <pre>
     * [3, 12, 22, 34, 41, 47]
     * - 6@49
     * [5, 12, 34, 44, 47, 49]
     * - 3 съвпадения
     * {12=1, 34=2, 47=4}
     * </pre>
     *
     * @param proposals     списък от комбинации за проверка
     * @param officialRes официалната изтеглена комбинация (6 числа)
     */
    static void checkAndPrint(List<List<Integer>> proposals, List<Integer> officialRes) {
        final int SPACES = 6;
        final int TOTAL_LENGTH = 9;
        int cntMatch;
        Map<Integer, Integer> digitIdx;

        for (List<Integer> row : proposals) {
            Print.printList(officialRes);
            // 3 15 24 27 31 35 -> 31524273135.length()
            String tmpStr1 = officialRes.toString()
                    .replace(",", "")
                    .replace(" ", "");
            int officialResLength = tmpStr1.length();

            System.out.println(" ".repeat((TOTAL_LENGTH - officialResLength) + SPACES * " ".length())
                    + "-  6@49");
            Print.printList(row);

            cntMatch = 0;
            digitIdx = new HashMap<>();
            for (int i = 0; i < row.size(); i++) {
                if (officialRes.contains(row.get(i))) {
                    digitIdx.put(row.get(i), i);
                    cntMatch++;
                }
            }
            // 3 15 24 27 31 35 -> 31524273135.length()
            String tmpStr2 = row.toString()
                    .replace(",", "")
                    .replace(" ", "");
            int rowLength = tmpStr2.length();

            String one = (cntMatch == 1) ? " съвпадение" : " съвпадения";
            System.out.println(" ".repeat((TOTAL_LENGTH - rowLength) + SPACES * " ".length())
                    + "-  " + cntMatch + one);
            Print.printMatchingInfo(digitIdx);
        }
    }

    // В оригиналният си вид метода е: yourSupposes(){List<String> rawWebData = ParseURL.readFromFile(new File
    ////                ("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt"));.......}
    /**
     * Избира реда от {@code officialToto.txt}, извлечен от последните онлайн тиражи с най-голям индекс - последният
     * тираж.
     * <p>Сайта ако не е достъпен съответният ред от масива на siteData ще бъде:</p>
     * <p>Грешка: Неуспешно свързване със сайта!</p>
     * <p>Това автоматично ще доведе до exception:</p>
     * <p>List<String> rawWebData = ParseURL.readFromFile(new File
     * ("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt"));</p>
     * <p>
     * Извършва следните действия:
     * <ul>
     *     <li>Чете всички редове от {@code /Users/blagojnikolov/Desktop/@tmp/officialToto.txt}.</li>
     *     <li>Пропуска редове със съобщение за грешка (напр. "Грешка: Невъзможно свързване със сайта.").</li>
     *     <li>Избира реда с най-голям индекс на тиража.</li>
     *     <li>Парсва номерата на тиража и неговия индекс от избрания ред.</li>
     *     <li>Извиква проверка чрез {@code checksByIndexes()} с числата и индекса.</li>
     * </ul>
     *
     * <p>Очакван формат на валиден ред:
     * <pre>
     *     26-30 MAR 2025 - 3,15,18,24,31,47
     * </pre>
     *
     * <p><b>Забележки:</b>
     * <ul>
     *     <li>Методът пропуска всички редове, които съдържат текстова грешка.</li>
     *     <li>Избира само реда с най-висок номер на тиража.</li>
     *     <li>Ако няма валиден ред, отпечатва съобщение и прекратява изпълнението.</li>
     * </ul>
     *
     * @see Manipulate#parseToInt(String)
     * @see Checks#checksByIndexes(List, int)
     */
    static String yourSupposes(List<String> siteData) {
        String error = "Error";
        String data = "";
        int maxIdx = Integer.MIN_VALUE, idx;

        for (String line : siteData) {
            if (!line.equalsIgnoreCase("❌ Грешка: Неуспешно свързване със сайта!")) {
                data = line;
                try {
                    idx = Manipulate.parseToInt(data.split("-")[0]).getFirst();
                    if (idx > maxIdx) {
                        maxIdx = idx;
                        data = line;
                    }
                } catch (Exception e) {
                    System.out.println("↪︎404: yourSupposes():" +
                            "Проблем с парсването на индекса на тиража.");
                }
            }
        }
        if (data.equalsIgnoreCase("")
                || data.equalsIgnoreCase("❌ Грешка: Неуспешно свързване със сайта!")) {
            System.out.println("↪︎404: yourSupposes(): " +
                    "Невъзможно свързване със сайта.");
            return error;
        }
        List<Integer> webData = Manipulate.parseToInt(data.split("-")[2]);
        int webIdx = maxIdx;
        checksByIndexes(webData, webIdx);
        return "No Error";
    }

    /**
     * Проверява дали подаденият ред с теглене съществува във файл.
     *
     * <p>Методът чете съдържанието на файл, намиращ се на дадения път (`filePath`),
     * и проверява дали редът с теглене (`drawData`) присъства в списъка от редове.
     *
     * <p>Използва се за избягване на дублиране при запис на нови тегления.
     *
     * <p>Ако възникне изключение (например файлът липсва или не може да се прочете), методът връща `false`
     * и отпечатва подходящо съобщение в конзолата.
     *
     * <p>data: 1 2 3 4 5 6</p>
     *
     * <p>Пример:
     * <pre>{@code
     * boolean exists = isDrawDataContainsIntoTheFile("/tmp/results.txt", "26-30 MAR 2025 - 3,15,18,24,31,47");
     * boolean exists = isDrawDataContainsIntoTheFile("/tmp/results.txt", "3 15 18 24 31 47");
     * }</pre>
     *
     * @param filePath Път към текстов файл, съдържащ тегления (например: "/tmp/results.txt")
     * @param drawData Ред с теглене, който се търси (например: "26-30 MAR 2025 - 3,15,18,24,31,47")
     * @return `true` ако редът присъства във файла, `false` ако липсва или възникне грешка
     */
    static boolean isDataContainedInTheFile(String filePath, String drawData) {
        try {
            File fromFile = new File(filePath);
            List<String> data = ParseURL.readFromFile(fromFile);
            return data.contains(drawData);
        } catch (Exception e) {
            System.out.println("↪︎404: isContainsInFile(): " +
                    "Файла не е достъпен или не съществува:" + filePath);
        }
        return false;
    }

    /**
     * Проверява дали има съвпадение между ключовете на два речника, като връща
     * съответната стойност от първия map при първо съвпадение.
     *
     * <p>Методът обхожда всички записи в {@code idxSupposes} и проверява дали
     * някой от ключовете му присъства в {@code idxDraw}.
     *
     * <p>Ако има съвпадение:
     * <ul>
     *   <li>Намира първия общ ключ</li>
     *   <li>Връща списъка от списъци с цели числа, асоциирани с този ключ от {@code idxSupposes}</li>
     * </ul>
     *
     * <p>Ако няма съвпадение между ключовете, методът връща {@code null}.
     *
     * <p>Примерна употреба: съпоставяне на данни от различни файлове по общ тираж/номер.
     *
     * @param idxSupposes Map, съдържащ предполагаеми стойности по индекс
     * @param idxDraw     Map, съдържащ реални стойности по индекс
     * @return Стойност от {@code idxSupposes} при първо съвпадение на ключ с {@code idxDraw}, или {@code null} ако няма съвпадение
     */
    public static List<List<Integer>> doTheseIdxMatch(Map<Integer, List<List<Integer>>> idxSupposes,
                                                      Map<Integer, List<Integer>> idxDraw) {
        for (Map.Entry<Integer, List<List<Integer>>> suppose : idxSupposes.entrySet()) {
            if (idxDraw.containsKey(suppose.getKey())) return suppose.getValue();
        }
        return null;
    }

    /**
     * Проверява дали последният записан тираж в архивния файл е различен от последния от сайта,
     * и ако е така — записва новите данни във файла.
     *
     * <p>Методът приема три входни аргумента:
     * <ul>
     *     <li><b>totoURL</b> – използва се само за изписване в лог при грешка</li>
     *     <li><b>archivePath</b> – път до локалния архивен файл с резултати</li>
     *     <li><b>siteData</b> – първият ред с данни от сайта (напр. "1-02 Jan 2025-3,16,23,36,41,49")</li>
     * </ul>
     *
     * <p>Методът:
     * <ol>
     *     <li>Извлича частта с изтеглените числа (1 2 3 4 5 6) от `siteData` (третият елемент след split по `-`)</li>
     *     <li>Чете последния ред от архивния файл</li>
     *     <li>Ако данните се различават — записва новия ред чрез `Write.write(...)`</li>
     * </ol>
     *
     * <p>Ако сайтът е недостъпен или възникне грешка при четене/запис, извежда съобщение в конзолата.
     *
     * @param totoURL     URL адресът на източника (използва се само за логване при грешка)
     * @param archivePath Път до локалния архивен файл
     * @param siteData    Ред с най-новите данни от сайта
     * @return <code>true</code>, ако е записан нов тираж; <code>false</code> – ако няма промяна или при грешка
     */
    static boolean checkAndWriteTotoData(String totoURL, String archivePath, String siteData) {
        String filePath = "/Users/blagojnikolov/Desktop/@tmp/allResults.txt";
        boolean isNewDraw = false;
        try {
            String fromToto = Manipulate.extractData(siteData, "-");
            List<String> archiveData = ParseURL.readFromFile(new File(archivePath));
            String lastArchiveDraw = archiveData.getLast();
            //String prevBeforLast = archiveData.get(archiveData.size() - 2);

            if (!fromToto.equalsIgnoreCase(lastArchiveDraw)) {
                Write.write(fromToto, archivePath, true);
                if (!isDataContainedInTheFile(filePath, fromToto)) {
                    Write.write(fromToto, filePath, true);
                }
                isNewDraw = true;
            }
        } catch (Exception e) {
            System.out.println("❌ Грешка: Неуспешно свързване със сайта:" + totoURL + e.getMessage());
        }
        return isNewDraw;
    }

    /**
     * Проверява дали подаденият стринг съдържа валидно десетично число.
     *
     * <p>Допустими са само цифри и десетични разделители — запетая (,) или точка (.).</p>
     *
     * <p>Използва се за предварителна валидация на входа преди преобразуване към {@code double}.</p>
     *
     * <p>Пример за валидни стойности:
     * <ul>
     *   <li>{@code "3"}</li>
     *   <li>{@code "5.7"}</li>
     *   <li>{@code "2,1"}</li>
     * </ul>
     * Пример за невалидни стойности:
     * <ul>
     *   <li>{@code "abc"}</li>
     *   <li>{@code "4x"}</li>
     *   <li>{@code "2..3"}</li>
     * </ul>
     * </p>
     *
     * @param line входен стринг за проверка
     * @return {@code true} ако съдържа само цифри и (.) или (,) като десетични символи, иначе {@code false}
     */
    static boolean isDigit(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isDigit(line.charAt(i))) {
                if (line.charAt(i) == ',' || line.charAt(i) == '.') continue;
                return false;
            }
        }
        return true;
    }

    static boolean searchFromDifferentDrawsInData(List<String> data) {
        boolean isDifferent = false;
        int min, max;
        List<Integer> tmpIndexes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            try {
                int drawIdx = Integer.parseInt(data.get(i).split("-")[0].trim());
                tmpIndexes.add(drawIdx);
            } catch (NumberFormatException e) {
                System.out.println("↪︎404: compareRows():" +
                        "Не мога да взема индекса на реда от сайта...");
            }
        }
        min = Manipulate.findMin(tmpIndexes);
        max = Manipulate.findMax(tmpIndexes);
        return min != max;
    }


    // Unused: ----------------------------------------------------------------------------------------------


    //todo: ⛔ 13.04.25 - Това трябва да се пипне:
    // Ако от bgToto няма нов резултат, то: Новият ред се добавя в bgToto.txt от другите два метода. Но при проверка
    // от checkAndWriteTotoData() метода вижда разлика между старият тираж от сайта и новия и го презаписва.

    //todo: ⛔ 13.04.25 - checkAndWriteTotoData() Трябва само да проверява за нови тиражи. Разкарай записването от него.
    // Измисли условието при което ще се записва във файловете ??? Тия трите тежки метода могат да отпаднат....
    // Провери за нови тиражи ако има нов запиши го навсякъде и готово....  <-
    // Кой е новият: Този флаг който е true съответства на ред от файла: officialToto.txt ;)

    /**
     * @param siteData <p>[0]: "27-03 Apr 2025-25, 29, 31, 34, 38, 44"</p>
     *                 <p>[1]: "27-03 Apr 2025-25, 29, 31, 34, 38, 44"</p>
     *                 <p>[2]: "27-03 Apr 2025-25, 29, 31, 34, 38, 44"</p>
     */
//    static boolean forNewDrawOld(List<String> siteData) throws FileNotFoundException {
//        boolean isNew = false;
//        try {
//            boolean[] checkRes = new boolean[3];
//            if (Write.lastDraw(siteData, checkRes)) isNew = true;
//            Print.messageX(isNew, checkRes, siteData);
//        } catch (IndexOutOfBoundsException e) {
//            System.out.println("404: Провери си мрежата...");
//        }
//        return isNew;
//    }

    // Заменен с: getNewDrawFromSite();

    /**
     * Намира най-новия тираж от списъка с данни, който е с дата **след** текущата дата минус 1 година.
     * <p>
     * Използва се за извличане на последния актуален запис от списък, където всеки ред съдържа дата
     * във формат <b>"dd MMM yyyy"</b> като втора стойност (след разделяне по тире).
     * <p>
     * Сравнява всяка дата в списъка и връща реда, съдържащ най-новата дата, по-нова от
     * {@code LocalDate.now().minusYears(3)}.
     *
     * <p><b>Пример:</b>
     * <pre>{@code
     *   List<String> siteData = List.of(
     *     "26-04 APR 2021 - 3,15,22,31,45,47",
     *     "25-29 MAR 2024 - 5,9,18,28,33,41"
     *   );
     *   String latest = newDrawInSiteData(siteData);
     *   // latest = "25-29 MAR 2024 - 5,9,18,28,33,41"
     * }</pre>
     *
     * @param siteData списък от редове с данни, всеки от които съдържа дата във формат "dd MMM yyyy", след тире
     * @return редът с най-новата дата (по-нова от преди 3 години), или празен стринг ако няма такъв
     * @throws DateTimeParseException    ако някоя дата не може да се парсне
     * @throws IndexOutOfBoundsException ако структурата на реда не съдържа поне два елемента при split("-")
     */
    static String forNewDrawInSiteData(List<String> siteData) {
        String draw = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate maxDate = LocalDate.now().minusYears(1);

        for (String siteDatum : siteData) {
            String date1 = siteDatum.trim().split("-")[1];
            LocalDate thisDate = LocalDate.parse(date1, formatter);
            if (thisDate.isAfter(maxDate)) {
                maxDate = thisDate;
                draw = siteDatum;
            }
        }
        return draw;
    }

    /**
     * <p>Парсва сайта:  <p>"https://bgtoto.com/6ot49_arhiv.php"<</p>
     * <p>Извлича от сайта масива със всеки тираж във вид: 6 8 33 35 37 45.</p>
     * <p>Сравнява последният записан масив в архива от "bgToto.txt".
     * с последният валиден тираж от сайта.</p>
     * <p>При липса на съвпадение(има нов тираж) записва този тираж в "bgToto.txt".</p>
     * <p>"bgTotoDb.getFirst()" е последният записан в архива тираж, сайта ги пази в този ред.</p>
     */
    static boolean checkAndWriteDataFromBgToto() {
        boolean isNewDraw = false;
        String bgTotoURL = "https://bgtoto.com/6ot49_arhiv.php";
        String archivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
        try {
            List<String> bgTotoDb = ParseURL.parseBgTotoURL();
            String fromBgToto = bgTotoDb.getFirst().trim()
                    .split("-")[1].trim()
                    .replace(",", " ");
            //bgTotoDb.forEach(System.out::println);
            List<String> fromArchive = ParseURL.readDB(archivePath);
            if (!fromArchive.getLast().equalsIgnoreCase(fromBgToto)) {
                Write.bgTotoToFile(bgTotoDb);
                isNewDraw = true;
            }
        } catch (Exception e) {
            System.err.println("❌ Грешка: Неуспешно свързване със сайта: "
                    + bgTotoURL + e.getMessage());
        }
        return isNewDraw;
    }

    /**
     * Проверява дали има нов тираж, публикуван на сайта на bg.toto.com.
     *
     * <p>Сравнява подадените данни с последния запис в архивния файл {@code results.txt}.
     * Ако подаденият ред {@code data} не съвпада с последния ред в архива, записва новите данни
     * в {@code result.txt} и също така в {@code fromSite.txt} за справка.
     *
     * @param data Ред с данни за най-новия тираж от сайта (напр. "27-03 Apr 2025-25, 29, 31, 34, 38, 44")
     * @return {@code true} ако има нов тираж и той е записан; {@code false} ако няма промяна
     */
    static boolean checksFromBgToto(String data) {
        String bgTotoURL = "https://bgtoto.com/6ot49_arhiv.php";
        String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";

        boolean isThereNewDraw = checkAndWriteTotoData(bgTotoURL, bgTotoArchivePath, data);
        if (isThereNewDraw) {
            String thisDRaw = Manipulate.extractData(data, "-");
            String dataModified = data.replace("-", "   ");

            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", dataModified)) {
                Write.write
                        (dataModified, "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", true);
            }

            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/result.txt", thisDRaw)) {
                Write.write(thisDRaw, "/Users/blagojnikolov/Desktop/@tmp/result.txt", true);
            }
            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", dataModified)) {
                Write.write(dataModified, "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", true);
            }
        }
        return isThereNewDraw;
    }

    /**
     * Проверява дали има нов тираж, публикуван на сайта на www.toto49.com.
     *
     * <p>Сравнява подадените данни с последния запис в архивния файл {@code results.txt}.
     * Ако подаденият ред {@code data} не съвпада с последния ред в архива, записва новите данни
     * в {@code results.txt} и също така в {@code fromSite.txt} за справка.
     *
     * <p>Записва (checkAndWriteTotoData) също данните в bgToto.txt и (write) bgTotoFromSite.txt</p>
     *
     * @param data Ред с данни за най-новия тираж от сайта (напр. "27-03 Apr 2025-25, 29, 31, 34, 38, 44")
     * @return {@code true} ако има нов тираж и той е записан; {@code false} ако няма промяна
     */
    static boolean checksFromToto49(String data) {
        String toto49URL = "https://www.toto49.com/arhiv/toto_49/2025";
        String toto49URLArchivePath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";

        boolean isThereNewDraw = checkAndWriteTotoData(toto49URL, toto49URLArchivePath, data);
        if (isThereNewDraw) {
            String thisDRaw = Manipulate.extractData(data, "-");
            String dataModified = data.replace("-", "   ");

            if (!isDataContainedInTheFile("/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", dataModified)) {
                Write.write
                        (dataModified, "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", true);
            }

            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt", thisDRaw)) {
                Write.write(thisDRaw, "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt", true);
            }
            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", dataModified)) {
                Write.write(dataModified, "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", true);
            }
        }
        return isThereNewDraw;
    }

    /**
     * Проверява дали има нов тираж, публикуван на сайта на www.toto.bg.
     *
     * <p>Сравнява подадените данни с последния запис в архивния файл {@code bgToto.txt}.
     * Ако подаденият ред {@code data} не съвпада с последния ред в архива, записва новите данни
     * в {@code bgToto.txt} и също така в {@code bgTotoFromSite.txt} за справка.
     *
     * <p>Записва (checkAndWriteTotoData) също данните в bgToto.txt и (write) bgTotoFromSite.txt</p>
     *
     * @param data Ред с данни за най-новия тираж от сайта (напр. "27-03 Apr 2025-25, 29, 31, 34, 38, 44")
     * @return {@code true} ако има нов тираж и той е записан; {@code false} ако няма промяна
     */
    static boolean checksFromOfficialToto(String data) {
        String officialTotoURL = "https://toto.bg/index.php?lang=1&pid=playerhistory";
        String officialTotoURLURLArchivePath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";

        boolean isThereNewDraw = checkAndWriteTotoData(officialTotoURL, officialTotoURLURLArchivePath, data);
        if (!isThereNewDraw) {
            String thisDRaw = Manipulate.extractData(data, "-");
            String dataModified = data.replace("-", "   ");

            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", dataModified)) {
                Write.write
                        (dataModified, "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt", true);
            }
            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt", thisDRaw)) {
                Write.write(thisDRaw, "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt", true);
            }
            if (!isDataContainedInTheFile(
                    "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", dataModified)) {
                Write.write(dataModified, "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt", true);
            }
        }
        return isThereNewDraw;
    }
}
