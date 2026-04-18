package com.github.mdcdi1315.basemodslib.utils.function;

class ByteAlwaysTruePredicate
    extends AlwaysTruePredicate<Byte>
    implements BytePredicate
{
    @Override
    public boolean predicate(byte value) { return true; }

    @Override
    public ByteAlwaysFalsePredicate AsAlwaysFalse() { return new ByteAlwaysFalsePredicate(); }
}
