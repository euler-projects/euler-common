/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.eulerframework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtils {
    
    public static Calendar now(){
        return Calendar.getInstance();
    }

    public static void setFiledToZero(Calendar calendar, int... field){
        for(int each : field){
            calendar.set(each, 0);
        }       
    }
    
    public static Calendar beginningOfTheDay(Calendar calendar){
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendar.getTime());
        DateUtils.setFiledToZero(cal, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
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
        return DateUtils.beginningOfTheDay(cal);
    }

    public static Calendar endingOfTheDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtils.endingOfTheDay(cal);
    }
    
    public static Calendar toCalendar(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Date parseDate (String source, String pattern) throws ParseException{
        return new SimpleDateFormat(pattern).parse(source);
    }

    public static String formatDate (Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parseDateFromUnixTimestamp(long unixTimestamp) {
        return new Date(unixTimestamp * 1000);
    }
    
    public static long getUnixTimestamp(Date date) {
        return date.getTime() / 1000;
    }
}
