/**
 * Provides classes for creating serialization pipelines, that is,
 * objects whose purpose is to manage batched de/serialization tasks.
 *
 * <h3>The pipeline concept</h3>
 * There are many times in Minecraft that you want to store or load many objects
 * serialized through {@link com.mojang.serialization.Codec} instances.
 * However, writing a proper serialization algorithm and handling all the
 * possible errors that may occur is a daunting task and is error-prone,
 * and you will have wasted ten minutes figuring out why your codec
 * translation code does not work. So, this API abstracts all of these
 * details away, and you only deal with known exceptions that you simply
 * handle them in the way you wish to.
 *
 * <h3>The abstractions</h3>
 * This package provides two primary abstractions:
 * An {@link com.github.mdcdi1315.basemodslib.codecs.pipelines.EncoderPipeline}
 * and a {@link com.github.mdcdi1315.basemodslib.codecs.pipelines.DecoderPipeline}
 * that describe how a pipeline is created and how users should use it. <br />
 * An encoder pipeline handles serializing objects to streams; it does that
 * through your own provided {@link com.mojang.serialization.Encoder} instance for the
 * in question object and the stream where the serialization result is to be stored.
 * Respectively, a decoder pipeline handles deserializing objects from streams,
 * you provide to it your {@link com.mojang.serialization.Decoder} instance to decode the
 * stream as and the data stream to decode. <br /> <br />
 * Note that both of these classes provide asynchronous encoding/decoding capabilities,
 * and it is possible to directly encode/decode to a file through {@link java.nio.file.Path} instances.
 *
 * <h3>Common usage of pipelines</h3>
 * A prime example of using a pipeline is {@linkplain net.minecraft.server.packs.resources.PreparableReloadListener preparable resource reload listeners}. <br />
 * While Minecraft has the simple JSON resource reload listener, it might not be enough for what you want to do. <br />
 * Or, you want to customize the process of how serialization is done.
 * E.g. you might want to use the Fast Binary Format instead of JSON,
 * or provide support for multiple types of formats.
 *
 * <h3>Default implementations</h3>
 * The library ships three default implementations of those pipelines,
 * one for JSON through the {@linkplain com.google.gson GSON} library,
 * one for NBT utilizing the Minecraft's implementation,
 * and one for the Fast Binary Format, which is what BML provides since 1.0.25.
 *
 * @since 1.0.42-beta9
 */
@MinecraftIndependentFeature
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.42-beta9")
package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.utils.annotations.MinecraftIndependentFeature;
