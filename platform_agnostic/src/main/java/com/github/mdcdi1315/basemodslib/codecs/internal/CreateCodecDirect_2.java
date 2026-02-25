package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.BiFunction;

public record CreateCodecDirect_2<TC, T1, T2>(
        App<RecordCodecBuilder.Mu<TC> , T1> cfd_1,
        App<RecordCodecBuilder.Mu<TC> , T2> cfd_2,
        BiFunction<T1, T2, TC> create_function
)
        implements Func2<RecordCodecBuilder.Instance<TC>, App<RecordCodecBuilder.Mu<TC>, TC>>
{
    @Override
    public App<RecordCodecBuilder.Mu<TC>, TC> function(RecordCodecBuilder.Instance<TC> input) { return input.group(cfd_1, cfd_2).apply(input, create_function); }
}
