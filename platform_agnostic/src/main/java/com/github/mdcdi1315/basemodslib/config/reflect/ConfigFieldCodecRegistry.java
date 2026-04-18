package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.codecs.EnumCodecCompareIgnoreCase;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the configuration field codec registry,
 * a registry that provides mappings from field class instances to {@link Codec} classes.
 *
 * <h3>Using custom class configuration fields</h3>
 * The new system has abstracted now the details of getting and decoding the field data -
 * as such, only registering here a capable {@link Codec} that can handle your input class is enough,
 * for example let's assume this:
 * <pre>{@code
 * public record ARecord(int value) {
 *     public static Codec<ARecord> CODEC = CodecUtils.CreateRecordCodecDirect(
 *          Codec.INT.fieldOf("value").forGetter((ARecord rc) -> rc.value),
 *          ARecord::new
 *     );
 * }
 * }</pre>
 *
 * The above record won't be de/encoded at all if we do not register a mapping for it:
 * <pre>{@code
 * ConfigFieldCodecRegistry.AddCodecMapping(ARecord.class, ARecord.CODEC)
 * }</pre>
 *
 * And now, we can freely de/encode such instances:
 * <pre>{@code
 * AModConfig cfg = ConfigManager.INSTANCE.LoadConfigurationFile(AModConfig.class);
 * }</pre>
 *
 * <h4>Additional notes</h4>
 * -&gt; {@link Enum} instances are implicitly handled by this class by creating a {@link EnumCodecCompareIgnoreCase} codec for it.
 *       While it is perfectly valid to register a codec for a derived {@link Enum} instance, the class won't use it.
 *       However, be noted that this might be subject to change in the future. <br />
 * -&gt; Default mappings for primitives, {@link String} and {@link ResourceLocation} are provided as well. <br />
 * -&gt; Once a new class has registered a configuration field mapping here, it does also implicitly get support for the Constraints subsystem.
 *      See {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints} package for more information.
 */
public final class ConfigFieldCodecRegistry
{
    private ConfigFieldCodecRegistry() {}

    private static final ConcurrentHashMap<Class<?>, Codec<?>> codec_references;

    static {
        codec_references = new ConcurrentHashMap<>(15);

        AddCodecReferenceDirect(Byte.class, Codec.BYTE);
        AddCodecReferenceDirect(byte.class, Codec.BYTE);
        AddCodecReferenceDirect(Short.class, Codec.SHORT);
        AddCodecReferenceDirect(short.class, Codec.SHORT);
        AddCodecReferenceDirect(Integer.class, Codec.INT);
        AddCodecReferenceDirect(int.class, Codec.INT);
        AddCodecReferenceDirect(Long.class, Codec.LONG);
        AddCodecReferenceDirect(long.class, Codec.LONG);
        AddCodecReferenceDirect(Float.class, Codec.FLOAT);
        AddCodecReferenceDirect(float.class, Codec.FLOAT);
        AddCodecReferenceDirect(Boolean.class, Codec.BOOL);
        AddCodecReferenceDirect(boolean.class, Codec.BOOL);
        AddCodecReferenceDirect(Double.class, Codec.DOUBLE);
        AddCodecReferenceDirect(double.class, Codec.DOUBLE);
        AddCodecReferenceDirect(String.class, Codec.STRING);
        AddCodecReferenceDirect(ResourceLocation.class, ResourceLocation.CODEC);
    }

    private static <T> void AddCodecReferenceDirect(Class<T> codec_type, Codec<T> codec) { codec_references.put(codec_type, codec); }

    /**
     * Retrieves a {@link Codec} mapping for the specified {@code config_field_backing_class}.
     * @param config_field_backing_class The {@link Class} of the configuration field to retrieve its codec.
     * @return The {@link Codec} mapping that can de/encode the specified {@code config_field_backing_class}.
     * @param <T> The type that can be de/encoded.
     * @throws ArgumentNullException {@code config_field_backing_class} is {@code null}.
     * @throws InvalidOperationException {@code config_field_backing_class} does not have a registered {@link Codec} mapping.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> Codec<T> GetCodecMapping(Class<T> config_field_backing_class)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(config_field_backing_class, "config_field_backing_class");
        if (ReflectionUtils.ExtendsClass(config_field_backing_class, Enum.class)) {
            return (Codec<T>) ConstructEnumCodec(config_field_backing_class);
        } else {
            Codec<T> codec = (Codec<T>) codec_references.get(config_field_backing_class);
            if (codec == null) {
                throw new InvalidOperationException("Codec mapping not existing for " + config_field_backing_class.getName() + " !");
            } else {
                return codec;
            }
        }
    }

    private static <T extends Enum<T>> Codec<?> ConstructEnumCodec(Class<?> unchecked_class) {
        return new EnumCodecCompareIgnoreCase<>((Class<T>) unchecked_class);
    }

    /**
     * Adds a {@link Codec} mapping for the specified config field {@link Class}. <br />
     * Configuration fields can have any kind of class when using the
     * default de/serialization mechanism - and this allows them to de/encode properly.
     * @param config_field_backing_class The {@link Class} to register a {@link Codec} mapping for.
     * @param codec The {@link Codec} to use whenever a field of type {@code config_field_backing_class} is shown up.
     * @param <T> The actual type under which the {@link Codec} will de/encode to.
     * @throws ArgumentNullException {@code config_field_backing_class} and/or {@code codec} are {@code null}.
     * @throws InvalidOperationException A codec mapping does already exist for {@code config_field_backing_class}.
     */
    public static <T> void AddCodecMapping(Class<T> config_field_backing_class, Codec<T> codec)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(codec, "codec");
        ArgumentNullException.ThrowIfNull(config_field_backing_class, "config_field_backing_class");
        if (codec_references.putIfAbsent(config_field_backing_class, codec) != null) {
            throw new InvalidOperationException("Codec mapping already registered for " + config_field_backing_class.getName() + " !");
        }
    }
}
