package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;

/**
 * Provides the mod instance collection specialized for client mod instances.
 */
@ClientOnlyEnvironment
public final class ClientModInstanceCollection
    extends ModInstanceCollection<IClientModInstance>
{
    public ClientModInstanceCollection() { super(); }

    @Override
    public void Freeze()
    {
        var cclass = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass();
        if (cclass == BaseModsLibClient.class) {
            super.Freeze();
        } else {
            throw new InvalidOperationException("Not possible to freeze the collection outside the BML class.");
        }
    }
}
