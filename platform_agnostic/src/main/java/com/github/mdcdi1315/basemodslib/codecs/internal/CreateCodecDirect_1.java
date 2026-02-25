package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record CreateCodecDirect_1<TC, T>(
        App<RecordCodecBuilder.Mu<TC> , T> cfd_1,
        Function<T, TC> create_function
)
    implements Func2<RecordCodecBuilder.Instance<TC>, App<RecordCodecBuilder.Mu<TC>, TC>>
{
    @Override
    public App<RecordCodecBuilder.Mu<TC>, TC> function(RecordCodecBuilder.Instance<TC> input) { return input.group(cfd_1).apply(input, create_function); }
}
