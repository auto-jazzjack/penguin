package io.penguin.pengiunlettuce.compress;

import io.penguin.penguincodec.Codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Compressor<V> implements Codec<V> {

    private static final int MAX_BYTE_SIZE = 4096;
    protected Codec<V> delegated;

    public Compressor(Codec<V> delegated) {
        this.delegated = delegated;
    }

    enum Kind {
        NONE,
        GZIP,
    }

    public static Kind kindValueOf(String kind) {
        try {
            return Kind.valueOf(kind.toUpperCase());
        } catch (Exception e) {
            return Kind.NONE;
        }
    }

    public static void copy(InputStream from, OutputStream to, int size) throws IOException {

        byte[] buf = new byte[Math.min(size, MAX_BYTE_SIZE)];
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
        }
    }
}
