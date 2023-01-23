package com.example.penguinql.core.prune.pojo;

import com.example.penguinql.core.prune.FieldMeta;
import com.example.penguinql.core.prune.GenericType;
import lombok.Getter;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

import static com.example.penguinql.core.prune.FieldUtils.PRIMITIVES;

@Getter
public class PojoField<M> extends FieldMeta<M> {

    private Method setter;
    private Method getter;
    private Constructor<M> newInstance;
    private final Field field;
    /**
     * This filed only meaningful when generic field is Collection(Map, List, Set)
     */
    private Class<?> clazz;

    /**
     * For the purpose Root field
     */
    public PojoField(Class<?> clazz, GenericType genericType) throws Exception {
        super(genericType);
        this.field = null;
        this.clazz = clazz;
        init(clazz);
        newInstance = (Constructor<M>) clazz.getConstructor();

    }


    public PojoField(Field field, GenericType genericType) throws Exception {
        super(genericType);
        this.field = field;


        PropertyDescriptor propertyDescriptor = Arrays.stream(Introspector.getBeanInfo(field.getDeclaringClass()).getPropertyDescriptors())
                .filter(i -> i.getName().equalsIgnoreCase(field.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find getter/setter" + field.getDeclaringClass() + " " + field));


        getter = propertyDescriptor.getReadMethod();
        setter = propertyDescriptor.getWriteMethod();

        getter.setAccessible(true);
        setter.setAccessible(true);


        if (PRIMITIVES.contains(field.getType()) || Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        init(unWrapCollection(field));
        newInstance = (Constructor<M>) unWrapCollection(field).getConstructor();

    }


    private void init(Class<?> clazz) throws Exception {
        if (clazz.isPrimitive()) {
            return;
        }

        for (Field i : clazz.getDeclaredFields()) {
            if (Modifier.isFinal(i.getModifiers()) || Modifier.isStatic(i.getModifiers())) {
                continue;
            }
            if (PRIMITIVES.contains(i.getType())) {
                this.leafChildren.put(i.getName(), new PojoField<>(i, GenericType.NONE));
            } else {
                this.extendableChildren.put(genericType.isCollection() ? VALUE : i.getName(), new PojoField<>(i, genericType(i)));
            }
        }

    }

    private Class<?> unWrapCollection(Field field) {
        Class<?> clazz = field.getType();

        if (clazz.isAssignableFrom(Map.class)) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
        } else if (clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Set.class)) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } else {
            return clazz;
        }
    }

    private GenericType genericType(Field targetField) {
        if (targetField.getType().isAssignableFrom(Set.class)) {
            return GenericType.SET;
        } else if (targetField.getType().isAssignableFrom(List.class)) {
            return GenericType.LIST;
        } else if (targetField.getType().isAssignableFrom(Map.class)) {
            return GenericType.MAP;
        } else {
            return GenericType.NONE;
        }

    }


    @Override
    public void setData(Object parent, M myself) {
        try {
            setter.invoke(parent, myself);
        } catch (Exception e) {
            throw new RuntimeException("Cannot assign value");
        }

    }

    @Override
    public M getData(Object parent) {
        try {
            return (M) this.getter.invoke(parent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public M baseInstance() {
        try {
            return newInstance.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create value");
        }
    }
}