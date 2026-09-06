package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

/**
 * Provides the mod instance collection specialized for server mod instances.
 */
public final class ServerModInstanceCollection
    extends ModInstanceCollection<IServerModInstance>
{
    public ServerModInstanceCollection() { super(); }

    @Override
    public void Freeze()
    {
        var cclass = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass();
        if (cclass == BaseModsLib.class) {
            super.Freeze();
        } else {
            throw new InvalidOperationException("Not possible to freeze the collection outside the BML class.");
        }
    }
}
