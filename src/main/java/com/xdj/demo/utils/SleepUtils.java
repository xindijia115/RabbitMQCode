package com.xdj.demo.utils;

/**
 * @author xia
 * @since 2023/11/22/14:53
 */
public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
