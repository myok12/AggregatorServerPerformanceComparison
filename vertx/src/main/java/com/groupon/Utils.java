package com.groupon;

class Utils {
    static Integer sum(Iterable<Integer> values) {
        int sum = 0;
        for (Integer value : values) {
            sum += value;
        }
        return sum;
    }
}
