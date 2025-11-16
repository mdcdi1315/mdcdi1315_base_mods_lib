package com.github.mdcdi1315.basemodslib.eventapi.mods;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;

/**
 * Event class invoked when the Minecraft client is setting up.
 * @since 1.0.7
 */
@ClientOnlyEnvironment
public final class ClientSetupEvent extends AbstractSetupEvent {}
