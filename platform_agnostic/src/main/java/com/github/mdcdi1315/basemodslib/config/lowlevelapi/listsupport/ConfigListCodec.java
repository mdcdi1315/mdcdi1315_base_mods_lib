package com.github.mdcdi1315.basemodslib.config.lowlevelapi.listsupport;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.ConfigList;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;
import com.github.mdcdi1315.basemodslib.codecs.StrictListCodec;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.List;

public final class ConfigListCodec
    implements Codec<ConfigList>
{
    private final Class<?> decoding_class;

    public ConfigListCodec(Class<?> ccfg)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ccfg, "ccfg");
        decoding_class = ccfg;
    }

    private static <T, TG> Pair<ConfigList, T> TranslateList(Pair<List<TG> , T> p)
    {
        List<TG> list_original = p.getFirst();
        ConfigList result = new ConfigList(list_original.size());
        for (TG element : list_original) { result.Add(element); }
        return Pair.of(result, p.getSecond());
    }

    private static <T, TG> DataResult<Pair<ConfigList, T>> DecodeInternal(DynamicOps<T> ops, T input, Class<TG> element)
    {
        Codec<TG> element_codec = ListElementRegistry.FindCodec(element);
        if (element_codec == null) {
            return DataResult.error(StringSupplier.FromDotNetFormatted("Cannot decode the list of element type {0} because there is no codec registered for it.", element.getName()));
        } else {
            return new StrictListCodec<>(element_codec).decode(ops, input).map(ConfigListCodec::TranslateList);
        }
    }

    private static <T, TG> DataResult<T> EncodeInternal(ConfigList input, DynamicOps<T> ops, T prefix, Class<TG> element)
    {
        Codec<TG> element_codec = ListElementRegistry.FindCodec(element);
        if (element_codec == null) {
            return DataResult.error(StringSupplier.FromDotNetFormatted("Cannot decode the list of element type {0} because there is no codec registered for it.", element.getName()));
        } else {
            return new StrictListCodec<>(element_codec).encode(input.AsImmutableList(element), ops, prefix);
        }
    }

    @Override
    public <T> DataResult<Pair<ConfigList, T>> decode(DynamicOps<T> ops, T input)  {
        return DecodeInternal(ops , input, decoding_class);
    }

    @Override
    public <T> DataResult<T> encode(ConfigList input, DynamicOps<T> ops, T prefix) {
        return EncodeInternal(input, ops, prefix, decoding_class);
    }
}
