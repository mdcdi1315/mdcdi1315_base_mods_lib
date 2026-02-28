package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func4;

import com.github.mdcdi1315.basemodslib.codecs.DataResultBuilder;

import com.mojang.serialization.DataResult;

public final class DataResultBuilderOfThree<T1, T2, T3, TR>
    extends DataResultBuilder<TR>
{
    private final DataResult<T1> first;
    private final DataResult<T2> second;
    private final DataResult<T3> third;
    private final Func4<T1, T2, T3, TR> creator;

    public DataResultBuilderOfThree(
            DataResult<T1> first,
            DataResult<T2> second,
            DataResult<T3> third,
            Func4<T1, T2, T3, TR> creator
    )
    {
        this.first = first;
        this.third = third;
        this.second = second;
        this.creator = creator;
    }

    @Override
    public DataResult<TR> Build() {
        return first.isSuccess() ? (
                second.isSuccess() ? (
                        third.isSuccess() ?
                                DataResult.success(creator.function(first.result().get(), second.result().get(), third.result().get())) :
                                DataResult.error(third.error().get().messageSupplier())
                ) : DataResult.error(second.error().get().messageSupplier())
        ) : DataResult.error(first.error().get().messageSupplier());
    }
}
