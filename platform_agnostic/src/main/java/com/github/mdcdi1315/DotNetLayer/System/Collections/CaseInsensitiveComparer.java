package com.github.mdcdi1315.DotNetLayer.System.Collections;

public class CaseInsensitiveComparer
    implements IComparer
{
    public CaseInsensitiveComparer() {}

    @Override
    public int Compare(Object x, Object y)
    {
        if (x instanceof String sa && y instanceof String sb) {
            return String.CASE_INSENSITIVE_ORDER.compare(sa, sb);
        } else {
            return Comparer.Default.Compare(x, y);
        }
    }
}
