package io.penguin.penguincore.compress;

import java.io.InputStream;
import java.io.OutputStream;

public interface Compressor {

    default byte[] compress(byte[] input) throws Exception {
        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }

    default OutputStream compress(InputStream inputStream) throws Exception {
        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }

    default byte[] decompress(byte[] input) throws Exception {
        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }

    default OutputStream decompress(InputStream inputStream) throws Exception {
        throw new UnsupportedOperationException(this.getClass().getName() + " is not implemented");
    }
}
