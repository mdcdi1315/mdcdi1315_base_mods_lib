package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func6;

import com.github.mdcdi1315.basemodslib.codecs.DataResultBuilder;

import com.mojang.serialization.DataResult;

public final class DataResultBuilderOfFive<T1, T2, T3, T4, T5, TR>
    extends DataResultBuilder<TR>
{
    private final DataResult<T1> first;
    private final DataResult<T2> second;
    private final DataResult<T3> third;
    private final DataResult<T4> fourth;
    private final DataResult<T5> fifth;
    private final Func6<T1, T2, T3, T4, T5, TR> creator;

    public DataResultBuilderOfFive(
            DataResult<T1> first,
            DataResult<T2> second,
            DataResult<T3> third,
            DataResult<T4> fourth,
            DataResult<T5> fifth,
            Func6<T1, T2, T3, T4, T5, TR> creator
    )
    {
        this.first = first;
        this.third = third;
        this.fifth = fifth;
        this.second = second;
        this.fourth = fourth;
        this.creator = creator;
    }

    @Override
    public DataResult<TR> Build()
    {
        return first.isSuccess() ? (
                second.isSuccess() ? (
                        third.isSuccess() ? (
                                fourth.isSuccess() ? (
                                        fifth.isSuccess() ?
                                                DataResult.success(creator.function(first.result().get(), second.result().get(), third.result().get(), fourth.result().get(), fifth.result().get())) :
                                                DataResult.error(fifth.error().get().messageSupplier())
                                ) : DataResult.error(fourth.error().get().messageSupplier())
                        ) : DataResult.error(third.error().get().messageSupplier())
                ) : DataResult.error(second.error().get().messageSupplier())
        ) : DataResult.error(first.error().get().messageSupplier());
    }
}
