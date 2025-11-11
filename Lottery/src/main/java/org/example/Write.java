package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Клас {@code Write} управлява всички операции по записване на файлове в контекста на
 * работа със система за обработка на тото-резултати, архиви и статистика.
 *
 * <p><b>Какво прави?</b></p>
 * Този клас се грижи за създаване, дописване и управление на файлове,
 * които съхраняват:
 * <ul>
 *     <li>всички тегления (draws) на тотото, във файлове с различно предназначение (официални, временни, архивни и т.н.);</li>
 *     <li>дата и час на последно записано теглене (чрез LastVisitDateFile);</li>
 *     <li>статистики за съвпадения спрямо подадена комбинация;</li>
 *     <li>сурови или филтрирани резултати;</li>
 *     <li>потребителски комбинации за проверка.</li>
 * </ul>
 *
 * <p><b>Как го прави?</b></p>
 * Използва различни {@code FileWriter} потоци (в режим append или overwrite), за да записва
 * списъци от резултати, draw данни или анализирана информация към различни текстови файлове.
 * В повечето случаи записва във формат, съвместим с останалата система (по редове или колони).
 * Основава се на вход от други класове като {@code Manipulate}, {@code Checks}, {@code PersonalR},
 * {@code Statistical}, които подават вече обработени или подготвени данни.
 *
 * <p><b>Защо го прави?</b></p>
 * Целта му е да централизира изхода от програмата – така целият запис на резултати, draw-ове и анализи
 * минава през единен механизъм, който гарантира:
 * <ul>
 *     <li>съхранение с правилно кодиране (UTF-8);</li>
 *     <li>гарантирано създаване на файлове при нужда;</li>
 *     <li>разделяне на различните типове информация (резултати, проверки, архиви);</li>
 *     <li>добавяне на времеви етикети при нужда (чрез {@code LocalDateTime});</li>
 *     <li>улеснен последващ анализ и визуализация.</li>
 * </ul>
 *
 * <p><b>Ключови методи:</b></p>
 * <ul>
 *     <li>{@code writeOfficialFiles()} – пише draw-ове в няколко синхронизирани файла (официални, архив, POSIX формат);</li>
 *     <li>{@code write()} – пише draw в конкретен файл и ако е нов, актуализира и файл с последната записана дата;</li>
 *     <li>{@code writeDrawDB()} – пише draw-ове в officialToto.txt (локален файл);</li>
 *     <li>{@code writeToOfficialDrawsTxt()} – основна функция за извеждане на резултат с мета информация;</li>
 *     <li>{@code writeMyPickList()} – запазва потребителски комбинации в drawX.txt (според текущото теглене);</li>
 *     <li>{@code writeToOfficialDrawsTxt()} – извежда обобщена статистика за съвпадения спрямо draw;</li>
 *     <li>{@code toOfficialDrawsTxtRaw()} – записва необработен draw в зависимост от режима.</li>
 * </ul>
 *
 * <p><b>Свързани зависимости:</b></p>
 * Класът използва:
 * <ul>
 *     <li>{@code Manipulate.extractData()} – за извличане на draw данни от сурови входове;</li>
 *     <li>{@code Checks.validDateFormat()} – валидация на форматите за дата;</li>
 *     <li>{@code FileProcess.searchFileByName()} – проверка за вече съществуващи записи;</li>
 *     <li>{@code Statistical.calculateStats()} – изчисляване на съвпадения спрямо draw.</li>
 * </ul>
 *
 * <p><b>Забележки:</b></p>
 * Повечето методи поддържат параметър {@code append}, чрез който се указва дали да се дописва
 * към съществуващ файл или да се презаписва. Изключения се хващат с {@code try-catch}, а при
 * липса на файл се показва съобщение в конзолата.
 *
 * @author bNN
 * @version 1.0
 */
public class Write {
    public static void main(String[] args) {}

    /**
     * Записва новото теглене във всички свързани временни и архивни файлове,
     * използвани за анализ, визуализация и статистически обработки.
     *
     * <p><strong>Извършва две модификации на входния ред:</strong></p>
     * <ol>
     *   <li><b>Форматирано копие {@code modifiedData}:</b>
     *       Заменя всички тирета („-“) с <b>три интервала</b> за по-добра визуализация.</li>
     *   <li><b>Числово копие {@code thisDraw}:</b>
     *       Извлича само числата от тегленето чрез {@link Manipulate#extractData(String, String)}.</li>
     * </ol>
     *
     * <p><strong>Пример:</strong></p>
     *
     * <pre>{@code
     * Входен ред (newDRaw):
     *   "132-21 APR 2025-MONDAY - 1,4,18,23,38,42"
     *
     * modifiedData (за визуални файлове):
     *   "132  21 APR 2025  MONDAY   1,4,18,23,38,42"
     *
     * thisDraw (само числата):
     *   "1 4 18 23 38 42"
     * }</pre>
     *
     * <p><strong>Файлове, в които се записва:</strong></p>
     *
     * <ul>
     *   <li><b>{@code bgTotoFromSite.txt}</b> – {@code modifiedData}</li>
     *   <li><b>{@code fromSite.txt}</b> – {@code modifiedData}</li>
     *   <li><b>{@code bgToto.txt}</b> – {@code thisDraw}</li>
     *   <li><b>{@code results.txt}</b> – {@code thisDraw}</li>
     *   <li><b>{@code allResults.txt}</b> – {@code thisDraw}</li>
     * </ul>
     *
     * <p><strong>Допълнителна информация:</strong></p>
     * <ul>
     *   <li>Файловете се отварят в {@code append = true} режим – добавя се към края им</li>
     *   <li>Методът не валидира формата на {@code newDRaw} – счита се за предварително проверен</li>
     * </ul>
     *
     * <p><strong>Обработка на грешки:</strong></p>
     * В случай на проблем при достъпа до файл или запис, се извежда съобщение:
     * <pre>
     *  404: lastDraw(): Провери си файловете за паразитни записи...
     * </pre>
     *
     * @param newDRaw входен ред от сайт с теглене във формат:
     *                {@code 132-21 APR 2025-MONDAY - 1,4,18,23,38,42}
     */
    static void writeThisDrawInAllFiles(String newDRaw, List<String> siteData, boolean[] urls) {
        String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
        String bgTotoFromSitePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";   // Основен архив
        String resultsArchivePath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";          // LabView
        String forSite49ArchivePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";       // Архив за 49 сайта
        String allResArchivePath = "/Users/blagojnikolov/Desktop/@tmp/allResults.txt";
        String officialTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/officialTotoArchive.txt";
        String officialTotoPath = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";

        boolean isAppend = false;
        String thisDraw, modifiedData, line;
        line = newDRaw.trim();
        modifiedData = line.replace("-", "   ");
        thisDraw = Manipulate.extractData(line, "-");

        try {
            if (urls[0] || urls[2]) {
                Write.write(modifiedData, bgTotoFromSitePath, true);
                Write.write(modifiedData, officialTotoArchivePath, true);
            }
            if (urls[1]) Write.write(modifiedData, forSite49ArchivePath, true);
            //Write.write(modifiedData, officialTotoPath, false);

            // Записва във officialToto.txt:
            List<String> tmp = new ArrayList<>(List.of(modifiedData));
            for (int i = 0; i < 3; i++) {
                if (i != 0) isAppend = true;
                Write.writeDrawDB(tmp, isAppend);
            }
            //From LAbView files:
            Write.write(thisDraw, bgTotoArchivePath, true);
            Write.write(thisDraw, resultsArchivePath, true);
            Write.write(thisDraw, allResArchivePath, true);
        } catch (Exception e) {
            System.out.println("↪︎404: lastDraw(): " +
                    "Провери си файловете за паразитни записи...");
        }
    }

    /**
     * Записва един ред текст във файл (при условие, че не се съдържа в него),
     * като дава възможност за добавяне или презаписване.
     *
     * <p>Методът създава или отваря съществуващ файл по подадения път (`filePath`)
     * и записва предоставения текстов ред (`line`) на нов ред. Поведението на записа
     * се контролира от флага `append`.
     *
     * <p>Ако файлът не съществува, той ще бъде създаден. Ако `append` е `false`, съдържанието на файла ще бъде презаписано.
     * Ако е `true`, редът ще бъде добавен в края на файла.
     *
     * <p>В случай на грешка при запис, се извежда съобщение в конзолата с името на файла.
     *
     * <p>Пример за използване:
     * <pre>{@code
     * Write.write("26-30 MAR 2025 - 3,15,18,24,31,47", "/tmp/results.txt", true);
     * }</pre>
     *
     * @param data     Редът от текст, който ще бъде записан (например: "1-02 Jan 2025 - 3,15,18,24,31,47")
     * @param filePath Пътят до файла, в който ще се записва
     * @param append   Ако е true — добавя реда към края; ако е false — презаписва файла
     */
    static void write(String data, String filePath, boolean append) {
        File toFile = null;
        try {
            if (!Checks.isDataContainedInTheFile(filePath, data)) {
                toFile = new File(filePath);
                FileWriter writer = new FileWriter(toFile, append);

                writer.write(data);
                writer.write("\n");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("↪︎404: Файлът " + toFile.getName() + " не може да бъде записан.");
        }
    }

    //todo: ✅ 9.06.25 - Изтрий го. Стара, къдрава логика.
    static void writeOld(String data, String filePath, boolean append) {
        String isChecked = "/Users/blagojnikolov/Desktop/@tmp/lastVisitDate.txt";
        File lastVisitDateFile;
        File toFile = null;
        try {
            if (!Checks.isDataContainedInTheFile(filePath, data)) {
                toFile = new File(filePath);
                lastVisitDateFile = new File(isChecked);
                //FileWriter checkWriter = new FileWriter(lastVisitDateFile, true);
                FileWriter writer = new FileWriter(toFile, append);

                writer.write(data);
                //checkWriter.write("true" + "\n");
                writer.write("\n");
                writer.close();
            } else {
                lastVisitDateFile = new File(isChecked);
                FileWriter checkWriter = new FileWriter(lastVisitDateFile, true);
                //checkWriter.write("false" + "\n");
                checkWriter.close();
            }
        } catch (IOException e) {
            if (toFile != null) {
                System.out.println("↪︎404: Файлът " + toFile.getName() + " не може да бъде записан.");
            } else {
                System.out.println("↪︎404: write():  Файлът с път: " + filePath + " липсва.");
            }
        }
    }

    private static void writeYourSuppose(Set<List<Integer>> res, FileWriter writer) throws IOException {
        for (List<Integer> row : res) {
            for (Integer el : row) {
                writer.write(el + " ");
            }
            writer.write("\n");
        }
        writer.write("\n");
    }

    /**
     * Записва списък от тиражи в текстов файл {@code officialToto.txt} в локален път.
     * <p>
     * Всеки елемент от списъка {@code drawDB} се записва като отделен ред във файла.
     * Ако файлът вече съществува, съдържанието му ще бъде презаписано.
     * <p>
     * Пътят е зададен като абсолютен и фиксиран:
     * {@code /Users/blagojnikolov/Desktop/@tmp/officialToto.txt}
     *
     * <p><b>Примерен запис:</b><br>
     * {@code 26-30 MAR 2025 - 3,15,18,24,31,47}
     *
     * @param drawDB списък с тиражи за запис — всеки елемент представлява един ред
     */
    static void writeDrawDB(List<String> drawDB, boolean append) {
        String path = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";
        try {
            File toFile = new File(path);
            FileWriter writer = new FileWriter(toFile, append);

            for (String line : drawDB) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("↪︎404: writeDrawDB(): " +
                    "файлът officialToto.txt не може да бъде записан.");
        }

    }

    /**
     * <p>Обработва масив, всеки ред на който е във вида: 1-3, 16, 23, 36, 41.</p>
     * <p>Връща масив в вида: 3 16 23 36 41 49.</p>
     * <p>Записва резултата във bgToto.txt.</p>
     *
     * @param bgTotoDB Масива върнат от: parseBgTotoURL()
     */
    static void bgTotoToFile(List<String> bgTotoDB) {
        try {
            File toFile = new File("/Users/blagojnikolov/Desktop/@tmp/bgToto.txt");
            FileWriter writer = new FileWriter(toFile);
            for (int i = bgTotoDB.size() - 1; i >= 0; i--) {
                String line = bgTotoDB.get(i)
                        .substring(bgTotoDB.get(i).indexOf("-") + 1)
                        .replace(",", " ");
                writer.write(line.trim() + "\n");
            }
            writer.close();
            System.out.println(toFile.getName() + " е записан успешно.");
        } catch (Exception e) {
            System.out.println("404: writeBgTotoToFile():" +
                    "Грешка при записването в файла: bgToto.txt");
        }
    }

    /**
     * Генерира и записва резултатите от симулиран тираж във файл.
     * <p>
     * Файлът съдържа следната информация:
     * <ul>
     *   <li>Номер на тиража</li>
     *   <li>Текуща дата и час на записа</li>
     *   <li>Официален резултат от сайта и неговото стандартно отклонение</li>
     *   <li>Симулирани резултати: стойности, съвпадения и използвано отклонение</li>
     * </ul>
     * <p>
     * Името на файла е динамично — съдържа индекса на тиража и текущия timestamp.
     * Записът се осъществява в директорията: {@code ~/Library/Mobile Documents/.../tmpRes/}.
     * <p>
     * Освен това се извикват два помощни метода:
     * {@code writeYourSuppose()} — записва симулираните числа подредени по ред;
     * {@code write7by7Matrix()} — записва резултатите във вид на 7x7 матрица.
     *
     * @param suppose    Симулирани числа за тиража.
     * @param matchedSum Сума от съвпадения с предишен тираж.
     * @param std        Стандартно отклонение, използвано при генериране.
     * @param drawIdx    Уникален индекс на тиража, използван и в името на файла.
     * @throws IOException Ако възникне проблем при писане във файла.
     * @see Statistic#calculateStd(String)
     * @see Checks#getNewDrawFromSite(List) (List)
     * @see Write#writeYourSuppose(Set, FileWriter)
     * @see Write#write7by7Matrix(Set, FileWriter)
     */
    static void toFile(Set<List<Integer>> suppose, int matchedSum,
                       double std, int drawIdx) throws IOException {

        List<String> data = ParseURL.readDB("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt");
        String newDrawInSiteData = Checks.getNewDrawFromSite(data);
        String officialResult = Manipulate.extractData(newDrawInSiteData, "-");

        try {
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String currentTime = time.format(formatter);
            File toFile = new File(
                    "/Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/tmpRes/"
                            + drawIdx + "-" + currentTime + ".txt");

            FileWriter writer = new FileWriter(toFile, StandardCharsets.UTF_8);
            writer.write("N" + drawIdx + " от: ");
            writer.write(currentTime + "\n");
            double stdOffRes = (Statistic.calculateStd(officialResult));
            String stdOfResByStrFormat = String.format("%.2f", stdOffRes);
            writer.write(
                    "\n" + "Official result: " + officialResult + " - " + "Std: " + stdOfResByStrFormat + "\n");
            writer.write("Matched Sum: " + matchedSum + " - " + "Std: " + std + "\n");
            writer.write("\n");
            writer.write("\n");

            writeYourSuppose(suppose, writer);
            write7by7Matrix(suppose, writer);
            System.out.println("Залогът е записан в: " + toFile.getName());
        } catch (FileNotFoundException e) {
            System.out.println("↪︎404: writer: " +
                    "Нещо кардинално се е оплескало с - File, Scanner или DateTime");
        }
    }

    //Записва визуализацията в 7х7 масив (6@49) на трите предположения
    private static void write7by7Matrix(Set<List<Integer>> res, FileWriter writer) throws IOException {
        for (List<Integer> row : res) {
            int[][] tmp = new int[7][7];
            int[][] matrix = Manipulate.fill649Arr();

            for (int el : row) {
                Manipulate.searchThisElIn649Matrix(el, matrix, tmp);
            }

            for (int i = 0; i < tmp.length; i++) {
                for (int j = 0; j < tmp[i].length; j++) {
                    if (tmp[i][j] != 0) {
                        writer.write((tmp[i][j] + " "));
                    } else writer.write(" - ");
                }
                writer.write("\n");
            }
            writer.write("\n");
        }
        writer.close();
    }


    // Unused: ----------------------------------------------------------------------------------------------

    /**
     * Записва информация за тиражите на Тото 2 (6 от 49) в текстов файл, ако има нов тираж.
     *
     * <p>Методът извиква {@code Date.createdDayDrawString()}, която връща списък от
     * низове, съдържащи информация за дните и датите на тегленията. Тази информация
     * се записва ред по ред във файл {@code bgTotoFromSite.txt} в директорията
     * {@code ~/Desktop/@tmp}.
     *
     * <p>Записът се извършва само ако параметърът {@code isNewDraw} е {@code true}.
     * Ако е {@code false}, методът просто връща управление без да прави нищо.
     *
     * <p><b>Формат на всеки ред във файла:</b>
     * <pre>
     * 12 - 02 Jan 2025 - THURSDAY
     * 13 - 05 Jan 2025 - SUNDAY
     * ...
     * </pre>
     *
     * <p><b>Пример за повикване:</b>
     * <pre>{@code
     * bgTotoModifiedDraws(true);  // Записва данни, ако има нов тираж
     * }</pre>
     *
     * @param isNewDraw булев флаг, указващ дали има нов тираж; ако е {@code false}, методът не прави нищо
     */
    static void bgTotoModifiedDraws(boolean isNewDraw) {
        if (!isNewDraw) return;
        File toFile = new File(
                "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt");
        try {
            FileWriter writer = new FileWriter(toFile);
            List<String> data = Date.createdDayDrawString("   ");
            for (String el : data) {
                writer.write(el + "\n");
            }
            System.out.println(toFile.getName() + " е записан успешно.");
            writer.close();
        } catch (IOException e) {
            System.out.println("404: writeBgTotoModifiedDraws(): " +
                    "Проблем с записа в" + toFile.getName() + " ...");
        }
    }

    //Използва се само в: forNewDrawOld();

    /**
     * Проверява за нови тиражи от три източника и записва резултата, ако е наличен нов.
     * <p>
     * Методът анализира входните данни от три сайта (bgToto, toto49, officialToto), извиквайки
     * вътрешната логика за проверка на нови тиражи. Ако бъде открит нов тираж от който и да е от
     * източниците, методът записва променените данни в съответните архивни файлове и файлове за текуща употреба.
     *
     * <p>Формат на входните данни (siteData):
     * <ul>
     *   <li>siteData.get(0) — 30-13 Apr 2025-16, 17, 20, 23, 34, 40 - ред с данни от bgToto</li>
     *   <li>siteData.get(1) — 30-13 Apr 2025-16, 17, 20, 23, 34, 40 - ред с данни от toto49</li>
     *   <li>siteData.get(2) — 30-13 Apr 2025-16, 17, 20, 23, 34, 40 - ред с данни от officialToto</li>
     *
     *   <li>thisDraw — 16 17 20 23 34 40 </li>
     *   <li>modifiedData — 30   13 Apr 2025   16, 17, 20, 23, 34, 40 </li>
     * </ul>
     *
     * <p>Ако {@code isNew} е {@code true}, всички свързани файлове ще бъдат обновени с новите редове.
     *
     * @param siteData Списък с три реда, по един от всеки сайт. Всеки ред съдържа тиража като текст.
     * @param checkRes Булев масив с три стойности, които ще бъдат запълнени с резултатите от проверките:
     *                 <ul>
     *                   <li>checkRes[0] – {@code true}, ако bgToto има нов тираж</li>
     *                   <li>checkRes[1] – {@code true}, ако toto49 има нов тираж</li>
     *                   <li>checkRes[2] – {@code true}, ако officialToto има нов тираж</li>
     *                 </ul>
     * @throws RuntimeException ако възникне грешка по време на обработката.
     */
    static boolean lastDraw(List<String> siteData, boolean[] checkRes) {
        boolean isNew;
        String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
        String bgTotoFromSitePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
        String resultsArchivePath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";
        String fromSiteArchivePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
        String allResArchivePath = "/Users/blagojnikolov/Desktop/@tmp/allResults.txt";
        String officialTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";

        String bgTotoURL = "https://bgtoto.com/6ot49_arhiv.php";
        String toto49URL = "https://www.toto49.com/arhiv/toto_49/2025";
        String officialTotoURL = "https://toto.bg/index.php?lang=1&pid=playerhistory";
        try {
            boolean isNewFromBgToto =
                    Checks.checkTotoData(bgTotoURL, bgTotoFromSitePath, siteData.get(0).trim());
            boolean isNewFromToto49 =
                    Checks.checkTotoData(toto49URL, fromSiteArchivePath, siteData.get(1).trim());
            boolean isNewFromOfficialToto =
                    Checks.checkTotoData(officialTotoURL, bgTotoFromSitePath, siteData.get(2).trim());

            //todo: ✅ 20.04.25 -
            // Проблема е, че siteData взима текущите резултати - директно от сайтовете. Някой се е обновил друг не е...
            // Един да има нов -> isNew == true, докато и останалите два сайта не си обновят резултатите
            // От мазалат в записването във файловете те спасява допълнителната проверка в write().
            // Помисли как да излезеш от ситуацията. Виж: FileProcessing

            List<String> officialData = FileProcesses.readFromFile(officialTotoArchivePath);
            //boolean isNewDraw = FileProcesses.findingNewDraw(officialData);

            isNew = ((isNewFromBgToto || isNewFromToto49 || isNewFromOfficialToto));
            checkRes[0] = isNewFromBgToto;
            checkRes[1] = isNewFromToto49;
            checkRes[2] = isNewFromOfficialToto;

            String thisDraw = "", modifiedData = "", line;
            if (isNewFromBgToto) {
                line = siteData.getFirst().trim();
                modifiedData = line.replace("-", "   ");
                thisDraw = Manipulate.extractData(line, "-");
            } else if (isNewFromToto49) {
                line = siteData.get(1).trim();
                modifiedData = line.replace("-", "   ");
                thisDraw = Manipulate.extractData(line, "-");
            } else if (isNewFromOfficialToto) {
                line = siteData.get(2).trim();
                modifiedData = line.replace("-", "   ");
                thisDraw = Manipulate.extractData(line, "-");
            }
            if (isNew) {
                try {

                    Write.write(modifiedData, bgTotoFromSitePath, true);
                    Write.write(modifiedData, fromSiteArchivePath, true);
                    Write.write(thisDraw, bgTotoArchivePath, true);
                    Write.write(thisDraw, resultsArchivePath, true);
                    Write.write(thisDraw, allResArchivePath, true);
                } catch (Exception e) {
                    System.out.println("404: lastDraw(): " +
                            "Провери си файловете за паразитни записи...");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isNew;
    }

    /**
     * <p>Записва резултатите от List<"String"> data - всички изтеглени до сега тиражи в "fromSite.txt".</p>
     * <p>List<"String"> data е в формат: 1-02 Jan 2025-3, 16, 23, 36, 41</p>
     */
    protected void toFormSiteFile(List<String> data) {
        String fromSitePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
        String tmp;
        try {
            File toFile = new File(fromSitePath);
            FileWriter writer = new FileWriter(toFile);

            //List<String> data = this.webData;
            for (String el : data) {
                tmp = el.replace("-", "   ");
                writer.write(tmp);
                writer.write("\n");
            }
            writer.close();
            System.out.println("Данните са записани в: " + toFile.getName());
        } catch (IOException f) {
            System.out.println("404 writeAllWebData(): Файла е криминален. " +
                    "Не можах да запиша Нищо");
        }
    }

    /**
     * Записва Всички резултати от List<"String"> data в двата файла: "results.txt" и "allResults.txt".
     * <p>List<"String"> data е в формат: 1-02 Jan 2025-3, 16, 23, 36, 41</p>
     */
    protected void toResAndAllResFiles(List<String> data) {
        String resultPath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";
        String allResFilePath = "/Users/blagojnikolov/Desktop/@tmp/allResults.txt";
        try {
            File toResFile = new File(resultPath);
            File toAllResFile = new File(allResFilePath);
            FileWriter toResFileWriter = new FileWriter(toResFile);
            FileWriter toAllResFileWriter = new FileWriter(toAllResFile, true);

            for (String webEl : data) {
                String[] line = webEl.trim().split("-");
                if (line.length > 1) {
                    String webData = line[2];
                    toResFileWriter.write(webData.replace(", ", " "));           // <- *
                    toResFileWriter.write("\n");
                    toAllResFileWriter.write(webData.replace(", ", " "));        // <- и тук !
                    toAllResFileWriter.write("\n");
                } else {
                    System.out.println("404 writeThisDraw(): Проблем с дължината на реда с данни");
                    System.out.println(" - Нищо не е записано в " + toResFile.getName());
                }
            }
            toResFileWriter.close();
            toAllResFileWriter.close();
            System.out.println("Данните са записани в: " + toResFile.getName()
                    + " и " + toAllResFile.getName());
        } catch (IOException f) {
            System.out.println("404 writeThisDraw(): Файла е криминален. " +
                    "Не можах да запиша Нищо");
            System.out.println("Виж: " + resultPath + "\n" + allResFilePath);
        }
    }
}
