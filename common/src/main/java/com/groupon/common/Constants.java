package com.groupon.common;

public class Constants {
    public static final String FORM_ROUTE = "/";
    public static final String CALC_ROUTE = "/calc";
    public static final String METHOD_PARAM = "method";
    public static final String EXPRESSION_PARAM = "exp";
    static final String DELAY_PARAM = "delay";

    public static final String[] SUM_SERVER_HOSTS = new String[] {"api-klaatu-app1.snc1",
            "api-util-app2.snc1"};
    public static final Integer[] SUM_SERVER_PORTS = new Integer[] {9000, 9000};
    public static final String SUM_SERVER_URI = "/sum";
    public static final String SUM_SERVER_NUMS_PARAM = "nums";
    public static final String SUM_SERVER_DELAY_PARAM = "delay";
}
