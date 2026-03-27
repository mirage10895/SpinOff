package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

public class DateUtils {

    public static final String CLASSIC_DATE = "yyyy-MM-dd";

    public static String toString(LocalDate date) {
        return date.getDayOfMonth()
                + " - " + date.getMonth().getValue()
                + " - " + date.getYear();
    }

    public static String toDisplayString(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(dateTimeFormatter);
    }

    public static String displayDuration(int minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        if (duration.minus(Duration.of(59, ChronoUnit.MINUTES)).getSeconds() < 0) {
            // minutes
            return minutes +  " min.";
        }
        if (duration.minus(Duration.of(24, ChronoUnit.HOURS)).getSeconds() < 0) {
            // heures
            return LocalTime.MIDNIGHT.plus(duration).format(DateTimeFormatter.ofPattern("H'h'mm"));
        }
        long jours = minutes / 1440;
        int heures = (minutes % 1440) / 60;
        return jours + "j. " + heures + "h.";
    }
}
