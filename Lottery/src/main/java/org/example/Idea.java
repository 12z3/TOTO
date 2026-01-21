package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Idea {
	public static void main(String[] args) throws FileNotFoundException {
//		List<String> siteData = Manipulate.getThreeOfficialDB();
//		boolean x = forNewDrawN(siteData);
//		newSuppose(false);

		//System.out.println(prevDrawFromFile("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt"));
		System.out.println(ParseURL.readDB
				("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt").getLast());
	}

	 /**
	 * Основна логика за проверка и обработка на нов тираж от сайта.
	 *
	 * <p>Ако се установи, че в подадените редове има ново теглене,
	 * методът:
	 * <ol>
	 *     <li>извлича най-новия ред чрез {@link #getNewDrawFromSite(List)}</li>
	 *     <li>извършва запис във всички съответни файлове чрез {@link Write#writeThisDrawInAllFiles(String, List, boolean[])}</li>
	 *     <li>извежда резултат чрез {@code Print.messageX()}</li>
	 * </ol>
	 *
	 * <p>Очакваният вход е списък от редове, прочетени от сайта, всеки във формат:
	 * <pre>
	 * индекс-дата-ден - числа
	 * например:
	 * 123-02 APR 2025-WEDNESDAY -3,15,18,24,31,47
	 * </pre>
	 *
	 * <p>Методът хваща потенциален {@link IndexOutOfBoundsException}, ако някой ред е с грешен формат.</p>
	 *
	 * @param siteData редове с тегления от сайта
	 * @return {@code true} ако е добавен нов ред, {@code false} ако не е открит нов тираж
	 * @throws FileNotFoundException ако липсва файл при опит за запис
	 */
	static boolean forNewDrawN(List<String> siteData, boolean[] checkRes) throws FileNotFoundException {
		boolean isNew = false;
		//boolean[] checkRes = new boolean[3];
		try {
			if (isNewDrawFromSite(siteData)) {
				String newDraw = getNewDrawFromSite(siteData);
				Write.writeThisDrawInAllFiles(newDraw, siteData, checkRes);
				isNew = true;
				checkRes = getURLArr(checkRes, siteData);
			}
			Print.messageX(isNew, checkRes, siteData);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("404: Провери си мрежата...");
		} catch (FileNotFoundException fe) {
			System.out.println("404: Файла в който записваш е криминален...");
		}
		return isNew;
	}

	/**
	 * Връща най-новия ред от списъка с тегления, ако той е след определена дата.
	 *
	 * <p>Всеки ред съдържа дата на теглене във формат:
	 * <pre>
	 *     индекс - dd MMM yyyy - ден - числа
	 * </pre>
	 * Пример:
	 * <pre>
	 *     124-04 APR 2025-SATURDAY - 3,15,18,24,31,47
	 * </pre>
	 *
	 * <p>Методът сравнява всяка дата с референтна дата (точно преди 1 година)
	 * и връща най-новия ред, ако такъв бъде намерен.</p>
	 *
	 * @param siteData списък от редове, съдържащи тегления от сайта
	 * @return редът с най-новата дата или {@code ""} ако няма такъв
	 */
	static String getNewDrawFromSite(List<String> siteData) {
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
	 * Проверява дали в подадените редове от сайта има нов тираж,
	 * като сравнява индексите на записите.
	 *
	 * <p>Очаква се всеки ред да е във формат:
	 * <pre>
	 *     124-04 APR 2025-SATURDAY - 3,15,18,24,31,47
	 * </pre>
	 * като числото преди първата тире представлява поредния индекс.</p>
	 *
	 * <p>Извлича всички индекси, намира най-големия и най-малкия.
	 * Ако са различни — значи има нов запис, върща {@code true}.</p>
	 *
	 * @param data списък с текстови редове, прочетени от източник (сайт)
	 * @return {@code true} ако има нов запис, {@code false} ако всички са еднакви
	 */
	static boolean isNewDrawFromSite(List<String> data) {
		List<Integer> digits = new ArrayList<>();
		String draw = null;
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
	 * Освен това, ако има поне една промяна, съответният елемент в {@code checkRes[]} също се обновява.
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
	static boolean[] getURLArr(boolean[] checkRes, List<String> siteData) {
		String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
		String resultsArchivePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
		//String bgTotoArchivePath = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
		//String resultsArchivePath = "/Users/blagojnikolov/Desktop/@tmp/results.txt";

		String bgTotoURL = "https://bgtoto.com/6ot49_arhiv.php";
		String toto49URL = "https://www.toto49.com/arhiv/toto_49/2025";
		String officialTotoURL = "https://toto.bg/index.php?lang=1&pid=playerhistory";
		try {
			boolean isNewFromBgToto =
					Checks.checkTotoData(bgTotoURL, bgTotoArchivePath, siteData.get(0).trim());
			boolean isNewFromToto49 =
					Checks.checkTotoData(toto49URL, resultsArchivePath, siteData.get(1).trim());
			boolean isNewFromOfficialToto =
					Checks.checkTotoData(officialTotoURL, resultsArchivePath, siteData.get(2).trim());


			if ((isNewFromBgToto || isNewFromToto49 || isNewFromOfficialToto)) {
				checkRes[0] = isNewFromBgToto;
				checkRes[1] = isNewFromToto49;
				checkRes[2] = isNewFromOfficialToto;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new boolean[]{checkRes[0], checkRes[1], checkRes[2]};
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
	protected static void newSuppose(boolean isGaussian) {
		double std = -1.1;
		int matches = -1;
		Set<List<Integer>> suppose = null;

		String stdRegEx = "^(?:[0-9](?:[.,]\\d+)?|[1-9][0-9]?(?:[.,]\\d+)?|30(?:[.,]0+)?)$";
		String matchesRegEx = "^[0-5]$";

		String officialTotoPath = "/Users/blagojnikolov/Desktop/@tmp/officialToto.txt";
		String bgTotoFilePath = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
		Scanner scanner = new Scanner(System.in);

		System.out.print("Да генерирам ли предложения за залози?: ");
		String ans = scanner.nextLine();
		ans = Generator.validateAns(ans, scanner, "Y", "N");

		if (ans.equalsIgnoreCase("Y")) {
			String today = Date.findNextDrawDay();
			//System.out.println(today.toUpperCase());
			//Frequency.ofMatchForDayOfWeek(today);

			//int numberOdDays = Manipulate.allSpecificDayCntInArchive(today);
			System.out.print("\nСтатистика за ");
			Map<Integer, List<Integer>> map = Frequency.ofMatching(today, true);
			Print.printMap(map);
			while (ans.equalsIgnoreCase("Y")) {
				if (isGaussian) {
					System.out.print("\nstd: ");
					String line = scanner.nextLine();
					std = Generator.digitsValidator(stdRegEx, scanner, line, true);

					System.out.print("matches: ");
					line = scanner.nextLine();
					matches = (int) Generator.digitsValidator(matchesRegEx, scanner, line, false);

					List<String> data = ParseURL.readFromFile(new File(officialTotoPath));
					String tmp = Manipulate.extractData(data.getFirst(), "-");
					List<Integer> oldDraw = Manipulate.strToListOfInt(tmp);
					suppose = Generator.newGaussianProposalList(oldDraw, std, matches);
					//suppose = Generator.ofNewSupposeListOfFrequencyMatches();
					Generator.printProposalList(suppose, std, matches);

				} else {
					suppose = Generator.ofNewProposalListOfFrequencyMatches();
					Generator.printProposalList(suppose, std, matches);
				}
				System.out.print("Нов?: ");
				String newAns = scanner.nextLine();
				ans = Generator.validateAns(newAns, scanner, "Y", "N");
			}
		}

		System.out.print("Да ги запиша ли?: ");
		String answer = Manipulate.answer();
		if (answer.equals("Y")) {
			Integer webIdx = WebData.getWebIdxFromFileData(bgTotoFilePath).getLast();
			try {
				Write.toFile(suppose, matches, std, ++webIdx);
			} catch (IOException e) {
				System.out.println("404: purposeSuppose(): " +
						"Не мога да запиша файла");
			}
		}
	}
}


