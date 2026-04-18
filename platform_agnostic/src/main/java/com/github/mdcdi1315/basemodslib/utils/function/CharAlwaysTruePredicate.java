package com.github.mdcdi1315.basemodslib.utils.function;

class CharAlwaysTruePredicate
    extends AlwaysTruePredicate<Character>
    implements CharPredicate
{
    @Override
    public boolean predicate(char value) { return true; }

    @Override
    public CharAlwaysFalsePredicate AsAlwaysFalse() { return new CharAlwaysFalsePredicate(); }
}
