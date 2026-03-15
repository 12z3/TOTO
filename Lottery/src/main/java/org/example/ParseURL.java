package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Клас {@code ParseURL} предоставя помощни методи за четене и извличане на данни от локални файлове,
 * съдържащи текстови представяния на резултати от тото тегления.
 *
 * <p>Основната му роля е да абстрахира четенето на текстови файлове, които съдържат резултати, извлечени
 * от различни източници (официални сайтове като <a href="https://toto.bg">toto.bg</a>, <a href="https://toto49.com">toto49.com</a> и др.),
 * като ги преобразува в структури от тип {@code List<String>} за по-нататъшен анализ или обработка.</p>
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *   <li>Чете файлове ред по ред и ги връща като списъци от текстови редове</li>
 *   <li>Извлича и форматира данни за последни тегления</li>
 *   <li>Замества интервали с тирета в редовете при нужда (например при посоки на залог)</li>
 *   <li>Служи като връзка между файловата система и логическите структури на приложението</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * <ul>
 *   <li>Използва {@code Scanner} за редово четене на файловете</li>
 *   <li>Използва {@code File} обекти за дефиниране на пътя до файловете</li>
 *   <li>Обработва IOException и FileNotFoundException с диагностични съобщения</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * Повечето функционалности в проекта зависят от това да четат последователни тегления от текстови
 * файлове. Тези данни идват от външни сайтове и се съхраняват локално. {@code ParseURL} служи като
 * надежден слой между файловото съдържание и Java логиката, за да се гарантира:
 * <ul>
 *   <li>Чистота на входа</li>
 *   <li>Гъвкавост при обработка на файлове от различен произход</li>
 *   <li>Лесно повторно използване на функционалностите</li>
 * </ul>
 *
 * <h2>Основни методи:</h2>
 * <ul>
 *   <li>{@code parseBgTotoURL()} – извлича резултати от сайта на българския тотализатор</li>
 *   <li>{@code parseOfficialURL()} – извлича резултати от официалния файл с draw</li>
 *   <li>{@code parseToto49URL()} – извлича архивни тегления от външен сайт</li>
 *   <li>{@code readFromFilePath(String path)} – чете файл по зададен път</li>
 *   <li>{@code readFromFile(File file)} – чете съдържание на файл и връща списък от редове</li>
 * </ul>
 *
 * <h2>Зависимости:</h2>
 * <ul>
 *   <li>{@code java.util.List, java.util.Scanner}</li>
 *   <li>{@code java.io.File, java.io.FileNotFoundException}</li>
 * </ul>
 *
 * <p>Този клас се използва от почти всички други части на системата – като {@code Manipulate}, {@code Write}, {@code Checks},
 * и {@code Statistic}, за да заредят изходните си данни.</p>
 *
 * @author bNN
 * @version 1.0
 */
public class ParseURL {

	public static void main(String[] args) {
		List<String> l = parseOfficialURL();
		l.forEach(System.out::println);
	}

	static Document fetchBgTotoArchiveByYear(int year) throws IOException {

		final String url = "https://bgtoto.com/6ot49_arhiv.php";

		// 1) Зареждаме страницата (за да вземем формуляра + „скрити“ полета, ако има)
		Document doc = Jsoup.connect(url)
				.userAgent("Mozilla/5.0")
				.referrer("https://bgtoto.com/")
				.timeout(20_000)
				.get();

		// 2) Намираме формуляра, който съдържа падащото меню за годината
		Element formEl = doc.selectFirst("form:has(select)");
		if (formEl == null) {
			throw new IllegalStateException("Не е намерен формуляр с избор на година на страницата.");
		}
		if (!(formEl instanceof FormElement)) {
			throw new IllegalStateException("Намереният елемент не е FormElement (не може да се подаде като формуляр).");
		}
		FormElement form = (FormElement) formEl;

		// 3) Намираме падащото меню за годината (избираме първото 'select' в този формуляр)
		Element yearSelect = form.selectFirst("select");
		if (yearSelect == null) {
			throw new IllegalStateException("Не е намерено поле за година (select) във формуляра.");
		}

		// 4) Задаваме избраната година
		yearSelect.val(String.valueOf(year));

		// 5) Подаваме формуляра (Jsoup ще използва action/method според HTML на формуляра)
		Connection submitConn = form.submit()
				.userAgent("Mozilla/5.0")
				.referrer("https://bgtoto.com/")
				.timeout(20_000);

		// Връщаме документа след подаване (GET или POST – според формуляра)
		return submitConn.execute().parse();
	}

	/**
	 * Клас {@code ParseURL} съдържа помощни методи за:
	 * <ul>
	 *     <li>Извличане на данни от уеб страници, свързани с тото тегления</li>
	 *     <li>Извличане на последни резултати от официални сайтове</li>
	 *     <li>Четене на съдържание от локални текстови файлове</li>
	 *
	 *     <li>При грешка в свързването със сайта връща предишният записан резултат</li>
	 * </ul>
	 *
	 * <h2>Какво прави?</h2>
	 * Класът изпълнява следните основни функции:
	 * <ol>
	 *   <li>Свързва се с външни сайтове като <a href="https://bgtoto.com">bgtoto.com</a> и <a href="https://info.toto.bg">info.toto.bg</a> чрез Jsoup</li>
	 *   <li>Парсва HTML таблици с последни резултати от тегления</li>
	 *   <li>Форматира резултати (дата, теглени числа) в текстов формат</li>
	 *   <li>Чете текстови файлове и конвертира редове в списъци</li>
	 *   <li>При грешка в свързването със сайта връща предишният записан резултат</li>
	 * </ol>
	 *
	 * <h2>Как го прави?</h2>
	 * <ul>
	 *   <li>Методът {@code parseBgTotoURL()}:
	 *     <ul>
	 *       <li>Чете последния запис от локален файл с последни тегления</li>
	 *       <li>Извлича индекса и числата</li>
	 *       <li>Свързва се с https://bgtoto.com/goto49_arhiv.php и анализира таблицата</li>
	 *       <li>Прескача старите записи и събира новите в {@code StringBuilder}</li>
	 *     </ul>
	 *   </li>
	 *   <li>Методът {@code parseOfficialURL()}:
	 *     <ul>
	 *       <li>Използва Jsoup за достъп до https://info.toto.bg</li>
	 *       <li>Парсва дата на теглене, извлича тиражния номер и числата</li>
	 *       <li>Форматира резултата като една текстова линия</li>
	 *     </ul>
	 *   </li>
	 *   <li>Методът {@code readDB(String fromFilePath)}:
	 *     <ul>
	 *       <li>Чете файл построчно, премахва интервали и добавя в списък</li>
	 *     </ul>
	 *   </li>
	 *   <li>Методът {@code readFromFile(File file)}:
	 *     <ul>
	 *       <li>Чете файл и връща списък от редовете му</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * <h2>Защо го прави?</h2>
	 * <ul>
	 *   <li>Автоматизира събирането на актуална информация за тото тегления</li>
	 *   <li>Позволява централизирана обработка на тегленията от различни източници</li>
	 *   <li>Осигурява възможност за анализ, визуализация и съхранение</li>
	 * </ul>
	 *
	 * <h2>Използвани библиотеки:</h2>
	 * <ul>
	 *   <li>{@code Jsoup} – за извличане на HTML елементи</li>
	 *   <li>{@code java.util.Scanner}, {@code java.io.File} – за работа с файлове</li>
	 *   <li>{@code java.time.LocalDate}, {@code DateTimeFormatter} – за манипулация на дати</li>
	 * </ul>
	 *
	 * <h2>Пример за употреба:</h2>
	 * <pre>{@code
	 * List<String> newResults = ParseURL.parseBgTotoURL();
	 * List<String> lastDraw = ParseURL.parseOfficialURL();
	 * List<String> archive = ParseURL.readDB("/path/to/db.txt");
	 * }</pre>
	 *
	 * @author bNN
	 */
	static List<String> parseBgTotoURL() {
		List<String> res = new ArrayList<>();
		String prevDrawTmp = readDB("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt").getLast();
		String prevDrawIdx = prevDrawTmp.split("-")[0];
		String prevDrawNums = prevDrawTmp.split("-")[2].replace(" ", "");
		String prevDraw = prevDrawIdx + "-" + prevDrawNums;

		try {
			Document page = fetchBgTotoArchiveByYear(2026);
//			Document page = Jsoup.connect("https://bgtoto.com/6ot49_arhiv.php").get();
			Elements table = page.select("table");
			Elements cols = table.get(table.size() - 3).select("tr");

			boolean skipFirst = true;
			for (Element el : cols) {
				if (skipFirst) {
					skipFirst = false;
					continue;
				}
				StringBuilder stb = new StringBuilder();
				if (res.size() > cols.size()) break;
				Elements row = el.select("td");
				stb.append(row.get(row.size() - 3).text());
				stb.append("-").append(row.get(row.size() - 2).text());
				res.add(stb.toString());
			}
		} catch (Exception e) {
			System.err.println("❌ Грешка: Неуспешно свързване със сайта: https://bgtoto.com");
			//res.add("❌ Грешка: Неуспешно свързване със сайта!");
			res.add(prevDraw);
		}
		if (res.isEmpty()) {
			res.add(prevDraw);
		}
		return res;
	}

	/**
	 * Извлича информация за последния проведен 6/49 тираж от официалния сайт на Българския спортен тотализатор
	 * и връща форматиран ред с информацията за него: {@code <номер>-<дата>-<числа>}.
	 *
	 * <li>При грешка в свързването със сайта връща предишният записан резултат</li>
	 *
	 * <p>Методът използва <b>Jsoup</b> за парсване на HTML съдържание от:
	 * <a href="https://toto.bg/index.php?lang=1&pid=playerhistory">toto.bg/playerhistory</a>
	 *
	 * <p>Процесът включва:
	 * <ol>
	 *   <li>Симулира браузър с User-Agent и timeout за стабилност</li>
	 *   <li>Намира номера на тиража и датата (от <code>div.tiraj</code>)</li>
	 *   <li>Намира изтеглените числа (от <code>span.ball-white</code>)</li>
	 *   <li>Форматира датата от {@code dd.MM.yyyy} към {@code dd MMM yyyy} (пример: "01.02.2025" → "01 Feb 2025")</li>
	 *   <li>Съставя низ във формат:
	 *       <pre>{@code
	 *       1-02 Jan 2025-3, 10, 15, 23, 36, 41
	 *       }</pre>
	 *   </li>
	 *   <li>Добавя този ред в списъка</li>
	 * </ol>
	 *
	 * <p>res.add(Предишното теглене);</p>
	 *
	 * <p>Ако не може да осъществи връзка (напр. сайтът е недостъпен), се добавя съобщение за грешка:
	 * <pre>
	 * Грешка: Невъзможно свързване със сайта.
	 * </pre>
	 *
	 * @return списък с един или повече реда във формат {@code <номер>-<дата>-<числа>}, или съобщение за грешка
	 */
	static List<String> parseOfficialURL0() {
		List<String> res = new ArrayList<>();
		String prevDraw = readDB("/Users/blagojnikolov/Desktop/@tmp/fromSite.txt").getLast();
		// 1-02 Jan 2025-3, 16, 23, 36, 41, 49
		try {
			//https://info.toto.bg/
			//https://toto.bg/index.php?lang=1&pid=playerhistory
			Document page = Jsoup.connect("https://www.toto.bg")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36")
					.timeout(10000)
					.get();
			Elements draws = page.select("span.ball-white");
			Elements dates = page.select("div.tiraj");

			String dateTmp = dates.getFirst().text().trim();
			String inputDay = dateTmp.split("-")[1].trim();
			String drawIdx = dateTmp.split("-")[0].trim().split(" ")[1].trim();

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
			LocalDate localDate = LocalDate.parse(inputDay, inputFormatter);
			String date = localDate.format(outputFormatter);

			int cnt = 0;
			StringBuilder stb = new StringBuilder();
			stb.append(drawIdx)
					.append("-")
					.append(date)
					.append("-");
			for (Element num : draws) {
				if (cnt < 5) {
					stb.append(num.text()).append(", ");
				} else {
					stb.append(num.text());
				}
				cnt++;
				if (cnt % 6 == 0) break;
			}
			//System.out.println(stb);
			res.add(stb.toString());
		} catch (IOException e) {
			// 🔥 Всякакви други I/O грешки (например 404, DNS проблеми)
			System.err.println("❌ Грешка: Неуспешно свързване със сайта: https://toto.bg");
			//res.add("❌ Грешка: Неуспешно свързване със сайта!");
			res.add(prevDraw);
		}

		return res;
	}


	static List<String> parseOfficialURL() {
		final String fromFilePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
		final String prevDraw = readDB(fromFilePath).getLast();

		final String url = "https://info.toto.bg/results/6x49";
		final String ua  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

		try {
			Document page = Jsoup.connect(url)
					.userAgent(ua)
					.referrer("https://www.google.com/")
					.header("Accept-Language", "bg-BG,bg;q=0.9,en-US;q=0.8,en;q=0.7")
					.timeout(15_000)
					.get();

			if (isCaptchaPage(page)) {
				System.err.println("✗ CAPTCHA от " + url + " (title=" + page.title() + ")");
				return List.of(prevDraw);
			}

			Elements dates = page.select("div.tiraj");
			Elements draws = page.select("span.ball-white");

			if (dates.isEmpty() || draws.size() < 6) {
				System.err.println("✗ Липсват очакваните елементи в " + url);
				return List.of(prevDraw);
			}

			// "Тираж 11 - 12.02.2026"
			String dateTmp = dates.first().text().trim();
			String inputDay = dateTmp.split("-")[1].trim();
			String drawIdx  = dateTmp.split("-")[0].trim().split(" ")[1].trim();

			DateTimeFormatter inF  = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter outF = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
			String date = LocalDate.parse(inputDay, inF).format(outF);

			StringBuilder sb = new StringBuilder();
			sb.append(drawIdx).append("-").append(date).append("-");

			for (int i = 0; i < 6; i++) {
				if (i > 0) sb.append(", ");
				sb.append(draws.get(i).text());
			}

			return List.of(sb.toString());

		} catch (IOException e) {
			System.err.println("✗ I/O към " + url);
			return List.of(prevDraw);
		}
	}

	private static boolean isCaptchaPage(Document page) {
		String title = page.title();
		if (title != null && title.toLowerCase(Locale.ROOT).contains("captcha")) return true;

		String html = page.outerHtml();
		return html.contains("Radware Page Verifying your browser")
				|| html.contains("Radware Captcha Page")
				|| html.contains("captcha.perfdrive.com")
				|| html.contains("cdn.perfdrive.com");
	}




	private static int[] findFirstValidSix(String text) {
		Matcher m = Pattern.compile("\\b(\\d{1,2})\\b").matcher(text);

		ArrayDeque<Integer> win = new ArrayDeque<>(6);
		while (m.find()) {
			int v = Integer.parseInt(m.group(1));
			if (v < 1 || v > 49) {
				win.clear();
				continue;
			}
			win.addLast(v);
			if (win.size() > 6) win.removeFirst();

			if (win.size() == 6) {
				HashSet<Integer> set = new HashSet<>(win);
				if (set.size() == 6) {
					int[] out = new int[6];
					int i = 0;
					for (int x : win) out[i++] = x;
					return out;
				}
			}
		}
		throw new IllegalStateException("Не са намерени 6 валидни числа (1..49).");
	}


	static List<String> parseOfficialURL1() {
		final String fromFilePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";

		// Предишна стойност (резервен изход), ако сайтът върне неочаквано съдържание
		final String prevDraw = readDB(fromFilePath).getLast();

		final List<String> res = new ArrayList<>();

		try {
			// 1) Вземаме отговор + парсваме HTML
			Connection.Response resp = Jsoup.connect("https://info.toto.bg/")
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
					.referrer("https://www.google.com/")
					.timeout(10_000)
					.followRedirects(true)
					.execute();

			Document page = resp.parse();

			// 2) Извличане на текст (тук работим с реалното съдържание, без да разчитаме на несъществуващи класове)
			final String allText = page.text();

			// 3) Търсим "Тираж N - dd.MM.yyyy"
			//    Примерен шаблон: "Тираж 11 - 12.02.2026"
			Pattern tirajPattern = Pattern.compile("\\bТираж\\s+(\\d+)\\s*-\\s*(\\d{2}\\.\\d{2}\\.\\d{4})\\b");
			Matcher tirajMatcher = tirajPattern.matcher(allText);

			if (!tirajMatcher.find()) {
				// Диагностика (кратко) + резервен изход
				System.err.println("❌ Грешка: Не е намерен ред 'Тираж N - dd.MM.yyyy' в върнатия текст.");
				System.err.println("  HTTP код: " + resp.statusCode());
				res.add(prevDraw);
				return res;
			}

			String drawIdx = tirajMatcher.group(1);
			String inputDay = tirajMatcher.group(2);

			// 4) Форматиране на датата (ако ти трябва точно този формат)
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

			LocalDate localDate;
			try {
				localDate = LocalDate.parse(inputDay, inputFormatter);
			} catch (DateTimeParseException ex) {
				System.err.println("❌ Грешка: Невалидна дата: " + inputDay);
				res.add(prevDraw);
				return res;
			}

			String date = localDate.format(outputFormatter);

			// 5) Числата: взимаме първата шестица числа СЛЕД намерения "Тираж ..."
			//    Така избягваме да хванем произволни числа отгоре по страницата.
			int start = tirajMatcher.end();
			String tail = allText.substring(start);

			Pattern sixNumsPattern = Pattern.compile("\\b(\\d{1,2})(?:\\s+(\\d{1,2})){5}\\b");
			Matcher numsMatcher = sixNumsPattern.matcher(tail);

			if (!numsMatcher.find()) {
				System.err.println("❌ Грешка: Не са намерени 6 числа след реда за тиража.");
				res.add(prevDraw);
				return res;
			}

			// Вземаме целия съвпаднал блок (напр. "1 2 3 4 5 6") и нормализираме интервалите
			String numsBlock = numsMatcher.group(0).trim().replaceAll("\\s+", " ");

			// 6) Сглобяване на изхода (същата структура, която си ползвал)
			//    drawIdx-date-числа
			StringBuilder stb = new StringBuilder();
			stb.append(drawIdx).append("-")
					.append(date).append("-")
					.append(numsBlock);

			res.add(stb.toString());
			return res;

		} catch (IOException e) {
			// Грешки при връзка (включително 403/404/време за изчакване/DNS и т.н.)
			System.err.println("❌ Грешка: Неуспешно свързване със сайта: https://info.toto.bg/");
			res.add(prevDraw);
			return res;
		} catch (RuntimeException e) {
			// За да не пада програмата при неочаквано съдържание
			System.err.println("❌ Грешка: Неочакван формат на страницата: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			res.add(prevDraw);
			return res;
		}
	}



	/**
	 * Извлича резултати от архива на сайта <a href="https://www.toto49.com/arhiv/toto_49/2025">toto49.com</a>
	 * за всички тегления на играта 6/49 за дадената година (2025).
	 *
	 * <li>При грешка в свързването със сайта връща предишният записан резултат</li>
	 *
	 * <p>Методът извършва следното:
	 * <ol>
	 *   <li>Извършва HTTP заявка чрез библиотеката Jsoup към страницата с архиви</li>
	 *   <li>Парсва HTML таблицата, съдържаща резултатите</li>
	 *   <li>Игнорира заглавния ред и редове с недостатъчно колони</li>
	 *   <li>Пропуска последните 2 колони със служебни стойности (обикновено "*")</li>
	 *   <li>Форматира валидните редове като стрингове в следния формат:
	 *       <pre>1-02 Jan 2025-3,16,23,36,41,49</pre>
	 *   </li>
	 * </ol>
	 *
	 * <p>HTML структура на таблицата (опростено):
	 * <pre>{@code
	 *   <tr>
	 *     <td>1</td>
	 *     <td>02 Jan 2025</td>
	 *     <td>3</td>
	 *     <td>16</td>
	 *     <td>23</td>
	 *     <td>36</td>
	 *     <td>41</td>
	 *     <td>49</td>
	 *     <td>*</td>
	 *     <td>*</td>
	 *   </tr>
	 * }</pre>
	 *
	 * <p>res.add(Предишното теглене);</p>
	 *
	 * <p>Примерен резултат:
	 * <pre>1-02 Jan 2025-3,16,23,36,41,49</pre>
	 *
	 * <p>Ако сайтът е недостъпен или възникне грешка, методът:
	 * <ul>
	 *   <li>Извежда съобщение за грешка в конзолата</li>
	 *   <li>Връща празен списък</li>
	 * </ul>
	 *
	 * @return Списък от форматирани редове (номер-дата-резултати) за всяко теглене
	 */
	static protected List<String> parseToto49URL() {
		String URL = "https://www.toto49.com/arhiv/toto_49/2026";
		List<String> res = new ArrayList<>();
		String prevDraw = readDB("/Users/blagojnikolov/Desktop/@tmp/fromSite.txt").getLast();
		try {
			Document doc = Jsoup.connect(URL).get();
			Elements rows = doc.select("tr");
			StringBuilder stb;

			if (rows.isEmpty()) {
				System.out.println("↪︎404 - getRawDataFormSite(): " +
						"Няма данни в сайта...");
				return res;
			} else {
				// Пропуска първия ред (заглавния)
				for (int j = 1; j < rows.size(); j++) {
					Elements row = rows.get(j).select("td");
					String x = row.text();

					// Проверка дали редът има достатъчно клетки
					if (row.size() < 3) {
						System.out.println("↪︎Ред: " + j + " е пропуснат. Липсват данни...!");
						continue;
					}
					// x = 1 02 Jan 2025 3, 16, 23, 36, 41, 49 * *
					// Пропуска последните 2 колони(*)
					stb = new StringBuilder();
					for (int i = 0; i < row.size() - 2; i++) {       // 1
						String el = row.get(i).text().trim();        // 02Jan2025
						if (i < row.size() - 3) {                    // 3,16,23,36,41,49
							stb.append(el).append("-");
						} else stb.append(el);
					}

					String tmpStr = stb.toString().replace("-", "   ");
					Write.write(tmpStr,
							"/Users/blagojnikolov/Desktop/@tmp/fromSite49.txt", true);
					res.add(stb.toString());                         // 1-02 Jan 2025-3, 16, 23, 36, 41, 49
				}
			}
		} catch (IOException ex) {
			System.err.println("❌ Грешка: Неуспешно свързване със сайта: https://toto49.com");
			//rawData.add("❌ Грешка: Неуспешно свързване със сайта!");
			res.add(prevDraw);
		}
		return res;
	}

	/**
	 * Чете текстов файл от подадения път и връща списък от редове, като всеки ред се
	 * обработва чрез замяна на интервалите с тире ("-").
	 *
	 * <p>Методът е подходящ за импортиране на бази данни или лотарийни редове,
	 * където интервалите трябва да бъдат нормализирани.
	 *
	 * <p>Пример:
	 * <pre>
	 * Входен ред: "31   17 Apr 2025   3, 12, 19, 23, 26, 49"
	 * Изход:      "31-17 Apr 2025-3, 12, 19, 23, 26, 49"
	 * </pre>
	 *
	 * @param fromFilePath пълен или относителен път до текстовия файл
	 * @return списък от обработени редове от файла
	 */
	static List<String> readDB(String fromFilePath) {
		List<String> res = new ArrayList<>();
		try {
			File fromFile = new File(fromFilePath);
			Scanner reader = new Scanner(fromFile);

			while (reader.hasNext()) {
				String x = reader.nextLine();
				if (!x.isEmpty()) {
					String tmp = x.replace("   ", "-");
					res.add(tmp);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("↪︎404: write():" +
					"Грешка при четенето от в файла \"BetDirection.txt\"");
		}
		return res;
	}

	/**
	 * Чете съдържанието на подаден {@link File} обект и връща списък от редовете му
	 * без никакви модификации.
	 *
	 * <p>Методът е подходящ, когато се иска просто зареждане на съдържанието на файл,
	 * без да се пипа формата му.
	 *
	 * <p>В случай на грешка при отваряне на файла, извежда съобщение в конзолата.
	 *
	 * @param file {@code File} обект, представящ файла за четене
	 * @return списък от редовете във файла като {@code List<String>}
	 */
	static List<String> readFromFile(File file) {
		List<String> lines = new ArrayList<>();
		try {
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println("↪︎404: readFromFile(): " +
					" Не мога да прочета " + file.getName());
		}
		return lines;
	}

	static List<Double> readArData(String path) {
		List<Double> xValues = new ArrayList<>();
		List<Double> yValues = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(path))) {

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				int idx = line.indexOf("  ");
				String tmp = line.substring(0, idx);
				double num = Double.parseDouble(tmp);

				yValues.add(num);

			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return yValues;
	}
}
