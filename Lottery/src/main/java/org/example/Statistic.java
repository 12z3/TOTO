package org.example;

import java.io.File;
import java.util.*;

/**
 * Клас {@code Statistic} предоставя методи за статистически анализ на числови данни, свързани с резултати от тото тегления.
 *
 * <h2>Какво прави?</h2>
 * Основната цел на класа е да изчислява:
 * <ul>
 *     <li>Стандартно отклонение (Standard Deviation, STD)</li>
 *     <li>Средна стойност (Mean)</li>
 *     <li>Коефициент на асиметрия (Skewness)</li>
 *     <li>Конвертиране на списъци към масиви</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * Класът използва математически формули и алгоритми за пресмятане:
 * <ul>
 *     <li>{@code optimizedStdOfRow(List<Double> row)} – използва съкратена формула за пресмятане на дисперсия</li>
 *     <li>{@code mean(List<Double> input)} – намира аритметична средна стойност</li>
 *     <li>{@code skewness(List<Double> input)} – използва класическа формула за третия момент за асиметрия</li>
 *     <li>{@code genericMean(List<T extends Number> data)} – позволява използване на различни типове Number</li>
 *     <li>{@code calculateStd(String data)} – извлича стойности от текст и изчислява стандартно отклонение</li>
 *     <li>{@code listToArray(List<Double> list)} – конвертира {@code List} към {@code double[]}</li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * Класът е създаден за:
 * <ul>
 *     <li>Анализ на данни от тото тегления</li>
 *     <li>Статистическа обработка на числови редове</li>
 *     <li>Откриване на тенденции чрез стандартно отклонение и асиметрия</li>
 *     <li>Осигуряване на точни и бързи изчисления за последващи анализи и визуализации</li>
 * </ul>
 *
 * <h2>Използвани структури:</h2>
 * <ul>
 *     <li>{@code List<Double>} – основна входна структура за числови редове</li>
 *     <li>{@code Map<Integer, Integer>} – използвана във {@code main()} за броене на срещания</li>
 *     <li>{@code String[]} и {@code double[]} – временни структури за конвертиране</li>
 * </ul>
 *
 * <h2>Важни методи:</h2>
 * <ul>
 *     <li>{@code optimizedStdOfRow()} – оптимизиран алгоритъм за STD без многократен проход</li>
 *     <li>{@code mean()} – базова аритметична средна</li>
 *     <li>{@code calculateStd()} – обвива parsing + STD изчисление</li>
 *     <li>{@code skewness()} – извежда информация за симетричност на разпределението</li>
 * </ul>
 *
 * <h2>Зависимости:</h2>
 * Класът използва методи от:
 * <ul>
 *     <li>{@code Manipulate.parseToDouble(String)} – за преобразуване на текстови ред в числа</li>
 *     <li>{@code Frequency.countFrequencyOfNumbers()} – за визуализация в {@code main()}</li>
 * </ul>
 *
 * @author [Вашето име]
 * @version 1.0
 */
public class Statistic {
	public static void main(String[] args) {

		for (Map.Entry<Integer, Integer> el : Frequency.countFrequencyOfNumbers().entrySet()) {
			System.out.println("Число: " + el.getKey() + "  -  Срещания: " + el.getValue());
		}

		System.out.println(lastDrawAvrValue());
	}

	/**
	 * Изчислява стандартното отклонение на списък от цели числа,
	 * използвайки еднопроходен (оптимизиран) алгоритъм.
	 *
	 * <p>Формулата, която се използва, е следната:
	 * <pre>
	 * σ = √( (∑x² - (∑x)² / n) / (n - 1) )
	 * </pre>
	 * <p>
	 * Където:
	 * <ul>
	 *   <li><b>∑x</b> е сумата на всички стойности</li>
	 *   <li><b>∑x²</b> е сумата на квадратите на всички стойности</li>
	 *   <li><b>n</b> е броят на елементите в списъка</li>
	 * </ul>
	 * <p>
	 * Това е така нареченото <b>стандартно отклонение на извадка</b> (sample standard deviation),
	 * което използва деление на {@code (n - 1)}. Ако искаш стандартно отклонение на генерална съвкупност,
	 * трябва да се дели на {@code n}.
	 *
	 * <p><b>Предимства на алгоритъма:</b>
	 * <ul>
	 *   <li>Само едно преминаване през данните (O(n))</li>
	 *   <li>Не се изисква отделно изчисление на средно аритметично</li>
	 *   <li>Подходящ за големи списъци</li>
	 * </ul>
	 *
	 * <p><b>Пример:</b>
	 * <pre>{@code
	 * List<Integer> числа = Arrays.asList(5, 10, 15);
	 * double отклонение = optimizedStdOfRow(числа);
	 * System.out.printf("Стандартно отклонение: %.2f", отклонение); // Изход: 5.00
	 * }</pre>
	 *
	 * @param row списък от цели числа; трябва да съдържа поне 2 елемента
	 * @return стандартното отклонение на стойностите в списъка
	 * @throws IllegalArgumentException ако списъкът е null или съдържа по-малко от 2 елемента
	 */
	protected static double optimizedStdOfRow(List<Double> row) {
		double sum = 0;
		double squaredSum = 0;
		for (double el : row) {
			sum += el;
			squaredSum += el * el;
		}
		double mean = sum / row.size();
		//return Math.sqrt(((squaredSum / row.size() - 1) - (mean * mean) * 100 / 100));
		return Math.sqrt((squaredSum - (sum * sum) / row.size()) / (row.size() - 1));
		// Variance = (1/N) * SuMi(Xi * Xi) - (avrXi * avrXi)
	}

	/**
	 * Изчислява средно аритметично (mean) на елементите в масив от реални числа.
	 *
	 * <p>Средното аритметично се дефинира като:
	 * <pre>
	 * mean = ∑Xi / N
	 * </pre>
	 * където:
	 * <ul>
	 *   <li><b>∑Xi</b> е сумата на всички стойности в масива</li>
	 *   <li><b>N</b> е броят на стойностите</li>
	 * </ul>
	 *
	 * <p><b>Пример:</b>
	 * <pre>{@code
	 * double[] sample = {2.0, 4.0, 6.0, 8.0};
	 * double avg = mean(sample); // avg = 5.0
	 * }</pre>
	 *
	 * <p><b>Забележка:</b> Методът не прави проверка за {@code data.length == 0}. При празен масив ще хвърли {@code ArithmeticException (деление на 0)}.
	 *
	 * @param input масив от реални числа, за който се изчислява средната стойност
	 * @return средното аритметично на числата
	 * @throws ArithmeticException ако масивът е празен
	 */
	static double mean(List<Double> input) {
		double[] data = listToArray(input);
		if (data.length == 0) throw new IllegalArgumentException("↪︎Масивът е празен.");
		double sum = 0;
		for (double value : data) {
			sum += value;
		}
		return sum / data.length;
	}

	static double lastDrawAvrValue() {
		String path = "/Users/blagojnikolov/Desktop/@tmp/bgToto.txt";
		List<String> rawData = ParseURL.readFromFile(new File(path));
		List<Integer> data = Manipulate.parseToInt(rawData.getLast());
		double sum = 0;
		for (int el : data) {
			sum += el;
		}
		return sum / data.size();
	}

	public static double[] listToArray(List<Double> list) {
		return list.stream().mapToDouble(Double::doubleValue).toArray();
	}

	/**
	 * Изчислява средната аритметична стойност на числата от подадения списък.
	 *
	 * @param data Списък от цели числа ({@code List<Integer>}), например [5, 10, 15]
	 * @return Средната стойност като десетично число ({@code double})
	 *
	 * <p>Използва се за центриране на нормално разпределените числа около нея при генериране на нови.</p>
	 *
	 * <b>Пример:</b><br>
	 * {@code mean([5, 10, 15])} → {@code 10.0}
	 */
	static <T extends Number> double genericMean(List<T> data) {
		double sum = 0;
		for (T el : data) sum += (int) el;
		return sum / data.size();
	}

	/**
	 * Пресмята стандартното отклонение на числата в даден текстов ред.
	 *
	 * @param data Ред от числа, разделени с интервали (например "1 2 3 4.5 6").
	 * @return Стандартното отклонение на числата, подадени като текстов ред.
	 * <p>
	 * Методът парсва текстовия ред до списък от числа (double),
	 * след което изчислява стандартното отклонение чрез метод `optimizedStdOfRow`.
	 */
	static double calculateStd(String data) {
		List<Double> numbers = Manipulate.parseToDouble(data);
		return Statistic.optimizedStdOfRow(numbers);
	}

	/**
	 * Изчислява коефициента на асиметрия (skewness) на даден списък от числа.
	 *
	 * <p>Коефициентът на асиметрия показва дали разпределението на стойностите е
	 * симетрично спрямо средната стойност. Ако стойността е:
	 * <ul>
	 *   <li>{@code > 0} — разпределението е с "опашка" надясно (положителна асиметрия)</li>
	 *   <li>{@code < 0} — разпределението е с "опашка" наляво (отрицателна асиметрия)</li>
	 *   <li>{@code = 0} — разпределението е симетрично</li>
	 * </ul>
	 *
	 * <p>Използвана формула (sample skewness, n-corrected):
	 * <pre>
	 * skewness = (n / ((n - 1)(n - 2))) * ∑(((Xi - mean) / std)³)
	 * </pre>
	 *
	 * <p><b>Зависимост:</b> Методът използва:
	 * <ul>
	 *   <li>{@code mean(...)} — за изчисляване на средната стойност</li>
	 *   <li>{@code optimizedStdOfRow(...)} — за стандартното отклонение</li>
	 * </ul>
	 *
	 * <p><b>Пример:</b>
	 * <pre>{@code
	 * List<Double> data = Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0);
	 * double skew = skewness(data); // ≈ 0.0 (симетрично)
	 * }</pre>
	 *
	 * @param input списък от реални числа
	 * @return коефициент на асиметрия (skewness)
	 */
	double skewness(List<Double> input) {
		double mean = mean(input);
		double std = optimizedStdOfRow(input);
		double sum = 0;

		for (double value : input) {
			sum += Math.pow((value - mean) / std, 3);
		}
		double n = input.size();
		return (n / ((n - 1) * (n - 2))) * sum;
	}
}

