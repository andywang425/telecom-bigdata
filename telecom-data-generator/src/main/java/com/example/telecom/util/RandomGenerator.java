package com.example.telecom.util;

import com.example.telecom.config.Distribution;
import com.example.telecom.enums.DistributionType;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class RandomGenerator {
    private static final RandomUtils randomUtils = RandomUtils.secure();
    private static final RandomDataGenerator generator = new RandomDataGenerator();

    public static <T> T getRandomElement(List<T> list) {
        return list.get(randomUtils.randomInt(0, list.size()));
    }

    public static <T> List<T> getRandomElements(List<T> list, int n) {
        if (n > list.size()) {
            throw new IllegalArgumentException("n cannot be greater than the size of the list");
        }

        Set<Integer> indexes = new HashSet<>();

        while (indexes.size() < n) {
            int index = randomUtils.randomInt(0, list.size());
            indexes.add(index);
        }

        List<T> result = new ArrayList<>();
        for (Integer index : indexes) {
            result.add(list.get(index));
        }
        return result;
    }

    public static double getGaussian(double mean, double stdDev, double min, double max) {
        double random = generator.nextGaussian(mean, stdDev);

        if (random >= min && random < max) {
            return random;
        } else {
            return getGaussian(mean, stdDev, min, max);
        }
    }

    public static double randomDouble(double min, double max) {
        return randomUtils.randomDouble(min, max);
    }

    public static long randomLong(long min, long max) {
        return randomUtils.randomLong(min, max);
    }

    public static boolean rateTest(double rate) {
        return randomUtils.randomDouble(0, 1) < rate;
    }

    public static <T> T weightedRandom(List<Pair<T, Double>> pmf) {
        return new EnumeratedDistribution<>(pmf).sample();
    }

    public static double getDistributionRandom(Distribution distribution) {
        List<Map<String, Double>> args = distribution.getArgs();

        DistributionType type = distribution.getDistributionType();
        Map<String, Double> arg = RandomGenerator.getRandomElement(args);

        if (type == DistributionType.UNIFORM) {
            double lower = arg.get("lower");
            double upper = arg.get("upper");
            return RandomGenerator.randomDouble(lower, upper);
        } else if (type == DistributionType.NORMAL) {
            double mean = arg.get("mean");
            double stdDev = arg.get("stddev");
            double min = arg.get("min");
            double max = arg.get("max");
            return RandomGenerator.getGaussian(mean, stdDev, min, max);
        } else {
            throw new IllegalArgumentException("Unknown distribution type: " + type);
        }
    }
}
