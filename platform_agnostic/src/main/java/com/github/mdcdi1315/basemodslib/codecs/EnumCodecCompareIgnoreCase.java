package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a special variant of the {@link EnumCodec} class that compares the enumeration constants ignoring their casing during decode process.
 * @param <T> The type of the enumeration class to be de/encoded.
 */
public class EnumCodecCompareIgnoreCase<T extends Enum<T>>
    extends EnumCodec<T>
{
    /**
     * Initializes a new instance of the {@link EnumCodecCompareIgnoreCase} class from the specified class that describes the enumeration type to de/encode.
     *
     * @param enum_class The class that provides information on how to de/encode the specified enumeration value.
     * @throws ArgumentNullException {@code enum_class} is {@code null}.
     */
    public EnumCodecCompareIgnoreCase(Class<T> enum_class) throws ArgumentNullException { super(enum_class); }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
    {
        var dsv = ops.getStringValue(input);
        if (dsv.isSuccess()) {
            String constant = dsv.result().get();
            for (T value : enum_class.getEnumConstants())
            {
                if (value.name().equalsIgnoreCase(constant)) {
                    return DataResult.success(new Pair<>(value , input));
                }
            }
            return DataResult.error(StringSupplier.FromFormatted("Cannot find the enumeration constant %s in class %s." , constant , enum_class.getName()));
        } else {
            return DataResult.error(StringSupplier.FromFormatted("Not a string value: %s" , input));
        }
    }
}
