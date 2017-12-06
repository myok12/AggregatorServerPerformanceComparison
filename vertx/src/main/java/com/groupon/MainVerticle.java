package com.groupon;

import static com.groupon.common.Constants.CALC_ROUTE;
import static com.groupon.common.Constants.EXPRESSION_PARAM;
import static com.groupon.common.Constants.FORM_ROUTE;
import static com.groupon.common.Constants.METHOD_PARAM;
import static com.groupon.common.HtmlUtils.buildCalculateForm;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import com.groupon.common.Method;
import com.groupon.common.expression_tree.ExpressionTree.Tree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;

public class MainVerticle extends AbstractVerticle {
    // private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private SumNetworkService sumNetworkService;



    @Override
    public void start(Future<Void> fut) {
        sumNetworkService = new SumNetworkService(vertx);
        Method[] methods = new Method[]{
                Method.fromMemory(),
                new Method(Method.METHOD_NAME_NETWORK, sumNetworkService::sumOverNetwork),
                new Method(Method.METHOD_EVENTBUS_NETWORK, integers -> {
                    Single<Message<Integer>> messageSingle = vertx.eventBus().rxSend(Method.METHOD_EVENTBUS_NETWORK, integers, new DeliveryOptions().setCodecName(CollectionMessageCodec.NAME));
                    return messageSingle.map(Message::body);
                })
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
                Tree tree = parseExpressionTree(exp);
                Single<Integer> sum = new ExpressionTreeSummarizer(method.getMapper()).sum(tree);
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
