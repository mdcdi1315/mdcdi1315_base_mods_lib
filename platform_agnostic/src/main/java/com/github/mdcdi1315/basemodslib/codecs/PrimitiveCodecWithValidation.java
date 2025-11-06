package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * A special derivative of {@link PrimitiveCodec} for defining validation routines before the actual read number is finally returned to the caller.
 * @param <TPR> The type of the number to decode. This must be a derivative of {@link Number} class.
 */
public abstract class PrimitiveCodecWithValidation<TPR extends Number> // Only valid for numeric types
    extends PrimitiveCodec<TPR>
{
    private static <T> DataResult<T> ErrorMapper(DataResult.Error<Number> pr) {
        return DataResult.error(pr::message);
    }

    @Override
    protected <T> T Write(DynamicOps<T> ops, TPR value) {
        return ops.createNumeric(value);
    }

    @Override
    protected <T> DataResult<TPR> Read(DynamicOps<T> ops, T input) {
        DataResult<Number> n = ops.getNumberValue(input);
        var e = n.error();
        return e
                .<DataResult<TPR>>map(PrimitiveCodecWithValidation::ErrorMapper)
                .orElse(Validate(Mapper(n.result().get())));
    }

    /**
     * Maps the read number into the specified numeric instance. <br />
     * Typically, the method provides the value from mapping functions already contained in the {@link Number} class.
     * @param number The number to map as {@link TPR}.
     * @return The mapped numeric instance.
     */
    protected abstract TPR Mapper(Number number);

    /**
     * Provides a routine for validating the mapped number.
     * @param number The number to validate.
     * @return A data result, indicating whether validation succeeded or not.
     */
    protected abstract DataResult<TPR> Validate(TPR number);
}
