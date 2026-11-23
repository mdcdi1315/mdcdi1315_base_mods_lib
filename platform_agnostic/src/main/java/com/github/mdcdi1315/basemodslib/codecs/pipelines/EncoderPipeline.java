package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Action0;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.mojang.serialization.Encoder;
import com.mojang.serialization.DynamicOps;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.CompletableFuture;

/**
 * Provides a way to submit multiple encoding tasks together,
 * regardless of the codec that is being used for encoding the data. <br />
 * You just specify the data converter to use and the writer that writes
 * those converted data, and you are then good to go!
 * @param <TOps> The type of the data converter value.
 */
public abstract class EncoderPipeline<TOps>
{
    /**
     * Provides the data converter that is under use.
     */
    @NotNull
    protected final DynamicOps<TOps> data_converter;

    /**
     * Initializes a new instance of the {@link EncoderPipeline} class.
     * @param data_converter The data converter to use.
     * @throws ArgumentNullException {@code data_converter} is {@code null}.
     */
    public EncoderPipeline(DynamicOps<TOps> data_converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(data_converter, "data_converter");
        this.data_converter = data_converter;
    }

    private record EncodeData<T, D>(EncoderPipeline<T> pipeline, D value, Encoder<D> encoder, OpenFileForWriteFunction stream_out)
            implements Action0
    {
        @Override
        public void action()
        {
            try (var stream_instance = stream_out.Open()) {
                if (stream_instance == null) {
                    throw new SerializationException("The stream instance returned by the provided function is null!!");
                } else {
                    pipeline.Encode(value, encoder, stream_instance);
                }
            } catch (IOException e) {
                throw new AsyncIOErrorException(e);
            }
        }
    }

    private record FileWriteFunction(Path path)
        implements OpenFileForWriteFunction
    {
        @Override
        public OutputStream Open() throws IOException { return PathDefaultOpenFunction(path); }
    }

    private static OutputStream PathDefaultOpenFunction(Path path)
            throws IOException
    {
        return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Encodes the specified converted object to the specified {@code stream}.
     * @param value The converted object that can be saved to {@code stream}.
     * @param stream The stream to save the converted object to.
     * @throws IOException If any I/O errors aroused during writing.
     * @throws SerializationException If any invalid serialization patterns occur during writing to the stream.
     */
    protected abstract void EncodeToStream(@AllowNull TOps value, @DisallowNull OutputStream stream) throws IOException, SerializationException;

    /**
     * Encodes the specified object value, with the specified encoder, to the specified stream.
     * @param value The object value to encode.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param stream_out The {@link OutputStream} to encode the value to.
     * @param <T> The type of the object to encode.
     * @throws IOException An I/O error occurred during writing.
     * @throws SerializationException If any other serialization error occurs.
     * @throws ArgumentNullException {@code encoder} and/or {@code stream_out} are {@code null}.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final <T> void Encode(@AllowNull T value, Encoder<T> encoder, OutputStream stream_out)
        throws IOException, ArgumentNullException, SerializationException
    {
        ArgumentNullException.ThrowIfNull(encoder, "encoder");
        ArgumentNullException.ThrowIfNull(stream_out, "stream_out");
        var data_result = encoder.encodeStart(data_converter, value);
        if (data_result.isError()) {
            throw new ErrorDataResultException(data_result.error().get());
        } else {
            EncodeToStream(data_result.result().get(), stream_out);
        }
    }

    /**
     * Encodes the specified object value, with the specified encoder, to the specified stream.
     * This is the asynchronous variant of the {@link #Encode(Object, Encoder, OutputStream)} method.
     * @param value The value to be encoded.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param executor The {@link Executor} where this encoding task is to be executed to.
     * @param stream_out A function, that upon invoking it, it provides the {@link OutputStream} to encode the value to.
     * @return A {@link CompletableFuture} indicating the status of the encoding process.
     * @param <T> The type of the object to encode.
     * @throws ArgumentNullException {@code encoder} and/or {@code stream_out} and/or {@code executor} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream_out}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<Void> EncodeAsync(@AllowNull T value, Encoder<T> encoder, OpenFileForWriteFunction stream_out, Executor executor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(encoder, "encoder");
        ArgumentNullException.ThrowIfNull(executor, "executor");
        ArgumentNullException.ThrowIfNull(stream_out, "stream_out");
        return CompletableFuture.runAsync(new EncodeData<>(this, value, encoder, stream_out), executor);
    }

    /**
     * Encodes the specified object value, with the specified encoder, to the specified stream.
     * This is the asynchronous variant of the {@link #Encode(Object, Encoder, OutputStream)} method.
     * @param value The value to be encoded.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param stream_out A function, that upon invoking it, it provides the {@link OutputStream} to encode the value to.
     * @return A {@link CompletableFuture} indicating the status of the encoding process.
     * @param <T> The type of the object to encode.
     * @throws ArgumentNullException {@code encoder} and/or {@code stream_out} and/or {@code executor} are {@code null}.
     * @apiNote Note that the stream returned via the function reference provided in the {@code stream_out}
     * parameter is auto-disposed once the encoding completes or fails. <br /> <br />
     *
     * The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<Void> EncodeAsync(@AllowNull T value, Encoder<T> encoder, OpenFileForWriteFunction stream_out)
            throws ArgumentNullException
    {
        return EncodeAsync(value, encoder, stream_out, ForkJoinPool.commonPool());
    }

    /**
     * Encodes the specified object value, with the specified encoder, to the specified file.
     * @param value The object value to encode.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param file The {@link Path} where the serialization result is to be stored to.
     * @param <T> The type of the object to encode.
     * @throws IOException An I/O error occurred during writing.
     * @throws SerializationException If any other serialization error occurs.
     * @throws ArgumentNullException {@code encoder} and/or {@code file} are {@code null}.
     */
    public final <T> void EncodeToFile(@AllowNull T value, Encoder<T> encoder, Path file)
            throws IOException, ArgumentNullException, SerializationException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        ArgumentNullException.ThrowIfNull(encoder, "encoder");
        try (OutputStream os = PathDefaultOpenFunction(file)) { Encode(value, encoder, os); }
    }

    /**
     * Encodes the specified object value, with the specified encoder, to the specified file. <br />
     * This is the asynchronous variant of the {@link #EncodeToFile(Object, Encoder, Path)} method.
     * @param value The object value to encode.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param file The {@link Path} where the serialization result is to be stored to.
     * @param executor The {@link Executor} where this encoding task is to be executed to.
     * @param <T> The type of the object to encode.
     * @throws ArgumentNullException {@code encoder} and/or {@code file} and/or {@code executor} are {@code null}.
     * @apiNote The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<Void> EncodeToFileAsync(@AllowNull T value, Encoder<T> encoder, Path file, Executor executor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        return EncodeAsync(value, encoder, new FileWriteFunction(file), executor);
    }

    /**
     * Encodes the specified object value, with the specified encoder, to the specified file. <br />
     * This is the asynchronous variant of the {@link #EncodeToFile(Object, Encoder, Path)} method.
     * @param value The object value to encode.
     * @param encoder The {@link Encoder} able to encode the specified object.
     * @param file The {@link Path} where the serialization result is to be stored to.
     * @param <T> The type of the object to encode.
     * @throws ArgumentNullException {@code encoder} and/or {@code file} are {@code null}.
     * @apiNote The serialization exceptions, if any, are reported through the returned {@link CompletableFuture}. <br />
     * The case where this method can throw is on argument validation, and only {@code null} validation is required,
     * so only {@link ArgumentNullException} is directly thrown, if any argument has an invalid value.
     */
    @NotNull
    public final <T> CompletableFuture<Void> EncodeToFileAsync(@AllowNull T value, Encoder<T> encoder, Path file)
    {
        return EncodeToFileAsync(value, encoder, file, ForkJoinPool.commonPool());
    }
}
