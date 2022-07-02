package com.example.penguinql.core.setter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class POJOFieldMethod<P, M> implements GeneralFieldMethod<P, M> {

    private Method setter;
    private Method getter;
    private Constructor<M> newInstance;

    public POJOFieldMethod(Class<P> containingType, Field targetField) throws Exception {

        PropertyDescriptor propertyDescriptor = Arrays.stream(Introspector.getBeanInfo(containingType).getPropertyDescriptors())
                .filter(i -> i.getName().equalsIgnoreCase(targetField.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find getter/setter" + containingType + " " + targetField));


        if (!FieldUtils.PRIMITIVES.contains(targetField.getType())
                && !targetField.getType().isAssignableFrom(List.class)
                && !targetField.getType().isAssignableFrom(Set.class)
                && !targetField.getType().isAssignableFrom(Map.class)) {
            newInstance = (Constructor<M>) targetField.getType().getConstructor();
        } else {
            newInstance = null;
        }
        getter = propertyDescriptor.getReadMethod();
        setter = propertyDescriptor.getWriteMethod();

        getter.setAccessible(true);
        setter.setAccessible(true);
    }

    @Override
    public void setData(P parent, M myself) {
        try {
            setter.invoke(parent, myself);
        } catch (Exception e) {
            throw new RuntimeException("Cannot assign value");
        }

    }

    @Override
    public M getData(P parent) {
        try {
            return (M)getter.invoke(parent);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read value");
        }
    }

    @Override
    public M defaultInstance() {
        try {
            return newInstance.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create value");
        }
    }
}
