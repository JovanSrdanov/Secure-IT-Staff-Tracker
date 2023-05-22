package jass.security.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static Boolean isEndDateAfterStartDate(Date startDate, Date endDate) {
        return endDate.after(startDate);
    }
}
