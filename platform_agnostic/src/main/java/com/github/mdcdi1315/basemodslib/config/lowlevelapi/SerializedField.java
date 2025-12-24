package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.serialization.Codec;

public final class SerializedField
{
    private static Codec<SerializedField> codec;

    private final String name, comment;
    private final Object value;

    public static Codec<SerializedField> GetCodec() {
        if (codec == null)
        {
            codec = CodecUtils.CreateCodecDirect(
                    Codec.STRING.fieldOf("name").forGetter(SerializedField::GetName),
                    ConfigValueCodec.INSTANCE.fieldOf("value").forGetter(SerializedField::GetValue),
                    Codec.STRING.optionalFieldOf("comment" , StringUtils.Empty).forGetter(SerializedField::GetComment),
                    SerializedField::new
            );
        }
        return codec;
    }

    public SerializedField(String name, @MaybeNull Object value, @MaybeNull String comment) {
        ArgumentNullException.ThrowIfNull(name, "name");
        this.name = name;
        this.comment = comment;
        this.value = value;
    }

    public SerializedField(String name, @MaybeNull Object value) {
        this(name, value, null);
    }

    @NotNull
    public String GetName() {
        return name;
    }

    @MaybeNull
    public String GetComment() {
        return comment;
    }

    @MaybeNull
    public Object GetValue() {
        return value;
    }
}
