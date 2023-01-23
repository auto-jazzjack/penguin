package com.example.penguinql.util;

import com.google.protobuf.Descriptors;

public class ProtoUtil {

    public static boolean isPrimitive(Descriptors.FieldDescriptor fieldDescriptor) {
        if (fieldDescriptor.isMapField()) {
            return false;
        } else if (fieldDescriptor.isRepeated()) {
            return false;
        } else if (Descriptors.FieldDescriptor.Type.MESSAGE.equals(fieldDescriptor.getType())) {
            return false;
        } else {
            return true;
        }
    }

    public static Pair<Descriptors.FieldDescriptor, Descriptors.FieldDescriptor> getMapDescriptor(Descriptors.FieldDescriptor fieldDescriptor) {

        if (!fieldDescriptor.isMapField()) {
            return null;
        } else {

            return Pair.of(
                    fieldDescriptor.getMessageType().getFields().get(0),
                    fieldDescriptor.getMessageType().getFields().get(1)
            );
        }
    }

    public static Pair<Descriptors.FieldDescriptor, Descriptors.FieldDescriptor> getRepeatedDescriptor(Descriptors.FieldDescriptor fieldDescriptor) {

        if (fieldDescriptor.isMapField()) {
            return null;
        } else if (fieldDescriptor.isRepeated()) {

        }
        return null;
    }
}
