package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action0;

record TranslateRunnableToAction0(Runnable a)
    implements Action0
{
    @Override
    public void run() { a.run(); }

    @Override
    public void action() { a.run(); }
}
