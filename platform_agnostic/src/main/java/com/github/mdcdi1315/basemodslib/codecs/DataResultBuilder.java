package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.basemodslib.codecs.internal.DataResultBuilderOfFive;
import com.github.mdcdi1315.basemodslib.codecs.internal.DataResultBuilderOfFour;
import com.github.mdcdi1315.basemodslib.codecs.internal.DataResultBuilderOfThree;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;
import com.github.mdcdi1315.basemodslib.codecs.internal.DataResultBuilderOfTwo;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a way of building {@link DataResult} values from multiple values and concatenate them together. <br />
 * Typically used for record instances during decoding.
 * @param <TResult> The result type to be produced.
 * @since 1.0.19
 */
public abstract class DataResultBuilder<TResult>
{
    /**
     * Builds a {@link DataResult}.
     * @return The built {@link DataResult} instance.
     */
    public abstract DataResult<TResult> Build();

    /**
     * Creates a {@link DataResultBuilder} from the current builder, and specifying an input value obtained from a {@link com.mojang.serialization.Decoder#decode(DynamicOps, Object)}.
     * @param input The input value to specify it as a {@link Pair}.
     * @return A {@link DataResultBuilder} of the pair that contains the result of the current builder, plus the input obtained.
     * @param <TO> The type of input value, typically it is the type specified in the implementation of {@link DynamicOps}.
     */
    public final <TO> DataResultBuilder<Pair<TResult, TO>> WithOpsInput(TO input) { return new WithOpsInput<>(this, input); }

    /**
     * Maps the current {@link DataResultBuilder} to a new {@link DataResultBuilder} of the desired result type.
     * @param mapper The function that maps from {@link TResult} to {@link TMapped}.
     * @return A {@link DataResultBuilder} instance that returns a {@link TMapped} object instead of a {@link TResult} object.
     * @param <TMapped> The type of the object that the returned builder will create.
     */
    public final <TMapped> DataResultBuilder<TMapped> Map(Func2<TResult, TMapped> mapper) { return new Mapped<>(this, mapper); }

    /**
     * Before returning a success value, if any, check whether that value satisfies the given predicate.
     * @param predicate The predicate that must be also satisfied for the built {@link DataResult} to return {@link DataResult#isSuccess()}.
     * @return A new {@link DataResultBuilder} that applies a check on the return value.
     */
    public final DataResultBuilder<TResult> WithCheck(Predicate<TResult> predicate) { return new WithCheck<>(this, predicate); }

    private static final class WithOpsInput<TO ,TR>
            extends DataResultBuilder<Pair<TR, TO>>
    {
        private final TO ops_input;
        private final DataResultBuilder<TR> root;

        public WithOpsInput(DataResultBuilder<TR> root, TO input) { this.root = root; this.ops_input = input; }

        @Override
        public DataResult<Pair<TR, TO>> Build()
        {
            DataResult<TR> result = root.Build();
            return result.isSuccess() ?
                    DataResult.success(Pair.of(result.result().get(), ops_input)) :
                    DataResult.error(result.error().get().messageSupplier());
        }
    }

    private static final class Mapped<TS, TR>
            extends DataResultBuilder<TR>
    {
        private final Func2<TS, TR> mapper;
        private final DataResultBuilder<TS> root;

        public Mapped(DataResultBuilder<TS> root, Func2<TS, TR> mapper) { this.root = root; this.mapper = mapper; }

        @Override
        public DataResult<TR> Build()
        {
            DataResult<TS> result = root.Build();
            return result.isSuccess() ?
                    DataResult.success(mapper.function(result.result().get())) :
                    DataResult.error(result.error().get().messageSupplier());
        }
    }

    private static final class WithCheck<TR>
        extends DataResultBuilder<TR>
    {
        private final Predicate<TR> check;
        private final DataResultBuilder<TR> root;

        public WithCheck(DataResultBuilder<TR> root, Predicate<TR> chk) { this.root = root; check = chk; }

        @Override
        public DataResult<TR> Build()
        {
            DataResult<TR> result = root.Build();
            if (result.isSuccess()) {
                TR value = result.result().get();
                if (check.test(value)) {
                    return DataResult.success(value);
                } else {
                    return DataResult.error(StringSupplier.FromDotNetFormatted("Predicate was not satisfied. Value: {0}", value));
                }
            } else {
                return result;
            }
        }
    }

    private static final class Direct<TR>
        extends DataResultBuilder<TR>
    {
        private final DataResult<TR> value;

        public Direct(DataResult<TR> value) { this.value = value; }

        @Override
        public DataResult<TR> Build() { return value; }
    }

    /**
     * Creates a new {@link DataResultBuilder} from an already existing {@link DataResult}.
     * @param result The already existing data result to apply.
     * @return A new {@link DataResultBuilder} instance.
     * @param <TR> The type of the data result that is also returned by the {@link DataResultBuilder} upon build.
     */
    public static <TR> DataResultBuilder<TR> Of(DataResult<TR> result) { return new Direct<>(result); }

    /**
     * Creates a new {@link DataResultBuilder} instance from two {@link DataResult} instances. <br />
     * The function specified in {@code creator} parameter specifies how the two inputs will be transformed.
     * @param v1 The first {@link DataResult} instance.
     * @param v2 The second {@link DataResult} instance.
     * @param creator The function that converts the results of the {@code v1} and {@code v2} parameters into an instance of type {@link TR}.
     * @return A new {@link DataResultBuilder} that returns an object of type {@link TR}.
     * @param <T1> Type that is returned from the first {@link DataResult} parameter.
     * @param <T2> Type that is returned from the second {@link DataResult} parameter.
     * @param <TR> The result type that is returned from the builder by applying the function given in {@code creator}.
     */
    public static <T1, T2, TR> DataResultBuilder<TR> Of(DataResult<T1> v1, DataResult<T2> v2, Func3<T1, T2, TR> creator) { return new DataResultBuilderOfTwo<>(v1, v2, creator); }

    /**
     * Creates a new {@link DataResultBuilder} instance from three {@link DataResult} instances. <br />
     * The function specified in {@code creator} parameter specifies how the two inputs will be transformed.
     * @param v1 The first {@link DataResult} instance.
     * @param v2 The second {@link DataResult} instance.
     * @param v3 The third {@link DataResult} instance.
     * @param creator The function that converts the results of the {@code v1} and {@code v2} and {@code v3} parameters into an instance of type {@link TR}.
     * @return A new {@link DataResultBuilder} that returns an object of type {@link TR}.
     * @param <T1> Type that is returned from the first {@link DataResult} parameter.
     * @param <T2> Type that is returned from the second {@link DataResult} parameter.
     * @param <T3> Type that is returned from the third {@link DataResult} parameter.
     * @param <TR> The result type that is returned from the builder by applying the function given in {@code creator}.
     */
    public static <T1, T2, T3, TR> DataResultBuilder<TR> Of(DataResult<T1> v1, DataResult<T2> v2, DataResult<T3> v3, Func4<T1, T2, T3, TR> creator) { return new DataResultBuilderOfThree<>(v1, v2, v3, creator); }

    /**
     * Creates a new {@link DataResultBuilder} instance from four {@link DataResult} instances. <br />
     * The function specified in {@code creator} parameter specifies how the two inputs will be transformed.
     * @param v1 The first {@link DataResult} instance.
     * @param v2 The second {@link DataResult} instance.
     * @param v3 The third {@link DataResult} instance.
     * @param v4 The fourth {@link DataResult} instance.
     * @param creator The function that converts the results of the {@code v1} and {@code v2} and {@code v3} parameters into an instance of type {@link TR}.
     * @return A new {@link DataResultBuilder} that returns an object of type {@link TR}.
     * @param <T1> Type that is returned from the first {@link DataResult} parameter.
     * @param <T2> Type that is returned from the second {@link DataResult} parameter.
     * @param <T3> Type that is returned from the third {@link DataResult} parameter.
     * @param <T4> Type that is returned from the fourth {@link DataResult} parameter.
     * @param <TR> The result type that is returned from the builder by applying the function given in {@code creator}.
     * @since 1.0.20
     */
    public static <T1, T2, T3, T4, TR> DataResultBuilder<TR> Of(DataResult<T1> v1, DataResult<T2> v2, DataResult<T3> v3, DataResult<T4> v4, Func5<T1, T2, T3, T4, TR> creator) { return new DataResultBuilderOfFour<>(v1, v2, v3, v4, creator); }

    /**
     * Creates a new {@link DataResultBuilder} instance from five {@link DataResult} instances. <br />
     * The function specified in {@code creator} parameter specifies how the two inputs will be transformed.
     * @param v1 The first {@link DataResult} instance.
     * @param v2 The second {@link DataResult} instance.
     * @param v3 The third {@link DataResult} instance.
     * @param v4 The fourth {@link DataResult} instance.
     * @param v5 The fifth {@link DataResult} instance.
     * @param creator The function that converts the results of the {@code v1} and {@code v2} and {@code v3} parameters into an instance of type {@link TR}.
     * @return A new {@link DataResultBuilder} that returns an object of type {@link TR}.
     * @param <T1> Type that is returned from the first {@link DataResult} parameter.
     * @param <T2> Type that is returned from the second {@link DataResult} parameter.
     * @param <T3> Type that is returned from the third {@link DataResult} parameter.
     * @param <T4> Type that is returned from the fourth {@link DataResult} parameter.
     * @param <T5> Type that is returned from the fifth {@link DataResult} parameter.
     * @param <TR> The result type that is returned from the builder by applying the function given in {@code creator}.
     * @since 1.0.20
     */
    public static <T1, T2, T3, T4, T5, TR> DataResultBuilder<TR> Of(DataResult<T1> v1, DataResult<T2> v2, DataResult<T3> v3, DataResult<T4> v4, DataResult<T5> v5, Func6<T1, T2, T3, T4, T5, TR> creator) { return new DataResultBuilderOfFive<>(v1, v2, v3, v4, v5, creator); }

}

