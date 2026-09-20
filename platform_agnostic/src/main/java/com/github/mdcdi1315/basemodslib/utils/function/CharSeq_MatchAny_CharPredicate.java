package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;

public record CharSeq_MatchAny_CharPredicate(CharPredicate predicate)
    implements Predicate<CharSequence>
{
    @Override
    public boolean predicate(CharSequence obj)
    {
        int len = obj.length();
        if (len > 0)
        {
            for (int I = 0; I < len; I++)
            {
                if (predicate.predicate(obj.charAt(I)))
                    return true;
            }
        }
        return false;
    }
}
