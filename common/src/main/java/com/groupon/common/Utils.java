package com.groupon.common;

import java.util.List;
import java.util.stream.Collectors;

import static com.groupon.common.Constants.*;

public class Utils {
    static Integer sum(Iterable<Integer> values) {
        int sum = 0;
        for (Integer value : values) {
            sum += value;
        }
        return sum;
    }

    private static int currRequestIndex = 0;

    private static int getServerIndex() {
        // Round robin
        int idx = currRequestIndex % SUM_SERVER_HOSTS.length;
        currRequestIndex++;
        return idx;
    }

    public static String urlForCalc(List<Integer> numbers) {
        int serverIdx = getServerIndex();
        String numsAsString = numbers.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        return "http://" + SUM_SERVER_HOSTS[serverIdx] + ":" + SUM_SERVER_PORTS[serverIdx] +
                SUM_SERVER_URI + "?" +
                SUM_SERVER_DELAY_PARAM + "=100" +
                "&" + SUM_SERVER_NUMS_PARAM + "=" + numsAsString;
    }
}
