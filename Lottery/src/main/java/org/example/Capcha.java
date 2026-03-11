package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Capcha {

	public static void main(String[] args) {
		List<String> l = parseOfficialURL();
		l.forEach(System.out::println);
	}

	// ====== 1) Обвивка без аргументи (за да няма compile errors) ======
	static List<String> parseOfficialURL() {
		final String fromFilePath = "/Users/blagojnikolov/Desktop/@tmp/fromSite.txt";
		final String prevDraw = readDB(fromFilePath).getLast();

		final String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
		return parseOfficialURL(prevDraw, ua);
	}

	// ====== 2) Реалната логика ======
	private static List<String> parseOfficialURL(String prevDraw, String ua) {

		// A) Първи опит: info.toto.bg (твоят оригинал)
		try {
			Document page = Jsoup.connect("https://info.toto.bg/")
					.userAgent(ua)
					.referrer("https://www.google.com/")
					.timeout(10_000)
					.get();

			if (isCaptchaPage(page)) {
				System.err.println("✗ Грешка: Върната е CAPTCHA страница (title=" + page.title() + "). Пробвам toto.bg ...");
				return parseFromTotoBgOrPrev(prevDraw, ua);
			}

			Elements draws = page.select("span.ball-white");
			Elements dates = page.select("div.tiraj");

			if (dates.isEmpty() || draws.size() < 6) {
				System.err.println("✗ Грешка: Липсват очакваните елементи в info.toto.bg. Пробвам toto.bg ...");
				return parseFromTotoBgOrPrev(prevDraw, ua);
			}

			// "Тираж 11 - 12.02.2026"
			String dateTmp = dates.first().text().trim();
			dateTmp = normalizeText(dateTmp);

			String inputDay = dateTmp.split("-")[1].trim();
			String drawIdx = dateTmp.split("-")[0].trim().split(" ")[1].trim();

			DateTimeFormatter inF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter outF = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
			String date = LocalDate.parse(inputDay, inF).format(outF);

			StringBuilder sb = new StringBuilder(64);
			sb.append(drawIdx).append("-").append(date).append("-");

			for (int i = 0; i < 6; i++) {
				if (i > 0) sb.append(", ");
				sb.append(draws.get(i).text());
			}

			return List.of(sb.toString());

		} catch (IOException e) {
			System.err.println("✗ Грешка: I/O към info.toto.bg. Пробвам toto.bg ...");
			return parseFromTotoBgOrPrev(prevDraw, ua);
		}
	}

	// ====== 3) Резервен официален източник: toto.bg ======
	private static List<String> parseFromTotoBgOrPrev(String prevDraw, String ua) {
		// Пробвай и двата езика. При теб браузърът зарежда без CAPTCHA.
		// Ако единият не съдържа текстовите маркери, другият може.
		final String[] urls = {
				"https://www.toto.bg/?lang=1&pid=toto1_10",
				"https://www.toto.bg/?lang=2&pid=toto1_10"
		};

		for (String url : urls) {
			try {
				Document page = Jsoup.connect(url)
						.userAgent(ua)
						.referrer("https://www.google.com/")
						.timeout(15_000)
						.followRedirects(true)
						.get();

				if (isCaptchaPage(page)) {
					System.err.println("✗ Грешка: CAPTCHA и в " + url + " (title=" + page.title() + ")");
					continue;
				}

				// Запис на това, което Jsoup наистина получава (за край на гадаенето)
				dumpDebug(page, url.contains("lang=1") ? "toto_bg_lang1" : "toto_bg_lang2");

				String text = normalizeText(page.text());

				// 1) Търсим реда за тираж (BG/EN)
				Matcher md = Pattern.compile("\\b(?:Тираж|Draw)\\s+(\\d+)\\s*-\\s*(\\d{2}\\.\\d{2}\\.\\d{4})\\b",
								Pattern.CASE_INSENSITIVE)
						.matcher(text);

				if (!md.find()) {
					System.err.println("✗ Грешка: Не намерих 'Тираж/Draw N - dd.MM.yyyy' в " + url);
					continue;
				}

				String drawIdx = md.group(1);
				String inputDay = md.group(2);

				DateTimeFormatter inF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				DateTimeFormatter outF = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
				String date = LocalDate.parse(inputDay, inF).format(outF);

				// 2) След реда за тиража взимаме първата валидна шестица 1..49
				String after = text.substring(md.end());
				int[] nums = findFirstValidSix_1to49(after);

				StringBuilder sb = new StringBuilder(64);
				sb.append(drawIdx).append("-").append(date).append("-");
				for (int i = 0; i < 6; i++) {
					if (i > 0) sb.append(", ");
					sb.append(nums[i]);
				}
				return List.of(sb.toString());

			} catch (IOException ignored) {
				System.err.println("✗ Грешка: I/O към " + url);
			} catch (RuntimeException ex) {
				System.err.println("✗ Грешка: Неочакван формат в " + url + " : " + ex.getMessage());
			}
		}

		return List.of(prevDraw);
	}

	// ====== 4) CAPTCHA детекция (твоя Radware случай) ======
	private static boolean isCaptchaPage(Document page) {
		String title = page.title();
		if (title != null && title.toLowerCase(Locale.ROOT).contains("captcha")) return true;

		String html = page.outerHtml();
		return html.contains("Radware Captcha Page")
				|| html.contains("captcha.perfdrive.com")
				|| html.contains("cdn.perfdrive.com")
				|| html.contains("shieldsquare_styles")
				|| html.contains("aperture/aperture.js");
	}

	// ====== 5) Нормализация на тирета/интервали ======
	private static String normalizeText(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);
		s = s.replace('\u00A0', ' ');
		s = s.replace('\u2013', '-').replace('\u2014', '-').replace('\u2011', '-')
				.replace('\u2212', '-').replace('\uFF0D', '-');
		return s.replaceAll("\\s+", " ").trim();
	}

	// ====== 6) Намира първата валидна шестица 1..49 (без повторение) ======
	private static int[] findFirstValidSix_1to49(String text) {
		Matcher m = Pattern.compile("\\b(\\d{1,2})\\b").matcher(text);
		ArrayDeque<Integer> win = new ArrayDeque<>(6);

		while (m.find()) {
			int v = Integer.parseInt(m.group(1));
			if (v < 1 || v > 49) {
				win.clear();
				continue;
			}

			win.addLast(v);
			if (win.size() > 6) win.removeFirst();

			if (win.size() == 6) {
				HashSet<Integer> set = new HashSet<>(win);
				if (set.size() == 6) {
					int[] out = new int[6];
					int i = 0;
					for (int x : win) out[i++] = x;
					return out;
				}
			}
		}
		throw new IllegalStateException("Не са намерени 6 валидни числа (1..49) след реда за тиража.");
	}

	// ====== 7) Debug: записва реалното съдържание от Jsoup ======
	private static void dumpDebug(Document page, String tag) {
		try {
			Path dir = Paths.get(System.getProperty("user.home"), "Desktop", "@tmp");
			Files.createDirectories(dir);

			Path htmlFile = dir.resolve(tag + ".html");
			Path txtFile = dir.resolve(tag + ".txt");

			Files.writeString(htmlFile, page.outerHtml(), StandardCharsets.UTF_8);
			Files.writeString(txtFile, page.text(), StandardCharsets.UTF_8);
		} catch (Exception ignored) {
		}
	}

	// ====== ТВОЯ метод (оставям го като е, ти си го имаш) ======
	static List<String> readDB(String path) {
		// <-- тук е твоят код
		return List.of("10-08 Feb 2026-12, 20, 26, 30, 41, 49");
	}
}

