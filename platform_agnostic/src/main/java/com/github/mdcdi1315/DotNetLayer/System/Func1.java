package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Func")
public interface Func1<@DotNetByRefParameter(ByRefParameterType.OUT) TResult>
{
    TResult function();
}
