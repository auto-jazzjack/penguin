package io.penguin.pengiunlettuce.compress;

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
    public byte[] serialize(V source) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutStream = new GZIPOutputStream(new BufferedOutputStream(byteArrayOutputStream))) {
            gzipOutStream.write(this.delegated.serialize(source));
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public V deserialize(byte[] source) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try (GZIPInputStream gzipInStream = new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(source)))) {
            copy(gzipInStream, outStream, source.length * 2);
        }
        return this.delegated.deserialize(outStream.toByteArray());
    }
}
