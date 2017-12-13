package com.groupon.common;

import java.util.List;
import java.util.Map;
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

    public static String urlForCalc(List<Integer> numbers, Map<String, String> parameters) {
        int serverIdx = getServerIndex();
        String parametersString = parameters.entrySet().stream().map(entry -> entry.getKey() +
                "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String numsAsString = numbers.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        return "http://" + SUM_SERVER_HOSTS[serverIdx] + ":" + SUM_SERVER_PORTS[serverIdx] +
                SUM_SERVER_URI + "?" +
                SUM_SERVER_NUMS_PARAM + "=" + numsAsString + (parametersString.length() > 0 ? "&"
                + parametersString : "");
    }

    public static int stripPaddingOptionallyFromResponse(String body) {
        int firstLine = body.indexOf('\n');
        String resWithoutPad = firstLine == -1 ? body : body.substring(0, firstLine);
        return Integer.valueOf(resWithoutPad);
    }
}
