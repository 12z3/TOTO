package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Клас {@code WebData} отговаря за зареждане и представяне на индексирани
 * тиражи на тото игри, извлечени от файл с изход от уебсайт (например `fromSite.txt`).
 *
 * <h2>Какво прави?</h2>
 * <ul>
 *   <li>Чете редове от подаден текстов файл с информация за тегления</li>
 *   <li>Извлича числов индекс на всеки ред (обикновено номер на тираж)</li>
 *   <li>Формира структура тип {@code Map<Integer, List<Integer>>} с индекс като ключ и списък с числа (тираж) като стойност</li>
 * </ul>
 *
 * <h2>Как го прави?</h2>
 * <ul>
 *   <li>Методът {@link #getWebIdxFromFileData(String)}:
 *     <ul>
 *       <li>Чете всички редове от файл с помощта на {@code ParseURL.readFromFile()}</li>
 *       <li>Извлича първия елемент на реда (до три последователни интервала) като числов индекс</li>
 *       <li>Добавя този индекс в списък</li>
 *     </ul>
 *   </li>
 *   <li>Методът {@link #getLastWebIdxDrawMap(List, int)}:
 *     <ul>
 *       <li>Създава {@code TreeMap}, в който добавя една двойка: индекс → списък с числата от тиража</li>
 *       <li>Може да се използва като структура за изграждане на база данни или анализ на резултати</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Защо го прави?</h2>
 * <ul>
 *   <li>За да позволи лесно свързване на конкретен тираж (по индекс) със списък от изтеглени числа</li>
 *   <li>Позволява структурирана обработка на данни от онлайн източник</li>
 *   <li>Подходящо за сортиране, филтриране или последваща статистическа обработка</li>
 * </ul>
 *
 * <h2>Примери за употреба:</h2>
 * <pre>{@code
 * List<Integer> lastDraw = WebData.getWebIdx("/path/to/bgTotoFromSite.txt");
 * Map<Integer, List<Integer>> drawMap = WebData.getLastWebIdxDrawMap(lastDraw, 1556);
 * }</pre>
 *
 * <h2>Зависимости:</h2>
 * <ul>
 *   <li>{@code ParseURL.readFromFile()} – метод за четене на файл с редове като {@code List<String>}</li>
 *   <li>{@code java.util.TreeMap, java.util.List, java.util.ArrayList}</li>
 * </ul>
 *
 * @author bNN
 * @version 1.0
 */
public class WebData {
	public static void main(String[] args) {
		getWebIdxFromFileData("/Users/blagojnikolov/Desktop/@tmp/fromSite49.txt").forEach(System.out::println);
	}

	/**
	 * Връща номера и тиража на съответното теглене.
	 *
	 * @param draw: резултата от тегленето на тираж с този номер.
	 * @param idx:  номера на тиража.
	 * @return Мап: номер-тираж.
	 */
	public static Map<Integer, List<Integer>> getLastWebIdxDrawMap(List<Integer> draw, int idx) {
		Map<Integer, List<Integer>> m = new TreeMap<>();
		m.put(idx, draw);
		return m;
	}

	/**
	 * Зарежда редове от текстов файл, намиращ се на подадения {@code path}.
	 * <p>
	 * Методът се използва за четене на данни, обикновено индексирани редове от предишно записани тото тегления,
	 * които впоследствие се използват за сравнения или анализи.
	 *
	 * <p>При неуспешен достъп до файла (например ако не съществува или не може да бъде прочетен),
	 * методът извежда съобщение в конзолата и връща празен списък.
	 *
	 * <p><b>Пример:</b></p>
	 * <pre>{@code
	 * List<String> draws = getWEBIdx("/Users/blagojnikolov/Desktop/@tmp/bgTotoFromSite.txt");
	 * // -> [27   03 Apr 2025   25, 29, 31, 34, 38, 44, ...]
	 * }</pre>
	 *
	 * @param path Пълният път до текстов файл, съдържащ редове с данни.
	 * @return Списък от индексите на тиражите съдържащи се във файла; Празен списък при грешка.
	 */
	public static List<Integer> getWebIdxFromFileData(String path) {
		List<Integer> res = new ArrayList<>();
		try {
			File fromFile = new File(path);
			List<String> lines = ParseURL.readFromFile(fromFile);
			for (String line : lines) {
				String s = line.trim().split(" {3}")[0];
				res.add(Integer.parseInt(s.trim()));
			}
		} catch (Exception e) {
			System.out.println("404: getWEbIdx():" +
					" Няма достъп до файла " + path);
		}
		return res;
	}
}
