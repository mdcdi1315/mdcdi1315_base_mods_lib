package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;

@SuppressWarnings("NullableProblems")
record CharSeq_TrueForAll_CharPredicate(CharPredicate char_predicate)
    implements Predicate<CharSequence>
{
    @Override
    public boolean predicate(CharSequence obj)
    {
        int len = obj.length();
        for (int I = 0; I < len; I++)
        {
            if (!char_predicate.predicate(obj.charAt(I)))
                return false;
        }
        return true;
    }

    @Override
    public boolean test(CharSequence charSequence) { return predicate(charSequence); }

    @Override
    public Predicate<CharSequence> negate() { return new NegatedPredicate<>(this); }

    @Override
    public Predicate<CharSequence> or(java.util.function.Predicate<? super CharSequence> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    public Predicate<CharSequence> and(java.util.function.Predicate<? super CharSequence> other) { return new CompatibleAndPredicateImpl<>(this, other); }
}
