package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a class for custom configuration fields of custom types. <br />
 * Note that, implementing a custom {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} will not work appropriately. <br />
 * As such, users that want custom configuration fields should extend from this base class instead.
 * @param <T> The type of the field to be encoded and decoded.
 * @since 1.0.15
 */
public abstract class CustomConfigField<T>
    extends BaseConfigField<T>
{
    protected CustomConfigField(String name, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        super(name, constraints);
    }

    protected CustomConfigField(String name, @AllowNull String comment, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
    }

    /**
     * Gets a {@link Codec} that can de/encode the current field.
     * @return The {@link Codec} handling the de/serialization of the associated field.
     */
    @NotNull
    public abstract Codec<T> GetCodec();

    public final <TD> DataResult<TD> Encode(DynamicOps<TD> ops, TD prefix) {
        return GetCodec().encode(GetValue(), ops, prefix);
    }

    public final <TD> DataResult<Pair<T, TD>> Decode(DynamicOps<TD> ops, TD input) {
        return GetCodec().decode(ops, input);
    }
}
