package com.github.mdcdi1315.basemodslib.utils.function;

class CharAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Character>
    implements CharPredicate
{
    @Override
    public boolean predicate(char value) { return false; }

    @Override
    public CharAlwaysTruePredicate AsAlwaysTrue() { return new CharAlwaysTruePredicate(); }
}
