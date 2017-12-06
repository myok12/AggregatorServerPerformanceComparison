package com.groupon;

import java.util.List;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

import com.groupon.common.Method;
import com.groupon.common.Utils;

public class SumVerticle extends AbstractVerticle {

    private HttpClient httpClient;

    public SumVerticle(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void start() throws Exception {
        Observable<Message<List<Integer>>> sumMemory = vertx.eventBus().<List<Integer>>localConsumer(Method.METHOD_EVENTBUS_NETWORK).toObservable();
        sumMemory.subscribe(message -> {
            HttpClientRequest request = httpClient.getAbs(Utils.urlForCalc(message.body()));
            request.putHeader("User-Agent", "Vertx");
            request.putHeader("Accept-Encoding", "gzip");
            request.setTimeout(1_000_000);

            request.toObservable()
                    .flatMap(HttpClientResponse::toObservable)
                    .map(buffer -> buffer.toString("UTF-8"))
                    .reduce((s, s2) -> s + s2)
                    .subscribe(body -> {
                        int sum = Integer.valueOf(body.substring(0, body.indexOf('\n')));
                        // logger.debug("Received from network: " + sum);
                        message.reply(sum);
                    }, fail -> message.fail(500, fail.getMessage()));
            request.end();
        });
    }

}
