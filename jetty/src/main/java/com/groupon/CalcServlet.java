package com.groupon;

import com.groupon.common.Method;
import com.groupon.common.expression_tree.ExpressionTree.Tree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;
import rx.Single;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.groupon.Methods.methods;
import static com.groupon.common.Constants.EXPRESSION_PARAM;
import static com.groupon.common.Constants.METHOD_PARAM;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

@WebServlet(name = "Form", urlPatterns = {"/"}, asyncSupported = true, initParams = {
        @WebInitParam(name = "", value = "")
})
public class CalcServlet extends HttpServlet {
    private static void respondWithError(HttpServletResponse response, AsyncContext asyncContext,
                                         Throwable throwable) {
        throwable.printStackTrace();
        response.setContentType("text/html");
        try {
            response.getWriter().println(throwable.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        asyncContext.complete();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String methodName = request.getParameter(METHOD_PARAM);
        Method method = Arrays.stream(methods).filter(methodDefinition ->
                methodDefinition.getName().equals(methodName)).findFirst().orElseThrow(() ->
                new RuntimeException("Couldn't find method: " + methodName));

        String exp = request.getParameter(EXPRESSION_PARAM);
        Tree tree = parseExpressionTree(exp);
        Map<String, String> parameters = request.getParameterMap().entrySet().stream().collect
                (Collectors.toMap(Map.Entry::getKey,
                entry -> Arrays.stream(entry.getValue()).collect(Collectors.joining(","))));
        Single<Integer> sum = new ExpressionTreeSummarizer(method.getMapper().apply(parameters)).sum(tree);

        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(1_000_000); // in ms

        sum.subscribe(
                integer -> {
                    try {
                        response.setContentType("text/html");
                        response.getWriter().println(exp + "=" + integer);
                        response.setStatus(HttpServletResponse.SC_OK);
                        asyncContext.complete();
                    } catch (IOException e) {
                        respondWithError(response, asyncContext, e);
                    }
                },
                throwable -> respondWithError(response, asyncContext, throwable));
    }
}
