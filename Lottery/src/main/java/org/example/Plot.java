package org.example;

import java.util.Scanner;

public class Plot {
	static long graphAndHistogram() {
		long sTime = 0, eTime = 0;
		Scanner scanner = new Scanner(System.in);
		System.out.print("\nГрафика трябва ли ти... ?: ");
		String answer = Manipulate.answer();
		while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")) {
			System.out.println("Y||N: ");
			answer = scanner.nextLine();
		}
		if (answer.equalsIgnoreCase("Y")) {
			sTime = System.nanoTime();
			new XYData().plotStd();
			eTime = System.nanoTime();
		}
		return eTime - sTime;
	}
}
