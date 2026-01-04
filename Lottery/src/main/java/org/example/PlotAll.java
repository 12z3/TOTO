package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlotAll extends JFrame {
	PlotAll() {
		super(" ");
		XYSeriesCollection dataset = createDataSet();

		JFreeChart chart = ChartFactory.createXYLineChart(
				"",
				"",
				"",
				dataset
		);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.BLACK);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// Създаване на панел и добавяне към JFrame
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setSize(1000,1000);
		//chartPanel.setPreferredSize(new Dimension(1000, 1000));
		setContentPane(chartPanel);
	}

	public static XYSeriesCollection createDataSet() {
		List<List<Double>> data = Profitable.newDataList();
		XYSeriesCollection dataset = new XYSeriesCollection();

		int idx = 0, cnt;
		for (List<Double> line : data) {
			cnt = 0;
			XYSeries series = new XYSeries("S" + ++idx);
			for (Double aDouble : line) {
				series.add(++cnt, aDouble);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	static void plot() {
		SwingUtilities.invokeLater(() -> {
			PlotAll graph = new PlotAll();
			graph.setSize(800, 600);
			graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			graph.setVisible(true);
		});
	}

	public static void main(String[] args) {
		plot();
	}
}
