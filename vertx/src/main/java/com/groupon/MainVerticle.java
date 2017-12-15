package com.groupon;

import com.groupon.common.Method;
import com.groupon.common.expression_tree.ExpressionTree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;
import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.groupon.common.Constants.*;
import static com.groupon.common.HtmlUtils.buildCalculateForm;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

public class MainVerticle extends AbstractVerticle {
    private EventBusHelper eventBusHelper;

    @Override
    public void start(Future<Void> fut) {
        SumOverNetwork sumOverNetwork = new SumOverNetwork(vertx);

        eventBusHelper = new EventBusHelper(vertx, sumOverNetwork);
        eventBusHelper.registerEventBusConsumer();

        Method[] methods = new Method[]{
                Method.fromMemory(),
                new Method(Method.METHOD_NAME_NETWORK, sumOverNetwork::sumOverNetwork),
                new Method(Method.METHOD_EVENT_BUS_NETWORK, parameters ->
                        integers -> eventBusHelper.sendEventBusMessage(parameters, integers))
        };

        Router router = Router.router(vertx);

        router.get(FORM_ROUTE).handler(event -> event.response()
                .putHeader("content-type", "text/html")
                .end(buildCalculateForm(
                        Arrays.stream(methods).map(Method::getName).toArray(String[]::new))));
        router.get(CALC_ROUTE).handler(event -> {
            try {
                String methodName = event.request().getParam(METHOD_PARAM);
                Method method = Arrays.stream(methods).filter(methodDefinition ->
                        methodDefinition.getName().equals(methodName)).findFirst().orElseThrow(() ->
                        new RuntimeException("Couldn't find method: " + methodName));

                String exp = event.request().getParam(EXPRESSION_PARAM);
                ExpressionTree.Tree tree = parseExpressionTree(exp);
                Map<String, String> parameters = StreamSupport.stream(event.request().params()
                                .getDelegate().spliterator(),
                        false).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Single<Integer> sum = new ExpressionTreeSummarizer(method.getMapper().apply
                        (parameters))
                        .sum(tree);
                sum.subscribe(
                        integer -> event.response()
                                .putHeader("content-type", "text/html;charset=utf-8")
                                .putHeader("Server", "Vertx(xxxxxxxxxxxxxxx)")
                                .end(exp + "=" + integer),
                        throwable -> event.response().setStatusCode(500).end(throwable.getMessage()));
            } catch (Throwable throwable) {
                event.response().setStatusCode(500).end(throwable.getMessage());
            }
        });

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8081, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

}
