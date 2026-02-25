package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CreateCodecDirect_6<TC, T1, T2, T3, T4, T5, T6>(
        App<RecordCodecBuilder.Mu<TC> , T1> cfd_1,
        App<RecordCodecBuilder.Mu<TC> , T2> cfd_2,
        App<RecordCodecBuilder.Mu<TC> , T3> cfd_3,
        App<RecordCodecBuilder.Mu<TC> , T4> cfd_4,
        App<RecordCodecBuilder.Mu<TC> , T5> cfd_5,
        App<RecordCodecBuilder.Mu<TC> , T6> cfd_6,
        Function6<T1, T2, T3, T4, T5, T6, TC> create_function
)
        implements Func2<RecordCodecBuilder.Instance<TC>, App<RecordCodecBuilder.Mu<TC>, TC>>
{
    @Override
    public App<RecordCodecBuilder.Mu<TC>, TC> function(RecordCodecBuilder.Instance<TC> input) {
        return input.group(cfd_1, cfd_2, cfd_3, cfd_4, cfd_5, cfd_6).apply(input, create_function);
    }
}
