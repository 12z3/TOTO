package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class Profitable {
	private String date;
	private String drawNumbers;
	private String std;
	private String mean;
	private String six;
	private String five;
	private String four;
	private String tree;


	//todo: ✅ 13.07.25 - Обмисли класа как да ги имплементираш
	//                     Трябва ти метод който намира кои числа се срещат 1 път, 2 път, 3 пъти и т.н.

	public static void main(String[] args) throws IOException {

		List<Element> urls = profitUrl();
        for (Element el : urls) System.out.println(el.absUrl("href"));
		System.out.println();

		Map<String, String> data = profitableSeries(urls);
        for (Map.Entry<String,String> entry: data.entrySet())
            System.out.println(entry.getKey() + ": " + entry.getValue());

//		printStatisticProfitableInfo(data, 21, 15);
//
//		System.out.println();
//		Map<String, String> res = allProfitInfo(urls);
//		for (Map.Entry<String, String> entry : res.entrySet()) {
//			System.out.println(entry.getKey() + ": " + "\n" + entry.getValue());
//		}
//
//		System.out.println(totalProfitDraw(allProfitInfo(profitUrl())));
//		plotProfitStd();

//		List<Double> std = getStdFromProfitSeries(data);
//		std.forEach(System.out::println);
		Print.profitInfo();
        Print.lastDrawProfitInfo(getLastOfficialProfitInfo());
	}

	/**
	 * Извлича списък с всички налични URL адреси на тиражи от страницата на Тото 6 от 49.
	 *
	 * <li> Методът използва библиотеката Jsoup за свързване към официалната страница на резултатите.
	 * След успешна връзка парсва HTML съдържанието и селектира всички елементи
	 * от навигационното меню, които съдържат линкове към различни тиражи.</li>
	 *
	 * <li>Основна роля:
	 * - Играе ролята на генератор на входни линкове към отделните архивирани тиражи.</li>
	 *
	 * <li>Ваимодействие:
	 * - Предоставя изход, който служи като входен параметър за метода profitableDigits.
	 * - Ако страницата не е достъпна, се отпечатва съобщение за грешка и се връща празен списък.</li>
	 *
	 * @return Списък с HTML елементи (тип Element), съдържащи URL адресите на отделните тиражи.
	 */
	static List<Element> profitUrl() {
		List<Element> profitableURL = new ArrayList<>();
		try {
			Document doc = Jsoup.connect("https://info.toto.bg/results/6x49").get();
			// Намиране на <li class="disabled"> и вътрешните <a> тагове
			Elements urlList = doc.select("ul.dropdown-menu-right li a");
			//profitableURL.add(el.absUrl("href"));
			profitableURL.addAll(urlList);
		} catch (IOException e) {
			System.out.println("Няма достъп до сайта: https://info.toto.bg/results/6x49");
		}
		return profitableURL;
	}

	/**
	 * Извлича печелившите числа от всеки линк към конкретен тираж.
	 *
	 * <li> За всеки подаден HTML елемент от списъка (който съдържа абсолютен линк към страница с резултати),
	 * методът:
	 * - свързва се с тази страница;
	 * - селектира числата от резултатите;
	 * - селектира датата на тиража;
	 * - добавя двойка (дата → числа) към изходната map структура.</li>
	 *
	 * <li> Основна роля:
	 * - Парсва съдържанието на отделните тиражни страници и извлича конкретните резултати от всяка една.</li>
	 *
	 * <li>Взаимодействие:
	 * - Използва резултатите от profitableUrl() за вход.
	 * - Връща Map, която се използва за статистическа обработка или визуализация на данни.</li>
	 *
	 * <li> При грешка:
	 * - Отпечатва съобщение и връща (възможно непълна) структура с вече събраните резултати.
	 *
	 * @param profitableURLList списък с HTML елементи, съдържащи URL адресите на отделните тиражи
	 * @return Сортирана (по дата) map структура, където ключът е датата на тиража, а стойността — печелившите числа.</li>
	 */
	static Map<String, String> profitableSeries(List<Element> profitableURLList) {
		Map<String, String> dateDrow = new TreeMap<>();
		try {
			for (Element url : profitableURLList) {
				String thisUrl = url.absUrl("href");
				Document sampleUrl = Jsoup.connect(thisUrl).get();
				Elements draw = sampleUrl.select("div.col-sm-6.text-right.nopadding span.ball-white");
				Elements date = sampleUrl.select("h2.tir_title");
				dateDrow.put(date.text(), draw.text());
			}
		} catch (IOException e) {
			System.out.println("Няма достъп до сайта: https://info.toto.bg/results/6x49");
			System.out.println("Не мога да извлека печелившите числа");
		}
		return dateDrow;
	}

	/**
	 * Извлича и структурира подробна таблична информация за всеки архивиран тираж от подадените URL адреси.
	 *
	 * <li>Основна роля:
	 * - Методът реализира обобщена агрегация на всички налични числови и текстови данни, публикувани на страниците
	 * на отделните тиражи (напр. печеливши числа, групи, суми, броеве печалби, джакпоти и т.н.).
	 * - Извлича както мета-информация (датата на тиража), така и табличното съдържание под нея.</li>
	 * <p>
	 * Вътрешна логика:
	 * <ol>
	 *   <li>Свързва се с всяка страница по подадения URL (Jsoup).</li>
	 *   <li>Извлича HTML таблица с CSS селектор "table", след това селектира редовете ("tr").</li>
	 *   <li>Пропуска заглавния ред (index 0).</li>
	 *   <li>За всеки ред събира текстовото съдържание на клетките ("td") и го добавя към StringBuilder.</li>
	 *   <li>В края добавя обобщена стойност към map структура с ключ – датата на тиража, и стойност – всички таблици като текст.</li>
	 * </ol>
	 *
	 * <li> Взаимодействие:
	 * - Използва вход, който обикновено е резултат от метода {@code profitableUrl()}.
	 * - Подходящ за по-нататъшна визуализация, експортиране или аналитична обработка.</li>
	 *
	 * <li>При грешка:
	 * - Изхвърля {@code RuntimeException}, ако възникне непредвидена грешка (напр. парсинг, връзка, null елементи).</li>
	 *
	 * @param profitableURLList Списък от {@code Element} обекти, които съдържат абсолютни адреси на тиражни страници.
	 *                          Обикновено тези елементи са генерирани чрез парсване на страницата с архиви на Тото 6 от 49.
	 * @return Сортирана map структура (по дата на тиража), в която:
	 * <ul>
	 *   <li>ключът е текстовото представяне на датата,</li>
	 *   <li>стойността е текстов dump на таблицата от конкретния тираж.</li>
	 * </ul>
	 */
	static Map<String, String> allProfitInfo(List<Element> profitableURLList) {
		StringBuilder stb;
		Map<String, String> dateProfitSeriesInfo = new TreeMap<>();
		try {
			for (Element url : profitableURLList) {
				String thisUrl = url.absUrl("href");
				Document sample = Jsoup.connect(thisUrl).get();
				Elements table = sample.select("table");
				Elements date = sample.select("h2.tir_title");
				Elements cols = table.select("tr");

				stb = new StringBuilder();
				for (int i = 0; i < cols.size(); i++) {
					if (i == 0) continue;
					Elements row = cols.get(i).select("td");
					String profitSeries = row.text();
					stb.append(profitSeries).append("\n");
				}
				stb.append("\n");
				dateProfitSeriesInfo.put(date.text(), stb.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dateProfitSeriesInfo;
	}

	static List<String> totalProfitDraw(Map<String, String> profitableInfo) {
		List<String> profit = new ArrayList<>();
		for (Map.Entry<String, String> entry : profitableInfo.entrySet()) {
			String[] lineData = entry.getValue().split(" ");
			if (!lineData[2].equals("0")) {
				profit.add(entry.getKey());
			}
		}
		return profit;
	}


	/**
	 * Преобразува текстово представени числови редове в списък от цели числа.
	 *
	 * <li>Архитектурна роля:
	 * - Методът служи за трансформация на извлечените данни от тиражи (във вид на String редове)
	 * към машинно обрабатваем вид — списъци от цели числа (Integer).
	 * - Тази трансформация е критична стъпка за всяка форма на статистическа обработка,
	 * например: намиране на стандартно отклонение, честота на срещане, корелация и т.н.</li>
	 * <p>
	 * Логика и поведение:
	 * <ol>
	 *   <li>Методът приема map, в който ключовете обикновено са дати на тиражи, а стойностите са
	 *   списъци от печеливши числа, представени като един ред текст.</li>
	 *   <li>Всеки ред се разделя по интервал и се преобразува към Integer.</li>
	 *   <li>Неуспешни конверсии се хващат чрез {@code NumberFormatException}, като числото се игнорира,
	 *   а грешката се логва в конзолата.</li>
	 *   <li>Всяка редица от цели числа се добавя като отделен списък в общ списък от списъци.</li>
	 * </ol>
	 *
	 * <li>Взаимодействие:
	 * - Използва се като вход в методи, които работят със статистика (напр. изчисления на стандартни отклонения).
	 * - Може да се използва и за извеждане на графика, трендове или други визуализации, базирани на числовите данни.</li>
	 *
	 * <li>Устойчивост:
	 * - Грешки в формата на числата не прекъсват изпълнението — всяка невалидна стойност се пропуска.
	 * - Подходящ за работа с данни от несигурен източник (напр. уеб парсинг), при които има риск от шум в редовете.</li>
	 *
	 * @param profitSeries Map структура, в която:
	 *                     <ul>
	 *                       <li>ключовете са идентификатори (напр. дати или номера на тиражи),</li>
	 *                       <li>стойностите са текстови редове с числа, разделени с интервали.</li>
	 *                     </ul>
	 * @return Списък от списъци с цели числа, където всяка вътрешна колекция съответства на един ред от входа.
	 */
	private static List<List<Integer>> getIntSeries(Map<String, String> profitSeries) {
		List<List<Integer>> intSeries = new ArrayList<>();
		for (Map.Entry<String, String> el : profitSeries.entrySet()) {
			// 16 17 20 23 34 40
			String line = el.getValue().trim();
			String[] tmpLine = line.trim().split(" ");
			List<Integer> tmpInt = new ArrayList<>();

			for (String s : tmpLine) {
				try {
					tmpInt.add(Integer.parseInt(s));
				} catch (NumberFormatException e) {
					System.out.println("404: getStdFromProfitSeries():" +
							" Не мога да парсна числото: " + s);
				}
			}
			intSeries.add(tmpInt);
		}
		return intSeries;
	}

	static List<Double> getStdFromProfitSeries(Map<String, String> profitSeries) {
		List<Double> stds = new ArrayList<>();
		List<List<Integer>> tmpInt = getIntSeries(profitSeries);

		for (List<Integer> series : tmpInt) {
			double thisStd = Std.stdOfRowD(series);
			stds.add(thisStd);
		}
		return stds;
	}

	static List<Double> getMeanFromProfitSeries(Map<String, String> profitSeries) {
		List<Double> means = new ArrayList<>();
		List<List<Integer>> tmpInt = getIntSeries(profitSeries);

		for (List<Integer> series : tmpInt) {
			double thisMean = Std.mean(series);
			means.add(thisMean);
		}
		return means;
	}

	/**
	 * Извежда в конзолата пълна обобщена информация за всяка записана числова серия,
	 * включително стойностите на стандартното отклонение и средната стойност, форматирани и подравнени.
	 *
	 * <li>Архитектурна роля:
	 * - Методът действа като крайна точка за представяне на резултати от статистическа обработка,
	 * комбинирайки данните от входен map с изчислените метрики `std` и `mean`.
	 * - Осигурява ясно и визуално подравнено представяне за по-лесен анализ от потребителя (или системата).</li>
	 * <p>
	 * Функционална логика:
	 * <ul>
	 *   <li>Получава map от данни, като ключът е идентификатор (напр. дата), а стойността — числова серия като текст.</li>
	 *   <li>Съответно за всяка серия се извличат изчислени стойности на стандартно отклонение и средна стойност.</li>
	 *   <li>С помощта на параметрите {@code cntStdSpace} и {@code cntMeanSpace} се изчисляват отстояния
	 *       за подравняване спрямо дължината на текста.</li>
	 *   <li>Всеки ред се отпечатва в конзолата със следната структура:
	 *       <pre>
	 *         ДАТА: СЕРИЯ std: XX.XX    mean: XX.XX
	 *       </pre>
	 *   </li>
	 * </ul>
	 *
	 * <li>Взаимодействие:
	 * - Използва резултатите от методи като {@code getStdFromProfitSeries(...)} и {@code getMeanFromProfitSeries(...)}.
	 * - Методът не връща стойност – служи изцяло за визуализация на обработените данни.</li>
	 * <p>
	 * Параметри:
	 *
	 * @param data         входен map с оригинални серии – дата -> серия (като String)
	 * @param cntStdSpace  задава желаната ширина на колоната за std
	 * @param cntMeanSpace задава желаната ширина на колоната за mean
	 */
	static void printStatisticProfitableInfo(Map<String, String> data, int cntStdSpace, int cntMeanSpace) {
		List<Double> std = getStdFromProfitSeries(data);
		List<Double> mean = getMeanFromProfitSeries(data);
		int idx = 0;

		// (spaceCnt = const - arr.length) <- (spaceCnt + arr.length = const)
		System.out.println("Брой тиражи: " + data.size());
		for (Map.Entry<String, String> entry : data.entrySet()) {
			int spaceCnt = cntStdSpace - entry.getValue().length();
			String stdS = String.format("%.2f", std.get(idx));
			String meanS = String.format("%.2f", mean.get(idx++));

			System.out.println(entry.getKey() + ": " + entry.getValue() + " ".repeat(spaceCnt) +
					"std: " + stdS + " ".repeat(cntMeanSpace - ("std: ".length() + stdS.length())) +
					"mean: " + meanS);
		}
	}

	static List<List<Integer>> extractDigitSeries(Map<String, String> series) {
		List<Integer> tmpSeries;
		List<List<Integer>> digitSeries = new ArrayList<>();

		for (Map.Entry<String, String> line : series.entrySet()) {
			String row = line.getValue();
			tmpSeries = Manipulate.parseToInt(row);
			digitSeries.add(tmpSeries);
		}
		return digitSeries;
	}

	static List<List<Double>> newDataList() {
		Map<String, String> data = profitableSeries(profitUrl());
		List<List<Integer>> list = extractDigitSeries(data);

		List<List<Double>> tmpD = new ArrayList<>();
		List<Double> tmp;
		for (List<Integer> line : list) {
			tmp = new ArrayList<>();
			for (int el : line) {
				tmp.add((Double.parseDouble(String.valueOf(el))));
			}
			tmpD.add(tmp);
		}
		return tmpD;
	}

	static void plotProfitStd() {
		Map<String, String> data = profitableSeries(profitUrl());
		List<Double> std = getStdFromProfitSeries(data);
		List<Double> x = new ArrayList<>();
		for (int i = 0; i < std.size(); i++) {
			x.add(i + 31.0);
		}
		plot(x, std);
	}

	//todo: ✅ 14.07.25 - За всеки един масив извикай plot

	// Фиксира размера на profit графиката:
	static void plot(List<Double> x, List<Double> y) {
		SwingUtilities.invokeLater(() -> {
			ProfitPlot graph = new ProfitPlot(x, y);
			graph.setSize(700, 400);
			graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			graph.setVisible(true);
		});
	}

	static List<String> profitInformation() {
		Map<String, String> profitSeries = profitableSeries(profitUrl());
		Map<String, String> allProfitSeries = allProfitInfo(profitUrl());
		List<Double> std = getStdFromProfitSeries(profitSeries);
		List<Double> mean = getMeanFromProfitSeries(profitSeries);
		List<String> list = new ArrayList<>();
		StringBuilder stb;

		int cnt = 0;
		for (Map.Entry<String, String> entry : profitSeries.entrySet()) {
			stb = new StringBuilder();
			String date = entry.getKey();
			String draw = entry.getValue();

			String stdStr = String.format("%.2f", std.get(cnt));
			String meanStr = String.format("%.2f", mean.get(cnt++));
			stb.append(date)
					.append(" - ")
					.append(draw)
					.append("\n")
					.append("std: ")
					.append(stdStr)
					.append(" ")
					.append("mean: ")
					.append(meanStr)
					.append("\n");
			stb.append(allProfitSeries.get(date));
			list.add(stb.toString());
		}
		return list;
	}

    protected static String getLastOfficialProfitInfo(){
        try {
            String res = profitInformation().getLast();
            return res;
        } catch (Exception e) {
           return "WiFi ERROR.";
        }
    }
}