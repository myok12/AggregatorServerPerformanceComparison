package com.groupon;

import com.groupon.common.Method;
import com.groupon.common.Utils;
import com.groupon.common.expression_tree.ExpressionTree.Tree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import java.util.Arrays;
import java.util.List;

import static com.groupon.common.Constants.*;
import static com.groupon.common.HtmlUtils.buildCalculateForm;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    // TODO: Finish supporting delay
    private Single<Integer> sumOverNetwork(List<Integer> values) {
        if (values.size() == 0) return Single.error(new Exception("Cannot sum 0 numbers"));
        if (values.size() == 1) return Single.just(values.get(0));
        return Single.fromEmitter(emitter -> {
            HttpClientRequest request = vertx.createHttpClient().getAbs(Utils.urlForCalc(values));

            request.toObservable().toSingle()
                    .flatMap(response -> response.toObservable().toSingle())
                    .map(buffer -> buffer.toString("UTF-8"))
                    .subscribe(body -> {
                        int sum = Integer.valueOf(body);
                        logger.debug("Received from network: " + sum);
                        emitter.onSuccess(sum);
                    }, emitter::onError);
            request.end();
        });
    }

    @Override
    public void start(Future<Void> fut) {
        Method[] methods = new Method[]{
                Method.fromMemory(),
                new Method(Method.METHOD_NAME_NETWORK, this::sumOverNetwork),
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
                        integer -> event.response().end(exp + "=" + integer),
                        throwable -> event.response().setStatusCode(500).end(throwable.getMessage()));
            } catch (Throwable throwable) {
                event.response().setStatusCode(500).end(throwable.getMessage());
            }
        });

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

}
