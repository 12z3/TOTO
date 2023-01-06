package task.TOTOOLD.TotoTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TimeToTOTO {
    public static void main(String[] args) {

        LocalDateTime ldt = getLocalDateTime();
        whatTimeToTotoIs(ldt);
    }

    private static LocalDateTime getLocalDateTime() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Кога е следващият Тираж? " +
//                "\nВъведи (година месец ден час минути) разделени с интервал: ");       // next:2023 01 05 18 45

        String currentDateTime = "2023 01 08 18 45";

        String[] dataTimeFormatAnswer = currentDateTime
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
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy, E - a- c 'ден:' HH:mm:ss ч ");
        DateTimeFormatter formatDateA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();

        int dYear = timeOfToto.getYear() - now.getYear();
        int dDays = timeOfToto.getDayOfMonth() - now.getDayOfMonth();
        int dMinutes = timeOfToto.getMinute() - now.getMinute();
        int count = 0;

        if (dYear == 0) {
            dDays = timeOfToto.getDayOfMonth() - now.getDayOfMonth();
        } else if (dYear < 0 || dYear > 2) {
            dDays = -1;
        } else {
            dDays = 31 - (now.getDayOfMonth() + timeOfToto.getDayOfMonth());
        }

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
        long dHours = (hour2 - hour1);
        if (dHours < 0) {
            dHours = 24 - (hour1 - hour2);
            //dDays--;
            count--;
        }

        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();
        long dMins = (min2 - min1);
        if (dMins < 0) {
            dMins = 60 - (min1 - min2);
            dHours--;
        }


        // if (dDays == 1 && dHours == 0 && dMins == 0) dDays = 0;

        System.out.println("The Day is: " + timeOfToto.format(formatDate));
        System.out.println("ToDay is: " + now.format(formatDate) + "\n" + "\n"
                + "Reminders: "
                + count + " days (in " + timeOfToto.getDayOfWeek() + ") - "
                + (dHours + " hours " + "and "
                + (dMins + " minutes")));
    }
}
