package org.example;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatTxt {

	/**
	 * Валидира потребителски вход, който трябва да съдържа само текст без никакви цифри.
	 * <p>
	 * Методът приема регулярен израз и входна стойност, която валидира срещу този шаблон.
	 * Ако стойността не отговаря, потребителят се подканя да въведе нова, докато не бъде въведен коректен текст.
	 *
	 * <p>Подходящ регулярен израз за текст без цифри:
	 * <pre>{@code ^[a-zA-Zа-яА-ЯёЁ]+$}</pre>
	 * - Позволява само букви (латиница и кирилица)
	 * - Не допуска числа, специални символи или интервали
	 *
	 * @param scanner Обект {@link Scanner} за четене на потребителския вход.
	 * @param text    Начална стойност за валидиране.
	 * @return Стойността, която отговаря на шаблона и е приета за валиден текст.
	 */
	static boolean textValidator(Scanner scanner, String text) {
		boolean isText = false;
		String regEx = "^\\D*$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);

		while (!matcher.matches()) {
			System.out.print("Това чудо съдържа цифри... Трябва да бъде само текст:");
			text = scanner.nextLine();
			matcher = pattern.matcher(text);
		}
		if (matcher.matches()) isText = true;
		return isText;
	}

	/**
	 * Валидира и преобразува входа от потребителя до валиден брой съвпадения (между 0 и 6).
	 *
	 * <p>Методът извършва следните действия:</p>
	 * <ol>
	 *   <li>Проверява дали входният стринг е число с помощта на {@code isDigit(String)}</li>
	 *   <li>Ако не е число, изисква нов вход, докато не получи валидно число</li>
	 *   <li>Преобразува числото в double, закръгля нагоре с {@code Math.ceil(...)} и каства към int</li>
	 *   <li>Проверява дали числото попада в допустимия интервал [0, 6]</li>
	 *   <li>Ако не попада, продължава да изисква вход, докато не получи валидна стойност</li>
	 * </ol>
	 *
	 * <p>Използва се при въвеждане на стойност за броя съвпадения с предишен тираж.</p>
	 *
	 * @param line    началният вход от потребителя (може да е невалиден)
	 * @param scanner обект на {@link Scanner}, използван за четене от конзолата
	 * @return валидно цяло число от 0 до 6 (включително)
	 */
	static int makeMatches(String line, Scanner scanner) {
		while (!Checks.isDigit(line)) {
			System.out.print(line + " 6 > трябва да бъде число > 0: ");
			line = scanner.nextLine();
		}
		int x = (int) Math.ceil(Double.parseDouble(line));
		while (x < 0 || x >= 6) {
			System.out.print(line + " трябва да бъде > 0 и < 6: ");
			line = scanner.nextLine();
			while (!Checks.isDigit(line)) {
				System.out.print(line + " трябва да бъде число: ");
				line = scanner.nextLine();
			}
			x = (int) Math.ceil(Double.parseDouble(line));
		}
		return x;
	}
}
