package org.example;

import org.jfree.chart.ui.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// Java StdLib:  https://www.youtube.com/watch?v=_-7YdoMwJrA

/**
 * Клас {@code XYData} отговаря за зареждането и организирането на координатни данни (X, Y),
 * необходими за визуализация чрез графики и хистограми.
 * Наследява {@code XYGraph}, като разширява функционалността му със собствени методи
 * за зареждане и обработка на числови данни от файл.
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *     <li>Чете данни от файл {@code sourceFilePath}, съдържащ по една стойност (Y) на ред</li>
 *     <li>Генерира съответстващи X-координати автоматично (1, 2, 3, ...)</li>
 *     <li>Запазва X и Y стойностите във вътрешни списъци</li>
 *     <li>Позволява извеждане на стойностите на екрана или графично</li>
 *     <li>Създава графика (линейна) и хистограма в отделни прозорци</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * <ol>
 *     <li>Файлът се намира в {@code sourceFilePath}, дефиниран като постоянен път към файл с данни</li>
 *     <li>{@link #getCoordinates()}:
 *         <ul>
 *             <li>Чете всяка стойност от файла и я записва в {@code yData}</li>
 *             <li>Добавя автоматично X стойност (1, 2, 3, ...)</li>
 *             <li>Връща нов {@code XYData} обект с тези две колекции</li>
 *         </ul>
 *     </li>
 *     <li>{@link #plotStd()}:
 *         <ul>
 *             <li>Зарежда координатите</li>
 *             <li>Извиква {@code plotHistogram()} и {@code plotStdGraph()} за визуализация</li>
 *         </ul>
 *     </li>
 *     <li>{@link #plotHistogram()} – стартира визуализация на хистограма с помощта на {@code SwingUtilities}</li>
 *     <li>{@link #plotStdGraph()} – стартира визуализация на линейна графика със зададени размери</li>
 * </ol>
 *
 * <h2>Защо го прави?</h2>
 * <ul>
 *     <li>За да подпомогне графично представяне на резултати от числов анализ (например стойности на STD, време, измервания)</li>
 *     <li>За да бъде използван от по-широк контекст (например клас {@code Std}) като средство за визуална интерпретация</li>
 *     <li>За да предложи модулна и преизползваема логика за изчертаване на статистически данни</li>
 * </ul>
 *
 * <h2>Ключови методи:</h2>
 * <ul>
 *     <li>{@link #getCoordinates()} – зарежда данните от файл</li>
 *     <li>{@link #getXdata()}, {@link #getYdata()} – достъп до координатите</li>
 *     <li>{@link #printXY(Double[], Double[])} – извежда стойностите на екрана</li>
 *     <li>{@link #plotHistogram()} – визуализира хистограма</li>
 *     <li>{@link #plotStdGraph()} – визуализира стандартна XY графика</li>
 *     <li>{@link #plotStd()} – координира цялата визуализация</li>
 * </ul>
 *
 * <h2>Външни зависимости:</h2>
 * <ul>
 *     <li>{@code XYGraph} – базов клас за графика</li>
 *     <li>{@code Histogram} – клас за изчертаване на хистограми</li>
 *     <li>{@code SwingUtilities}, {@code JFrame}, {@code Cursor} – за UI</li>
 *     <li>{@code Scanner}, {@code File} – за четене на данни</li>
 * </ul>
 *
 * <h2>Пример за входен файл:</h2>
 * <pre>
 * 3.4
 * 2.7
 * 5.0
 * </pre>
 *
 * <h2>Пример за генерирани масиви:</h2>
 * <pre>
 * X: [1.0, 2.0, 3.0]
 * Y: [3.4, 2.7, 5.0]
 * </pre>
 *
 * @see XYGraph
 * @see Histogram
 * @author [Вашето име]
 * @version 1.0
 */
public class XYData extends XYGraph {
	private final String sourceFilePath = "/Users/blagojnikolov/Desktop/@tmp/sdtAllRes.txt";
	private List<Double> yData;
	private List<Double> xData;

	XYData(List<Double> yData, List<Double> xData) {
		super();
		this.yData = yData;
		this.xData = xData;
	}

	XYData() {}

	public static void main(String[] args) {
		XYData xy = new XYData();
		xy.printXY(xy.getCoordinates().getXdata().toArray(new Double[0]),
				xy.getCoordinates().getYdata().toArray(new Double[0]));
		//xy.plotStd(false);
	}

	private static void plotHistogram() {
		SwingUtilities.invokeLater(() -> {
			Histogram chart = new Histogram();
			// Фиксира размера на хистограмата
			chart.setSize(800, 378);
			UIUtils.centerFrameOnScreen(chart);
			// Фиксира позицията върху екрана
			chart.setLocation(0, 590); // X = 50 пиксела от ляво, Y = 500 пиксела надолу
			chart.setVisible(true);
			chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			//chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}

	private static void plotStdGraph() {
		SwingUtilities.invokeLater(() -> {
			XYGraph graph = new XYGraph();
			// Фиксира размера на основната графиката:
			graph.setSize(1990, 590);
			//graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			graph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			graph.setVisible(true);
			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			// Фиксира позицията върху екрана
			graph.setLocation(0, 30); // X = 50 пиксела от ляво, Y = 50 пиксела отгоре
		});
	}

	// Основният метод който чертае графиката и хистограмата
	void plotStd() {
		XYData xy = new XYData();
		XYData coordinates = xy.getCoordinates();
		Double[] xData = coordinates.getXdata().toArray(new Double[0]);
		Double[] yData = coordinates.getYdata().toArray(new Double[0]);
		// this.stdDraw(grid, xData, yData);
		Profitable.plotProfitStd();
		plotHistogram();
		plotStdGraph();
		//System.out.println(".... :)");
	}

	List<Double> getYdata() {
		return this.yData;
	}

	List<Double> getXdata() {
		return this.xData;
	}

	void printXY(Double[] yData, Double[] xData) {
		for (Double y : yData) System.out.print(y + "   ");
		System.out.println();
		for (Double x : xData) System.out.print(x + "   ");
	}

	private XYData getCoordinates() {
		String fileName = "";
		List<Double> yData = new ArrayList<>();
		List<Double> xData = new ArrayList<>();

		try {
			File fromFile = new File(this.sourceFilePath);
			fileName = fromFile.getName();
			Scanner scanner = new Scanner(fromFile);
			double x = 1;

			while (scanner.hasNext()) {
				double y = Double.parseDouble(scanner.nextLine().trim());
				yData.add(y);
				xData.add(x++);
			}

		} catch (FileNotFoundException e) {
			System.out.println("404: Dot: getData: " +
					"Нямам достъп до файла: -> " + fileName);
		}
		return new XYData(yData, xData);
	}

}
