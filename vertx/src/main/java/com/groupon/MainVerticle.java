package com.groupon;

import com.groupon.common.Method;
import com.groupon.common.Utils;
import com.groupon.common.expression_tree.ExpressionTree.Tree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import java.util.function.Function;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.groupon.common.Constants.*;
import static com.groupon.common.HtmlUtils.buildCalculateForm;
import static com.groupon.common.Utils.stripPaddingOptionallyFromResponse;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

public class MainVerticle extends AbstractVerticle {
    // private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private HttpClient httpClient;

    // TODO: Finish supporting delay
    private Function<List<Integer>, Single<Integer>> sumOverNetwork(Map<String, String>
                                                                    parameters) {
        return values -> Single.fromEmitter(emitter -> {
            HttpClientRequest request = httpClient.getAbs(Utils.urlForCalc(values, parameters));
            request.putHeader("User-Agent", "Vertx");
            request.putHeader("Accept-Encoding", "gzip");
            request.setTimeout(1_000_000);

            request.toObservable()
                    .flatMap(HttpClientResponse::toObservable)
                    .map(buffer -> buffer.toString("UTF-8"))
                    .reduce((s, s2) -> s + s2)
                    .subscribe(body -> {
                        // logger.debug("Received from network: " + sum);
                        emitter.onSuccess(stripPaddingOptionallyFromResponse(body));
                    }, emitter::onError);
            request.end();
        });
    }

    @Override
    public void start(Future<Void> fut) {
        httpClient = vertx.createHttpClient(
                new HttpClientOptions()
                        .setKeepAlive(false)
                        .setMaxPoolSize(64) // 5 default // this is per host
                        .setLogActivity(false)
                        .setMaxWaitQueueSize(-1) // -1 default
                        //.setPipelining(true)
                        //.setPipeliningLimit(100)
                        .setProtocolVersion(HttpVersion.HTTP_1_0) // 1.1 default
                        .setConnectTimeout(1_000_000)
                        .setIdleTimeout(1_000) // For some reasons now connections are established
                        // with 0.
                        .setTcpNoDelay(true) // Same as in jetty.
                        .setTryUseCompression(false));
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
