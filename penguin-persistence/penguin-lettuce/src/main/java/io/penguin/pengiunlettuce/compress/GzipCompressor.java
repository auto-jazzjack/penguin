package io.penguin.pengiunlettuce.compress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.penguin.penguincodec.Codec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor<V> extends Compressor<V> {

    public GzipCompressor(Codec<V> delegated) {
        super(delegated);
    }

    @Override
    public void serialize(V source, ByteBuf buf) throws Exception {
        /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutStream = new GZIPOutputStream(new BufferedOutputStream(byteArrayOutputStream))) {
            this.delegated.serialize(source, buf);
        }
        byteArrayOutputStream.toByteArray();*/
    }

    @Override
    public V deserialize(ByteBuf source) throws Exception {
        /*ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try (GZIPInputStream gzipInStream = new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(source)))) {
            copy(gzipInStream, outStream, source.readableBytes() * 2);
        }
        return this.delegated.deserialize(ByteBufAllocator.DEFAULT.buffer());*/
        return null;
    }
}
