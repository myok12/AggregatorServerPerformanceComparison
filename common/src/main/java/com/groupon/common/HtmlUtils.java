package com.groupon.common;

import static com.groupon.common.Constants.*;

public class HtmlUtils {
    public static String buildCalculateForm(String[] methods) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Crazy calculator</h1>");
        sb.append("<form action=\"" + CALC_ROUTE + "\" method=\"get\">");
        sb.append("Method: ");
        for (String method : methods) {
            sb.append("<input id=\"method").append(method).append("\" type=\"radio\" " +
                    "name=\"" + METHOD_PARAM + "\" value=\"").append(method).append("\">");
            sb.append("<label for=\"method").append(method).append("\">").append(method)
                    .append("</label>") ;
        }
        sb.append("<br/>");
        sb.append("Calculation: ");
        sb.append("<input type=\"text\" name=\"" + EXPRESSION_PARAM + "\" value=\"1+2+3\" " +
                "size=\"100\">");
        sb.append("<br/>");
        sb.append("Padding: ");
        sb.append("<input type=\"number\" name=\"" + PADDING_PARAM + "\" value=\"10000\" " +
                "size=\"5\">");
        sb.append("<br/>");
        sb.append("Delay: ");
        sb.append("<input type=\"number\" name=\"" + DELAY_PARAM + "\" value=\"1000\" " +
                "size=\"5\">");
        sb.append("<br/>");
        sb.append("<input type=\"submit\" value=\"Go calc\">");
        sb.append("</form>");
        return sb.toString();
    }
}
