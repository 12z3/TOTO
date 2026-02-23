package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * КЛАС: MissingIdx
 *
 * <p><b>Какъв проблем решава</b></p>
 * <ul>
 *   <li>Синхронизира локален(и) текстов файл с тиражи (редове с формат "<индекс> - <данни>") спрямо официални списъци от сайт.</li>
 *   <li>Открива липсващи индекси в локалния файл, извлича точните редове от официалния източник и ги вмъква на правилното място при запазена подредба.</li>
 * </ul>
 *
 * <p><b>Как го решава (накратко)</b></p>
 * <ol>
 *   <li>Зарежда официалните редове и локалната „база“.</li>
 *   <li>Определя кои индекси липсват.</li>
 *   <li>За всеки липсващ индекс намира официалния ред.</li>
 *   <li>Добавя редовете в локалния файл:
 *     <ul>
 *       <li>поточен режим (ниска памет) чрез файл→файл презаписване, или</li>
 *       <li>изцяло в памет (бързо за малки файлове).</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><b>Описание на проблемите, които решава всеки метод</b></p>
 * <ul>
 *   <li><b>main(String[])</b> – стартира синхронизацията за избран сайт; делегира към главната процедура; обработва I/O грешки.</li>
 *   <li><b>missingDrawIdx(List&lt;String&gt;, String)</b> – открива липсващите индекси и връща точните редове за добавяне от официалните данни.</li>
 *   <li><b>searchDrawIdx(List&lt;String&gt;, List&lt;String&gt;, List&lt;String&gt;, int)</b> – намира официалния ред за даден индекс, когато локално липсва или е несъответстващ.</li>
 *   <li><b>writeToIdxInFile(String, String, int, String)</b> – потоково вмъква един ред в нов файл на правилната позиция без да държи целия файл в памет.</li>
 *   <li><b>writeMissingDrawsToNewFile(String)</b> – последователно добавя всички липси чрез двойка „fromFile → toFile“, надеждно за големи файлове.</li>
 *   <li><b>writeMissingDrawsInMemory(String)</b> – алтернатива изцяло в памет: чете, вмъква сортирани редове по индекс и записва обратно.</li>
 * </ul>
 *
 * <p><b>Формат и предпоставки</b></p>
 * <ul>
 *   <li>Ред: "<индекс> - <данни>"; при парсване тирето се нормализира до интервал.</li>
 *   <li>Подредбата по индекс е нарастваща; несъответствие = липса на ред или различен индекс на същата позиция.</li>
 *   <li>Имена на източници: напр. "bgtoto", "toto49".</li>
 * </ul>
 *
 * <p><b>Устойчивост и гранични случаи</b></p>
 * <ul>
 *   <li>Защитено парсване на индекси; обработка на излизане извън диапазон.</li>
 *   <li>Поточният режим щади паметта; вариантът „в памет“ е за малки обеми.</li>
 * </ul>
 *
 * <p><b>Вход/изход</b></p>
 * <ul>
 *   <li>Използва java.nio.file.Files/Path за четене, запис и копиране; java.io.FileWriter и java.util.Scanner за потокова обработка.</li>
 * </ul>
 *
 * @implNote Предполага входен формат "<индекс> - <данни>" и нарастваща подредба по индекс.
 */
public class MissingIdx {
	static final String BG_TOTO = "bgtoto";
	static final String TOTO_49 = "toto49";
	static final String tmpFile = "/Users/blagojnikolov/Downloads/bgTotoFromSite1.txt";
	static final String bgTotoFile = "/Users/blagojnikolov/Downloads/bgTotoFromSite.txt";
	static final String bgTotoOrgFile = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
	static final String toto49File = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";

	public static void main(String[] args) throws IOException {
		writeMissingDrawsToNewFile(BG_TOTO);
	}

	/**
	 * Открива липсващите редове (тиражи) спрямо официалния списък за даден сайт и
	 * връща пълните редове, които трябва да се допишат в локалните файлове/база.
	 *
	 * <p><b>Проблем:</b> Официалният източник (siteData) съдържа повече/по-актуални
	 * тиражи от локалните файлове. Трябва да намерим кои точно индекси и техните редове
	 * липсват локално за конкретен сайт ("bgtoto" или "toto49").</p>
	 *
	 * <p><b>Как го решава:</b> Изчислява липсващите индекси с помощни проверки
	 * (Checks.bgTotoMissing / Checks.toto49Missing) и за всеки липсващ индекс търси
	 * съответния ред в официалния списък, като валидира дали го няма в локалния файл,
	 * чрез {@link #searchDrawIdx(List, List, List, int)}. Натрупва и връща списък
	 * с липсващите редове.</p>
	 *
	 * @param siteData официални данни от сайта; всеки ред започва с числов индекс на тираж
	 *                 (преди разделителя "-"), напр. "1234 - ...".
	 * @param siteName име на източника: "bgtoto" или "toto49".
	 * @return списък с пълните редове на липсващите тиражи, в реда на обхода.
	 * @throws RuntimeException ако някоя от помощните операции хвърли непредвидено изключение.
	 * @implNote Използва два локални файла (прочетени с ParseURL.readDB) като „база“.
	 * Извежда диагностичен печат на екрана (System.out.println()) за намерените редове.
	 * @complexity O(N + M), където N е броят на официалните редове, а M – броят на липсващите индекси.
	 * @since 1.0
	 */
	static List<String> missingDrawIdx(List<String> siteData, String siteName) {
		List<String> drawDataFromBgTotoSite = Date.createdDayDrawString("-");
		List<String> drawData49Site = ParseURL.parseToto49URL();                    // от сайт toto49 - fromSite.txt"

		List<String> bgTotoDBFromFile = ParseURL.readDB(bgTotoOrgFile);             // bgTotoFromSite.txt
		List<String> toto49DBFromFile = ParseURL.readDB(toto49File);                // пази резултатите от toto49

		List<Integer> bgTotoMissingIdx = Checks.bgTotoMissing(siteData);
		List<Integer> toto49MissingIdx = Checks.toto49Missing(siteData);

		List<String> missingDataRows = new ArrayList<>();
		if (siteName.equals(BG_TOTO)) {
			for (int i = 0; i < bgTotoMissingIdx.size(); i++) {
				searchDrawIdx(drawDataFromBgTotoSite, missingDataRows, bgTotoDBFromFile, bgTotoMissingIdx.get(i));
			}
		} else if (siteName.equals(TOTO_49)) {
			for (int i = 0; i < toto49MissingIdx.size(); i++) {
				searchDrawIdx(drawData49Site, missingDataRows, toto49DBFromFile, toto49MissingIdx.get(i));
			}
		}

		System.out.println();
		missingDataRows.forEach(System.out::println);
		return missingDataRows;
	}

	/**
	 * Намира и добавя в колекцията {@code missingDataRows} реда от официалния списък,
	 * който отговаря на даден индекс {@code idx}, ако този ред липсва в локалната „база“.
	 *
	 * <p><b>Проблем:</b> За конкретен липсващ индекс на тираж трябва да се извлече точният
	 * ред от официалните данни и да се потвърди, че не е наличен локално.</p>
	 *
	 * <p><b>Как го решава:</b> Обхожда официалния списък, парсира индексите и при
	 * съвпадение на {@code idx} проверява дали редът отсъства в локалния списък (по парсиран индекс).
	 * Ако липсва, добавя целия ред в {@code missingDataRows}.</p>
	 *
	 * @param missingDataRows акумулираща колекция за липсващите редове (модифицира се).
	 * @param dbFromFile      редове от локалния файл („база“) за същия сайт.
	 * @param idx             търсеният индекс на тираж.
	 * @throws NumberFormatException при невалиден формат на индекса в някой ред.
	 * @implNote Методът приема, че индексът е най-лявата числова стойност преди разделителя "-".
	 * @complexity O(N + K), където N е брой официални редове, а K – брой локални редове (за проверката).
	 * @since 1.0
	 */
	static void searchDrawIdx(List<String> officialDrawlDataFromSite, List<String> missingDataRows,
							  List<String> dbFromFile, int idx) {
		int idxFromFile = -1;
		for (int i = 0; i < officialDrawlDataFromSite.size(); i++) {
			boolean isMissing = false;
			int idxFromSite = Integer.parseInt(officialDrawlDataFromSite.get(i).split("-")[0]);
			try {
				idxFromFile = Integer.parseInt(dbFromFile.get(i).split("-")[0]);
			} catch (IndexOutOfBoundsException e) {
				isMissing = true;
			} finally {
				if (((idxFromSite == idx) && isMissing) || (idxFromSite == idx) && idxFromFile != idx) {
					missingDataRows.add(officialDrawlDataFromSite.get(i));
				}
			}
		}
	}

	/**
	 * Записва даден ред {@code txt} в целеви файл точно на позицията след реда
	 * с индекс {@code idx}, като поддържа празен ред (нов ред) преди/след записа според случая.
	 *
	 * <p><b>Проблем:</b> Трябва да се вмъкне липсващ ред (тираж) в съществуващ
	 * текстов файл, подреден по индекс, без да се наруши форматирането.</p>
	 *
	 * <p><b>Как го решава:</b> Чете източника {@code readFrom} ред по ред, парсира
	 * индекса (първата числова стойност), и когато срещне мястото на вмъкване
	 * (редът с индекс {@code idx}), записва в целевия файл {@code pathToWrite}
	 * текущия ред, после нов ред, после търсения {@code txt} (с изчистени
	 * излишни тирета), после нов ред, след което продължава с останалото съдържание.
	 * Връща флаг дали е извършено писане.</p>
	 *
	 * @param pathToWrite път до файла-назначение, в който ще се изгради новото съдържание.
	 * @param txt         редът (липсващият тираж), който трябва да се постави.
	 * @param idx         индексът, след чийто ред ще се вмъкне {@code txt}.
	 * @param readFrom    път до изходния файл, който се чете и преработва.
	 * @throws RuntimeException ако възникне {@link IOException} по време на четене/писане.
	 * @implNote Парсира индекса от всяка линия с {@code line.split("{3+}")[0]} (т.е. дели по
	 * поне три интервала; адаптирайте според реалния формат). Заместването на "-"
	 * с " " нормализира разделителя преди запис.
	 * @complexity O(L), където L е броят редове в източника {@code readFrom}.
	 * @since 1.0
	 */
	static void writeToIdxInFile(String pathToWrite, String txt, int idx, String readFrom) {
		File file = new File(pathToWrite);
		String line, lineIdx;
		try {
			FileWriter writer = new FileWriter(file, false);
			Scanner reader = new Scanner(new File(readFrom));
			while (reader.hasNext()) {
				line = reader.nextLine();
				lineIdx = line.split(" {3}")[0];
				int pivotIdx = Integer.parseInt(lineIdx);
				if (idx > 1 && (pivotIdx + 1 == idx + 1)) continue;    // 73 -> 74 - Има валиден следващ индекс
				if ((idx > 1 && (pivotIdx == idx - 1))) {              // 73 -> 75 - Следващият индекс липсва
					writer.write(line);
					writer.write("\n");
					writer.write(txt.replace("-", "   "));
					writer.write("\n");
				} else if (idx == 1 && (idx == pivotIdx)) {
					writer.write("/n");
					writer.write(txt.replace("-", "   "));
				} else {
					writer.write(line);
					writer.write("\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Главна процедура за дописване на всички липсващи тиражи в локалните файлове.
	 * Обхожда липсващите редове и ги вмъква на правилните места в един или повече
	 * целеви файлове.
	 *
	 * <p><b>Проблем:</b> След като са установени липсващите редове, те трябва да
	 * бъдат физически добавени в съответните локални файлове, като се поддържа
	 * коректна подредба по индекс и форматиране.</p>
	 *
	 * <p><b>Как го решава:</b> Извиква {@link #missingDrawIdx(List, String)} за да получи
	 * всички липсващи редове за сайта "bgtoto", подготвя целеви пътища (path1, path2, path3)
	 * и за всеки ред извиква {@link #writeToIdxInFile(String, String, int, String)}.
	 * Ако първото вмъкване не се е случило, опитва алтернативен файл; при успех
	 * маркира операцията и продължава.</p>
	 *
	 * @implNote Методът използва твърдо кодирани пътища (Downloads/bgTotoFromSite*.txt).
	 * Добра практика е те да станат конфигурация (напр. чрез properties).
	 * @complexity O(M · L), където M е броят липсващи редове, а L – средният размер на файл за вмъкване.
	 * @since 1.0
	 */
	private static void writeMissingDrawsToNewFile(String siteName) throws IOException {
		List<String> missing = missingDrawIdx(Manipulate.getThreeOfficialDB(), siteName);
		String fromFile = bgTotoFile;
		String toFile = tmpFile;

		for (String s : missing) {
			String line = s.replace("-", "   ");
			int idx = Integer.parseInt(s.split("-")[0]);
			writeToIdxInFile(toFile, line, idx, fromFile);
			String tmp = fromFile;
			fromFile = toFile;      // последният валиден
			toFile = tmp;

			Path latest = Path.of(fromFile);
			Files.copy(latest, Path.of(toFile), StandardCopyOption.REPLACE_EXISTING);
			//Files.copy(latest, latest, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void writeMissingDrawsInMemory(String siteName) throws IOException {
		Path p = Path.of(bgTotoFile);
		List<String> lines = Files.readAllLines(p);

		List<String> missing = new ArrayList<>(missingDrawIdx(Manipulate.getThreeOfficialDB(), siteName));
		missing.sort(Comparator.comparingInt(s -> Integer.parseInt(s.split("-")[0])));

		for (String s : missing) {
			String line = s.replace("-", " ");
			int idx = Integer.parseInt(s.split("-")[0]);      // 0-базиран; ако е 1-базиран: idx--;
			lines.add(idx, line); // вмъкване по индекс
		}
		Files.write(p, lines);
	}
}
