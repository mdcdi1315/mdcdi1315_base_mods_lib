package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function13;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CreateCodecDirect_13<TC, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(
        App<RecordCodecBuilder.Mu<TC> , T1> cfd_1,
        App<RecordCodecBuilder.Mu<TC> , T2> cfd_2,
        App<RecordCodecBuilder.Mu<TC> , T3> cfd_3,
        App<RecordCodecBuilder.Mu<TC> , T4> cfd_4,
        App<RecordCodecBuilder.Mu<TC> , T5> cfd_5,
        App<RecordCodecBuilder.Mu<TC> , T6> cfd_6,
        App<RecordCodecBuilder.Mu<TC> , T7> cfd_7,
        App<RecordCodecBuilder.Mu<TC> , T8> cfd_8,
        App<RecordCodecBuilder.Mu<TC> , T9> cfd_9,
        App<RecordCodecBuilder.Mu<TC> , T10> cfd_10,
        App<RecordCodecBuilder.Mu<TC> , T11> cfd_11,
        App<RecordCodecBuilder.Mu<TC> , T12> cfd_12,
        App<RecordCodecBuilder.Mu<TC> , T13> cfd_13,
        Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, TC> create_function
)
        implements Func2<RecordCodecBuilder.Instance<TC>, App<RecordCodecBuilder.Mu<TC>, TC>>
{
    @Override
    public App<RecordCodecBuilder.Mu<TC>, TC> function(RecordCodecBuilder.Instance<TC> input)
    {
        return input.group(
                cfd_1, cfd_2, cfd_3, cfd_4, cfd_5,
                cfd_6, cfd_7, cfd_8, cfd_9, cfd_10,
                cfd_11, cfd_12, cfd_13
        ).apply(
                input,
                create_function
        );
    }
}
