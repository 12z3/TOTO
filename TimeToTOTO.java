package task.TOTO.TotoTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TimeToTOTO {
    public static void main(String[] args) {

        LocalDateTime ldt = getLocalDateTime();
        whatTimeToTotoIs(ldt);
    }

    private static LocalDateTime getLocalDateTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Кога е следващият Тираж? " +
                "\nВъведи (година месец ден час минути) разделени с интервал: ");       // next:2022 12 25 18 45

        String[] dataTimeFormatAnswer = scanner.nextLine()
                .trim()
                .split(" ");
        int year = Integer.parseInt(dataTimeFormatAnswer[0]);
        int month = Integer.parseInt(dataTimeFormatAnswer[1]);
        int dayOfMonth = Integer.parseInt(dataTimeFormatAnswer[2]);
        int hour = Integer.parseInt(dataTimeFormatAnswer[3]);
        int minute = Integer.parseInt(dataTimeFormatAnswer[4]);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    private static void whatTimeToTotoIs(LocalDateTime timeOfToto) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a c 'ден:' HH:hh:ss ч ");

        LocalDateTime now = LocalDateTime.now();

        int count = 0;
        int dDays = timeOfToto.getDayOfMonth() - now.getDayOfMonth();

       while (dDays < 0) {
            System.out.println("... Я си оправи времената");
            dDays = getLocalDateTime().getDayOfMonth() - now.getDayOfMonth();
        }
        while (dDays != 0) {
            dDays--;
            count++;
        }
        long hour1 = now.getHour();
        long hour2 = timeOfToto.getHour();
        long dHours = Math.abs(hour2 - hour1);

        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();
        long dMins = Math.abs(min2 - min1);

       // if (dDays == 1 && dHours == 0 && dMins == 0) dDays = 0;

            System.out.println("The Day is: " + timeOfToto.format(formatDate));
        System.out.println("ToDay is: " + now.format(formatDate) + "\n" + "\n"
                + "Reminders: "
                + count + " days (in " + timeOfToto.getDayOfWeek() + ") - "
                + (dHours + " hours " + "and "
                + (dMins + " minutes")));
    }
}
