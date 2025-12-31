package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProfitPlot extends JFrame {

	public ProfitPlot(List<Double> x, List<Double> y) {
		super("Profitable Std");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(100, 100);

		XYSeriesCollection dummyDataset = new XYSeriesCollection(); // временно
		JFreeChart chart = ChartFactory.createXYLineChart(" ",
				"Draw", "Std", dummyDataset);
		XYPlot plot = chart.getXYPlot();
		chart.removeLegend();


		// Създаваме и добавяме реалния dataset с анотации
		XYSeriesCollection dataset = createDataSet(x, y, plot);
		plot.setDataset(dataset);

		ChartPanel panel = new ChartPanel(chart);

//		Dimension d = new Dimension(100, 100);
//		panel.setPreferredSize(d);
//		panel.setMinimumSize(d);
//		panel.setMaximumSize(d);

		setContentPane(panel);
		//pack();               // мащабира по зададените 100x100
		setLocation(1600, 1000); // примерно долен десен ъгълЦ

		// Фон
		chart.setBackgroundPaint(Color.BLACK);
		plot.setBackgroundPaint(Color.BLACK);
		panel.setBackground(Color.BLACK);
		getContentPane().setBackground(Color.BLACK);

		// Мрежа
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		// Оси
		plot.getDomainAxis().setLabelPaint(Color.WHITE);
		plot.getRangeAxis().setLabelPaint(Color.WHITE);
		plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
		plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRange(false);
		rangeAxis.setLowerBound(6);
		rangeAxis.setUpperBound(20);

		// Заглавие
		chart.getTitle().setPaint(Color.WHITE);

		// Рендерер
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		plot.setRenderer(renderer);
	}

	private XYSeriesCollection createDataSet(List<Double> x, List<Double> y, XYPlot plot) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries(" ");

		for (int i = 0; i < y.size(); i++) {
			double xi = x.get(i);
			double yi = y.get(i);
			series.add(xi, yi);

			// Добавяне на стойности до всяка точка
			XYTextAnnotation annotation = new XYTextAnnotation(String.valueOf((int) yi), xi, yi);
			annotation.setFont(new Font("Arial", Font.PLAIN, 10));
			annotation.setPaint(Color.WHITE);
			plot.addAnnotation(annotation);
		}

		dataset.addSeries(series);
		return dataset;
	}
}
