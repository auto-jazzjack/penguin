package io.penguin.penguincore.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    public static int DEFAULT_SIZE = 4096;

    public static long copy(InputStream from, OutputStream to, int size) throws IOException {
        byte[] buf = new byte[size];
        long total = 0L;

        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                return total;
            }

            to.write(buf, 0, r);
            total += (long) r;
        }
    }
}
