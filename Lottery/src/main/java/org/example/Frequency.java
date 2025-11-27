package org.example;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Клас {@code Frequency} служи за статистически анализ на честотата на срещане на числа в тегленията на тото 6/49.
 * Той позволява групиране, филтриране и извеждане на информация за определени дни от седмицата,
 * както и съпоставяне на броя съвпадения между тиражи.
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *     <li>Извежда колко често се среща всяко число във всички тегления</li>
 *     <li>Филтрира тиражите по ден от седмицата (например само четвъртък)</li>
 *     <li>Групира тиражите според броя на съвпаденията</li>
 *     <li>Извежда честота на съвпадения във вид на таблица</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * Класът използва:
 * <ul>
 *     <li>{@code ParseURL.readDB()} – за зареждане на тегления от файл</li>
 *     <li>{@code Manipulate.extractData()} – за разделяне на редове с данни</li>
 *     <li>{@code Manipulate.strToListOfInt()} – за конвертиране на низове в списъци от цели числа</li>
 *     <li>{@code Date.countDrawOnDay()} – брои тиражи по ден от седмицата</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * <ul>
 *     <li>За анализ на повтарящи се числа в тото тегленията</li>
 *     <li>За намиране на най-често срещани числа</li>
 *     <li>За проверка на вероятностите на съвпадения в избрани дни</li>
 *     <li>За проверка на статистическата значимост на определени тиражи</li>
 * </ul>
 *
 * <h2>Ключови методи:</h2>
 * <ul>
 *     <li>{@code countFrequencyOfNumbers()} – създава карта с честоти на всяко число</li>
 *     <li>{@code filteringDrawsByWeekday(String dayOfWeek, boolean print)} – филтрира тегления по ден от седмицата</li>
 *     <li>{@code countMatches(List<int[]> data)} – пресмята съвпадения между тиражи</li>
 *     <li>{@code ofMatching()} – групира тиражите според броя съвпадения</li>
 *     <li>{@code #ofMatchesForDayOfWeek(String)} – анализира честота на срещания по ден</li>
 * </ul>
 *
 * <h2>Входни данни:</h2>
 * Данните се зареждат от файл `bgTotoFromSite.txt`, като всеки ред съдържа номерата на тираж.
 *
 * <h2>Изходни резултати:</h2>
 * <ul>
 *     <li>Извеждане в конзолата (чрез {@code System.out.print})</li>
 *     <li>Възможност за принтиране на анализ за различни дни (четвъртък, неделя и др.)</li>
 * </ul>
 *
 * @author bNN
 * @version 1.0
 */
public class Frequency {
	public static void main(String[] args) {
//		Frequency.ofMatchesForDayOfWeek("thursday");
//		System.out.println();
//		List<int[]> data = filteringDrawsByWeekday("thursday", true);
//		for (Map.Entry<Integer, Integer> el : mapFindFrequency(data).entrySet()) {
//			System.out.println(el.getKey() + "  -  " + el.getValue());
//		}

//		for (Map.Entry<Integer, List<Integer>> el : ofMatching("thursday", true).entrySet()) {
//			System.out.println(el.getKey() + "  -  " + el.getValue());
//		}
//		for (Map.Entry<Integer, List<Integer>> el : ofMatching("sunday", true).entrySet()) {
//			System.out.println(el.getKey() + "  -  " + el.getValue());
//		}
		//System.out.println(countMatches(data));

		printFrequencyStatistic(4);
	}

	/**
	 * Създава карта, която групира индексите на тегленията по брой съвпадения.
	 *
	 * <p>Всеки ключ в резултата е брой съвпадения (от 0 до {@code max}),
	 * а стойността е списък от числата с брой срещания = key в изтеглените до сега тиражи .</p>
	 *
	 * <p>Използва се подаденият ден от седмицата {@code today}, за да се филтрират тегленията,
	 * след което се броят съвпаденията за всяко теглене.</p>
	 * <p>
	 * Пример:
	 * <pre>{@code
	 *   {
	 *     0 = [2, 8],
	 *     1 = [1, 4, 9],
	 *     2 = [3, 6, 7],
	 *     3 = [5]
	 *   }
	 * }</pre>
	 *
	 * @param today ден от седмицата, като текст (напр. {@code "thursday"})
	 * @return {@code Map<Integer, List<Integer>>}, където ключът е брой съвпадения, а стойността е списък от индекси на тегления
	 */
	static Map<Integer, List<Integer>> ofMatching(String today, boolean printDay) {
		// data = Всички тегления от началото на годината до днес за даден ден (НЕДЕЛЯ, ЧЕТВЪРТЪК)
		List<int[]> data = filteringDrawsByWeekday(today, printDay);
		// Броя на срещанията на всяко число от 1 до 49 в data.
		// На idx 0 са срещанията на числото 1, на idx 1 са срещанията на числото 2 и т. н.
		List<Integer> frequency = countMatches(data);

		Map<Integer, List<Integer>> frequencyCount = new TreeMap<>();  // честота - брой
		int max = Manipulate.findMax(frequency);
		for (int i = 0; i <= max; i++) frequencyCount.put(i, new ArrayList<>());
		// число: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ...  =  e на j + 1 позиция
		//  freq: 0, 6, 2, 2, 2, 2, 2, 5, 2, 6 ...   = j
		for (int i = 0; i <= frequencyCount.size(); i++) {
			for (int j = 0; j < frequency.size(); j++) {
				// Броя срещания на числото 1 == 0?, броя срещания на числото 2 == 0? и т.н
				if (frequency.get(j) == i) {
					// Числото j е на индекс j + 1.
					frequencyCount.get(i).add(j + 1);
				}
			}
		}
		return frequencyCount;
	}

	/**
	 * Изчислява колко пъти се среща всяко число от 1 до 49 включително
	 * в списък от всички тегления до текущият момент и връща списък с тези честоти.
	 *
	 * <p> Срещания: 0, 6, 2, 2, 2, 2, 2, 5, 2, 6,.......</p>
	 * <p> Число:    1, 2, 3, 4, 5, 6, 7, 8, 9, 10,..... </p>
	 * <p>Ако дадено число не се среща нито веднъж, в резултата ще фигурира с честота 0.</p>
	 *
	 * @param data списък от тегления, където всяко теглене е масив от цели числа (например 6 числа от тотото)
	 * @return списък с 49 елемента, където всеки елемент на индекс i представя броя на срещанията на числото (i + 1)
	 *
	 * <p>Пример:</p>
	 * <pre>{@code
	 * List<int[]> draws = List.of(
	 *     new int[]{1, 5, 7},
	 *     new int[]{1, 7, 9}
	 * );
	 * List<Integer> frequencies = matchesCount(draws);
	 * System.out.println(frequencies.get(0)); // => 2 (числото 1 се среща 2 пъти)
	 * System.out.println(frequencies.get(6)); // => 2 (числото 7 се среща 2 пъти)
	 * System.out.println(frequencies.get(48)); // => 0 (числото 49 се среща 0 пъти)
	 * }</pre>
	 */
	static List<Integer> countMatches(List<int[]> data) {
		// 1-1, 2-6, 3-2, 4-2, 5-2 ....
		Map<Integer, Integer> digitFrequency = mapFindFrequency(data);
		List<Integer> matches = new ArrayList<>();

		for (int i = 1; i < 50; i++) {                      // За всяко число i:
			if (digitFrequency.get(i) == null) {            // Числото не е било изтегляно:
				digitFrequency.put(i, 0);
			}
			// число: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ...  = i + 1
			//  freq: 0, 6, 2, 2, 2, 2, 2, 5, 2, 6 ...   = i
			//  Метода е add() без idx - добавя в края на списъка:
			//  На idx = 0 в matches постави срещанията на числото 1 и т. н
			matches.add(digitFrequency.get(i));
		}
		return matches;
	}

	/**
	 * Изчислява честотата на срещане на всяко от числата от 1 до 49
	 * в тегленията, извършени в конкретен ден от седмицата.
	 *
	 * <p>Методът филтрира тегленията според деня {@code dayOfWeek},
	 * след което за всяко число от 1 до 49 брои колко пъти се среща в тези тегления.
	 * Резултатите се извеждат в табличен формат чрез {@code printFrequencyOfMatch()}.</p>
	 *
	 * <p>Пример за изход (за число 1, срещано 14 пъти):</p>
	 * <pre>
	 * 1  - 0   2  - 6   3  - 2   4  - 2   5  - 2   6  - 2   7  - 2   8  - 5   9  - 2   10 - 6
	 * 11 - 5   12 - 6   13 - 5   14 - 0   15 - 2   16 - 3   17 - 3   18 - 5   19 - 3   20 - 3
	 * 21 - 0   22 - 9   23 - 7   24 - 3   25 - 2   26 - 6   27 - 4   28 - 2   29 - 5   30 - 1
	 * 31 - 6   32 - 0   33 - 5   34 - 4   35 - 5   36 - 5   37 - 8   38 - 3   39 - 6   40 - 2
	 * 41 - 5   42 - 6   43 - 5   44 - 5   45 - 2   46 - 5   47 - 5   48 - 2   49 - 4
	 * </pre>
	 *
	 * @param dayOfWeek ден от седмицата (например {@code "thursday"})
	 * @see Frequency#printFrequencyOfMatch(int, int)
	 * @see Frequency#filteringDrawsByWeekday(String, boolean)
	 * @see Manipulate#bs(int[], int)
	 */
	static void ofMatchesForDayOfWeek(String dayOfWeek) {
		System.out.println();
		List<int[]> data = filteringDrawsByWeekday(dayOfWeek, true);
		for (int i = 1; i <= 49; i++) {
			int digitCnt = 0;
			for (int[] arr : data) {
				if (Manipulate.bs(arr, i) != -1) {
					digitCnt++;
				}
			}
			printFrequencyOfMatch(i, digitCnt);
		}
		System.out.println();
	}

	/**
	 * Извежда на екрана честота на съвпадение във форматирана таблица.
	 * <p>
	 * Изходът е подравнен спрямо номера на съвпаденията (i), като за едноцифрените
	 * се добавя интервал за визуално изравняване. След всеки десети елемент
	 * се започва нов ред, което оформя таблица с 10 стойности на ред.
	 * Формат: <br>
	 * {@code i - digitCnt}
	 * <p>
	 * Примерен ред:
	 * <pre>
	 * 1 - 0   2 - 5   3 - 12  ... 10 - 8
	 * </pre>
	 *
	 * @param i        Броят на съвпаденията (обикновено в интервала 0 до 49)
	 * @param digitCnt Колко пъти се е срещнал този брой съвпадения
	 */
	private static void printFrequencyOfMatch(int i, int digitCnt) {
		String space;
		if (i < 10) {
			space = "  - ";
		} else {
			space = " - ";
		}
		System.out.print(i + space + digitCnt + "   ");
		if (i % 10 == 0) {
			System.out.println();
		}
	}

	/**
	 * Изчислява честотата на срещане на всяко число в списък от цели масиви.
	 * <p>
	 * Методът създава и попълва речник (Map), в който всеки ключ е уникално число,
	 * а стойността му е броят на срещанията му в цялата входна структура.
	 * Данните трябва да бъдат предоставени като списък от цели масиви —
	 * например при входни лотарийни тегления или подобни числови комбинации.
	 * <p>
	 * Пример:
	 * <pre>
	 * Вход: [[5, 6, 7], [5, 7, 8]]
	 * Изход: {5=2, 6=1, 7=2, 8=1}
	 * </pre>
	 * <p>
	 * Използва LinkedHashMap, за да запази реда на първоначалната среща.
	 *
	 * @param data Списък от масиви с цели числа, от които се извлича честотата на срещания
	 */
	static Map<Integer, Integer> mapFindFrequency(List<int[]> data) {
		Map<Integer, Integer> numCounts = new TreeMap<>();
		for (int[] arr : data) {
			int cnt;
			for (int el : arr) {
				if (numCounts.containsKey(el)) {
					cnt = numCounts.get(el);
				} else cnt = 0;
				numCounts.put(el, ++cnt);
			}
		}
		return numCounts;
	}

	/**
	 * Извлича и връща всички тегления от файл, които съвпадат с конкретен ден от седмицата. Това са всички тиражи
	 * включващи конкретният ден от началото на текущата година.

	 * <li>Методът зарежда съдържанието от файла `bgTotoFromSite.txt`, парсва датите от всяко теглене,
	 * и връща само тези редове (тегления), чийто ден съвпада с подадения аргумент `dayOfWeek`.
	 * <p>Метода изцяло разчита, че дните на тиражите в файла ще се редуват в последователността:
	 * ЧЕТВЪРТЪК - НЕДЕЛЯ - ЧЕТВЪРТЪК или НЕДЕЛЯ - ЧЕТВЪРТЪК - НЕДЕЛЯ.</li></p>

	 * <li>Проверява първата дата от архива, за да определи дали да започне итерацията от индекс `0` или `1`,
	 * в зависимост от това дали първата дата попада в желания ден.</li>
	 *
	 * <b>Пример:</b>
	 * <pre>
	 * Вход: "THURSDAY"
	 * Връща: само тези редове от архива, чийто ден от седмицата е четвъртък.
	 * </pre>
	 *
	 * @param dayOfWeek Ден от седмицата като текст, напр. "THURSDAY", "SUNDAY". Без значение от малки/главни букви.
	 * @return Списък от масиви (int[]), съдържащ само тегленията, които са се случили в посочения ден.
	 */
	static List<int[]> filteringDrawsByWeekday(String dayOfWeek, boolean printDay) {
		DayOfWeek thisDay = DayOfWeek.valueOf(dayOfWeek.toUpperCase(Locale.ROOT));

		String path = "/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt";
		List<String> data = ParseURL.readFromFile(new File(path));
		String firstDateInArch = data.getFirst().split(" {3}")[1].trim();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
		LocalDate firstDrawDate = LocalDate.parse(firstDateInArch, formatter);

		List<int[]> draws = new ArrayList<>();
		if (printDay) {
			String dayNameInBulgarian = thisDay.getDisplayName(TextStyle.FULL, new Locale("bg"));
			String dayNameInGB = thisDay.getDisplayName(TextStyle.FULL, new Locale("en"));

			Map<String, Integer> dayDrawCnt = Date.countDrawDay(dayNameInGB);
			System.out.println
					(dayNameInBulgarian.toUpperCase() + " - " + dayDrawCnt.get(dayNameInGB) + " тиража" + ": ");
		}

		if (firstDrawDate.getDayOfWeek() == thisDay) {
			for (int i = 0; i < data.size(); i++) {
				String tmpDraw = Manipulate.extractData(data.get(i), "   ");
				int[] res = Manipulate.strToListOfInt(tmpDraw).stream().mapToInt(Integer::intValue).toArray();
				draws.add(res);
			}
		} else {
			for (int i = 1; i < data.size(); i += 1) {
				String tmpDraw = Manipulate.extractData(data.get(i), "   ");
				int[] res = Manipulate.strToListOfInt(tmpDraw).stream().mapToInt(Integer::intValue).toArray();
				draws.add(res);
			}
		}
		return draws;
	}

	/**
	 * Преброява колко пъти всяко число се среща във всички тегления.
	 *
	 * <p>Използва двойно вложен цикъл:
	 * <ul>
	 *   <li>Обхожда всеки ред (теглене) от списъка {@code draws}</li>
	 *   <li>След това обхожда всяко число от конкретното теглене</li>
	 *   <li>Ако числото вече съществува в речника {@code numCnt}, увеличава броя му с 1</li>
	 *   <li>Ако не съществува — добавя го с начална стойност 1</li>
	 * </ul>
	 *
	 * <p>Подходът е напълно функционален и подходящ при работа с вложени структури като
	 * {@code List<List<Integer>>}.
	 *
	 * <p>Пример:
	 * <pre>
	 * Вход:
	 * [[5, 12, 34], [12, 23, 34], [5, 34, 41]]
	 *
	 * Изход:
	 * {
	 *   5=2,
	 *   12=2,
	 *   23=1,
	 *   34=3,
	 *   41=1
	 * }
	 * </pre>
	 *
	 * @return Map с честота на срещане на всяко число от всички тегления
	 */
	static Map<Integer, Integer> countFrequencyOfNumbers() {
		List<List<Integer>> draws = Manipulate.parseToIntDrawsFromFile(ParseURL.readDB(
				"/Users/blagojnikolov/Desktop/@tmp/bgToto.txt"));

		Map<Integer, Integer> numCnt = new HashMap<>();
		Integer cnt;
		for (List<Integer> draw : draws) {
			for (Integer integer : draw) {
				if (numCnt.containsKey(integer)) {
					cnt = numCnt.get(integer);
				} else {
					cnt = 0;
				}
				numCnt.put(integer, ++cnt);
			}
		}
		return numCnt;
	}


	// ---------------------------------------------------------------------
	// Друг начин за намиране на броя съвпадения на числата от тиражите:
	// ---------------------------------------------------------------------

	/**
	 * Преброява колко пъти всяко число се среща във всички тегления.
	 *
	 * <p>Използва двойно вложен цикъл:
	 * <ul>
	 *   <li>Обхожда всеки ред (теглене) от списъка {@code draws}</li>
	 *   <li>След това обхожда всяко число от конкретното теглене</li>
	 *   <li>Ако числото вече съществува в речника {@code numCnt}, увеличава броя му с 1</li>
	 *   <li>Ако не съществува — добавя го с начална стойност 1</li>
	 * </ul>
	 *
	 * <p>Подходът е напълно функционален и подходящ при работа с вложени структури като
	 * {@code List<List<Integer>>}.
	 *
	 * <p>Пример:
	 * <pre>
	 * Вход:
	 * [[5, 12, 34], [12, 23, 34], [5, 34, 41]]
	 *
	 * Изход:
	 * {
	 *   5=2,
	 *   12=2,
	 *   23=1,
	 *   34=3,
	 *   41=1
	 * }
	 * </pre>
	 *
	 * @return Map с честота на срещане на всяко число от всички тегления
	 */
	static Map<Integer, Integer> countFrequencyOfNumbersX() {
		List<List<Integer>> draws =
				Frequency.drawsFromThatDay
						(filteringDrawsByWeekday("Sunday", true));

		Map<Integer, Integer> numCnt = new HashMap<>();
		Integer cnt;
		for (List<Integer> draw : draws) {
			for (Integer integer : draw) {
				if (numCnt.containsKey(integer)) {
					cnt = numCnt.get(integer);
				} else {
					cnt = 0;
				}
				numCnt.put(integer, ++cnt);
			}
		}
		return numCnt;
	}

	/**
	 * @param draws Резултата от метода:
	 *                 <p>static List<int[]> filteringDrawsByWeekday(String dayOfWeek, boolean printDay):</p>
	 *              <li>Тиражите от всяко теглене за реперният ден</li>
	 * @return Всички числа от тиражите за даденият ден от началото на текущата година в списък от масиви.
	 */
	static List<List<Integer>> drawsFromThatDay(List<int[]> draws) {
		List<List<Integer>> res = new ArrayList<>();
		for (int i = 0; i < draws.size(); i++) {
			List<Integer> tmp = new ArrayList<>();
			for (int el : draws.get(i)) {
				tmp.add(el);
			}
			res.add(tmp);
		}
		return res;
	}

	/**
	 * Изчислява списък от числа, групирани по броя на срещанията им (честота),
	 * въз основа на предишно изчислена карта с броя на срещанията на всяко число.
	 * <p>
	 * Архитектурна роля:
	 * <ul>
	 *   <li>Методът трансформира представяне от Map<Число, Честота> към Map<Честота, Списък от Числа>.</li>
	 *   <li>Служи като ключова фаза в анализ на повтарящи се стойности (напр. в лотарийни резултати).</li>
	 *   <li>Действа като междинна стъпка между ниско ниво (сурови честоти) и високо ниво (класификация по поведение).</li>
	 * </ul>
	 * <p>
	 * Функционална логика:
	 * <ol>
	 *   <li>Взема входна map от числа и техните честоти чрез {@code countFrequencyOfNumbers()}.</li>
	 *   <li>За всяка честота събира всички числа, които имат точно тази стойност на честота.</li>
	 *   <li>Използва {@code HashMap} за бърз достъп до групи по честота.</li>
	 * </ol>
	 * <p>
	 * Взаимодействие:
	 * <ul>
	 *   <li>Извиква вътрешен метод {@code countFrequencyOfNumbers()}, който връща {@code Map<Integer, Integer>}.</li>
	 *   <li>Резултатът от този метод обикновено се използва при статистически анализ и визуализация на честоти.</li>
	 * </ul>
	 * <p>
	 * Мотивация за използваните структури:
	 * <ul>
	 *   <li>{@code HashMap<Integer, List<Integer>>} – за бърза агрегация на числа с еднаква честота.</li>
	 *   <li>{@code ArrayList<Integer>} – защото се очаква динамичен брой числа за всяка честота и не е необходима сортираност.</li>
	 * </ul>
	 *
	 * @return Map, в която ключът е честотата на срещане, а стойността е списък с всички числа, появяващи се с тази честота.
	 */
	static Map<Integer, List<Integer>> countFrequencies() {
		List<Integer> numbersWithSameFrequency;

		// Броя на срещанията на всяко число от 1 до 49 в data. Map<Брой срещания, число>.
		Map<Integer, List<Integer>> frequencyListOfDigits = new HashMap<>();
		// Число - Брой на срещанията му в тиражите.Map<Брой срещания, List<числата с тези срещания>>.
		Map<Integer, Integer> digitFrequency = countFrequencyOfNumbersX();
		for (Map.Entry<Integer, Integer> entry : digitFrequency.entrySet()) {
			int frequency = entry.getValue();
			if (frequencyListOfDigits.containsKey(frequency)) {
				numbersWithSameFrequency = frequencyListOfDigits.get(frequency);
			} else {
				numbersWithSameFrequency = new ArrayList<>();
			}
			numbersWithSameFrequency.add(entry.getKey());
			frequencyListOfDigits.put(entry.getValue(), numbersWithSameFrequency);
		}
		return frequencyListOfDigits;
	}

	private static void printFrequencyStatistic(int spaces) {
		Map<Integer, List<Integer>> m = countFrequencies();
		for (Map.Entry<Integer, List<Integer>> el : m.entrySet()) {
			System.out.printf("%d срещания за:", el.getKey());
			String str = el.getKey().toString();
			System.out.println(" ".repeat(spaces - str.length()) + el.getValue());
		}
	}
}
