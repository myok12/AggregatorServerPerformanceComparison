package com.groupon;

import java.util.List;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

import com.groupon.common.Method;
import com.groupon.common.Utils;

public class SumNetworkService {

    private HttpClient httpClient;

    public SumNetworkService(Vertx vertx) {
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
        Observable<Message<List<Integer>>> sumMemory = vertx.eventBus().<List<Integer>>localConsumer(Method.METHOD_EVENTBUS_NETWORK).toObservable();
        sumMemory.subscribe(message -> callSumNetwork(message.body())
                .subscribe(body -> {
                    int sum = Integer.valueOf(body.substring(0, body.indexOf('\n')));
                    // logger.debug("Received from network: " + sum);
                    message.reply(sum);
                }, fail -> message.fail(500, fail.getMessage())));
    }

    public Observable<String> callSumNetwork(List<Integer> values) {
        HttpClientRequest request = httpClient.getAbs(Utils.urlForCalc(values));
        request.putHeader("User-Agent", "Vertx");
        request.putHeader("Accept-Encoding", "gzip");
        request.setTimeout(1_000_000);

        Observable<String> responseObservable = request.toObservable()
                .flatMap(HttpClientResponse::toObservable)
                .map(buffer -> buffer.toString("UTF-8"))
                .reduce((s, s2) -> s + s2);
        request.end();
        return responseObservable;
    }

}
