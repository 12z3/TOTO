package task.TOTO.TotoTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeToTOTO {
    public static void main(String[] args) {

        LocalDateTime ldt = getLocalDateTime();
        whatTimeToTotoIs(ldt);
    }

    private static LocalDateTime getLocalDateTime() {

        String currentDateTime = "2023 01 29 18 45 ";
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
        long hour1 = now.getHour();
        long hour2 = timeOfToto.getHour();
        long dHours = (hour2 - hour1);
        int count = 0;

        //TODO: Как ще направиш проверката за минала дата?

        if (dYear == 0) {
            dDays = timeOfToto.getDayOfMonth() - now.getDayOfMonth();
        } else if (dYear < 0 || dYear > 2) {
            dDays = -1;
        } else {
            dDays = 31 - (now.getDayOfMonth() + timeOfToto.getDayOfMonth());
        }

        if (dHours < 0) {
            dHours = 24 - (hour1 - hour2);
            count--;
        }

        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();
        long dMins = (min2 - min1);
        if (dMins < 0) {
            dMins = 59 - (min1 - min2);
            dHours--;
        }

        while (dDays != 0) {
            dDays--;
            count++;
        }

        if (count < 0) {
            System.out.print("Има нещо сбъркано в ДАТАТА");
            return;
        }


        System.out.println("The Day is: " + timeOfToto.format(formatDate));
        System.out.println("ToDay is: " + now.format(formatDate) + "\n" + "\n"
                + "Reminders: "
                + count + " days (in " + timeOfToto.getDayOfWeek() + ") "
                + (dHours + " hours " + "and "
                + (dMins + " minutes")));
    }
}
