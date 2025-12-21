package org.example;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ODE {
	public static void main(String[] args) {
		final double STEP = 0.001;
		final double X0 = 1;
		final double Y0 = 0;
		final int POINTS = 6000;

		List<Double> y = ODE.rk2(X0, Y0, STEP, POINTS);

		List<Double> x = new ArrayList<>();
		double xi = X0;
		for (double i = 0; i < y.size(); i++) {
			x.add(xi);
			xi += STEP;
		}

		List<Double> euler = ODE.euler(X0, Y0, STEP, POINTS);
		List<Double> differences = ODE.differences(y, euler);
		ODE.print(y, differences);
		plot(x, y);
		plot(x, euler);
		plot(x, differences);
		System.out.printf("\nМаксимална грешка: %.3f %%\n", maxError(differences) * 100);
	}

	static double derivative(double x, double y) {
		return Math.pow(x, 2) + y * Math.sin(x);
	}

	/**
	 * Приблизително числено решение на начална стойностна задача (НЗЗ) чрез метода на Рунге-Кута от втори ред.
	 *
	 * <p>Методът {@code rk2} изчислява числено апроксимация на обикновено диференциално уравнение (ОДУ)
	 * от вида {@code dy/dx = f(x, y)} с начална стойност {@code y(x0) = y0}. Използва класическия второразреден
	 * Runge-Kutta алгоритъм с фиксирана стъпка {@code h} за зададен брой итерации.
	 *
	 * <p>На всяка стъпка методът:
	 * <ul>
	 *   <li>Изчислява първата оценка на наклона {@code k1 = f(x, y)}</li>
	 *   <li>Използва {@code k1}, за да намери временна стойност {@code y1 = y + k1 * h}</li>
	 *   <li>Изчислява втората оценка {@code k2 = f(x + h, y1)}</li>
	 *   <li>Изчислява средната стойност {@code k = (k1 + k2) / 2}</li>
	 *   <li>Извежда новата стойност на {@code y = y + k * h} и я добавя в резултата</li>
	 * </ul>
	 *
	 * <p>Този алгоритъм се използва в инженерни, физични и математически симулации, където аналитичното решение
	 * на ОДУ не е възможно, но численото поведение трябва да бъде проследено с добра точност при сравнително
	 * ниски изчислителни разходи.
	 *
	 * @param x0     началната стойност на независимата променлива (например времето)
	 * @param y0     началната стойност на функцията {@code y(x)}
	 * @param h      стъпката на интегриране (например времеви интервал)
	 * @param points брой на итерациите/точките, които ще бъдат изчислени
	 * @return списък {@code List<Double>} от стойностите на {@code y} при всяка стъпка
	 */
	static List<Double> rk2(double x0, double y0, double h, int points) {
		double x1, y, y1, avrSlope, slopeX0, slopeX1;
		List<Double> function = new ArrayList<>();
		int cnt = 0;

		while (cnt < points) {
			slopeX0 = derivative(x0, y0);
			x1 = x0 + h;
			y1 = y0 + (slopeX0 * h);
			slopeX1 = derivative(x1, y1);

			avrSlope = (slopeX0 + slopeX1) / 2;
			y = y0 + (avrSlope * h);
			function.add(y);

			x0 = x1;
			y0 = y;
			cnt++;
		}
		return function;
	}

	/**
	 * Извършва числено интегриране на обикновено диференциално уравнение (ОДУ)
	 * чрез метода на Ойлер (Euler method) с фиксирана стъпка.
	 *
	 * <p>Методът {@code euler} решава начална стойностна задача от вида {@code dy/dx = f(x, y)}
	 * със зададена начална стойност {@code y(x0) = y0}, като прилага итеративно формулата:
	 *
	 * <pre>
	 *     y_{n+1} = y_n + h * f(x_n, y_n)
	 * </pre>
	 *
	 * <p>На всяка итерация текущата стойност на производната {@code f(x, y)} се изчислява чрез
	 * метода {@code derivative(...)}, а резултатът {@code y} се актуализира чрез стандартната
	 * формула на Ойлер. Получената стойност се записва в списък за последващ анализ.
	 *
	 * <p>Методът е прост и бърз, но с по-ниска точност спрямо методи от по-висок ред като Runge-Kutta.
	 * Подходящ е за учебни цели или за бърза груба оценка на поведението на решението.
	 *
	 * @param x0 началната стойност на независимата променлива
	 * @param y0 началната стойност на функцията {@code y(x)}
	 * @param h стъпката на интегриране
	 * @param points брой итерации (брой изчислени стойности)
	 * @return списък {@code List<Double>} със стойностите на {@code y} за всяка стъпка
	 */
	static List<Double> euler(double x0, double y0, double h, int points) {
		double x1, y1, slopeX0;
		List<Double> function = new ArrayList<>();
		int cnt = 0;

		while (cnt < points) {
			slopeX0 = derivative(x0, y0);
			x1 = x0 + h;
			y1 = y0 + (slopeX0 * h);
			function.add(y1);

			x0 = x1;
			y0 = y1;
			cnt++;
		}
		return function;
	}


	private static List<Double> differences(List<Double> a, List<Double> b) {
		List<Double> res = new ArrayList<>();
		for (int i = 0; i < a.size(); i++) {
			res.add(a.get(i) - b.get(i));
		}

		return res;
	}

	private static void print(List<Double> list1, List<Double> list2) {
		for (int i = 0; i < list1.size(); i++) {
			System.out.printf("%d. %.3f" +
					"           %.3f\n", i, list1.get(i), list2.get(i));
		}
	}

	static void plot(List<Double> x, List<Double> y) {
		SwingUtilities.invokeLater(() -> {
			PlotF graph = new PlotF(x, y);
			graph.setSize(800, 600);
			graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			graph.setVisible(true);
		});
	}

	private static double maxError(List<Double> errors) {
		double maxError = 0;
		for (double el : errors) maxError = Math.max(maxError, Math.abs(el));
		return maxError;
	}
}
