package org.example;

import java.io.FileNotFoundException;
import java.util.List;


/*
		 Сайта ако не е достъпен, съответният ред от масива на siteData ще бъде:
		 "Грешка: Неуспешно свързване със сайта!" -> трябва да върнеш последният валиден тираж, а не Грешката...
		 Това автоматично ще доведе до exception в forNewDraw(), getThreeOfficialDB(), getNewDrawFromSite(),
		                                         forNewDrawN(siteData);
 */

/*
    todo 18.08.25: Чупи ти се проверката за липсващи тиражи, защото:
           - Проверяваш дали има нов тираж и в трите сайта без да държиш сметка от кой сайт е тиража.
           - Има ли, записваш във всички файлове.
           - Търсиш липсващ като сравняваш индексите на записите във файловете, но:
                - в  officialToto.txt Уж пазиш резултатите от даденият сайт, а записваш независимо дали
                  новият тираж е от този сайт.
                -
           - Пренапиши и двата метода за търсене като вмъkнеш в messageX(..... siteData)
           - WriteAll може да не го пипаш. Добави проверка за последният индекс на записаният тираж
                  дали съответства на индекса на тиража от сайта.
                - След което ако idx1 < idx2 то липсващите са idx1 + (idx2 - idx1).... помисли го.


 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        long start = System.nanoTime();
        System.out.println();
        //Извлича данните: При положение, че има ЕДИН различен ще върне, че има нов тираж.
        // За да избегнеш това трябва да държиш сметка дали метода (forNewDraw()) е изпълняван или не за този ден.
        List<String> siteData = Manipulate.getThreeOfficialDB();
        String lastDrawProfitableInfo = Profitable.getLastOfficialProfitInfo();
        if (lastDrawProfitableInfo.equalsIgnoreCase("WiFi ERROR.")) return;

        //List<String> siteData;
        //siteData = ParseURL.readDB("/Users/blagojnikolov/Desktop/@tmp/officialToto.txt");
        long parseTime = System.nanoTime();

        siteData.forEach(System.out::println);
        System.out.println();

        //Проверява и записва:
        long checkTime = System.nanoTime();
        Checks.forNewDraw(siteData);
        Std.calculateAndWriteStd();
        long midTime = System.nanoTime();

        Print.lastOfficialDraw();
        Print.lastDrawProfitInfo(lastDrawProfitableInfo);

        String check = Checks.yourSupposes(siteData);
        if (check.equalsIgnoreCase("Error")) {
            System.out.println("Грешка в Checks.yourSupposes() ...");
            return;
        }

        long pltTime = Plot.graphAndHistogram();
        long profitTime = Print.profitInfo();

        //Генерира нов залог:
        //long sGenTime = System.nanoTime();
        long time = Generator.newProposal();
        //long eGenTime = System.nanoTime();
        long end = System.nanoTime();

        double webTime = (((parseTime - start)) / 1000_000.0);
        double checksTime = ((midTime - checkTime) / 1000_000.0);
        double genNewDrawTime = time / 1000_000.0;
        double plotTime = pltTime / 1000_000.0;
        double totalTime = (end - start) / 1000_000.0;
        double printNewDrawTime = (totalTime - (webTime + checksTime + genNewDrawTime + plotTime)) / 1000.0;

        System.out.printf("%nЗаявка към сайтовете: %.1f ms%n", webTime);
        System.out.printf("Проверка: %.1f ms%n", checksTime);
        System.out.printf("Генериране на нов залог: %.1f ms%n", genNewDrawTime);
        System.out.printf("Изчертаване на графиките: %.1f ms%n", plotTime);

        Print.duration(printNewDrawTime, "Въвеждане и печатане на нов залог");
        Print.duration(totalTime / 1000.0, "Общо време");

    }
}
