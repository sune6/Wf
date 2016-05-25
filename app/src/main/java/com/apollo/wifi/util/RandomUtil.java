package com.apollo.wifi.util;

import java.util.Random;

/**
 * Created by Sun
 * <p/>
 * 2016/5/17 14:20
 */
public class RandomUtil {

    public static float nextFloat(final float min, final float max) throws Exception {
        if (max < min) {
            throw new Exception("min < max");
        }
        if (min == max) {
            return min;
        }
        return min + ((max - min) * new Random().nextFloat());
    }
}
