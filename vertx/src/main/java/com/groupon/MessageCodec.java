package com.groupon;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;

public class MessageCodec<T extends Object> implements io.vertx.core.eventbus.MessageCodec<T, T> {
    public static final String CODEC_NAME = MessageCodec.class.getSimpleName();
    private static final int INT_SIZE = 4;

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
        return CODEC_NAME;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
