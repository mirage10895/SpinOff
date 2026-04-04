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

    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DISPLAY_HOUR_FORMATTER = DateTimeFormatter.ofPattern("H'h'mm");
    public static final DateTimeFormatter CLASSIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String toDisplayString(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    public static String displayDuration(int minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        if (duration.minus(Duration.of(59, ChronoUnit.MINUTES)).getSeconds() < 0) {
            // minutes
            return minutes +  " min.";
        }
        if (duration.minus(Duration.of(24, ChronoUnit.HOURS)).getSeconds() < 0) {
            // heures
            return LocalTime.MIDNIGHT.plus(duration).format(DISPLAY_HOUR_FORMATTER);
        }
        long jours = minutes / 1440;
        int heures = (minutes % 1440) / 60;
        return jours + "j. " + heures + "h.";
    }
}
