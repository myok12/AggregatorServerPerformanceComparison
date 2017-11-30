package com.groupon;

import com.groupon.common.HtmlUtils;
import com.groupon.common.Method;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(HtmlUtils.buildCalculateForm(Arrays.stream(Methods.methods)
                .map(Method::getName).toArray(String[]::new)));
    }
}