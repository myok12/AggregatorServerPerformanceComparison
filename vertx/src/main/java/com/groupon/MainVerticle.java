package com.groupon;

import com.groupon.expression_tree.ExpressionTree.Tree;
import com.groupon.expression_tree.ExpressionTreeSummarizer;
import io.vertx.core.Future;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.groupon.Constants.*;
import static com.groupon.HtmlUtils.buildCalculateForm;
import static com.groupon.Utils.sum;
import static com.groupon.expression_tree.ExpressionTreeParser.parseExpressionTree;

public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    // TODO: Finish supporting delay
    private Single<Integer> sumOverNetwork(List<Integer> values) {
        if (values.size() == 0) return Single.error(new Exception("Cannot sum 0 numbers"));
        if (values.size() == 1) return Single.just(values.get(0));
        String nums = values.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        return Single.fromEmitter(emitter -> {
            HttpClientRequest request = vertx.createHttpClient()
                    .get(new RequestOptions().setHost(SUM_SERVER_HOST).setPort(SUM_SERVER_PORT)
                            .setURI(SUM_SERVER_URI + "?" + SUM_SERVER_DELAY_PARAM + "="  +
                             "&" + SUM_SERVER_NUMS_PARAM + "=" + nums));
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
                new Method("Memory", values -> {
                    int sum = sum(values);
                    System.out.println("Replying from memory: " + sum);
                    return Single.just(sum);
                }),
                new Method("Network", this::sumOverNetwork),
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

    private class Method {
        private final String name;
        private final Function<List<Integer>, Single<Integer>> mapper;

        Method(String name, Function<List<Integer>, Single<Integer>> mapper) {
            this.name = name;
            this.mapper = mapper;
        }

        String getName() {
            return name;
        }

        Function<List<Integer>, Single<Integer>> getMapper() {
            return mapper;
        }
    }
}
