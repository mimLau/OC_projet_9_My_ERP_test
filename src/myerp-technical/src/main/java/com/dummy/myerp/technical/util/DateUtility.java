package com.dummy.myerp.technical.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtility {

    /**
     * Use to convert a Date into a Calendar object
     * @param date the date to be converted
     * @return a Calendar object
     */
    public static Calendar convertToCalender(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
