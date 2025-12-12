package org.example;

import java.awt.*;
import java.io.File;
import java.util.*;

import java.util.List;
import java.util.Scanner;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.ui.UIUtils;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class Histogram extends JFrame {
	public Histogram() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(100, 100);

		File fromFile = new File("/Users/blagojnikolov/Desktop/@tmp/sdtAllRes.txt");
		double[] data = getSeries(fromFile);
		//List<Double> dbL = new ArrayList<>((int) Double.parseDouble(Arrays.toString(data)));

		if (data == null || data.length == 0) {
			JOptionPane.showMessageDialog(this,
					"Грешка: Няма валидни данни за визуализация!", "Грешка",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int bins = 10;
		double min = Arrays.stream(data).min().orElse(0);
		double max = Arrays.stream(data).max().orElse(1);
		double binWidth = (max - min) / bins;
		int[] binCounts = new int[bins];

		for (double value : data) {
			int binIndex = (int) ((value - min) / binWidth);
			if (binIndex >= 0 && binIndex < bins) {
				binCounts[binIndex]++;
			}
		}

		XYSeries series = new XYSeries("Честотно разпределение");
		for (int i = 0; i < bins; i++) {
			double binCenter = min + i * binWidth + binWidth / 2;
			series.add(binCenter, binCounts[i]);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		JFreeChart histogram = ChartFactory.createXYBarChart(
				"Хистограма: СКО на тиражите",
				"СКО",
				false,
				"Честота",
				dataset
		);

		histogram.removeLegend();

		histogram.setBackgroundPaint(Color.BLACK);
		histogram.getTitle().setPaint(Color.WHITE);

		XYPlot plot = (XYPlot) histogram.getPlot();
		plot.setDomainAxis(new NumberAxis("Стойности"));
		plot.setRangeAxis(new NumberAxis("Честота"));
		plot.setBackgroundPaint(Color.BLACK);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		plot.getDomainAxis().setLabelPaint(Color.WHITE);
		plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
		plot.getRangeAxis().setLabelPaint(Color.WHITE);
		plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

//		double skewness = new Statistic().skewness(dbL);
//		XYTextAnnotation skewnessAnnotation =
//				new XYTextAnnotation(String.format("Асиметрия (S +/- 0.5):  %.2f;", skewness),
//						// Динамично променя координатите на текста за lastStdValue:
//						21.7, 18.2);
//		skewnessAnnotation.setPaint(Color.WHITE);
//		skewnessAnnotation.setFont(new Font("Arial", Font.PLAIN, 14));
//		plot.addAnnotation(skewnessAnnotation);

		if (histogram.getLegend() != null) {
			histogram.getLegend().setBackgroundPaint(Color.BLACK);
			histogram.getLegend().setItemPaint(Color.WHITE);
		}

		// **Премахване на сенките и ефектите**
		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setSeriesPaint(0, new Color(220, 20, 60));  // Чисто червен цвят
		renderer.setBarPainter(new StandardXYBarPainter()); // Премахване на 3D ефекти
		renderer.setShadowVisible(false); // Премахване на сенките
		renderer.setDrawBarOutline(false); // Без контури
		plot.setRenderer(renderer);

		ChartPanel chartPanel = new ChartPanel(histogram);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.setBackground(Color.BLACK);
		setContentPane(chartPanel);

		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		Histogram chart = new Histogram();
		chart.setSize(800, 600);
		UIUtils.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}

	double[] getSeries(File fromFile) {
		List<Double> stdData = new ArrayList<>();
		try {
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
					 - или Сканера е избушил...\
					 - или Файла е криминален...""");
		}
		return (stdData.stream().mapToDouble(Double::doubleValue).toArray());
	}
}
