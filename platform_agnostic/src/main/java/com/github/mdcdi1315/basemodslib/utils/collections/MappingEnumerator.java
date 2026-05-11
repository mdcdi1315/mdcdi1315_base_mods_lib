package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

/**
 * Provides an {@link IEnumerator} implementation that can map elements of the input enumerator, by a mapping function, to the specified object type.
 * @param <T> The type of the elements that the base enumerator provides.
 * @param <TR> The type of the elements that will be returned through the {@link MappingEnumerator#getCurrent()} method.
 * @since 1.0.26
 */
public final class MappingEnumerator<T, TR>
    extends BaseEnumerator<TR>
{
    private TR cached_current;
    private Func2<T , TR> mapper;
    private IEnumerator<T> enumerator;
    private T cached_pretransformed_current;

    /**
     * Initializes a new instance of the {@link MappingEnumerator} from the specified {@link IEnumerator} to be wrapped, and the {@link Func2} providing the way to map elements of type {@link T} to {@link TR}.
     * @param enumerator_to_wrap The source {@link IEnumerator} to wrap.
     * @param mapping_function The function that maps elements of type {@link T} to type {@link TR}.
     * @throws ArgumentNullException {@code enumerator_to_wrap} and/or {@code mapping_function} are {@code null}.
     */
    public MappingEnumerator(IEnumerator<T> enumerator_to_wrap, Func2<T, TR> mapping_function)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper = mapping_function, "mapping_function");
        ArgumentNullException.ThrowIfNull(enumerator = enumerator_to_wrap, "enumerator_to_wrap");
        cached_pretransformed_current = null;
        cached_current = null;
    }

    /**
     * Initializes a new instance of the {@link MappingEnumerator} from the specified {@link IEnumerator} to be wrapped, and the {@link Converter} providing the way to map elements of type {@link T} to {@link TR}.
     * @param enumerator_to_wrap The source {@link IEnumerator} to wrap.
     * @param mapping_function The function that maps elements of type {@link T} to type {@link TR}.
     * @throws ArgumentNullException {@code enumerator_to_wrap} and/or {@code mapping_function} are {@code null}.
     */
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

    /**
     * Gets the value of type {@link T} before it was transformed with the previously provided mapping function.
     * @return The value of type {@link T} that is the pre-transformation value of the value returned by the {@link #getCurrent()} method.
     */
    public T GetPretransformedCurrent() { return cached_pretransformed_current; }

    /**
     * Gets a {@link Func2} object that is a function that can map elements of type {@link T} to type {@link TR}.
     * @return The mapping function declared in either of the class constructors during construction.
     * @since 1.0.31
     */
    @NotNull
    public Func2<T, TR> GetMappingFunction() { return mapper; }

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
