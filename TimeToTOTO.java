package task.TOTO.TotoTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeToTOTO {
    public static void main(String[] args) {

        LocalDateTime ldt = getLocalDateTime();
        whatTimeToTotoIs(ldt);

        LocalDateTime future = LocalDateTime.of(2023, 2, 5, 18, 45);
        getTimeBetweenDate(future);
    }

    // todo: При текуща дата "2023 01 30 18 45" и следваща "2023 02 02 18 45" резултата е Идиотски.
    private static LocalDateTime getLocalDateTime() {

        String currentDateTime = "2023 02 05 18 45 ";
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
        int dDays = timeOfToto.getDayOfYear() - now.getDayOfYear();
        long hour1 = now.getHour();
        long hour2 = timeOfToto.getHour();
        long dHours = (hour2 - hour1);
        long min1 = now.getMinute();
        long min2 = timeOfToto.getMinute();
        long dMins = (min2 - min1);
        int count = 0, count1 = 0, count2 = 0;

        //TODO: Как ще направиш проверката за минала дата?

        if (dYear == 0) {
            dDays = timeOfToto.getDayOfYear() - now.getDayOfYear();
        } else if (dYear < 0 || dYear > 2) {
            dDays = -1;
        } else {
            dDays = 31 - (now.getDayOfYear() + timeOfToto.getDayOfYear());
        }

        if (timeOfToto.getMonth() == now.getMonth()) {
            if (dHours <= 0) {
//                if (dHours == 0) {
//                    dHours = 23 - (hour1 - hour2);
//                } else dHours = 24 - (hour1 - hour2);

                dHours = (dHours < 0) ? 24 - (hour1 - hour2) : dHours;
                //dHours = 23 - (hour1 - hour2);
                count--;
            }
        }
        if (dMins <= 0) {
//                if (dMins == 0){
//                    dMins = 59 - (min1 - min2);
//                } else dMins = 60 - (min1 - min2);

            dMins = (dMins < 0) ? 59 - (min1 - min2) : dMins;
            //dMins = 59 - (min1 - min2);
            dHours--;
        }

        while (dDays != 0) {
            dDays--;
            count++;
        }

        if (count < 0) {
            System.out.print("Има нещо объркано в ДАТАТА");
            return;
        }
//        } else {
//           //todo: ? (текущият ден + 7) > 30(31)
//        }

        System.out.println("The Day is: " + timeOfToto.format(formatDate));
        System.out.println("ToDay is: " + now.format(formatDate) + "\n" + "\n"
                + "Reminders: "
                + count + " days (in " + timeOfToto.getDayOfWeek() + ") "
                + (dHours + " hours " + "and "
                + ((dMins) + " minutes")));
    }

    private static void getTimeBetweenDate(LocalDateTime future) {
        LocalDateTime now = LocalDateTime.now();
        //LocalDateTime future = LocalDateTime.of(2023, 2, 5, 18, 45);

        long days = ChronoUnit.DAYS.between(now, future);
        long hours = ChronoUnit.HOURS.between(now, future) % 24;
        long minutes = ChronoUnit.MINUTES.between(now, future) % 60;

        System.out.println("Time remaining: " + days + " days, " + hours + " hours, " + minutes + " minutes");
    }
}
