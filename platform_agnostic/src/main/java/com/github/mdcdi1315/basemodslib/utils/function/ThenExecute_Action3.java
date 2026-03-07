package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action3;

record ThenExecute_Action3<T1, T2, T3>(Action3<T1, T2, T3> a1, Action3<T1, T2, T3> a2)
    implements Action3<T1, T2, T3>
{
    @Override
    public void action(T1 obj1, T2 obj2, T3 obj3)
    {
        a1.action(obj1, obj2, obj3);
        a2.action(obj1, obj2, obj3);
    }
}
