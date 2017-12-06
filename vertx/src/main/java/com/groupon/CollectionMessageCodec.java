package com.groupon;

import java.util.Collection;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;

public class CollectionMessageCodec<T extends Collection> implements MessageCodec<T, T> {
    private static final int INT_SIZE = 4;
    public static final String NAME = CollectionMessageCodec.class.getSimpleName();

    @Override
    public void encodeToWire(Buffer buffer, T obj) {
        try {
            String className = obj.getClass().getCanonicalName();
            byte[] encodedClassName = className.getBytes(CharsetUtil.UTF_8);
            buffer.appendInt(encodedClassName.length);
            buffer.appendBytes(encodedClassName);

            String strJson = Json.encode(obj);
            byte[] encoded = strJson.getBytes(CharsetUtil.UTF_8);
            buffer.appendInt(encoded.length);
            buffer.appendBytes(encoded);
        } catch (EncodeException jpe) {
            throw new IllegalArgumentException("Unexpected error while encoding to wire.", jpe);
        }
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        try {
            int length = buffer.getInt(pos);
            pos += INT_SIZE;
            String className = new String(buffer.getBytes(pos, pos + length), CharsetUtil.UTF_8);
            pos += length;
            length = buffer.getInt(pos);
            pos += INT_SIZE;
            String str = new String(buffer.getBytes(pos, pos + length), CharsetUtil.UTF_8);
            return (T) Json.decodeValue(str, Class.forName(className));
        } catch (ClassNotFoundException ioe) {
            throw new IllegalArgumentException("Unexpected error while decoding from the wire.", ioe);
        }
    }

    @Override
    public T transform(T obj) {
        return obj;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
