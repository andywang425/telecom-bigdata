package com.example.telecom.util;

import com.example.telecom.config.Distribution;
import com.example.telecom.enums.DistributionType;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class RandomTools {
    private static final RandomUtils randomUtils = RandomUtils.secure();
    private static final RandomDataGenerator generator = new RandomDataGenerator();

    /**
     * 从列表中随机取一个元素
     */
    public static <T> T getRandomElement(List<T> list) {
        return list.get(randomUtils.randomInt(0, list.size()));
    }

    /**
     * 从列表中随机取n个下标不同的元素
     */
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

    /**
     * 获取符合高斯分布（正态分布）的随机数
     * @param mean 均值
     * @param stdDev 标准差
     * @param min 最小值，如果小于该值则重新生成
     * @param max 最大值，如果大于该值则重新生成
     */
    public static double getGaussian(double mean, double stdDev, double min, double max) {
        double random = generator.nextGaussian(mean, stdDev);

        if (random >= min && random < max) {
            return random;
        } else {
            return getGaussian(mean, stdDev, min, max);
        }
    }

    /**
     * 获取一个在指定范围内均匀分布的随机数
     * @param min 最小值
     * @param max 最大值
     */
    public static double randomDouble(double min, double max) {
        return randomUtils.randomDouble(min, max);
    }

    /**
     * 获取一个在指定范围内均匀分布的随机数
     * @param min 最小值
     * @param max 最大值
     */
    public static long randomLong(long min, long max) {
        return randomUtils.randomLong(min, max);
    }

    /**
     * 概率测试
     */
    public static boolean rateTest(double rate) {
        return randomUtils.randomDouble(0, 1) < rate;
    }

    /**
     * 获取一个加权随机数
     * @param pmf 概率质量函数，每个Pair的value是权重
     */
    public static <T> T weightedRandom(List<Pair<T, Double>> pmf) {
        return new EnumeratedDistribution<>(pmf).sample();
    }

    /**
     * 获取一个符合指定分布的随机数
     */
    public static double getDistributionRandom(Distribution distribution) {
        List<Map<String, Double>> args = distribution.getArgs();

        DistributionType type = distribution.getDistributionType();
        Map<String, Double> arg = getRandomElement(args);

        if (type == DistributionType.UNIFORM) {
            double lower = arg.get("lower");
            double upper = arg.get("upper");
            return randomDouble(lower, upper);
        } else if (type == DistributionType.NORMAL) {
            double mean = arg.get("mean");
            double stdDev = arg.get("stddev");
            double min = arg.get("min");
            double max = arg.get("max");
            return getGaussian(mean, stdDev, min, max);
        } else {
            throw new IllegalArgumentException("Unknown distribution type: " + type);
        }
    }
}
