package com.android.example.todoer.utility;

import android.content.Context;
import android.text.format.Time;

import com.android.example.todoer.R;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class DateUtility {

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "Dec 6".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            return getFormattedMonthDay(context, dateInMillis);
        }
    }

    /**
     * Converts date format to the format "Month day", e.g "Jun 24".
     * @param context Context to use for resource localization
     * @param dateInMillis The formatted date string, expected to be of the form specified
     *                in DateUtility.DATE_FORMAT
     * @return The day in the form of a string formatted "Dec 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        return monthDayFormat.format(dateInMillis);
    }
}
