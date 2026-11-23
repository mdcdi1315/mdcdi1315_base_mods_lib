package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.CompletableFuture;

/**
 * Provides a way to submit multiple decoding tasks together,
 * regardless of the codec that is being used for decoding the data. <br />
 * You just specify the data converter to use and the reader that reads
 * those converted data, and you are then good to go!
 * @param <TOps> The type of the data converter value.
 */
public abstract class DecoderPipeline<TOps>
{
    /**
     * Provides the data converter that is under use.
     */
    @NotNull
    protected final DynamicOps<TOps> data_converter;

    /**
     * Initializes a new instance of the {@link DecoderPipeline} class.
     * @param data_converter The data converter to use.
     * @throws ArgumentNullException {@code data_converter} is {@code null}.
     */
    public DecoderPipeline(DynamicOps<TOps> data_converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(data_converter, "data_converter");
        this.data_converter = data_converter;
    }

    private record DecodeData<T, D>(DecoderPipeline<T> pipeline, Decoder<D> decoder, OpenFileForReadFunction stream)
            implements Func1<D>
    {
        @Override
        public D function()
        {
            try (var stream_instance = stream.Open()) {
                if (stream_instance == null) {
                    throw new SerializationException("The stream instance returned by the provided function is null!!");
                } else {
                    return pipeline.Decode(decoder, stream_instance);
                }
            } catch (IOException e) {
                throw new AsyncIOErrorException(e);
            }
        }
    }

    private record FileReadFunction(Path path)
            implements OpenFileForReadFunction
    {
        @Override
        public InputStream Open() throws IOException { return PathDefaultOpenFunction(path); }
    }

    private static InputStream PathDefaultOpenFunction(Path path)
            throws IOException
    {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    /**
     * Decodes an object from the specified input stream and gets data valid for the data converter to convert.
     * @param stream The stream to read data from.
     * @return The decoded value to use to the data converter on a later processing stage.
     * @throws IOException If any I/O errors arise during reading the file.
     * @throws SerializationException If any invalid serialization patterns occur during reading the stream.
     */
    @MaybeNull
    protected abstract TOps DecodeFromStream(@DisallowNull InputStream stream) throws IOException, SerializationException;

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified stream.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param stream The {@link InputStream} to decode the value from.
     * @param <T> The type of the object to decode.
     * @return The decoded object value.
     * @throws IOException An I/O error occurred during reading.
     * @throws SerializationException If any other serialization error occurs.
     * @throws ArgumentNullException {@code decoder} and/or {@code stream} are {@code null}.
     */
    @MaybeNull
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final <T> T Decode(Decoder<T> decoder, InputStream stream)
            throws IOException, ArgumentNullException, SerializationException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(decoder, "decoder");
        var data_result = decoder.decode(data_converter, DecodeFromStream(stream));
        if (data_result.isSuccess()) {
            return data_result.result().map(Pair::getFirst).get();
        } else {
            throw new ErrorDataResultException(data_result.error().get());
        }
    }

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified stream. <br />
     * This is the asynchronous variant of the {@link #Decode(Decoder, InputStream)} method.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param executor The {@link Executor} where this decoding task is to be executed to.
     * @param stream A function, that upon invoking it, it provides the {@link InputStream} to decode the value from.
     * @return A {@link CompletableFuture} indicating the status of the decoding process, and provides the result
     * of that execution, if the execution was successful.
     * @param <T> The type of the object to decode.
     * @throws ArgumentNullException {@code decoder} and/or {@code stream} and/or {@code executor} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<T> DecodeAsync(Decoder<T> decoder, OpenFileForReadFunction stream, Executor executor)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(decoder, "decoder");
        ArgumentNullException.ThrowIfNull(executor, "executor");
        return CompletableFuture.supplyAsync(new DecodeData<>(this, decoder, stream), executor);
    }

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified stream. <br />
     * This is the asynchronous variant of the {@link #Decode(Decoder, InputStream)} method.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param stream A function, that upon invoking it, it provides the {@link InputStream} to decode the value from.
     * @return A {@link CompletableFuture} indicating the status of the decoding process, and provides the result
     * of that execution, if the execution was successful.
     * @param <T> The type of the object to decode.
     * @throws ArgumentNullException {@code decoder} and/or {@code stream} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<T> DecodeAsync(Decoder<T> decoder, OpenFileForReadFunction stream)
        throws ArgumentNullException
    {
        return DecodeAsync(decoder, stream, ForkJoinPool.commonPool());
    }

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified file.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param file The {@link Path} to the file to read to decode the value from.
     * @param <T> The type of the object to decode.
     * @return The decoded object value.
     * @throws IOException An I/O error occurred during reading.
     * @throws SerializationException If any other serialization error occurs.
     * @throws ArgumentNullException {@code decoder} and/or {@code file} are {@code null}.
     */
    @MaybeNull
    public final <T> T DecodeFromFile(Decoder<T> decoder, Path file)
            throws IOException, ArgumentNullException, SerializationException
    {
        ArgumentNullException.ThrowIfNull(file, "path");
        ArgumentNullException.ThrowIfNull(decoder, "decoder");
        try (var stream = PathDefaultOpenFunction(file)) { return Decode(decoder, stream); }
    }

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified file. <br />
     * This is the asynchronous variant of the {@link #DecodeFromFile(Decoder, Path)} method.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param executor The {@link Executor} where this decoding task is to be executed to.
     * @param file The {@link Path} to the file to read to decode the value from.
     * @return A {@link CompletableFuture} indicating the status of the decoding process, and provides the result
     * of that execution, if the execution was successful.
     * @param <T> The type of the object to decode.
     * @throws ArgumentNullException {@code decoder} and/or {@code stream} and/or {@code executor} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<T> DecodeFromFileAsync(Decoder<T> decoder, Path file, Executor executor)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        return DecodeAsync(decoder, new FileReadFunction(file), executor);
    }

    /**
     * Decodes a previously encoded value, with the specified decoder, from the specified file. <br />
     * This is the asynchronous variant of the {@link #DecodeFromFile(Decoder, Path)} method.
     * @param decoder The {@link Decoder} able to decode the specified object.
     * @param file The {@link Path} to the file to read to decode the value from.
     * @return A {@link CompletableFuture} indicating the status of the decoding process, and provides the result
     * of that execution, if the execution was successful.
     * @param <T> The type of the object to decode.
     * @throws ArgumentNullException {@code decoder} and/or {@code stream} and/or {@code executor} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<T> DecodeFromFileAsync(Decoder<T> decoder, Path file)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        return DecodeAsync(decoder, new FileReadFunction(file), ForkJoinPool.commonPool());
    }
}
