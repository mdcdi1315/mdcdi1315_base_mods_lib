package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

record AlwaysFalseBiPredicate<T1, T2>()
    implements BiPredicate<T1, T2>
{
    @Override
    public boolean test(T1 input_1, T2 input_2) { return false; }

    @Override
    public boolean predicate(T1 input_1, T2 input_2) { return false; }

    @Override
    public BiPredicate<T1, T2> negate() { return new AlwaysTrueBiPredicate<>(); }

    @Override
    public BiPredicate<T1, T2> or(java.util.function.BiPredicate<? super T1, ? super T2> other)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(other, "other");
        return FunctionManipulations.AsBiPredicate((java.util.function.BiPredicate<T1, T2>)other);
    }

    @Override
    public BiPredicate<T1, T2> and(java.util.function.BiPredicate<? super T1, ? super T2> other) throws ArgumentNullException { return this; }

    @Override
    public BiPredicate<T1, T2> Xor(java.util.function.BiPredicate<? super T1, ? super T2> other)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(other, "other");
        return FunctionManipulations.AsBiPredicate((java.util.function.BiPredicate<T1, T2>)other);
    }
}
