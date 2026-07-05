package com.github.mdcdi1315.basemodslib.eventapi.mods;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent;
import com.github.mdcdi1315.basemodslib.eventapi.PermitsRecursiveFiring;

/**
 * Fired when the mod loading process has been completed successfully. <br />
 * Additional finish-up tasks may run during this event.
 */
@PermitsRecursiveFiring
public record ModLoadingCompleteEvent() implements IDestroyableEvent { }
