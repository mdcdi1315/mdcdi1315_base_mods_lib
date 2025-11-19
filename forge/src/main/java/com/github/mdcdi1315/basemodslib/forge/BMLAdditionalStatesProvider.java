package com.github.mdcdi1315.basemodslib.forge;

import net.minecraftforge.fml.*;

import java.util.List;

public final class BMLAdditionalStatesProvider
    implements IModStateProvider
{
    @Override
    public List<IModLoadingState> getAllStates() { return List.of(new DispatchFinalizeRegistriesEventLoadingState()); }
}
