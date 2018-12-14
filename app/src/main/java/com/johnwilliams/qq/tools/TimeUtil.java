package com.johnwilliams.qq.tools;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SimpleDateFormat sdf_today = new SimpleDateFormat("HH:mm", Locale.CHINA);
    public static String formatTime(long time){
        if (DateUtils.isToday(time)){//Today
            return sdf_today.format(time);
        } else {
            String date = sdf.format(time);
            Date today = new Date();
            if (date.substring(0, 4).equals(sdf.format(today).substring(0, 4)))
                return date.substring(5);
            else
                return date;
        }
    }
}
