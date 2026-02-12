package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Клас {@code FileProcess} предоставя помощни методи за работа с файлове и директории,
 * в контекста на система за обработка и анализ на тото данни.
 *
 * <p><b>Какво прави?</b></p>
 * Този клас се грижи за:
 * <ul>
 *   <li>намиране, създаване и филтриране на файлове с резултати от тото тегления;</li>
 *   <li>зареждане на draw данни от файлове;</li>
 *   <li>прочитане и анализиране на текстови файлове в директории;</li>
 *   <li>валидиране на файлови имена и съдържания;</li>
 *   <li>генериране на пълен път до десктопа на потребителя.</li>
 * </ul>
 *
 * <p><b>Как го прави?</b></p>
 * Използва класове от {@code java.io} и {@code java.util} за сканиране, четене и писане
 * на файлове. Част от логиката включва проверка дали даден файл е вече обработван,
 * както и дали съдържа смислени draw данни. Поддържат се форматирани файлове със
 * списъци от числа, разделени с интервали.
 *
 * <p><b>Защо го прави?</b></p>
 * Основната роля на класа е да обслужва други компоненти на системата, като:
 * <ul>
 *   <li>{@code Write}, който записва draw резултати;</li>
 *   <li>{@code Checks}, който валидира draw комбинации;</li>
 *   <li>{@code PersonalR}, {@code Manipulate}, които подават входни данни.</li>
 * </ul>
 * Без тази помощна логика останалите части на приложението няма да могат да достъпват,
 * валидират или анализират файлове.
 *
 * <p><b>Ключови методи:</b></p>
 * <ul>
 *   <li>{@code readDrawFile()} – зарежда draw комбинации от текстов файл във вид на списък от числа;</li>
 *   <li>{@code readOfficialTotoFile()} – прочита официален файл и връща всяка линия като draw;</li>
 *   <li>{@code findNewestFileByDate()} – намира най-нов файл по дата измежду дадени файлове;</li>
 *   <li>{@code findFileInDirectoryByName()} – търси файл по име и разширение в директория;</li>
 *   <li>{@code listDirectory()} – връща имена на файлове в директория без системни;</li>
 *   <li>{@code validPathFormat()} – проверява дали пътя съдържа дата, използвайки формат dd.MM.yyyy;</li>
 *   <li>{@code extractFileNameFromPath()} – извлича име на файл от пълен път като String;</li>
 *   <li>{@code getDesktopPath()} – намира абсолютен път до десктопа, работещ и под Windows, и под macOS.</li>
 * </ul>
 *
 * <p><b>Свързани класове и зависимости:</b></p>
 * Използва се от:
 * <ul>
 *   <li>{@code Write} – за да създаде и запише draw файлове;</li>
 *   <li>{@code Checks} – за да провери кои draw-ове вече съществуват;</li>
 *   <li>{@code PersonalR}, {@code CryptoGraphy}, {@code FileCry} – като вход/изход точка за draw данни.</li>
 * </ul>
 *
 * <p><b>Поддръжка на формати:</b></p>
 * Всички файлове са в текстов формат, обикновено съдържащ 6 или 7 числа, разделени с интервали.
 * Някои методи обработват и файлове с разширени статистики (напр. match count).
 *
 * @author bNN
 * @version 1.0
 */
public class FileProcesses {
	public static void main(String[] args) {
//		List<String> data = readFromFile("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt");
//		System.out.println(findingNewDraw(data));
		System.out.println(getJFilePath(101));
	}

	/**
	 * Търси и връща файл с дадено име {@code fileName} от директория {@code dir}.
	 *
	 * <p>Методът проверява дали подадената директория съществува и е валидна.
	 * Ако е така, обхожда всички файлове в нея и връща първия, чието име съвпада с подаденото.
	 *
	 * <p>Използва се например при търсене на последен резултатен файл, генериран от външна система (LabView).
	 *
	 * <p>Ако:
	 * <ul>
	 *   <li>директорията не съществува или не е директория – извежда съобщение в конзолата</li>
	 *   <li>файл с такова име не е намерен – връща {@code null}</li>
	 * </ul>
	 *
	 * @param dir      директория, в която се търси файлът
	 * @param fileName името на файла, който се търси
	 * @return {@code File} обект при съвпадение, или {@code null} ако няма такъв файл
	 */
	protected static File lastLabViewResFile(File dir, String fileName) {
		if (!dir.exists() || !dir.isDirectory()) {
			System.out.println(dir.getName() + " Не съществува");
		} else {
			File[] files = dir.listFiles();
			assert files != null;
			for (File thisFile : files) {
				if (thisFile.getName().equals(fileName)) {
					return thisFile;
				}
			}
		}
		return null;
	}

	/**
	 * Създава речник (map), който свързва индекс на тираж с лист от числови двойки (или други числови редове),
	 * извлечени от файлове, намиращи се в директория, описана от подадения {@code File}.
	 *
	 * <p>Логиката е следната:
	 * <ul>
	 *   <li>Чете имената на всички файлове чрез {@code JFileNames(file)}</li>
	 *   <li>Отваря всеки файл поотделно</li>
	 *   <li>Прескача първите 6 реда и започва четене от ред 7 до 10 (максимум 4 реда)</li>
	 *   <li>Всеки валиден ред се обработва чрез {@code parseThis(String)} и добавя в списък</li>
	 *   <li>Ако името на файла започва с валиден номер на тираж (двуцифрен префикс), записва
	 *       двойките в map с ключ {@code Integer.parseInt(prefix)}</li>
	 * </ul>
	 *
	 * <p>Формат на резултата:
	 * <pre>
	 * {
	 *   42 = {6 10 15 18 35 46, 4 7 10 32 35 38, 6 17 24 35 38 49}
	 *   ...
	 * }
	 * </pre>
	 *
	 * <p>Ако файл липсва или името му не започва с валиден номер — извежда съобщение в конзолата.
	 *
	 * @param file директория или файл, подаден като вход за търсене на други файлове
	 * @return {@code Map}, в който ключът е индексът на тиража, а стойността е списък от числови редове от файла
	 */
	public static Map<Integer, List<List<Integer>>> createPairIdxListMapFromJFiles(File file) {
		final int FROM_ROW = 7;
		final int TO_ROW = 10;
		Map<Integer, List<List<Integer>>> idxSupposes = new TreeMap<>();
		List<String> fileNames = jFilePaths(file);

		for (String name : fileNames) {
			List<List<Integer>> list = new ArrayList<>();
			int cnt = 1;

			File thisFile = new File(name);
			try (Scanner scanner = new Scanner(thisFile)) {
				while (scanner.hasNext() && cnt < TO_ROW) {
					String line = scanner.nextLine();
					if (cnt >= FROM_ROW) {
						list.add(parseThis(line));
					}
					cnt++;
				}
				if (isFileNameCorrect(thisFile.getName())) {
					int dashIdx = thisFile.getName().indexOf("-");
					idxSupposes.put(Integer.parseInt(thisFile.getName().substring(0, dashIdx)), list);
				} else {
					System.out.println("404: Името на файла\"" +
							thisFile.getName() + "\"  НЕ започва с номера на тиража ...");
				}
			} catch (FileNotFoundException e) {
				System.out.println("404: файлът \"" + name + "\" не съществува ...");
			}
		}
		return idxSupposes;
	}

	/**
	 * Извлича пълните пътища на всички файлове в дадена директория {@code dir}, като пропуска
	 * скритите файлове (т.е. такива, чието име започва с точка).
	 *
	 * <p>Използва {@code File.listFiles()} за обхождане на съдържанието на директорията и добавя
	 * към резултата само пътищата на видими файлове.
	 *
	 * <p>Пример:
	 * <pre>
	 * /path/to/dir/
	 * ├── .DS_Store        <-- ще бъде игнориран
	 * ├── 42.txt           <-- ще бъде включен
	 * ├── 43-04-2024.txt   <-- ще бъде включен
	 * </pre>
	 *
	 * <p>Ако директорията не съществува или е празна, методът връща празен списък и отпечатва
	 * съобщение за грешка в конзолата.
	 *
	 * @param dir директория, от която да се вземат файлове
	 * @return списък с пълните пътища на всички валидни файлове в директорията
	 */
	static List<String> jFilePaths(File dir) {
		List<String> fileName = new ArrayList<>();
		try {
			File[] fileList = dir.listFiles();
			if (fileList != null) {
				for (File el : fileList) {
					String name = el.getName().trim();
					if (name.charAt(0) != '.') {
						fileName.add(el.getPath());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("404: Директорията \"tmpRes\" не съществува");
		}
		return fileName;
	}

	private static List<Integer> parseThis(String line) {
		List<Integer> res = new ArrayList<>();
		String[] el = line.trim().split(" ");
		for (String el1 : el) res.add(Integer.parseInt(el1));
		return res;
	}

	/**
	 * Проверява коректността на името на файла - "name".
	 *
	 * @param name Името на файла.
	 * @return true or false.
	 */
	static boolean isFileNameCorrect(String name) {
		return name.length() > 2
				&& Character.isDigit(name.charAt(0))
				&& Character.isDigit(name.charAt(1));
	}

	/**
	 * Чете файл <code>toto.txt</code> и изгражда речник (map), свързващ номера на тираж с
	 * неговите съответстващи комбинации от числа, разпределени в три масива.
	 *
	 * <p>Файлът се очаква да съдържа повтарящи се блокове от 7 реда с формата:
	 * <ul>
	 *   <li>Ред 1: номер на тиража (всяка седма линия)</li>
	 *   <li>Редове 2–4: 3 комбинации от числа (по една на ред)</li>
	 *   <li>Редове 5–7: се пропускат</li>
	 * </ul>
	 *
	 * <p>Примерна структура във файла:
	 * <pre>
	 * 42
	 *  1  2  3  4  5  6
	 *  7  8  9 10 11 12
	 *  13 14 15 16 17 18
	 * ...
	 * </pre>
	 *
	 * <p>Всеки блок след като се прочете се съхранява във вид:
	 * <pre>
	 * {
	 *   42 = {6 10 15 18 35 46, 4 7 10 32 35 38, 6 17 24 35 38 49}
	 *   ...
	 * }
	 * </pre>
	 *
	 * <p>Ключова логика:
	 * <ul>
	 *   <li>Всеки 7-ми ред се проверява дали съдържа номер на тираж (число)</li>
	 *   <li>След него се прескачат 2 реда</li>
	 *   <li>Четат се 3 реда с комбинации и се добавят към списъка</li>
	 *   <li>Когато се съберат 3 комбинации, те се асоциират към намерения тираж</li>
	 * </ul>
	 *
	 * <p>Ако файлът липсва или не съдържа очаквания формат, извежда съобщения в конзолата.
	 *
	 * @return {@code Map}, в който ключът е номерът на тиража, а стойността е списък с 3 реда от числа
	 */
	public static Map<Integer, List<List<Integer>>> createPairIdxListMapFromLVFile() {
		final int ROW_OFFSET = 7;
		final int MAX_NUMBER_LENGTH = 3;
		final int ROWS_TO_SKIP_AFTER_NUM = 2;
		final int ROW_AFTER_LAST_DRAW = 5;

		String lvFilePath = "/Users/blagojnikolov/Desktop/@tmp/toto.txt";
		Map<Integer, List<List<Integer>>> idxSupposes = new HashMap<>();
		int rowCounter = 0, idx = -1, cnt = 0;

		try {
			File lvFile = new File(lvFilePath);
			if (!lvFile.exists()) {
				System.out.println("404: Файлът \"toto.txt\" не съществува ...");
			} else {
				Scanner scanner = new Scanner(lvFile);
				List<List<Integer>> treeList = new ArrayList<>();
				boolean numIsGet = false;

				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					// Всеки седми ред трябва да държи номера на тиража.
					if ((rowCounter % ROW_OFFSET == 0) && !line.contains("\t\t\t")) {
						String[] tmp = line.trim().split("\t");
						// Проверка дали е число или дата:
						if (tmp[0].length() <= MAX_NUMBER_LENGTH) {
							idx = Integer.parseInt(tmp[0]);
							numIsGet = true;
							cnt++;
						}
						// Проверява дали си взел правилно номера. След което трябва да прескочи един ред.
						// (cnt брои редовете след реда на номера). Когато си на номера на реда cnt = 1,
						// следващият ред трябва да се прескочи cnt = 2 и чак при cnt = 3
						// си на правилният ред за масивите.
					} else if (numIsGet && rowCounter > 0 && cnt > ROWS_TO_SKIP_AFTER_NUM) {
						List<Integer> list;
						list = (parseThis(line.replace("\t", " ")));
						treeList.add(list);
						// cnt = 5: Означава, че си преминал през трите масива със залозите.
						if (cnt == ROW_AFTER_LAST_DRAW) {
							idxSupposes.put(idx, treeList);
							treeList = new ArrayList<>();
							numIsGet = false;
							cnt = 0;
						}
					}
					rowCounter++;
					// Брои редовете след номера на реда от който е взет idx:
					if (numIsGet) cnt++;
				}
			}

		} catch (FileNotFoundException ex) {
			System.out.println("404: createPairIdxListMapFromLVFile():" +
					"Проблем с обработката на файл: \"toto.txt\" ...");
		}
		return idxSupposes;
	}

	/**
	 * Търси и връща пълния път към файл, чието име започва с дадения номер на тираж ({@code drawIdx}).
	 *
	 * <p>Методът обхожда списък от файлови пътища, генерирани от директория чрез {@code jFileNames()},
	 * и за всеки път:
	 * <ul>
	 *   <li>Извлича първите три символа след последния {@code /}, които трябва да представляват номера на тиража</li>
	 *   <li>Опитва се да ги парсне като цяло число</li>
	 *   <li>Ако съвпадат с {@code drawIdx}, връща целия път</li>
	 * </ul>
	 *
	 * <p>Пример за име на файл:
	 * <pre>
	 * /Users/blagojnikolov/.../19-03-03-2025 05:36:21.txt
	 * </pre>
	 * Ако {@code drawIdx == 19}, методът ще го разпознае по „19“ в името и ще върне пътя.
	 *
	 * <p>Ако парсването се провали, методът връща съобщение за грешка.
	 * Ако не се намери съвпадение, връща {@code null}.
	 *
	 * @param drawIdx номер на тиража, който се търси в имената на файловете
	 * @return пълният файлов път, ако е намерен, или {@code null} ако няма съвпадение; в случай на грешка — съобщение с "404"
	 */
	static String getJFilePath(int drawIdx) {
		List<String> paths = jFilePaths
				(new File("/Users/blagojnikolov/Library/Mobile " +
						"Documents/com~apple~CloudDocs/TOTO/tmpRes"));
		// /Users/blagojnikolov/Library/Mobile Documents/com~apple~CloudDocs/TOTO/tmpRes/19-03-03-2025 05:36:21.txt
		for (String path : paths) {
			int idx;
			int slashIdx = path.lastIndexOf("/");
			int dashIdx = path.indexOf("-");
			String x = path.substring(slashIdx + 1, dashIdx); // .....  /19- .... -> 19
			try {
				idx = Integer.parseInt(x);
				if (idx == drawIdx) return path.substring(slashIdx + 1);
			} catch (NumberFormatException e) {
				return "404: getJFilePath:" +
						" Не мога да взема коректно номера на залога от името на файла...";
			}
		}
		return null;
	}

	/**
	 * Чете текстов файл ред по ред и връща съдържанието му като списък от низове.
	 *
	 * <p>Методът се опитва да отвори файл по зададения път {@code fromFile}.
	 * Ако файлът съществува, прочита съдържанието му чрез {@code Scanner}
	 * и добавя всеки ред в {@code List<String>}.</p>
	 *
	 * <p>Ако файлът не може да бъде намерен, извежда съобщение за грешка в конзолата
	 * и връща празен списък.</p>
	 *
	 * @param fromFile път до текстовия файл, който трябва да се прочете (абсолютен или относителен)
	 * @return списък от редовете във файла; ако файлът липсва — празен списък
	 *
	 * <p><b>Пример:</b></p>
	 * <pre>{@code
	 * List<String> lines = readFromFile("/Users/blago/Desktop/totoData.txt");
	 * for (String line : lines) {
	 *     System.out.println(line);
	 * }
	 * }</pre>
	 *
	 * <p><b>Обработка на грешки:</b></p>
	 * Ако файлът не бъде открит:
	 * <pre>{@code
	 * System.out.println("404: readFromFile(): Файлът не е намерен ...");
	 * }</pre>
	 */
	static List<String> readFromFile(String fromFile) {
		List<String> res = new ArrayList<>();
		try {
			File file = new File(fromFile);
			Scanner scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				res.add(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println("404: readFromFile(): " +
					"Файлът не е намерен ...");
		}
		return res;
	}

	/**
	 * Проверява дали в подадените данни има нов тираж спрямо предходните записи.
	 *
	 * <p>Очаква се входният списък {@code data} да съдържа редове от вида:
	 * <pre>
	 *     123-02 APR 2025-WEDNESDAY - 3,15,18,24,31,47
	 * </pre>
	 * където първият елемент (преди първата тире {@code -}) е пореден индекс на тиража.</p>
	 *
	 * <p>Методът разделя всеки ред и извлича първото число (индекса), който парсва до {@code int}.
	 * Ако всички индекси са еднакви (тоест има само един запис), се приема, че няма нов тираж.</p>
	 *
	 * <p>При невалиден запис (невъзможност за парсване на индекс) се извежда съобщение в конзолата.</p>
	 *
	 * @param data списък от редове, представляващи записите с тиражи
	 * @return {@code true} ако има нов тираж (тоест ако минималният и максималният индекс се различават),
	 *         {@code false} ако всички индекси са еднакви или има само един запис
	 *
	 * <p><b>Пример:</b></p>
	 * <pre>{@code
	 * List<String> draws = List.of(
	 *     "123-02 APR 2025-WEDNESDAY - 3,15,18,24,31,47",
	 *     "124-04 APR 2025-SATURDAY - 5,12,23,34,35,45"
	 * );
	 * boolean hasNewDraw = findingNewDraw(draws); // връща true
	 * }</pre>
	 *
	 * <p><b>Грешка:</b></p>
	 * Ако не може да се парсне индексът:
	 * <pre>
	 * 404: findingNewDraw(): Не мога да парсна стринга до число. Формата явно е кофти...
	 * </pre>
	 */
	static boolean findingNewDraw(List<String> data) {
		List<Integer> digits = new ArrayList<>();
		int min, max;

		for (String line : data) {
			String tmpIdx = line.split("-")[0];
			try {
				int idx = Integer.parseInt(tmpIdx);
				digits.add(idx);
			} catch (NumberFormatException e) {
				System.out.println("404: findingNewDraw(): " +
						"Не мога да парсна стринга до число. Формата явно е кофти...");
			}
		}
		max = Manipulate.findMax(digits);
		min = Manipulate.findMin(digits);
		return min != max;
	}

	/**
	 * Връща абсолютен път към папката "Desktop" на текущия потребител,
	 * като добавя подаден допълнителен под-път (ако има такъв).
	 *
	 * <p><strong>Какво прави:</strong></p>
	 * Методът конструира пълния път към Desktop директорията на текущия потребител,
	 * независимо от операционната система (Windows, macOS, Linux и др.), и добавя към него
	 * стойността на параметъра `additionalPath`.
	 *
	 * <p><strong>Как го прави:</strong></p>
	 * 1. Извлича главната директория на потребителя чрез:
	 *    {@code System.getProperty("user.home")}
	 * 2. Определя операционната система чрез:
	 *    {@code System.getProperty("os.name").toLowerCase()}
	 * 3. Проверява дали операционната система съдържа "win"
	 *    (което указва Windows).
	 *    - Ако е Windows, добавя "\\Desktop\\" и след това `additionalPath`.
	 *    - Ако е друга ОС (например Linux, macOS), добавя директно `additionalPath`
	 *      към домашната директория.
	 *
	 * <p><strong>Защо го прави:</strong></p>
	 * - За да позволи на програмата да записва или чете от Desktop директорията
	 *   на потребителя по преносим и кросплатформен начин.
	 * - Windows има специфично именуване на Desktop като поддиректория в `user.home`,
	 *   докато в други операционни системи това може да бъде различно или изрично подадено.
	 * - Добавеният `additionalPath` позволява динамично добавяне на под-директория или име на файл
	 *   според нуждите на приложението (напр. "/@tmp/Crypted").
	 *
	 * @param additionalPath Стойност, която ще бъде добавена след Desktop директорията.
	 *                       Може да бъде празен низ, под-директория или име на файл.
	 * @return Пълен абсолютен път към Desktop директорията с допълнение, съобразен с операционната система.
	 */
	public static String getDesktopPath(String additionalPath) {
		String userHome = System.getProperty("user.home");
		String osName = System.getProperty("os.name").toLowerCase();

		String desktopPath;

		if (osName.contains("win")) {
			desktopPath = userHome + "\\Desktop\\" + additionalPath;
		} else {
			desktopPath = userHome + additionalPath;
		}

		return desktopPath;
	}
}
