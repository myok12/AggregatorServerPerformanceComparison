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

    public static String urlForCalc(List<Integer> numbers) {
        //.get(new RequestOptions().setHost(SUM_SERVER_HOST).setPort(SUM_SERVER_PORT)
        String numsAsString = numbers.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        return "http://" + SUM_SERVER_HOST + ":" + SUM_SERVER_PORT + SUM_SERVER_URI + "?" +
                SUM_SERVER_DELAY_PARAM + "=100" +
                "&" + SUM_SERVER_NUMS_PARAM + "=" + numsAsString;
    }
}
