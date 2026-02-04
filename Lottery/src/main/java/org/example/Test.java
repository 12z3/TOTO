package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
//		String textRegEx = "^[^\\d]*$";
//
//		Scanner scanner = new Scanner(System.in);
//		System.out.print("in: ");
//		String text = scanner.nextLine();
//		System.out.println(validatingText(textRegEx, scanner, text));
		//some();

		List<List<Double>> cov = offsetFromAverage();
		cov.forEach(System.out::println);

		//todo: ✅ 11.07.25 - от сайта:
		// 1ви метод: От даден URL извлича стойностите на числата;            List<Integer> getNums(String url)
		// 2ри метод: Извлича масив с URL-и.                                  List<String>  getUrlList(String url);
		// основен метод: От масива с URL-и извлича стойностите на числата.   List<List<Integer>> dataFromUrls(List<String> urls)

//		System.out.println(sqrt(4));
	}

	/**
	 * Валидира потребителски вход, който трябва да съдържа само текст без никакви цифри.
	 * <p>
	 * Методът приема регулярен израз и входна стойност, която валидира срещу този шаблон.
	 * Ако стойността не отговаря, потребителят се подканя да въведе нова, докато не бъде въведен коректен текст.
	 *
	 * <p>Подходящ регулярен израз за текст без цифри:
	 * <pre>{@code ^[a-zA-Zа-яА-ЯёЁ]+$}</pre>
	 * - Позволява само букви (латиница и кирилица)
	 * - Не допуска числа, специални символи или интервали
	 *
	 * @param regEx   Регулярен израз, който описва валиден текст.
	 * @param scanner Обект {@link Scanner} за четене на потребителския вход.
	 * @param text    Начална стойност за валидиране.
	 * @return Стойността, която отговаря на шаблона и е приета за валиден текст.
	 */
	static String validatingText(String regEx, Scanner scanner, String text) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);

		while (!matcher.matches()) {
			System.out.print("Това чудо съдържа цифри... Трябва да бъде само текст:");
			text = scanner.nextLine();
			matcher = pattern.matcher(text);
		}
		return matcher.group();
	}

	//todo: ✅ 6.05.25 - Ако днес търсенето е стартирано поне веднъж да не проверява за нови тиражи.
	// Запиши последната дата на стартиране на програмата и сравнявай с нея.

	static String getLastStartedDay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		LocalDateTime ldt = LocalDateTime.now();
		return ldt.format(formatter);
	}

	static void some() {
		String s = Date.findNextDrawDay();
		LocalDateTime ldt = LocalDateTime.now();
		String d = ldt.getDayOfWeek().plus(1).toString();
		if (s.equals(d)) System.out.println("!");
	}

	/**
	 * Извлича списък от числови стойности, представящи отклонението на всеки елемент
	 * от предварително дефинирана средна стойност, за всеки ред от числова таблица,
	 * прочетена от външен файл.
	 *
	 * <li> Методът има архитектурна роля на **трансформатор**, който приема сурови текстови
	 * данни (редове с числа), преобразува ги в числов формат и изчислява отклонението
	 * на всяко число спрямо референтна стойност (в случая: 24.5). Така получените
	 * отклонения се връщат в двуизмерна структура, подготвена за последващ анализ
	 * или визуализация.</li>
	 *
	 * <li> Основната логика следва триетапна трансформация:
	 * - прочит на редове от файл чрез абстракция (FileProcesses.readFromFile);
	 * - парсване на всеки ред чрез външна помощна логика (Manipulate.parseToDouble);
	 * - изчисляване на числовите отклонения спрямо зададена средна стойност.</li>
	 *
	 * <li> Методът е изцяло **самостоятелен**, без входни параметри, но силно обвързан
	 * с външни зависимости, чието поведение трябва да е стабилно и проверено.</li>
	 *
	 * @return Списък от списъци, където всеки вложен списък съдържа отклоненията на
	 * числата от съответния ред във файла спрямо зададената стойност.
	 * При възникнала грешка при четене/парсване – връща празен списък.
	 */
	static List<List<Double>> offsetFromAverage() {
		double average = 24.5;
		List<String> rawData = FileProcesses
				.readFromFile("/Users/blagojnikolov/Desktop/@tmp/allResults.txt");
		List<List<Double>> data = new ArrayList<>();
		try {
			for (String line : rawData) {
				List<Double> parseRawData = Manipulate.parseToDouble(line);
				data.add(parseRawData);
			}
		} catch (Exception e) {
			System.out.print("ERROR" + e.getMessage());
			return Collections.emptyList();
		}
		List<List<Double>> avrOffsets = new ArrayList<>();
		for (List<Double> list : data) {
			List<Double> offset = new ArrayList<>();
			for (double el : list) {
				double difference = el - average;
				offset.add(difference);
			}
			avrOffsets.add(offset);
		}
		return avrOffsets;
	}

	static double sqrt(double x) {
		double diff = 0.00001;
		double gues = x / 2;

		while (Math.abs((gues * gues) - x) > diff) {
			gues = (gues + (x / gues)) / 2;
		}
		return gues;
	}
}
