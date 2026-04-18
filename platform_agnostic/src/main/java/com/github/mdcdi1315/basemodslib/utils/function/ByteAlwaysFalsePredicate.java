package com.github.mdcdi1315.basemodslib.utils.function;

class ByteAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Byte>
    implements BytePredicate
{
    @Override
    public boolean predicate(byte value) { return false; }

    @Override
    public ByteAlwaysTruePredicate AsAlwaysTrue() { return new ByteAlwaysTruePredicate(); }
}
