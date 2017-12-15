package com.groupon;

import io.vertx.core.Vertx;

public class ApplicationLauncher extends io.vertx.core.Launcher {

    public static void main(String[] args) {
        new ApplicationLauncher().dispatch(args);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        vertx.eventBus().registerCodec(new MessageCodec<>());
    }

}
