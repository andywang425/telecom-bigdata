package com.example.telecom.util;

import org.apache.commons.math3.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateTimeUtils {
    /**
     * 一小时的纳秒数
     */
    private static final long NANOS_PER_HOUR = 3600000000000L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    /**
     * 获取指定范围内的一个随机日期
     *
     * @param yearPMF  年份的概率分布
     * @param monthPMF 月份的概率分布
     */
    public static LocalDate getRandomDate(List<Pair<String, Double>> yearPMF, List<Pair<String, Double>> monthPMF) {
        int year = Integer.parseInt(RandomTools.weightedRandom(yearPMF));
        int month = Integer.parseInt(RandomTools.weightedRandom(monthPMF));

        int daysOfMonth = YearMonth.of(year, month).lengthOfMonth();
        long day = RandomTools.randomLong(1, daysOfMonth + 1);
        int magicRandomDay = (int) RandomTools.getGaussian(day, daysOfMonth, 0, daysOfMonth) + 1;

        return LocalDate.of(year, month, magicRandomDay);
    }

    /**
     * 小时转精确时间
     *
     * @param hour double小时数
     */
    public static LocalTime hourToLocalTime(double hour) {
        return LocalTime.ofNanoOfDay((long) (hour * NANOS_PER_HOUR));
    }

    /**
     * 转换为 yyyy-MM-dd HH:mm:ss.SSS 的格式（Hive的TIMESTAMP类型）
     *
     * @param localDateTime 假设为东八区时间
     */
    public static String formatDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(formatter);
    }
}
