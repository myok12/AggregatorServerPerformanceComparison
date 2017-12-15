package com.groupon;

import com.groupon.common.Utils;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Single;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.groupon.common.Utils.stripPaddingOptionallyFromResponse;

public class SumOverNetwork {

    private HttpClient httpClient;

    public SumOverNetwork(Vertx vertx) {
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
    }


    Function<List<Integer>, Single<Integer>> sumOverNetwork(Map<String, String> parameters) {
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
}
