package com.groupon.common;

import rx.Single;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.groupon.common.Utils.sum;

public class Method {
    private static final String METHOD_NAME_MEMORY = "Memory";
    public static final String METHOD_NAME_NETWORK = "Network";

    private final String name;
    // The Map is for the request params.
    private final Function<Map<String, String>, Function<List<Integer>, Single<Integer>>> mapper;

    public Method(String name, Function<Map<String, String>, Function<List<Integer>,
            Single<Integer>>> mapper) {
        this.name = name;
        this.mapper = mapper;
    }

    public String getName() {
        return name;
    }

    public Function<Map<String, String>, Function<List<Integer>, Single<Integer>>> getMapper() {
        return mapper;
    }

    public static Method fromMemory() {
        return new Method(METHOD_NAME_MEMORY, map -> values -> {
            int sum = sum(values);
            return Single.just(sum);
        });
    }
}
