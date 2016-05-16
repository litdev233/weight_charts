package net.litdev.weight_charts.utils;


import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * 默认时间格式  yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    //一分钟
    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    //一小时
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    //一天
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    /**
     * 获取当前时间
     * @param formatStr 日期的格式，默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String GetNowTime(String formatStr){
        if(TextUtils.isEmpty(formatStr)){
            return new SimpleDateFormat(DATE_FORMAT_DEFAULT).format(new Date());
        }
        else{
            String str = "";
            try {
                str = new SimpleDateFormat(formatStr).format(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }
    }


    /**
     * 获取短时间，几分钟前等
     * @param dataStr 时间字符串
     * @param formatStr 时间的格式，默认 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getShortTime(String dataStr,String formatStr){
        String str= "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TextUtils.isEmpty(formatStr) ? DATE_FORMAT_DEFAULT : formatStr);

            Date date = sdf.parse(dataStr);
            Date curDate = new Date();

            long durTime = curDate.getTime() - date.getTime();
            int dayStatus = calculateDayStatus(date, curDate);

            if(durTime <= 10 * ONE_MINUTE_MILLIONS) {
                str = "刚刚";
            } else if(durTime < ONE_HOUR_MILLIONS) {
                str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
            } else if(dayStatus == 0) {
                str = durTime / ONE_HOUR_MILLIONS + "小时前";
            } else if(dayStatus == -1) {
                str = "昨天" + DateFormat.format("HH:mm", date);
            } else if(isSameYear(date, curDate) && dayStatus < -1) {
                str = DateFormat.format("MM-dd", date).toString();
            } else {
                str = DateFormat.format("yyyy-MM", date).toString();
            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return str;

    }

    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }
}
