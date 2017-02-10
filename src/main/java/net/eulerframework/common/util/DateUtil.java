package net.eulerframework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class DateUtil {
    
    public static Calendar now(){
        return Calendar.getInstance();
    }

    public static void setZero(Calendar calendar, int... field){
        for(int each : field){
            calendar.set(each, 0);
        }       
    }
    
    public static Calendar beginningOfTheDay(Calendar calendar){
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendar.getTime());
        DateUtil.setZero(cal, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        return cal;
    }

    public static Calendar endingOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static Calendar beginningOfTheDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.beginningOfTheDay(cal);
    }

    public static Calendar endingOfTheDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.endingOfTheDay(cal);
    }
    
    public static Calendar toCalendar(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Date parseDate (String source, String pattern) throws ParseException{
        return new SimpleDateFormat(pattern).parse(source);
    }

    public static Date parseDate (String source, String pattern, Locale local) throws ParseException{
        return new SimpleDateFormat(pattern, local).parse(source);
    }

    public static String formatDate (Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }
}
