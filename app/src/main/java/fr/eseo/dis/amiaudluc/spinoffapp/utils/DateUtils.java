package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

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

    public static String getStringFromDate(LocalDate date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.CLASSIC_DATE, Locale.US);
        return dateFormat.format(date);
    }

    public static String hoursFromMinutes(int minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        if (duration.minus(Duration.of(59, ChronoUnit.MINUTES)).getSeconds() < 0) {
            // minutes
            return minutes +  " min.";
        }
        return LocalTime.MIDNIGHT.plus(duration).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
