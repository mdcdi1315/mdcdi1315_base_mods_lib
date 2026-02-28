package com.github.mdcdi1315.basemodslib.codecs.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func3;

import com.github.mdcdi1315.basemodslib.codecs.DataResultBuilder;

import com.mojang.serialization.DataResult;

public final class DataResultBuilderOfTwo<T1, T2, TR>
        extends DataResultBuilder<TR>
{
    private final DataResult<T1> v1;
    private final DataResult<T2> v2;
    private final Func3<T1, T2, TR> creator;

    public DataResultBuilderOfTwo(DataResult<T1> v1, DataResult<T2> v2, Func3<T1, T2, TR> creator)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.creator = creator;
    }

    @Override
    public DataResult<TR> Build()
    {
        return v1.isSuccess() ? (
                v2.isSuccess() ?
                        DataResult.success(creator.function(v1.result().get(), v2.result().get())) :
                        DataResult.error(v2.error().get().messageSupplier())
        ) : DataResult.error(v1.error().get().messageSupplier());
    }
}