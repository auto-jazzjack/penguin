package io.penguin.penguinql.core.prune;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * This is definition of Type of myself and mother.
 */
@Getter
@AllArgsConstructor
public abstract class FieldMeta<M> {

    /**
     * This is definition of how parent define MySelf.
     */
    protected GenericType genericType;

    @Getter
    protected Map<String, FieldMeta<Object>> leafChildren;
    @Getter
    protected Map<String, FieldMeta<Object>> extendableChildren;
    public static final String VALUE = "value";


    public FieldMeta(GenericType genericType) {
        this.genericType = genericType;
        this.leafChildren = new HashMap<>();
        this.extendableChildren = new HashMap<>();
    }

    public abstract void setData(Object parent, M myself);

    public abstract M getData(Object parent);

    public abstract M baseInstance();
}