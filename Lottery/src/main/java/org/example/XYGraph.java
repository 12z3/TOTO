package org.example;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.List;
import java.util.Scanner;

import org.jfree.chart.*;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.*;

import javax.swing.*;

// Чертае графиката за съответният тираж

/**
 * Класът {@code XYGraph} предоставя функционалност за визуализация на двумерни числови данни
 * чрез линейна графика и хистограма. Наследява {@code JFrame} и съдържа {@code JPanel},
 * който override-ва метода {@code paintComponent(Graphics)} за извеждане на визуални елементи.
 * <p>
 * Основната цел на класа е да предостави самостоятелен графичен компонент, който може да се
 * използва за визуализиране на зависимости между стойности по X и Y. Поддържа автоматично
 * мащабиране спрямо размера на панела, оразмеряване на маркерите и отпечатване на стойности.
 * </p>
 *
 * <h2>Основни функционалности:</h2>
 * <ul>
 *   <li>Изчертаване на графика по подадени масиви от точки (X и Y).</li>
 *   <li>Поддържка на хистограма чрез клас {@code Histogram}.</li>
 *   <li>Автоматично определяне на мин/макс стойности.</li>
 *   <li>Преобразуване на координати към пиксели.</li>
 * </ul>
 *
 * <h2>Ключови методи:</h2>
 * <ul>
 *   <li>stdDraw(double[], double[]) – основен метод за извикване на визуализация по масиви.</li>
 *   <li>paintComponent(java.awt.Graphics) – override-нат метод, който отговаря за изчертаването.</li>
 *   <li>mapX(double)} и mapY(double) – мащабират стойности към пикселна координатна система.</li>
 *   <li>drawAxes(java.awt.Graphics2D) – изчертава координатните оси.</li>
 *   <li>drawGraph(java.awt.Graphics2D) – начертава линията, свързваща точките от графиката.</li>
 *<ul>
 * <h2>Особености:</h2>
 * <ul>
 *   <li>Автоматично изчисляване на min и max стойности за X и Y.</li>
 *   <li>Работа с {@code Double}-координати за по-голяма прецизност.</li>
 *   <li>Изчертаване на графика в реално време с {@code repaint()} и {@code setVisible(true)}.</li>
 * </ul>
 *
 * <h2>Особености:</h2>
 * <ul>
 *   <li>Автоматично изчисляване на мин и макс стойности за X и Y.</li>
 *   <li>Работа с {@code Double}-координати за по-голяма прецизност.</li>
 *   <li>Поддържа padding за вътрешни отстъпи от краищата на панела.</li>
 *   <li>Използва {@code setVisible(true)} и {@code repaint()} за показване и актуализация.</li>
 * </ul>
 *
 * <h2>Пример за използване:</h2>
 * <pre>{@code
 *     XYData xy = new XYData();              // Зарежда координати от файл
 *     xy.plotStd();                          // Изчертава графиката и хистограмата
 * }</pre>
 *
 * @author Blagoy Nikolov
 * @version 1.0
 * @since 2025-05-16
 */
public class XYGraph extends JFrame {
	private static final int wight = 2000;
	private static final int height = 600;
	private final List<Double> stdData = stdSeries();
	private final int stdSeriesSize = Objects.requireNonNull(stdSeries()).size();

	// Без този конструктор структурата се чупи яко.
	public XYGraph() {
		// Взима текущата дата и час и ги привежда във вид удобен за логаритмуване:
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.
				ofPattern("dd-MM-yyyy HH:mm:ss");
		String currentTime = time.format(myFormatObj);

		// Създава колекцията от стойности по Х и по У:
		XYSeriesCollection xySeries = dataSet();
		JFreeChart chart = ChartFactory
				.createXYLineChart(
						"Средно квадратично отклонение на тиражите до :  " + currentTime,
						"Номер на тиража",
						"Средно квадратично отклонение",
						xySeries);

		chart.removeLegend();

		// Създава графиката:
		XYPlot plot = chart.getXYPlot();
		ChartPanel panel = new ChartPanel(chart);

		setContentPane(panel);

		// Мащабира ординатата:
		plot.getRangeAxis().setRange(4, 23);
		// Мащабира абсцисата:
		plot.getDomainAxis().setRange(0, getStdSeriesSize() + 2);
		plot.setBackgroundPaint(Color.BLACK);

		// Промяна на цветовете:
		chart.setBackgroundPaint(Color.BLACK);               // Черен фон на графиката
		plot.setBackgroundPaint(Color.BLACK);                // Черен фон на координатната система
		panel.setBackground(Color.BLACK);                    // Черен фон на панела
		getContentPane().setBackground(Color.BLACK);         // Черен фон на целия прозорец

		chart.getTitle().setPaint(Color.WHITE);              // Бял цвят за заглавието

		plot.getDomainAxis().setLabelPaint(Color.WHITE);     // Бял текст за X-оста
		plot.getRangeAxis().setLabelPaint(Color.WHITE);      // Бял текст за Y-оста

		plot.getDomainAxis().setTickLabelPaint(Color.WHITE); // Бели числа за X-оста
		plot.getRangeAxis().setTickLabelPaint(Color.WHITE);  // Бели числа за Y-оста


		// Добавя графиката за медианата:
		double median = getMedian();
		XYPlot medianLine = chart.getXYPlot();
		ValueMarker markerLine = new ValueMarker(median);     // Y-позиция на линията
		markerLine.setPaint(new Color(220, 20, 60));
		markerLine.setStroke(new BasicStroke(1.3f));    // Дебелина на линията
		medianLine.addRangeMarker(markerLine);                // Добавяне към графиката
		// Задава параметрите на пунктира:
		markerLine.setStroke(new BasicStroke(
				1.3f,                       // Дебелина на линията
				BasicStroke.CAP_ROUND,            // Начин на завършване
				BasicStroke.JOIN_BEVEL,           // Начин на съединяване
				0,                                // Митер лимит (не е важно тук)
				new float[]{5, 5},                // Патерн: 5px линия, 5px празно
				0                                 // Фаза (начална точка)
		));

		// Добавя стойността на медианата:
		XYTextAnnotation medianAnnotation =
				new XYTextAnnotation(String.format("Медиана: %.2f;", median),
						5.4, 18.2);
		medianAnnotation.setPaint(Color.WHITE);
		medianAnnotation.setFont(new Font("Arial", Font.PLAIN, 14));
		plot.addAnnotation(medianAnnotation);

		// Добавя стойността на last Std value:
		double lastStdValue = getStdData().getLast();
		XYTextAnnotation lastStdAnnotation =
				new XYTextAnnotation(String.format(
						"STD - последен тираж : %.2f;", lastStdValue),
						9.9, 17.2);
		// Визуализира стойността на Std на последният тираж:
		XYTextAnnotation lastPointStdAnnotation =
				new XYTextAnnotation(String.valueOf(lastStdValue) + "; ",
						// Динамично променя координатите на текста за lastStdValue:
						getStdSeriesSize() + 0.55, lastStdValue + 0.1);
		// Визуализира стойността на Асиметрията в разпределението на Std на тиражите:
		double skewness = new Statistic().skewness((this.stdData));
		XYTextAnnotation skewnessAnnotation =
				new XYTextAnnotation(String.format("Асиметрия (S +/- 0.5):  %.2f;", skewness),
						// Динамично променя координатите на текста за lastStdValue:
						21.7, 18.2);

		lastStdAnnotation.setPaint(Color.WHITE);
		lastPointStdAnnotation.setPaint(Color.WHITE);
		skewnessAnnotation.setPaint(Color.WHITE);
		lastStdAnnotation.setFont(new Font("Arial", Font.PLAIN, 15));
		lastPointStdAnnotation.setFont(new Font("Arial", Font.PLAIN, 15));
		skewnessAnnotation.setFont(new Font("Arial", Font.PLAIN, 15));

		plot.addAnnotation(lastStdAnnotation);
		plot.addAnnotation(lastPointStdAnnotation);
		plot.addAnnotation(skewnessAnnotation);

		// Определя границите на графиката (xMin, xMax, yMin, yMax).
		// Отмества текста, за да остане в рамките на графиката (textX, textYStart).
		// Изчислява динамично разстояние между редовете (annotationSpacing).
		// Мащабира шрифта спрямо размера на графиката (baseFontSize).
		// Поставя анотациите една под друга с фиксирани отстояния.
		// Обновява графиката, за да приложи промените.

		// Взема текущите граници на графиката - Авто мащабиране
		double xMin = plot.getDomainAxis().getLowerBound();  // Най-лявата граница
		double xMax = plot.getDomainAxis().getUpperBound();  // Най-дясната граница
		double yMax = plot.getRangeAxis().getUpperBound();   // Най-горната граница
		double yMin = plot.getRangeAxis().getLowerBound();   // Най-долната граница

		// **Фиксирано X-отместване (х% от ширината), за да не влиза в осите**
		// xMax−xMin → изчислява широчината на графиката.
		// 0.01 → взима 1% от широчината, за да отмести текста малко надясно.
		// textX → начална X-координата на текста, която е близо до лявата граница,
		// но не прекалено близо, за да влезе в оста.
		double textOffsetX = (xMax - xMin) * 0.01;
		double textX = xMin + textOffsetX;

		// **Y-отместване започва от х% от височината, за да остане в рамките**
		// yMax е горният край на оста Y.
		// yMin е долният край.
		// yMax−yMin е височината на графиката.
		// (yMax−yMin)×0.04 изчислява 4% от височината.
		// Изваждайки тази стойност от yMax, получаваме нова позиция по-надолу от най-горната точка.
		double textYStart = yMax - (yMax - yMin) * 0.04;

		// **Динамично разстояние между редовете (х% от височината)**
		// yMax−yMin → височината на графиката.
		// ×0.065 → взема 6.5% от височината, за да определи разстоянието между отделните редове.
		// Осигурява равномерно разстояние между текстовите анотации, което мащабира заедно с графиката.
		double annotationSpacing = (yMax - yMin) * 0.065;

		// **Автоматично мащабиране на шрифта**
		// xMax−xMin → широчината на графиката.
		// ×0.02 → взема 2% от ширината и го използва за определяне на размера на шрифта.
		// (int) → закръгля числото към цяло число.
		int baseFontSize = (int) ((xMax - xMin) * 0.02); // Динамично спрямо ширината
		Font annotationFont = new Font("Arial", Font.BOLD, Math.max(12, baseFontSize));

		// **Точно подравняване на текстовете спрямо осите**
		medianAnnotation.setX(textX);
		medianAnnotation.setY(textYStart);
		medianAnnotation.setFont(annotationFont);
		medianAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);  // Гарантирано подравняване

		skewnessAnnotation.setX(textX);
		skewnessAnnotation.setY(textYStart - annotationSpacing);
		skewnessAnnotation.setFont(annotationFont);
		skewnessAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);

		lastStdAnnotation.setX(textX);
		lastStdAnnotation.setY(textYStart - 2 * annotationSpacing);
		lastStdAnnotation.setFont(annotationFont);
		lastStdAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);

		// **Принудително обновяване на графиката**
		// notifyListeners() → казва на графиката, че има промяна в данните.
		// fireChartChanged() → принудително обновява графиката.
		plot.notifyListeners(new PlotChangeEvent(plot));
		chart.fireChartChanged();


		// Те този RENDER прави цялата магия с чертаенето на графиката:
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, new Color(251, 51, 51));
		// Задава формата на точките:
		renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 5, 5));
		plot.setRenderer(renderer);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			XYGraph graph = new XYGraph();
			graph.setSize(wight, height);
			graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			graph.setVisible(true);
			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
		});
	}

	private XYSeriesCollection dataSet() {
		XYSeriesCollection xySeries = new XYSeriesCollection();
		XYSeries xy = new XYSeries("Std");
		int cnt = 0;

		try {
			File fromFile = new File("/Users/blagojnikolov/Desktop/@tmp/sdtAllRes.txt");
			if (!fromFile.exists()) {
				System.out.println(fromFile.getName() + " не съществува");
				return xySeries;
			} else {
				Scanner scanner = new Scanner(fromFile);
				while (scanner.hasNextDouble()) {
					double std = scanner.nextDouble();
					xy.add(++cnt, std);
				}
				xySeries.addSeries(xy);
			}
		} catch (Exception e) {
			System.out.println("""
					Не мога да взема стойностите от sdtAllRes.txt\
					 - или Скенера е избушил...\
					 - или Файла е криминален...""");
		}
		return xySeries;
	}

	private List<Double> stdSeries() {
		List<Double> stdData = new ArrayList<>();
		try {
			File fromFile = new File("/Users/blagojnikolov/Desktop/@tmp/sdtAllRes.txt");
			if (!fromFile.exists()) {
				System.out.println(fromFile.getName() + " не съществува");
				return null;
			} else {
				Scanner scanner = new Scanner(fromFile);
				while (scanner.hasNextDouble()) {
					double std = scanner.nextDouble();
					stdData.add(std);
				}
			}
		} catch (Exception e) {
			System.out.println("""
					Не мога да взема стойностите от sdtAllRes.txt\
					 - или Скенера е избушил...\
					 - или Файла е криминален...""");
		}
		return stdData;
	}

	private double getMedian() {
		int cnt = 0;
		double sum = -1;
		List<Double> stdData = stdSeries();
		if (stdData != null) {
			for (Double std : stdData) {
				sum += std;
				++cnt;
			}
		} else {
			System.out.println("Проблем с четенето от файла: sdtAllRes.txt");
		}
		return sum / cnt;
	}

	public int getStdSeriesSize() {
		return stdSeriesSize;
	}

	public List<Double> getStdData() {
		return stdData;
	}

	public double[] listToArray(List<Double> list) {
		return list.stream().mapToDouble(Double::doubleValue).toArray();
	}
}
