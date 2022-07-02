package com.example.penguinql.core.setter;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class POJOFieldMethod<P, M> implements GeneralFieldMethod<P, M> {

    private final Method setter;
    private final Method getter;

    public POJOFieldMethod(Class<P> containingType, Field targetField) throws Exception {

        PropertyDescriptor propertyDescriptor = Arrays.stream(Introspector.getBeanInfo(containingType).getPropertyDescriptors())
                .filter(i -> i.getName().equalsIgnoreCase(targetField.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find getter/setter"));

        getter = propertyDescriptor.getReadMethod();
        setter = propertyDescriptor.getWriteMethod();

        getter.setAccessible(true);
        setter.setAccessible(true);
    }

    @Override
    public void setDate(P parent, M myself) {
        try {
            setter.invoke(parent, myself);
        } catch (Exception e) {
            throw new RuntimeException("Cannot assign value");
        }

    }
}
