package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a {@link Codec} suitable to de/encode enumeration types. <br />
 * Derived classes are allowed for this implementation, and can alter the de/encoding behavior.
 * @param <T> The type of the enumeration class to be de/encoded.
 */
public class EnumCodec<T extends Enum<T>>
    implements Codec<T>
{
    /**
     * Provides the {@code enum_class} argument passed to the {@link #EnumCodec(Class)} constructor for derived classes.
     */
    @NotNull
    protected final Class<T> enum_class;

    /**
     * Initializes a new instance of the {@link EnumCodec} class from the specified class that describes the enumeration type to de/encode.
     * @param enum_class The class that provides information on how to de/encode the specified enumeration value.
     * @throws ArgumentNullException {@code enum_class} is {@code null}.
     */
    public EnumCodec(Class<T> enum_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enum_class, "enum_class");
        this.enum_class = enum_class;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        var dsv = ops.getStringValue(input);
        if (dsv.isSuccess()) {
            String constant = dsv.result().get();
            try {
                return DataResult.success(new Pair<>(T.valueOf(enum_class, constant) , input));
            } catch (IllegalArgumentException iae) {
                return DataResult.error(StringSupplier.FromFormatted("Cannot find the enumeration constant %s in class %s." , constant , enum_class.getName()));
            }
        } else {
            return DataResult.error(StringSupplier.FromFormatted("Not a string value: %s" , input));
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        // Will always succeed because enum instances are only those specified by the class itself.
        return DataResult.success(ops.createString(input.name()));
    }
}
