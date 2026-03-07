package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action2;

record ThenExecute_Action2<T1, T2>(Action2<T1, T2> a1, Action2<T1, T2> a2)
    implements Action2<T1, T2>
{
    @Override
    public void action(T1 obj1, T2 obj2)
    {
        a1.action(obj1, obj2);
        a2.action(obj1, obj2);
    }
}
