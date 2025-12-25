package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlotF extends JFrame {
	public PlotF(List<Double> x, List<Double> y) {
		super("Рунге-Кута 2");

		XYSeriesCollection dataset = createDataSet(x, y);

		JFreeChart chart = ChartFactory.createXYLineChart(
				" ", "X", "Y(X)", dataset
		);

		chart.removeLegend();

		XYPlot plot = chart.getXYPlot();
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);

		// Фон
		chart.setBackgroundPaint(Color.BLACK);
		plot.setBackgroundPaint(Color.BLACK);
		panel.setBackground(Color.BLACK);
		getContentPane().setBackground(Color.BLACK);

		// Мрежа
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		// Ос X и Y
		plot.getDomainAxis().setLabelPaint(Color.WHITE);
		plot.getRangeAxis().setLabelPaint(Color.WHITE);
		plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
		plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

		// Заглавие
		chart.getTitle().setPaint(Color.WHITE);

		// Легенда
//		LegendTitle legend = chart.getLegend();
//		legend.setItemPaint(Color.WHITE);
//		legend.setBackgroundPaint(Color.BLACK);
//		legend.setFrame(BlockBorder.NONE);

		// Рендерер
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		plot.setRenderer(renderer);
	}

	private XYSeriesCollection createDataSet(List<Double> x, List<Double> y) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries(" ");
		for (int i = 0; i < y.size(); i++) {
			series.add(x.get(i), y.get(i));
		}
		dataset.addSeries(series);
		return dataset;
	}
}
