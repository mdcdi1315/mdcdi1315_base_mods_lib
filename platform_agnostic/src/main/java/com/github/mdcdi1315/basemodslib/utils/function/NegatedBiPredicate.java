package com.github.mdcdi1315.basemodslib.utils.function;

record NegatedBiPredicate<T1, T2>(BiPredicate<T1, T2> predicate)
        implements BiPredicate<T1, T2>
{
    @Override
    public boolean test(T1 input_1, T2 input_2) { return !predicate.test(input_1, input_2); }

    @Override
    public boolean predicate(T1 input_1, T2 input_2) { return !predicate.test(input_1, input_2); }

    @Override
    public BiPredicate<T1, T2> negate() { return predicate; }
}
