package com.groupon;

import com.groupon.common.Method;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Single;

import java.util.List;
import java.util.Map;

class EventBusHelper {
    private final Vertx vertx;
    private final SumOverNetwork sumOverNetwork;

    EventBusHelper(Vertx vertx, SumOverNetwork sumOverNetwork) {
        this.vertx = vertx;
        this.sumOverNetwork = sumOverNetwork;
    }

    public void registerEventBusConsumer() {
        vertx.eventBus().<Data>localConsumer(Method.METHOD_EVENT_BUS_NETWORK).toObservable().subscribe(event -> {
            sumOverNetwork.sumOverNetwork(event.body().getParameters())
                    .apply(event.body().getValues())
                    .subscribe(event::reply, e -> event.fail(500, "Could not compute " + e.getMessage()));
        });
    }

    public Single<Integer> sendEventBusMessage(Map<String, String> parameters, List<Integer> values) {
        return vertx.eventBus().<Integer>rxSend(Method.METHOD_EVENT_BUS_NETWORK,
                new Data(values, parameters),
                new DeliveryOptions().setCodecName(
                        MessageCodec.CODEC_NAME)).map(Message::body);
    }

    public class Data {
        private final List<Integer> values;
        private final Map<String, String> parameters;

        public Data(List<Integer> values, Map<String, String> parameters) {
            this.values = values;
            this.parameters = parameters;
        }

        public List<Integer> getValues() {
            return values;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }
    }
}
