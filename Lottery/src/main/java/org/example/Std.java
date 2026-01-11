package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Клас {@code Std} е отговорен за изчисляване, прогнозиране и записване на стандартни отклонения (STD)
 * на тото тегления, използвайки различни статистически подходи, включително:
 * <ul>
 *     <li>Обикновена дисперсия (sample STD)</li>
 *     <li>Гаусово изглаждане на стойности (Kalman филтър)</li>
 *     <li>Сравнение между реално и прогнозирано STD</li>
 * </ul>
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *     <li>Чете числови резултати от тото тегления от файл</li>
 *     <li>Изчислява STD за всяко теглене (по ред)</li>
 *     <li>Прогнозира бъдещи стойности на STD чрез Kalman филтър</li>
 *     <li>Записва получените резултати в различни текстови файлове</li>
 *     <li>Форматира и извежда прогнозите в удобен за анализ вид</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * <ul>
 *     <li>Метод {@code calculateAndWriteStd()} управлява целия процес:
 *         <ul>
 *             <li>Чете входен файл с комбинации от числа</li>
 *             <li>Пресмята STD на всяка комбинация чрез {@code calcStdOfRow()}</li>
 *             <li>Прогнозира бъдещо STD чрез {@code predictNextGaussianStd()}</li>
 *             <li>Използва Kalman филтър чрез {@code predictNextKalmanStd()}</li>
 *             <li>Записва резултатите с {@code writeToFile()} и {@code writeMapToFile()}</li>
 *         </ul>
 *     </li>
 *     <li>Методът {@code getListOfSTD()} извлича списък с всички STD от файл</li>
 *     <li>{@code getStdMap()} връща Map, в който всеки ключ е момент от времето, а стойността – STD ред</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * <p>Класът служи за количествен анализ на тото тегления. Целта е:
 * <ul>
 *     <li>Да се прогнозира стойността на бъдещо STD (като индикатор за равномерност/разпръснатост на числата)</li>
 *     <li>Да се провери дали тенденциите на разпределение се запазват</li>
 *     <li>Да се сравняват реални и симулирани данни</li>
 * </ul>
 * </p>
 *
 * <h2>Зависимости и използвани структури:</h2>
 * <ul>
 *     <li>{@code List<Double>} – за съхраняване на поредици от стандартни отклонения</li>
 *     <li>{@code Map<Double, List<Double>>} – за съхранение на времеви STD редове</li>
 *     <li>{@code Scanner}, {@code File}, {@code FileWriter} – за работа с файлове</li>
 * </ul>
 *
 * <h2>Основни методи:</h2>
 * <ul>
 *     <li>{@code calculateAndWriteStd()} – главен метод, извършващ цялата операция</li>
 *     <li>{@code predictNextGaussianStd()} – прогнозиране чрез среднопретеглена оценка с шум</li>
 *     <li>{@code predictNextKalmanStd()} – прогнозиране чрез Kalman филтър</li>
 *     <li>{@code writeMapToFile()} – запис на Map с времеви ред</li>
 *     <li>{@code getListOfSTD()} – извличане на всички STD от файл</li>
 *     <li>{@code calcStdOfRow()} – изчисляване на STD за един ред</li>
 * </ul>
 *
 * @author bNN
 * @version 1.0
 */
public class Std {
	// Пресмята Std на всеки един тираж от results.txt и ги записва в stdResults.txt, sdtAllRes.txt

	public static void main(String[] args) throws IOException {
		Map<Double, List<List<Double>>> res =
				getStdMapNew(new File("/Users/blagojnikolov/Desktop/@tmp/allResults.txt"));

		for (Map.Entry<Double, List<List<Double>>> el : res.entrySet()) {
			System.out.printf("%.3f -> ", el.getKey());
			System.out.println(el.getValue());
		}

	}

	/**
	 * Пресмята Std на бъдещото теглене по метода на Гаус и Калман и го записва във файл.
	 * <p>Не използва "siteData".</p>
	 * <p>Използва:</p>
	 * <p>allResults.txt</p>
	 * <p>stdResults.txt</p>
	 * <p>sdtAllRes.txt</p>
	 *
	 * @throws FileNotFoundException :
	 */
	static void calculateAndWriteStd() throws FileNotFoundException {
		String fromFile = "/Users/blagojnikolov/Desktop/@tmp/allResults.txt";
		String toFile = "/Users/blagojnikolov/Desktop/@tmp/stdResults.txt";
		String toStdFile = "/Users/blagojnikolov/Desktop/@tmp/sdtAllRes.txt";

		Map<Double, List<Double>> res = getStdMap(new File(fromFile));
		//printRes(new File(fromFile), res);
		writeMapToFile(new File(toFile), res);

		List<Double> stdS = getListOfStd(new File(fromFile));
		//printStdMatches(stdS);
		writeListToFile(new File(toStdFile), stdS);
		System.out.println("Std prediction:");
		System.out.printf("Gaussian: %.2f%n", predictNextGaussianStd(stdS));
		predictNextKalmanStd(stdS);
        System.out.print("AR(p): " + " ".repeat( 10 - "AR(p): ".length()));
//        System.out.printf("%s%n", ARNextPoint.arPrediction());
        ARNextPoint.prediction(3);
	}

	/**
	 * Генерира ново предсказано стандартно отклонение на база нормално разпределение (Гаусово).
	 * <p>
	 * Методът използва стойностите от подадения списък {@code stdRes}, за да изчисли средната стойност (mean)
	 * и стандартното отклонение (standard deviation). След това създава случайна стойност от Гаусово разпределение
	 * (нормално разпределение със средна стойност 0 и отклонение 1) и я мащабира с изчисленото отклонение и средна стойност:
	 * <pre>
	 *     result = (randomGaussian * std) + mean
	 * </pre>
	 * Това е често използвана формула за получаване на реалистични стойности, които статистически
	 * се "вписват" в разпределението на подадените данни.
	 *
	 * @param stdRes Списък от стойности (стандартни отклонения), от които се изчисляват средната и отклонението.
	 * @return Нова стойност, която попада в нормално разпределение спрямо подадените данни.
	 * @see java.util.Random#nextGaussian()
	 * @see #mean(List)
	 * @see #stdOfRowD(List)
	 */
	static double predictNextGaussianStd(List<Double> stdRes) {
		double std = stdOfRowD(stdRes);
		double mean = mean(stdRes);
		Random rnd = new Random();
		double randomFactor = rnd.nextGaussian();
		return (randomFactor * std) + mean;
	}

	/**
	 * <p>Имплементира логиката на Филтъра на Калман.</p>
	 * <p>Предсказва бъдещата стойност на STD на бъдещият тираж на базата на STD стойностите на тиражите до сега.</p>
	 *
	 * @param allStd Масива с STD на всички тиражи до сега.
	 * @return Бъдещата (Калманова) STD стойност на тиража.
	 */
	public static double predictNextKalmanStd(List<Double> allStd) {
		double processNoise = 0.01;                // Q - Процесен шум
		double measurementNoise = 0.1;             // R - Шум на измерване
		double estimatedError = 1;                 // P - Начална грешка на оценка
		double kalmanGain = 0;                     // K - Калманов коефициент
		double estimate = allStd.get(0);     // Начална оценка


		for (int i = 1; i < allStd.size(); i++) {
			// Калкулация на Калмановия коефициент
			kalmanGain = estimatedError / (estimatedError + measurementNoise);

			// Обновяване на оценката
			estimate = estimate + kalmanGain * (allStd.get(i) - estimate);

			// Обновяване на грешката на оценката
			estimatedError = (1 - kalmanGain) * estimatedError + processNoise;

			//System.out.printf("Измерена стойност: %.4f, Оценена стойност: %.4f%n", allStd.get(i), estimate);
		}
		System.out.printf("Kalman:   %.2f%n", estimate);
		return estimate;
	}

	/**
	 * Записва резултатите от статистическа обработка (например стандартни отклонения по интервали)
	 * във файл във форматиран и подравнен вид.
	 *
	 * <p>Форматът на всеки ред в изходния файл е:
	 * <pre>{@code
	 *  2.00   -> [1, 2, 3, 4, 5, 6]
	 * }</pre>
	 * където:
	 * <ul>
	 *   <li>Ключът (напр. интервал) се форматира с 2 знака след десетичната запетая</li>
	 *   <li>До 7 символа се добавят интервали за подравняване (ако числото е по-късо)</li>
	 *   <li>Следва стрелка " -> ", интервал, и списък от стойности</li>
	 * </ul>
	 *
	 * <p>Примерен запис:
	 * <pre>{@code
	 *  2.00 -> [1, 2, 3, 4, 5, 6]
	 *  10.00 -> [11, 12, 13, 14, 15, 16]
	 * }</pre>
	 *
	 * <p>Ако възникне грешка при запис, методът извежда съобщение в конзолата:
	 * <code>"File is missing Bro ;)"</code>
	 *
	 * @param toFile Файл, в който ще бъдат записани резултатите
	 * @param res    Хеш-таблица (Map), в която:
	 *               <ul>
	 *                 <li>Ключът е интервал (например 5.0, 10.0)</li>
	 *                 <li>Стойността е списък със стойности (напр. стандартни отклонения)</li>
	 *               </ul>
	 */
	private static void writeMapToFile(File toFile, Map<Double, List<Double>> res) {
		try {
			FileWriter writer = new FileWriter(toFile);
			for (Map.Entry<Double, List<Double>> el : res.entrySet()) {
				String key = String.format("%.2f", el.getKey());
				//writer.write("\n");
				writer.write(key);
				// Цялата дължина(const = 7) - дължината на текста = дължината на интервалите.
				// 7 - key.length(); "->" да бъдат с еднакви отстъпи
				for (int i = 0; i < 7 - key.length(); i++) {
					writer.write(" ");
				}
				writer.write(" - > ");
				writer.write("  ");
				writer.write((el.getValue()) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("↪︎File is missing Bro ;)");
		}
	}

	/**
	 * Записва елементите на списък с десетични стойности (Double) във файл, всеки на отделен ред.
	 *
	 * <p>Всяка стойност се форматира с 2 знака след десетичната запетая
	 * и се записва заедно с интервал в края (напр. за по-лесно четене/подравняване).
	 *
	 * <p>Примерен изход във файла:
	 * <pre>{@code
	 * 0.25
	 * 1.00
	 * 3.14
	 * }</pre>
	 *
	 * <p>Ако възникне I/O грешка при запис, извежда съобщение в конзолата:
	 * <code>"File is missing Bro ;)"</code>
	 *
	 * @param toFile Файл, в който ще бъдат записани числата
	 * @param res    Списък с десетични стойности, които ще се запишат във файла
	 */
	private static void writeListToFile(File toFile, List<Double> res) {
		try {
			FileWriter writer = new FileWriter(toFile);
			for (int i = 0; i < res.size(); i++) {
				String key = String.format("%.2f", res.get(i));
				writer.write(key + "   ");
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("File is missing Bro ;)");
		}
	}

	static Map<Double, List<List<Double>>> getStdMapNew(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		Map<Double, List<List<Double>>> res = new LinkedHashMap<>();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) continue;

			String[] arr = line.split("\\s+");
			List<Double> row = strToDouble(arr);
			double std = Statistic.optimizedStdOfRow(row);

			if (!row.isEmpty() && !Double.isNaN(std) && !Double.isInfinite(std)) {
				res.computeIfAbsent(std, k -> new ArrayList<>()).add(row);
			}
		}

		return res;
	}

	/**
	 * Извлича и структурира числови редове от входен файл, като използва изчислено
	 * (и оптимизирано) стандартно отклонение като уникален ключ в Map структура.
	 *
	 * <p>Този метод играе критична роля в процеса на анализ и класификация на числови
	 * редове, като осигурява високоефективен начин за асоцииране на статистически
	 * характеристики към оригиналните данни. Всеки ред от файла се трансформира в
	 * списък от {@code Double} стойности и се индексира по неговото стандартно отклонение.</p>
	 *
	 * <p>Ключова особеност е стратегията за гарантиране на уникалност на ключовете:
	 * при наличие на вече съществуващо стандартно отклонение в Map-а, към него се
	 * добавя малка стойност (1e-9), докато се постигне уникалност. Това избягва
	 * презаписване на стойности при минимални статистически разлики между редове.</p>
	 *
	 * <p>Методът е част от по-широка архитектура за числов анализ, като обикновено се
	 * използва в контексти, където:
	 * <ul>
	 *   <li>се изисква групиране или сортиране на редове според тяхното отклонение,</li>
	 *   <li>се търсят аномалии на база статистическо отклонение,</li>
	 *   <li>или се изграждат визуални или текстови представяния на тенденции в данните.</li>
	 * </ul></p>
	 *
	 * @param file файл, съдържащ числови редове (по един ред на линия), които ще бъдат анализирани
	 * @return Map, в който ключовете са уникални стойности на стандартно отклонение, а стойностите – съответните редове
	 * @throws FileNotFoundException ако указаният файл не бъде намерен
	 */
	static Map<Double, List<Double>> getStdMap(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		Map<Double, List<Double>> stdDraw = new LinkedHashMap<>();
		List<Double> row;
		double std;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) continue;

			String[] arr = line.split("\\s+");
			row = strToDouble(arr);
			std = Statistic.optimizedStdOfRow(row);

			if (!row.isEmpty() && !Double.isNaN(std)) {
				// Добавяй към съвпадащата стойност на std 1e-9
				// докато не получиш уникална стойност на ключа.
				while (stdDraw.containsKey(std)) {
					std += 1e-9;
				}
				stdDraw.put(std, row);
			} else {
				System.out.println("404: getStdMap(): " +
						"Ключа е празен или не е число.");
				return null;
			}
		}
		return stdDraw;
	}


	/**
	 * Прочита редове с числа от текстов файл и изчислява стандартното отклонение за всеки ред.
	 *
	 * <p>Всеки ред във файла трябва да съдържа числови стойности, разделени с интервали.
	 * За всяка такава редица се изчислява стандартното отклонение чрез метода
	 * {@code Statistic.optimizedStdOfRow(...)} и се добавя в резултатен списък.
	 *
	 * <p>Примерен ред във файла:
	 * <pre>{@code
	 * 1 3 5 7 9
	 * }</pre>
	 * Примерен резултат: {@code [2.828, ...]}
	 *
	 * @param fromFile Файлът, съдържащ редове от числа
	 * @return Списък със стойности на стандартното отклонение за всеки ред
	 * @throws FileNotFoundException ако файлът не може да бъде намерен
	 */
	static List<Double> getListOfStd(File fromFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(fromFile);
		List<Double> res = new ArrayList<>();
		List<Double> row;
		double std;

		while (scanner.hasNext()) {
			String[] arr = scanner.nextLine().split(" ");
			row = strToDouble(arr);
			std = Statistic.optimizedStdOfRow(row);
			res.add(std);
		}
		return res;
	}

	static List<Double> strToDouble(String[] strArr) {
		List<Double> res = new ArrayList<>();
		for (String s : strArr) {
			if (!s.isBlank()) {
				res.add(Double.parseDouble(s));
			}
		}
		return res;
	}

	/**
	 * Изчислява стандартното отклонение (стандартна извадкова дисперсия) на числовите стойности в даден списък.
	 *
	 * <p>Този метод използва формулата за стандартно отклонение на извадка (sample standard deviation), при която
	 * деленето става на (n - 1), където n е броят на елементите. Това е подходящо, когато имаме извадка, а не пълна съвкупност.</p>
	 *
	 * <p>Методът приема списък от обекти, които наследяват {@link Number}, като работи с техните
	 * double стойности за изчисленията. Подходящ е за списъци с {@code Integer}, {@code Double}, {@code Float} и др.</p>
	 *
	 * @param <T> Тип на числовите елементи в списъка (наследник на {@link Number}).
	 * @param row Списък от числови стойности, чиято изменчивост се анализира.
	 * @return Стойността на стандартното отклонение на данните.
	 * @throws IllegalArgumentException ако списъкът е празен или съдържа само един елемент.
	 *
	 *                                  <h4>Пример:</h4>
	 *                                  <pre>{@code
	 *                                                                                                    List<Integer> числа = Arrays.asList(10, 12, 23, 23, 16, 23, 21, 16);
	 *                                                                                                    double std = stdOfRowD(числа); // връща ~4.9
	 *                                                                                                    }</pre>
	 */
	public static <T extends Number> double stdOfRowD(List<T> row) {
		double avrByRow, sum = 0, avrSums = 0;

		for (T el : row) sum += el.doubleValue();
		avrByRow = sum / row.size();
		for (T el : row) {
			double x = Math.pow(el.doubleValue() - avrByRow, 2);
			avrSums += x;
		}
		return Math.sqrt(avrSums / (row.size() - 1));
	}

	/**
	 * Изчислява средната аритметична стойност на числата в подадения списък.
	 *
	 * <p>Средната стойност се дефинира като сумата на всички стойности, разделена на броя им.
	 * Подходящо за базови статистически анализи.</p>
	 *
	 * @param row Списък от {@code Double} стойности, чиято средна стойност ще се изчисли.
	 * @return Средната аритметична стойност.
	 * @throws IllegalArgumentException ако списъкът е празен.
	 *
	 *                                  <h4>Пример:</h4>
	 *                                  <pre>{@code
	 *                                                                                                    List<Double> оценки = Arrays.asList(5.0, 4.5, 6.0);
	 *                                                                                                    double средно = mean(оценки); // връща 5.166...
	 *                                                                                                    }</pre>
	 */
	public static <T extends Number> double mean(List<T> row) {
		double avrByRow, sum = 0;

		for (T el : row) sum += el.doubleValue();
		avrByRow = sum / row.size();

		return avrByRow;
	}

	public static <T extends Number> List<Integer> variance(List<T> row) {
		double mean = mean(row);
		List<Integer> res = new ArrayList<>();
		for (T el : row) res.add((int) (el.doubleValue() - mean));
		return res;
	}

	private static void printRes(File file, Map<Double, List<Double>> res) {
		int cnt = 0;
		for (Map.Entry<Double, List<Double>> el : res.entrySet()) {
			System.out.printf("%.3f -> ", el.getKey());
			System.out.println(el.getValue());
			cnt++;
		}
		System.out.println(cnt);
	}

	/**
	 * Изчислява стандартното отклонение на редица от десетични числа.
	 *
	 * <p>Този метод първо намира средната стойност (аритметичното средно)
	 * на подадения списък от числа, след което изчислява стандартното отклонение
	 * по класическата формула с корекция на знаменателя (N - 1), подходяща за извадка.
	 *
	 * <p>Формулата използвана е:
	 * <pre>{@code
	 * std = sqrt(Σ(x_i - mean)^2 / (n - 1))
	 * }</pre>
	 *
	 * @param row Списък от десетични числа (напр. стойности от едно теглене)
	 * @return Стойността на стандартното отклонение
	 */
	public static <T> double stdOfRowDOld(List<Double> row) {
		double avrByRow, sum = 0, avrSums = 0;

		for (Double el : row) sum += el;
		avrByRow = sum / row.size();
		for (Double el : row) {
			double x = (Math.pow((el - avrByRow), 2));
			avrSums += x;
		}
		return Math.sqrt(avrSums / row.size() - 1);
	}

	public double calcStdOfRow(List<Integer> row) {
		double sum = 0, squaredSum = 0;
		for (Integer el : row) {
			sum += el;
			squaredSum += el * el;
		}
		int n = row.size();
		return Math.sqrt((squaredSum - (sum * sum) / n) / (n - 1));
	}

	void printStdMatches(List<Double> stdS) {
		int el, tmpMatch = 0;
		Map<Integer, Integer> stdMatch = new TreeMap<>();
		for (Double std : stdS) {
			el = (int) Math.ceil(std);
			if (stdMatch.containsKey(el)) {
				tmpMatch = stdMatch.get(el);
			}
			tmpMatch++;
			stdMatch.put(el, tmpMatch);
		}

		for (Map.Entry<Integer, Integer> x : stdMatch.entrySet()) {
			System.out.printf("el: %d is matched %d times%n", x.getKey(), x.getValue());
		}
	}


}
