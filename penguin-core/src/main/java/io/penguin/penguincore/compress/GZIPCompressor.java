package io.penguin.penguincore.compress;

import io.penguin.penguincore.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressor implements Compressor {

    public OutputStream compress(InputStream inputStream) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputStream.available() / 2);
        try (OutputStream compressor = new GZIPOutputStream(outputStream)) {
            IOUtils.copy(inputStream, compressor, IOUtils.DEFAULT_SIZE);
        }
        return outputStream;
    }

    @Override
    public OutputStream decompress(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputStream.available() * 2);
        try (InputStream compressor = new GZIPInputStream(inputStream)) {
            IOUtils.copy(compressor, outputStream, IOUtils.DEFAULT_SIZE);
        }
        return outputStream;
    }


}
