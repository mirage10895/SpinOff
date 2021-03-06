package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

public class DateUtils {

    public static final String CLASSIC_DATE = "yyyy-MM-dd";

    public static Date getDateFromString(String strDate, String strFormat){
        DateFormat format = new SimpleDateFormat(strFormat, Locale.US);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            LogUtils.e(LogUtils.DEBUG_TAG,"Error !",e);
            return null;
        }
    }

    public static String toString(Date date){
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.DATE))
                .concat(" - "+String.valueOf(cal.get(Calendar.MONTH)+1)
                        .concat(" - "+String.valueOf(cal.get(Calendar.YEAR))));
    }

    public static String getStringFromDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.CLASSIC_DATE, Locale.US);
        return dateFormat.format(date);
    }

    private static boolean isToday(Date date) {
        return android.text.format.DateUtils.isToday(date.getTime());
    }

    public static boolean isDayBefore(Date date) {
        Long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTimeInMillis(now);
        return !isToday(date) && date.before(calendar.getTime());
    }

}
