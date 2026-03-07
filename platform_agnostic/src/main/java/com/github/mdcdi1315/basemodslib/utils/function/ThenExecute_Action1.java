package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

record ThenExecute_Action1<T1>(Action1<T1> a_1, Action1<T1> a_2)
    implements Action1<T1>
{
    @Override
    public void action(T1 obj)
    {
        a_1.action(obj);
        a_2.action(obj);
    }
}
