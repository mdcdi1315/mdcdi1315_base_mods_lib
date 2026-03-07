package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

record AlwaysFalsePredicate<T>()
    implements Predicate<T>
{
    @Override
    public boolean test(T t) { return false; }

    @Override
    public boolean predicate(T obj) { return false; }

    @NotNull
    @Override // It is better to delegate to AlwaysTruePredicate since we always return false regardlessly of the input.
    public Predicate<T> negate() { return new AlwaysTruePredicate<>(); }

    @NotNull
    @Override // And-ing an always false predicate always yields false, so it is meaningless to test the input predicate. Worth to override.
    public Predicate<T> and(@NotNull java.util.function.Predicate<? super T> other) { return new AlwaysFalsePredicate<>(); }

    @NotNull
    @Override // Or-ing an always false predicate depends on the value of the input parameter. Worth to override.
    public java.util.function.Predicate<T> or(@NotNull java.util.function.Predicate<? super T> other) { return (java.util.function.Predicate<T>) other; }
}
