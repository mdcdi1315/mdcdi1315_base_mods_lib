package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

public final class MappingEnumerator<T, TR>
    extends BaseEnumerator<TR>
{
    private TR cached_current;
    private Func2<T , TR> mapper;
    private IEnumerator<T> enumerator;
    private T cached_pretransformed_current;

    public MappingEnumerator(IEnumerator<T> enumerator_to_wrap, Func2<T, TR> mapping_function)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper = mapping_function, "mapping_function");
        ArgumentNullException.ThrowIfNull(enumerator = enumerator_to_wrap, "enumerator_to_wrap");
        cached_pretransformed_current = null;
        cached_current = null;
    }

    public MappingEnumerator(IEnumerator<T> enumerator_to_wrap, Converter<T, TR> mapping_function)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapping_function, "mapping_function");
        ArgumentNullException.ThrowIfNull(enumerator = enumerator_to_wrap, "enumerator_to_wrap");
        mapper = FunctionManipulations.AsFunc2(mapping_function);
        cached_pretransformed_current = null;
        cached_current = null;
    }

    @Override
    public TR getCurrent() { return cached_current; }

    public T GetPretransformedCurrent() { return cached_pretransformed_current; }

    @Override
    protected void ResetImpl() throws InvalidOperationException { enumerator.Reset(); }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        boolean success = enumerator.MoveNext();
        if (success) {
            cached_pretransformed_current = enumerator.getCurrent();
            cached_current = mapper.function(cached_pretransformed_current);
        }
        return success;
    }

    @Override
    public void Dispose()
    {
        synchronized (this)
        {
            try {
                super.Dispose();
                if (enumerator != null) { enumerator.Dispose(); }
            } finally {
                mapper = null;
                enumerator = null;
                cached_current = null;
                cached_pretransformed_current = null;
            }
        }
    }
}
