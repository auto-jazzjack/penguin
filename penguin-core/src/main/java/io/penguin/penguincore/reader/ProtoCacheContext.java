package io.penguin.penguincore.reader;

import io.penguin.core.cache.penguin;

public class ProtoCacheContext implements CacheContext {

    private final penguin.CacheCodec message;

    public ProtoCacheContext(penguin.CacheCodec message) {
        this.message = message;
    }

    @Override
    public byte[] getValue() {
        return message.getPayload().toByteArray();
    }

    @Override
    public long getTimeStamp() {
        return message.getTimestamp();
    }
    //private V value;
}
