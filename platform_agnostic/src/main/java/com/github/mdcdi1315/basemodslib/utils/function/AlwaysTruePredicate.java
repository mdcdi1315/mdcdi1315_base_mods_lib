package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

class AlwaysTruePredicate<T>
    implements Predicate<T>, IAlwaysTruePredicate<T>
{
    @Override
    public boolean test(T obj) { return true; }

    @Override
    public boolean predicate(T obj) { return true; }

    @NotNull
    @Override // Relay to AsAlwaysFalse to avoid multiple overrides. We will have the uniformity guarantees anyway.
    public final AlwaysFalsePredicate<T> negate() { return AsAlwaysFalse(); }

    @NotNull
    @Override // It is better to delegate to AlwaysFalsePredicate since we always return true regardlessly of the input.
    public AlwaysFalsePredicate<T> AsAlwaysFalse() { return new AlwaysFalsePredicate<>(); }

    @NotNull
    @Override // Or-ing an always true predicate always yields true, regardlessly of the value of the input parameter. Worth to override.
    public final AlwaysTruePredicate<T> or(@NotNull java.util.function.Predicate<? super T> other) { return this; }

    @NotNull
    @Override // And-ing an always true predicate depends on the value of the input parameter. Worth to override.
    public final java.util.function.Predicate<T> and(@NotNull java.util.function.Predicate<? super T> other) { return (java.util.function.Predicate<T>) other; }
}
