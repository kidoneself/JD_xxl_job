package com.xxl.job.executor.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {

    public static String forecastDay(Integer n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //Calendar实例
        Calendar dd = Calendar.getInstance();
        dd.setTime(new Date());//设置传来的时间
        dd.add(Calendar.DATE, (int) n);//在这个时间基础上加上n天
        return sdf.format(dd.getTime());
    }
}
