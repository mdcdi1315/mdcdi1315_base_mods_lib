package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Predicate;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

record MapOrDefaultValue<TS, TR>(Predicate<TS> predicate, Func2<TS, TR> mapper, Func1<TR> default_value)
    implements Func2<TS, TR>
{
    public MapOrDefaultValue(Predicate<TS> predicate, Func2<TS, TR> mapper, TR default_value) { this(predicate, mapper, new ElementSupplier<>(default_value)); }

    @Override
    public TR function(TS input) {
        return predicate.predicate(input) ? mapper.function(input) : default_value().function();
    }

    @Override
    public TR apply(TS input) {
        return predicate.predicate(input) ? mapper.function(input) : default_value().function();
    }
}
