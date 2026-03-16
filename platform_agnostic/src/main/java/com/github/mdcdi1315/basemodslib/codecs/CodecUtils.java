package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.codecs.internal.*;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * Provides Codec manipulation &amp; easy creation methods.
 */
@SuppressWarnings("unused")
public final class CodecUtils
{
    private CodecUtils() {}

    /**
     * Gets a singleton of the 'probability' codec, that is a codec that can only take a floating range of values from 0 to 1, all inclusive.
     */
    public static final Codec<Float> FLOAT_PROBABILITY = new FloatProbabilityCodec();

    /**
     * Gets a singleton of the 'probability' codec, that is a codec that can only take a double-precision floating range of values from 0 to 1, all inclusive.
     */
    public static final Codec<Double> DOUBLE_PROBABILITY = new DoubleProbabilityCodec();

    /**
     * Gets a singleton of a codec that only accepts positive {@link Integer} values.
     */
    public static final Codec<Integer> POSITIVE_INTEGER = new PositiveIntegerCodec();

    /**
     * Gets a singleton of a codec that only accepts positive {@link Float} values.
     * @since 1.0.5
     */
    public static final Codec<Float> POSITIVE_FLOAT = new PositiveFloatCodec();

    /**
     * Gets a singleton of a codec that accepts zero or positive {@link Integer} values only.
     */
    public static final Codec<Integer> ZERO_OR_POSITIVE_INTEGER = new ZeroOrPositiveIntegerCodec();

    /**
     * Gets a singleton of a codec that can de/encode {@link Version} objects.
     * @since 1.0.14
     */
    public static final Codec<Version> VERSION = new VersionCodec();

    /**
     * Returns a codec that ensures that the given list {@link Codec} will always have a non-empty list, that is a list at least containing one element.
     * @param codec The list codec to create the non-empty list codec from.
     * @return A codec capable of checking that the list provided through {@code codec} is not empty. Substitutes the {@code codec} parameter.
     * @param <T> The type of the list's elements.
     * @throws ArgumentNullException {@code codec} is {@code null}.
     */
    public static <T> Codec<List<T>> NonEmptyList(Codec<List<T>> codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(codec , "codec");
        var c = new NonEmptyListChecker<T>(); // We do still need the checker for this case
        return codec.flatXmap(c , c);
    }

    /**
     * Constructs a list codec from the specified element codec, plus verifying that the list returned will be always non-empty.
     * @param elementcodec The codec that can de/serialize {@link T}.
     * @return A list codec capable of checking that the list is not empty. The elements are de/serialized by the provided {@code elementcodec} parameter.
     * @param <T> The type of the list's elements, it is also the type of the {@code elementcodec} parameter.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public static <T> Codec<List<T>> NonEmptyListFromElementCodec(Codec<T> elementcodec)
            throws ArgumentNullException
    {
        // For API parity reasons use the non-empty strict list codec.
        // It behaves the same as the DFU's ListCodec class does.
        return new NonEmptyStrictListCodec<>(elementcodec);
    }

    /**
     * Defines a codec for defining a short type field with the specified range.
     * @param min_inclusive The inclusive lower bound of the values that the new field accepts.
     * @param max_inclusive The inclusive upper bound of the values that the new field accepts.
     * @return A codec, also checking whether the number is in the bounds specified.
     * @implNote The parameters are perpetually declared as of type of integer so that the constant values are passed directly.
     * Do not be confused, however, because the method finally converts the bounds to short values.
     */
    public static Codec<Short> ShortRange(int min_inclusive , int max_inclusive)
    {
        return new ShortRangeCodec(min_inclusive , max_inclusive);
    }

    /**
     * Defines a codec for defining a byte type field with the specified range.
     * @param min_inclusive The inclusive lower bound of the values that the new field accepts.
     * @param max_inclusive The inclusive upper bound of the values that the new field accepts.
     * @return A codec, also checking whether the number is in the bounds specified.
     * @implNote The parameters are perpetually declared as of type of integer so that the constant values are passed directly.
     * Do not be confused, however, because the method finally converts the bounds to byte values.
     */
    public static Codec<Byte> ByteRange(int min_inclusive, int max_inclusive)
    {
        return new ByteRangeCodec(min_inclusive , max_inclusive);
    }

    /**
     * Creates an error data result of the specified error message and returns it back to the caller.
     * @param message The message to represent as an error.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} is {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateErrorDataResult(@ConstantExpected String message)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        return DataResult.error(new StringSupplier(message));
    }

    /**
     * Creates an error data result of the specified error message and partial result and returns it back to the caller.
     * @param message The message to represent as an error.
     * @param partial_result The partial result to also provide.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} and/or {@code partial_result} are {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateErrorDataResult(@ConstantExpected String message, T partial_result)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        ArgumentNullException.ThrowIfNull(partial_result, "partial_result");
        return DataResult.error(new StringSupplier(message), partial_result);
    }

    /**
     * Creates an error data result of the specified error message formatted by the Java formatting rules, the format arguments that transform the format string, and returns it back to the caller.
     * @param message The message to represent as an error.
     * @param format_args The formatting arguments that transform the message string.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} and/or {@code format_args} are {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateJavaFormattedErrorDataResult(@ConstantExpected String message, Object... format_args)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        ArgumentNullException.ThrowIfNull(format_args, "format_args");
        return DataResult.error(StringSupplier.FromFormatted(message, format_args));
    }

    /**
     * Creates an error data result of the specified error message formatted by the Java formatting rules, the format arguments that transform the format string, and returns it back to the caller.
     * @param message The message to represent as an error.
     * @param partial_result The partial result to also provide.
     * @param format_args The formatting arguments that transform the message string.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} and/or {@code partial_result} and/or {@code format_args} are {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateJavaFormattedErrorDataResultWithPartial(@ConstantExpected String message, T partial_result, Object... format_args)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        ArgumentNullException.ThrowIfNull(format_args, "format_args");
        return DataResult.error(StringSupplier.FromFormatted(message, format_args));
    }

    /**
     * Creates an error data result of the specified error message formatted by the .NET formatting rules, the format arguments that transform the format string, and returns it back to the caller.
     * @param message The message to represent as an error.
     * @param format_args The formatting arguments that transform the message string.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} and/or {@code format_args} are {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateDotNetFormattedErrorDataResult(@ConstantExpected String message, Object... format_args)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        ArgumentNullException.ThrowIfNull(format_args, "format_args");
        return DataResult.error(StringSupplier.FromDotNetFormatted(message, format_args));
    }

    /**
     * Creates an error data result of the specified error message formatted by the Java formatting rules, the format arguments that transform the format string, and returns it back to the caller.
     * @param message The message to represent as an error.
     * @param partial_result The partial result to also provide.
     * @param format_args The formatting arguments that transform the message string.
     * @return An error {@link DataResult} object.
     * @param <T> The type that would be returned if the {@link DataResult} object was a successful instance.
     * @throws ArgumentNullException {@code message} and/or {@code partial_result} and/or {@code format_args} are {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static <T> DataResult<T> CreateDotNetFormattedErrorDataResultWithPartial(@ConstantExpected String message, T partial_result, Object... format_args)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        ArgumentNullException.ThrowIfNull(format_args, "format_args");
        return DataResult.error(StringSupplier.FromDotNetFormatted(message, format_args));
    }

    /**
     * Creates a record codec directly from the specified applicative object.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     */
    public static <TCODEC , C1T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            Function<C1T, TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_1<>(codecfield1, instancecreatefunction));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     */
    public static <TCODEC , C1T , C2T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            BiFunction<C1T, C2T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_2<>(codecfield1, codecfield2, instancecreatefunction));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            Function3<C1T, C2T , C3T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_3<>(codecfield1, codecfield2, codecfield3, instancecreatefunction));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            Function4<C1T, C2T , C3T , C4T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_4<>(codecfield1, codecfield2, codecfield3, codecfield4, instancecreatefunction));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            Function5<C1T , C2T , C3T , C4T , C5T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_5<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            Function6<C1T , C2T , C3T , C4T , C5T , C6T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_6<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            Function7<C1T , C2T , C3T , C4T , C5T , C6T , C7T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_7<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            Function8<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_8<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            Function9<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_9<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            Function10<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_10<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            Function11<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_11<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            Function12<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_12<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            Function13<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_13<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            Function14<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_14<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param codecfield15 The applicative object representing the fifteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     * @param <C15T> The type of the fifteenth field of the created record codec.
     * @since 1.0.18
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            App<RecordCodecBuilder.Mu<TCODEC> , C15T> codecfield15,
            Function15<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_15<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                codecfield15,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param codecfield15 The applicative object representing the fifteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     * @param <C15T> The type of the fifteenth field of the created record codec.
     * @since 1.0.18
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, C16T> Codec<TCODEC> CreateCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            App<RecordCodecBuilder.Mu<TCODEC> , C15T> codecfield15,
            App<RecordCodecBuilder.Mu<TCODEC> , C16T> codecfield16,
            Function16<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, C16T, TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.create(new CreateCodecDirect_16<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                codecfield15,
                codecfield16,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative object.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     */
    public static <TCODEC , C1T> MapCodec<TCODEC> CreateMapCodecDirect(App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1, Function<C1T, TCODEC> instancecreatefunction) {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_1<>(codecfield1, instancecreatefunction));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     */
    public static <TCODEC , C1T , C2T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            BiFunction<C1T, C2T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_2<>(codecfield1, codecfield2, instancecreatefunction));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            Function3<C1T, C2T , C3T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_3<>(codecfield1, codecfield2, codecfield3, instancecreatefunction));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            Function4<C1T, C2T , C3T , C4T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_4<>(codecfield1, codecfield2, codecfield3, codecfield4, instancecreatefunction));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            Function5<C1T , C2T , C3T , C4T , C5T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_5<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            Function6<C1T , C2T , C3T , C4T , C5T , C6T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_6<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            Function7<C1T , C2T , C3T , C4T , C5T , C6T , C7T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_7<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            Function8<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_8<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            Function9<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_9<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            Function10<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_10<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            Function11<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_11<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            Function12<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_12<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            Function13<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_13<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record map codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            Function14<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T , TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_14<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param codecfield15 The applicative object representing the fifteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     * @param <C15T> The type of the fifteenth field of the created record codec.
     * @since 1.0.18
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            App<RecordCodecBuilder.Mu<TCODEC> , C15T> codecfield15,
            Function15<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_15<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                codecfield15,
                instancecreatefunction
        ));
    }

    /**
     * Creates a record map codec directly from the specified applicative objects.
     * @param codecfield1 The applicative object representing the first field of the record.
     * @param codecfield2 The applicative object representing the second field of the record.
     * @param codecfield3 The applicative object representing the third field of the record.
     * @param codecfield4 The applicative object representing the fourth field of the record.
     * @param codecfield5 The applicative object representing the fifth field of the record.
     * @param codecfield6 The applicative object representing the sixth field of the record.
     * @param codecfield7 The applicative object representing the seventh field of the record.
     * @param codecfield8 The applicative object representing the eighth field of the record.
     * @param codecfield9 The applicative object representing the ninth field of the record.
     * @param codecfield10 The applicative object representing the tenth field of the record.
     * @param codecfield11 The applicative object representing the eleventh field of the record.
     * @param codecfield12 The applicative object representing the twelveth field of the record.
     * @param codecfield13 The applicative object representing the thirteenth field of the record.
     * @param codecfield14 The applicative object representing the fourteenth field of the record.
     * @param codecfield15 The applicative object representing the fifteenth field of the record.
     * @param instancecreatefunction A function able to create a new instance of type {@link TCODEC}.
     * @return The constructed record codec.
     * @param <TCODEC> The type of the record to de/encode.
     * @param <C1T> The type of the first field of the created record codec.
     * @param <C2T> The type of the second field of the created record codec.
     * @param <C3T> The type of the third field of the created record codec.
     * @param <C4T> The type of the fourth field of the created record codec.
     * @param <C5T> The type of the fifth field of the created record codec.
     * @param <C6T> The type of the sixth field of the created record codec.
     * @param <C7T> The type of the seventh field of the created record codec.
     * @param <C8T> The type of the eighth field of the created record codec.
     * @param <C9T> The type of the ninth field of the created record codec.
     * @param <C10T> The type of the tenth field of the created record codec.
     * @param <C11T> The type of the eleventh field of the created record codec.
     * @param <C12T> The type of the twelveth field of the created record codec.
     * @param <C13T> The type of the thirteenth field of the created record codec.
     * @param <C14T> The type of the fourteenth field of the created record codec.
     * @param <C15T> The type of the fifteenth field of the created record codec.
     * @since 1.0.18
     */
    public static <TCODEC , C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, C16T> MapCodec<TCODEC> CreateMapCodecDirect(
            App<RecordCodecBuilder.Mu<TCODEC> , C1T> codecfield1,
            App<RecordCodecBuilder.Mu<TCODEC> , C2T> codecfield2,
            App<RecordCodecBuilder.Mu<TCODEC> , C3T> codecfield3,
            App<RecordCodecBuilder.Mu<TCODEC> , C4T> codecfield4,
            App<RecordCodecBuilder.Mu<TCODEC> , C5T> codecfield5,
            App<RecordCodecBuilder.Mu<TCODEC> , C6T> codecfield6,
            App<RecordCodecBuilder.Mu<TCODEC> , C7T> codecfield7,
            App<RecordCodecBuilder.Mu<TCODEC> , C8T> codecfield8,
            App<RecordCodecBuilder.Mu<TCODEC> , C9T> codecfield9,
            App<RecordCodecBuilder.Mu<TCODEC> , C10T> codecfield10,
            App<RecordCodecBuilder.Mu<TCODEC> , C11T> codecfield11,
            App<RecordCodecBuilder.Mu<TCODEC> , C12T> codecfield12,
            App<RecordCodecBuilder.Mu<TCODEC> , C13T> codecfield13,
            App<RecordCodecBuilder.Mu<TCODEC> , C14T> codecfield14,
            App<RecordCodecBuilder.Mu<TCODEC> , C15T> codecfield15,
            App<RecordCodecBuilder.Mu<TCODEC> , C16T> codecfield16,
            Function16<C1T , C2T , C3T , C4T , C5T , C6T , C7T , C8T , C9T , C10T , C11T , C12T , C13T , C14T, C15T, C16T, TCODEC> instancecreatefunction
    )
    {
        return RecordCodecBuilder.mapCodec(new CreateCodecDirect_16<>(
                codecfield1,
                codecfield2,
                codecfield3,
                codecfield4,
                codecfield5,
                codecfield6,
                codecfield7,
                codecfield8,
                codecfield9,
                codecfield10,
                codecfield11,
                codecfield12,
                codecfield13,
                codecfield14,
                codecfield15,
                codecfield16,
                instancecreatefunction
        ));
    }

}