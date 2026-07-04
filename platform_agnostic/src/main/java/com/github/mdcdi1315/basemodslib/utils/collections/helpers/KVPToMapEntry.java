package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ValueType;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;

import java.util.Map;

public record KVPToMapEntry<TK, TV>(KeyValuePair<TK, TV> pair)
    implements Map.Entry<TK, TV>
{
    public KVPToMapEntry
    {
        ValueType.ValidateNonNullStructure(pair);
    }

    @Override
    public TK getKey() { return pair.getKey(); }

    @Override
    public TV getValue() { return pair.getValue(); }

    @Override
    public TV setValue(TV value) { throw new UnsupportedOperationException("Not allowed to modify the key-value pair."); }
}
