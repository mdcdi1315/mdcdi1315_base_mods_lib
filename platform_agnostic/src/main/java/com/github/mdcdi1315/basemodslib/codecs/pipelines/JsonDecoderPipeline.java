package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.google.gson.*;

import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Provides a JSON {@link DecoderPipeline}.
 */
public final class JsonDecoderPipeline
    extends DecoderPipeline<JsonElement>
{
    private final Gson gson;
    private final Charset charset;

    /**
     * Initializes a new instance of the {@link JsonDecoderPipeline} class.
     */
    public JsonDecoderPipeline() { this(null, null, false); }

    /**
     * Initializes a new instance of the {@link JsonDecoderPipeline} class.
     * @param gson A {@link Gson} instance to configure all the JSON parsers to be used for this pipeline. <br />
     *             Can be {@code null}, which uses sensible defaults.
     * @param charset The character encoding under which the files are stored as. <br />
     *                Can be {@code null}, which specifies the {@link StandardCharsets#UTF_8} encoding.
     * @param use_compressed_data_converter Whether to use {@link JsonOps#COMPRESSED} or {@link JsonOps#INSTANCE}.
     */
    public JsonDecoderPipeline(@AllowNull Gson gson, @AllowNull Charset charset, boolean use_compressed_data_converter)
    {
        super(use_compressed_data_converter ? JsonOps.COMPRESSED : JsonOps.INSTANCE);
        this.gson = (gson == null) ? ConfigureDefault() : gson;
        this.charset = (charset == null) ? StandardCharsets.UTF_8 : charset;
    }

    @NotNull
    private static Gson ConfigureDefault()
    {
        return new GsonBuilder()
                .setStrictness(Strictness.STRICT)
                .setFormattingStyle(FormattingStyle.PRETTY)
                .create();
    }

    @Override
    protected JsonElement DecodeFromStream(InputStream stream)
            throws IOException, SerializationException
    {
        try (var reader = new InputStreamReader(stream, charset)) {
            return gson.fromJson(reader, JsonElement.class);
        } catch (JsonParseException jpe) {
            throw new JsonException(jpe);
        }
    }
}
