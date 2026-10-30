package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

public class StringEnumerator
    implements IEnumerator
{
    private final IEnumerator _baseEnumerator;

    StringEnumerator(IEnumerator mappings) { _baseEnumerator = mappings; }

    @Override
    public void Reset() { _baseEnumerator.Reset(); }

    @Override
    public boolean MoveNext() { return _baseEnumerator.MoveNext(); }

    @MaybeNull
    public String getCurrent() { return (String) _baseEnumerator.getCurrent(); }
}