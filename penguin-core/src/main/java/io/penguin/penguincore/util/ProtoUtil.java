package io.penguin.penguincore.util;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public class ProtoUtil {

    public static <M extends Message> M safeParseFrom(Parser<M> parser, byte[] bytes, M def) {

        try {
            return parser.parseFrom(bytes);
        } catch (Exception e) {
            return def;
        }
    }
}
