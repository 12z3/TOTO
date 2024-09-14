package Jackpot;

import java.util.*;

public class TotoWithSet {
	public static void main(String[] args) {
		play();
	}

	private static void play() {
//		List<List<Integer>> suppose = digitsGenerator(3);
//		printRes(suppose);
		List<Integer> officialRs = new ArrayList<>(List.of(5, 19, 21, 22, 47, 48));
		double officialRsStd = stdOfRow(officialRs);

//		for (List<Integer> row : suppose) {
//			System.out.print(checkMatchesByRow(row, officialRs) + "   ");
//		}
//		System.out.println();
		long  start = System.nanoTime();
		List<List<Integer>> res = generatorByMatches(officialRs, 8, 12.5);
		printRes(res);
		TestStd(res);
		double duration = (System.nanoTime() - start) / Math.pow(10, 9);
		System.out.printf("\nduration: %.2f s", duration);
	}

	private static void printRes(List<List<Integer>> suppose) {
		for (List<Integer> row : suppose) {
			for (int el : row) {
				System.out.print(el + " ");
			}
			System.out.println();
		}
	}

	private static List<List<Integer>> digitsGenerator(int cntOfRows) {
		List<List<Integer>> suppose = new ArrayList<>();
		Random rnd = new Random();
		Set<Integer> digits;
		int cnt = 0;

		while (cnt < cntOfRows) {
			digits = new HashSet<>();
			while (digits.size() < 6) {
				digits.add(rnd.nextInt(1, 50));
			}
			List<Integer> res = new ArrayList<>(digits);
			Collections.sort(res);
			suppose.add(res);
			cnt++;
		}
		return suppose;
	}

	private static List<Integer> sampleGenerator(int numOfDigits) {
		Random random = new Random();
		Set<Integer> digits = new HashSet<>();

		while (digits.size() < numOfDigits) {
			digits.add(random.nextInt(1, 50));
		}
		List<Integer> res = new ArrayList<>(digits);
		Collections.sort(res);
		return res;

	}


	// Генерира три масива чиито илементи имат средноквадратично отклонение = std
	private static List<List<Integer>> generatorByStd(double std) {
		int cnt = 0;
		double tmpStd = Integer.MIN_VALUE;
		List<Integer> tmpRow;
		List<List<Integer>> res = new ArrayList<>();

		while (cnt < 3) {
			tmpRow = new ArrayList<>();
			while (std != tmpStd) {
				tmpRow = sampleGenerator(6);
				tmpStd = stdOfRow(tmpRow);
			}
			Collections.sort(tmpRow);
			res.add(tmpRow);
			tmpStd = Integer.MIN_VALUE;
			cnt++;
		}
		return res;
	}


	// Генерира три масива чиито средноквадратично отклонение = std && сумарно и за трите масива броя на съвпаденията
	// в последният тираж е = matchesSum.
	private static List<List<Integer>> generatorByMatches(List<Integer> officialRes,
														  int matchesSum, double std) {
		List<List<Integer>> suppose = null;
		int tmpSum = 0, attempt = 0;
		while (tmpSum <= matchesSum) {
			tmpSum = 0;
			suppose = generatorByStd(std);
			for (List<Integer> row : suppose) {
				tmpSum += checkMatchesByRow(row, officialRes);
			}
			attempt++;
		}
		System.out.println();
		System.out.println("Attempt:  " + attempt + " <- ");
		return suppose;
	}

	// Проверка дали всяко едно число от row се съдържа в officialResult?
	private static int checkMatchesByRow(List<Integer> row, List<Integer> officialResult) {
		int matches = 0;
		for (Integer elOfRow : row) {
			for (Integer elOfOfficialResult : officialResult) {
				if (elOfRow.equals(elOfOfficialResult)) {
					matches++;
					break;
				}
			}
		}
		return matches;
	}

	// Генерира масив чиито средноквадратично отклонение е = std
	private static double stdOfRow(List<Integer> row) {
		double avrByRow, sum = 0, avrSums = 0;

		for (int el : row) sum += el;
		avrByRow = sum / row.size();
		for (int el : row) {
			double x = (Math.pow((el - avrByRow), 2));
			avrSums += x;
		}
		return Math.sqrt(avrSums / row.size());
	}

	private static void TestStd(List<List<Integer>> tmp) {
		System.out.print("Stds: ");
		for (int i = 0; i < tmp.size(); i++) {
			if (i < 2) {
				System.out.print(stdOfRow(tmp.get(i)) + " | ");
			} else System.out.print(stdOfRow(tmp.get(i)));
		}
	}
}
