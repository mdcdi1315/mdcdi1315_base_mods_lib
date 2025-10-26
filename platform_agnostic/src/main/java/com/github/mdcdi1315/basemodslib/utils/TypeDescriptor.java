package com.github.mdcdi1315.basemodslib.utils;

import java.lang.reflect.Type;

/**
 * Helper class for getting the class of type parameters. <br />
 * The described class object is returned through the {@link #DescribeTClass()} method.
 * @param <T> The type of the Java object to describe.
 */
public final class TypeDescriptor<T>
{
    private final Class<T> cls;

    public static <T> Class<T> DescribeTypeParameter() {
        return new TypeDescriptor<T>().DescribeTClass();
    }

    /**
     * Initializes a new instance of the type descriptor class.
     */
    @SuppressWarnings("unchecked")
    public TypeDescriptor() {
        Class<T> clt = null;
        try {
            Type gentype = getClass().getTypeParameters()[0].getBounds()[0];
            clt = (Class<T>) Class.forName(gentype.getTypeName());
        } catch (ClassNotFoundException ignored) {}
        cls = clt;
    }

    /**
     * Returns a class object describing the type {@link T}.
     * @return A {@link Class} object describing {@link T}.
     */
    public Class<T> DescribeTClass() {
        return cls;
    }
}
