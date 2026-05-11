package com.github.mdcdi1315.basemodslib.utils.function;

record XorBiPredicateImpl<T1, T2>(BiPredicate<T1, T2> predicate, java.util.function.BiPredicate<? super T1, ? super T2> other)
    implements BiPredicate<T1, T2>
{
    @Override
    public boolean test(T1 input_1, T2 input_2) { return predicate.predicate(input_1, input_2) ^ other.test(input_1, input_2); }

    @Override
    public boolean predicate(T1 input_1, T2 input_2) { return predicate.predicate(input_1, input_2) ^ other.test(input_1, input_2); }
}
