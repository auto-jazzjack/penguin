package io.penguin.penguincore.compress;

import io.penguin.penguincore.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressor implements Compressor {

    public byte[] compress(byte[] input) throws Exception {

        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }

    public OutputStream compress(InputStream inputStream) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputStream.available() / 2);
        try (OutputStream compressor = new GZIPOutputStream(outputStream)) {
            IOUtils.copy(inputStream, compressor, IOUtils.DEFAULT_SIZE);
        }
        return outputStream;
    }

}
