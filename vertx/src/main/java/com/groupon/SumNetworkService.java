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
import rx.Single;

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
        sumMemory.subscribe(message -> sumOverNetwork(message.body())
                .subscribe(message::reply, fail -> message.fail(500, fail.getMessage())));
    }


    // TODO: Finish supporting delay
    public Single<Integer> sumOverNetwork(List<Integer> values) {
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
                        int indexOfLine = body.indexOf('\n');
                        if (indexOfLine < 0) {
                            emitter.onSuccess(Integer.valueOf(body));
                        } else {
                            int sum = Integer.valueOf(body.substring(0, indexOfLine));
                            // logger.debug("Received from network: " + sum);
                            emitter.onSuccess(sum);
                        }
                    }, emitter::onError);
            request.end();
        });
    }

}
