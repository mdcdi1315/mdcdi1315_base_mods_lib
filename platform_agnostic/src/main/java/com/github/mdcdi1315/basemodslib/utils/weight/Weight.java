package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.codecs.PrimitiveCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides the randomized weighted logic as it was defined in Minecraft versions prior 1.21.5,
 * with a look on performance and proper serialization routines.
 * @since 1.0.18
 */
public final class Weight
{
    /**
     * Gets the single and only {@link Weight} value that is zero.
     */
    public static final Weight ZERO;

    /**
     * Gets the single and only {@link Weight} value that is one.
     */
    public static final Weight ONE;

    /**
     * This field provides a proper serialization codec for {@link Weight} instances, with appropriate validation and behavior as specified by the in-code {@link Weight#Of} method.
     */
    public static final Codec<Weight> CODEC;

    static {
        CODEC = new InternalCodec();
        ZERO = new Weight(0);
        ONE = new Weight(1);
    }

    private static final class InternalCodec
            extends PrimitiveCodec<Weight>
    {
        private static DataResult<Weight> ValidateAndReturn(Number n)
        {
            int decoded = n.intValue();
            return (decoded < 0) ?
                    CodecUtils.CreateJavaFormattedErrorDataResult("Weight must be more than or equal to zero.\nActual value: %d" , decoded) :
                    DataResult.success((decoded == 0) ? Weight.ZERO : ((decoded == 1) ? Weight.ONE : new Weight(decoded)));
        }

        @Override
        @SuppressWarnings({"OptionalIsPresent", "OptionalGetWithoutIsPresent"})
        protected <T> DataResult<Weight> Read(DynamicOps<T> ops, T input)
        {
            DataResult<Number> n = ops.getNumberValue(input);
            var e = n.error();
            return e.isPresent() ? DataResult.error(e.get().messageSupplier()) : ValidateAndReturn(n.result().get());
        }

        @Override
        protected <T> T Write(DynamicOps<T> ops, Weight value) { return ops.createNumeric(value.Value); }
    }

    /**
     * Gets the value held by this {@link Weight} instance.
     */
    public final int Value;

    // Enforce using the Of method below.
    private Weight(int weight) { Value = weight; }

    /**
     * Creates a new {@link Weight} instance through code, of the specified weight. <br />
     * Weights are integer values that are positive or zero.
     * @param wt The value that the {@link Weight} instance will have.
     * @return The created weight value. May return the value of {@link Weight#ZERO} or {@link Weight#ONE} fields depending on which integer is passed as the argument.
     * @throws ArgumentOutOfRangeException {@code weight} is less than zero.
     */
    public static Weight Of(int wt)
            throws ArgumentOutOfRangeException
    {
        if (wt < 0) {
            throw new ArgumentOutOfRangeException("wt" , "Weight must be more than or equal to zero.");
        } else {
            return (wt == 0) ? Weight.ZERO : ((wt == 1) ? Weight.ONE : new Weight(wt));
        }
    }
}
