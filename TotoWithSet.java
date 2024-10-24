package Jackpot;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TotoWithSet {

	// todo: Използвай филтър на Калман за да предсакжеш следващото най-вероятно std и на базата на него си
	//       генерирай масива ;) (Разпределението на std във времето го имаш.)
	//       С LabView си генерирай файла и дерзай.

	private final int matchedSums = 9;
	private final double std = 7; // 10

	public static void main(String[] args) throws IOException {
		new TotoWithSet().play();
	}

	//	Generates sets of lottery numbers with a target standard deviation and number of matches to a previous draw.
	private void play() throws IOException {
//		List<List<Integer>> suppose = digitsGenerator(3);
//		printRes(suppose);
		List<Integer> officialRes = new ArrayList<>(List.of(2, 12, 20, 23, 34, 45));
		double officialRsStd = stdOfRow(officialRes);
		System.out.println(officialRes);

//		for (List<Integer> row : suppose) {
//			System.out.print(checkMatchesByRow(row, officialRs) + "   ");
//		}
//		System.out.println();
		long start = System.nanoTime();
		List<List<Integer>> res = generatorByMatches(officialRes, this.matchedSums, this.std);
		printRes(res);
		TestStd(res);
		double duration = (System.nanoTime() - start) / Math.pow(10, 9);

		if (duration >= 60 && duration < 3600) {
			System.out.printf("\nduration: %.0f min", Math.ceil(duration) / 60);
		} else if (duration >= 3600) {
			System.out.printf("\nduration: %.2f hour", Math.ceil(duration) / 3600);
		} else if (duration < 60) {
			System.out.printf("\nduration: %.0f s", Math.ceil(duration));
		} else {
			System.out.println("\n404: Нещо безвъзвратно се е объркало ;)");
		}

		System.out.println();
		int[][] tmpResMatrix = print2DRes(res, officialRes);
		writer(res, officialRes, duration, this.matchedSums, std);
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
		SecureRandom rnd = new SecureRandom();
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
		SecureRandom random = new SecureRandom();
		Set<Integer> digits = new HashSet<>();

		while (digits.size() < numOfDigits) {
			digits.add(random.nextInt(1, 50));
		}
		List<Integer> res = new ArrayList<>(digits);
		Collections.sort(res);
		return res;

	}


	// Генерира три масива чиито елементи имат средноквадратично отклонение = std
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

		while (tmpSum < matchesSum) {
			tmpSum = 0;
			suppose = generatorByStd(std);
			for (List<Integer> row : suppose) {
				tmpSum += checkMatchesByRow(row, officialRes);
			}
			attempt++;
		}
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

	// Оптимизиран метод за изчисляване на стандартното отклонение:
	private static double optimizedStdOfRow(List<Integer> row) {
		double sum = 0;
		double squaredSum = 0;
		for (int el : row) {
			sum += el;
			squaredSum += el * el;
		}
		double mean = sum / row.size();
		return Math.sqrt((squaredSum / row.size()) - (mean * mean));  // Variance = (1/N)*SuMi(Xi*Xi) - (avrXi*avrXi)
	}

	private static void TestStd(List<List<Integer>> tmp) {
		System.out.print("Stds: ");
		for (int i = 0; i < tmp.size(); i++) {
			if (i < 2) {
				System.out.print(stdOfRow(tmp.get(i)) + " | ");
			} else System.out.print(stdOfRow(tmp.get(i)));
		}
	}

	static Double setTime(double time) {
		int hour = (int) time;
		int minutes = (int) (time - hour) * 60;
		String hStr, mStr, resStr;
		hStr = String.valueOf(hour);
		mStr = String.valueOf(minutes);
		resStr = hStr + mStr;

		return Double.parseDouble(resStr);
	}

	private static int[][] print2DRes(List<List<Integer>> res, List<Integer> officialRes) {
		int[][] matrix = fill649Arr();
		int[][] tmp = new int[0][];

		for (int i = 0; i < res.size(); i++) {
			List<Integer> thisRow = res.get(i);
			tmp = searchIntoThisRow(thisRow, matrix);
		}
		return tmp;
	}

	private static int[][] searchIntoThisRow(List<Integer> thisRow, int[][] matrix) {
		int[][] tmp = new int[7][7];
		for (int el : thisRow) {
			for (int i = 0; i < matrix.length; i++) {
				int idx = bs(matrix[i], el);
				if (idx != -1) {
					tmp[i][idx] = el;
				}
			}
		}
		print2D(tmp);
		return tmp;
	}

	private static void print2D(int[][] tmp) {
		System.out.println();
		for (int[] arr : tmp) {
			for (Integer el : arr) {
				if (el != 0) {
					System.out.print(el + " ");
				} else System.out.print(" - ");
			}
			System.out.println();
		}
	}

	private static int[][] fill649Arr() {
		int[][] arr = new int[7][7];
		int cnt = 1;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				arr[i][j] = cnt;
				cnt++;
			}
		}
		return arr;
	}

	static void writer(List<List<Integer>> res,
					   List<Integer> officialResult, double duration, double matchedSum, double std) throws IOException {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
		String currentTime = time.format(formatter);
		File file = new File(
				 currentTime + ".txt");
		FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);

		writer.write(currentTime + "\n");
		writer.write("\n" + "Official result: " + officialResult + "\n");
		writer.write("Matched Sum: " + matchedSum + " - " + "Std: " + std + "\n");

		if (duration >= 60 && duration < 3600) {
			String timeX = String.valueOf(Math.ceil((duration) / 60)).formatted("%.0f");
			writer.write(timeX + " min" + "\n");
		} else if (duration >= 3600) {
			String timeX = String.valueOf(Math.ceil((duration) / 3600)).formatted("%.0f");
			writer.write(timeX + " hour" + "\n");
		} else if (duration < 60) {
			writer.write(Math.ceil(duration) + " sec" + "\n");
		} else {
			System.out.println("\n404: Нещо безвъзвратно се е объркало ;)\n");
		}
		writer.write("\n");


		for (List<Integer> row : res) {
			for (Integer el : row) {
				writer.write(el + " ");
			}
			writer.write("\n");
		}
		writer.write("\n");

		int[][] matrix = fill649Arr();
		for (List<Integer> row : res) {
			int[][] tmp = new int[7][7];
			for (int el : row) {
				for (int i = 0; i < matrix.length; i++) {
					int idx = bs(matrix[i], el);
					if (idx != -1) {
						tmp[i][idx] = el;
					}
				}
			}

			for (int i = 0; i < tmp.length; i++) {
				for (int j = 0; j < tmp[i].length; j++) {
					if (tmp[i][j] != 0) {
						writer.write(String.valueOf(tmp[i][j] + " "));
					} else writer.write(" - ");
				}
				writer.write("\n");
			}
			writer.write("\n");
		}
		writer.close();
	}

	static int bs(int[] arr, int target) {
		int s = 0, e = arr.length - 1, m;

		while (s <= e) {
			m = s + (e - s) / 2;
			if (arr[m] == target) {
				return m;
			} else if (arr[m] < target) {
				s = m + 1;
			} else e = m - 1;
		}
		return -1;
	}
}
