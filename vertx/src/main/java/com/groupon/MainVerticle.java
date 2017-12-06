package com.groupon;

import static com.groupon.common.Constants.CALC_ROUTE;
import static com.groupon.common.Constants.EXPRESSION_PARAM;
import static com.groupon.common.Constants.FORM_ROUTE;
import static com.groupon.common.Constants.METHOD_PARAM;
import static com.groupon.common.HtmlUtils.buildCalculateForm;
import static com.groupon.common.expression_tree.ExpressionTreeParser.parseExpressionTree;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

import com.groupon.common.Method;
import com.groupon.common.Utils;
import com.groupon.common.expression_tree.ExpressionTree.Tree;
import com.groupon.common.expression_tree.ExpressionTreeSummarizer;

public class MainVerticle extends AbstractVerticle {
    // private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private HttpClient httpClient;

    // TODO: Finish supporting delay
    private Single<Integer> sumOverNetwork(List<Integer> values) {
        // if (values.size() == 0) return Single.error(new Exception("Cannot sum 0 numbers"));
        // if (values.size() == 1) return Single.just(values.get(0));
        return Single.fromEmitter(emitter -> {
            HttpClientRequest request = httpClient.getAbs(Utils.urlForCalc(values));
            request.putHeader("User-Agent", "Vertx");
            request.putHeader("Accept-Encoding", "gzip");
            request.setTimeout(1_000_000);

            request.toObservable()
                    .flatMap(HttpClientResponse::toObservable)
                    .map(buffer -> buffer.toString("UTF-8"))
                    .reduce((s, s2) -> s + s2)
                    .subscribe(body -> {
                        int sum = Integer.valueOf(body);
                        // logger.debug("Received from network: " + sum);
                        emitter.onSuccess(sum);
                    }, emitter::onError);
            request.end();
        });
    }

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().getDelegate().registerDefaultCodec(Collection.class, new CollectionMessageCodec<>());
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
        SumVerticle sumVerticle = new SumVerticle(httpClient);
        RxHelper.deployVerticle(vertx, sumVerticle);
        Method[] methods = new Method[]{
                Method.fromMemory(),
                new Method(Method.METHOD_NAME_NETWORK, this::sumOverNetwork),
                new Method(Method.METHOD_EVENTBUS_NETWORK, integers -> {
                    Single<Message<Integer>> messageSingle = vertx.eventBus().rxSend(Method.METHOD_EVENTBUS_NETWORK, integers, new DeliveryOptions().setCodecName(CollectionMessageCodec.NAME));
                    return messageSingle.flatMap(event -> Single.just(event.body()));
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
