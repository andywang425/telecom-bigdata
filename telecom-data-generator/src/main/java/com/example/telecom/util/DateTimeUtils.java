package com.example.telecom.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    private static final long NANOS_PER_HOUR = 3600000000000L;
    public static LocalDate getRandomDate(LocalDate start, LocalDate end) {
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        long randomDayOffset = RandomGenerator.randomLong(0, daysBetween + 1);

        return start.plusDays(randomDayOffset);
    }

    public static LocalTime hourToLocalTime(double hour) {
        return LocalTime.ofNanoOfDay((long) (hour * NANOS_PER_HOUR));
    }
}
