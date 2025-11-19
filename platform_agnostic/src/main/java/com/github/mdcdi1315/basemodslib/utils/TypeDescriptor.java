package com.github.mdcdi1315.basemodslib.utils;

import java.lang.reflect.Type;

/**
 * Helper class for getting the class of type parameters. <br />
 * The described class object is returned through the {@link #DescribeTClass()} method.
 * @param <T> The type of the Java object to describe.
 * @deprecated Although practical, this class cannot correctly determine the types and return {@link Object} at most times. <br />
 * This will not be removed for binary compatibility, but newer consumers of the library will not be able to use it.
 */
@Deprecated(since = "1.0.9")
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
