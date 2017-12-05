package com.groupon;

import com.groupon.common.Method;
import com.groupon.common.Utils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpVersion;
import rx.Single;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

class Methods {

    private static HttpClient httpClient = new HttpClient();
    static Method[] methods = new Method[]{
            Method.fromMemory(),
            new Method(Method.METHOD_NAME_NETWORK, integers ->
                    Single.fromEmitter(emitter ->
                            httpClient.newRequest(Utils.urlForCalc(integers))
                                    .agent("Jetty")
                                    .idleTimeout(1_000, TimeUnit.SECONDS)
                                    .version(HttpVersion.HTTP_1_0)
                                    .timeout(1_000, TimeUnit.SECONDS)
                                    .send(new BufferingResponseListener() {
                                        @Override
                                        public void onComplete(Result result) {
                                            if (result.isFailed()) {
                                                emitter.onError(result.getResponseFailure());
                                                return;
                                            }
                                            if (result.getResponse().getStatus() != HttpServletResponse.SC_OK) {
                                                emitter.onError(new Exception("Erred status code " + result
                                                        .getResponse().getStatus()));
                                                return;
                                            }
                                            String body = getContentAsString();
                                            int num = Integer.valueOf(body);
                                            emitter.onSuccess(num);
                                        }
                                    })))
    };

    static {
        try {
            httpClient.setMaxRequestsQueuedPerDestination(100_000_000); //1024 default
            httpClient.setMaxConnectionsPerDestination(64); //64 default
            httpClient.setConnectTimeout(1_000_000); // in ms

            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
