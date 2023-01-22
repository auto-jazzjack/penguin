package com.example.penguinql.core.setter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;


public class ProtoFieldMethod<P extends Message, M> implements GeneralFieldMethod<P, M> {

    private Descriptors.FieldDescriptor fieldDescriptor;

    @Override
    public void setData(P parent, M myself) {

    }

    @Override
    public M getData(P parent) {
        return null;
    }

    @Override
    public M defaultInstance() {
        return null;
    }
}
