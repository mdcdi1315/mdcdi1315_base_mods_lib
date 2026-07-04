package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Supports iterating over a {@link String} object and reading its individual characters. This class cannot be inherited.
 */
public final class CharEnumerator
    implements IEnumerator<Character>, ICloneable
{
    private int _index;
    private String _string;

    CharEnumerator(String theString)
    {
        _index = -1;
        _string = theString;
    }

    private CharEnumerator(String theString, int theIndex)
    {
        _index = theIndex;
        _string = theString;
    }

    @Override
    public Character getCurrent() { return _string.charAt(_index); }

    @Override
    public boolean MoveNext()
            throws InvalidOperationException
    {
        int index = _index + 1;
        int length = _string.length();

        if (index < length) {
            _index = index;
            return true;
        } else {
            _index = length;
            return false;
        }
    }

    @Override
    public void Reset() throws InvalidOperationException { _index = -1; }

    @Override
    public void Dispose() { _string = null; }

    @Override
    public Object Clone() { return new CharEnumerator(_string, _index); }
}
