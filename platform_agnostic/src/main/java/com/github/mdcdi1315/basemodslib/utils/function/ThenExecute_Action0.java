package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action0;

record ThenExecute_Action0(Action0 first, Action0 second)
    implements Action0
{
    @Override
    public void action()
    {
        first.action();
        second.action();
    }
}
