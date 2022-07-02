package com.example.penguinql.core.setter;

import lombok.Data;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class POJOFieldMethod<P, M> implements GeneralFieldMethod<P, M> {

    private Method setter;
    private Method getter;
    private GenericType genericType;
    private Constructor<M> newInstance;

    public POJOFieldMethod(Constructor<M> newInstance, GenericType genericType) {
        this.newInstance = newInstance;
        this.genericType = genericType;
    }

    public POJOFieldMethod(Class<P> containingType, Field targetField) throws Exception {

        PropertyDescriptor propertyDescriptor = Arrays.stream(Introspector.getBeanInfo(containingType).getPropertyDescriptors())
                .filter(i -> i.getName().equalsIgnoreCase(targetField.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find getter/setter" + containingType + " " + targetField));

        if (targetField.getType().isAssignableFrom(Set.class)) {
            this.genericType = GenericType.SET;
        } else if (targetField.getType().isAssignableFrom(List.class)) {
            this.genericType = GenericType.LIST;
        } else if (targetField.getType().isAssignableFrom(Map.class)) {
            this.genericType = GenericType.MAP;
        } else {
            this.genericType = GenericType.NONE;
        }
        if (!FieldUtils.PRIMITIVES.contains(targetField.getType()) && genericType.equals(GenericType.NONE)) {
            newInstance = (Constructor<M>) targetField.getType().getConstructor();
        } else {
            Type actualTypeArgument;
            switch (genericType) {
                case LIST:
                case SET:
                    actualTypeArgument = ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0];
                    newInstance = ((Class) actualTypeArgument).getConstructor();
                    break;
                case MAP:
                    actualTypeArgument = ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[1];
                    newInstance = ((Class) actualTypeArgument).getConstructor();
                    break;
                case NONE:
                default:
                    newInstance = null;
            }
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
            return (M) getter.invoke(parent);
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
