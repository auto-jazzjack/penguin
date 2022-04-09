package io.penguin.penguincore.compress;

import io.penguin.penguincore.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressor implements Compressor {

    public byte[] compress(byte[] input) throws Exception {

        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }

    public OutputStream compress(InputStream inputStream) throws Exception {
        //OutputStream gzipInputStream = new GZIPOutputStream(inputStream);
        //IOUtils.copy()
        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }
}
