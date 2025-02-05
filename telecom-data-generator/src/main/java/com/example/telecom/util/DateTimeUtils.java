package com.example.telecom.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    /**
     * 一小时的纳秒数
     */
    private static final long NANOS_PER_HOUR = 3600000000000L;

    /**
     * 获取指定范围内的一个随机日期
     * @param start 开始日期（包含）
     * @param end 结束日期（包含）
     */
    public static LocalDate getRandomDate(LocalDate start, LocalDate end) {
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        long randomDayOffset = RandomTools.randomLong(0, daysBetween + 1);

        return start.plusDays(randomDayOffset);
    }

    /**
     * 小时转精确时间
     * @param hour double小时数
     */
    public static LocalTime hourToLocalTime(double hour) {
        return LocalTime.ofNanoOfDay((long) (hour * NANOS_PER_HOUR));
    }
}
