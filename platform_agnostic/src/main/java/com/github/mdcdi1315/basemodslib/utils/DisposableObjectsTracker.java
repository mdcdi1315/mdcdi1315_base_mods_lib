package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

/**
 * Provides a class for managing {@link IDisposable} objects, and destroying them all in one call. <br />
 * Currently used by the mod loader layers, but can be used by your own code as well.
 * @since 1.0.3
 */
public final class DisposableObjectsTracker
    implements IDisposable
{
    private List<IDisposable> disposables;

    /**
     * Creates a new instance of the {@link DisposableObjectsTracker} class.
     */
    public DisposableObjectsTracker() {
        disposables = new List<>(10);
    }

    /**
     * Creates a {@link DisposableObjectsTracker} instance that it will destroy all the disposable objects automatically once the mod loading complete event is fired by the library.
     * @return A {@link DisposableObjectsTracker} object that will auto-dispose the registered disposable items once mod loading is complete.
     */
    public static DisposableObjectsTracker CreateWithModLoadingLifeTime()
    {
        DisposableObjectsTracker dot = new DisposableObjectsTracker();
        BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class , dot::Dispose_OnModLoadingComplete);
        return dot;
    }

    private void Dispose_OnModLoadingComplete(ModLoadingCompleteEvent event) {
        Dispose();
    }

    /**
     * Adds a disposable object to be disposed once the {@link #Dispose()} method is called.
     * @param disposable The object to dispose later.
     * @throws ArgumentNullException {@code disposable} is {@code null}.
     */
    public void AddDisposable(IDisposable disposable)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(disposable, "disposable");
        disposables.Add(disposable);
    }

    /**
     * Disposes all the objects stored in the current tracker.
     */
    @Override
    public void Dispose()
    {
        var en = disposables.GetEnumerator();
        try {
            IDisposable d = null;
            while (en.MoveNext()) {
                try {
                    (d = en.getCurrent()).Dispose();
                } catch (Exception ex) {
                    if (d == null) {
                        BaseModsLib.LOGGER.error("DisposableObjectsTracker@{}: Failed to dispose an object because the enumerator encountered a catastrophic failure.\nException data: {}" , hashCode() , ex);
                    } else {
                        BaseModsLib.LOGGER.warn("DisposableObjectsTracker@{}: Failed to dispose the object of type {} with hash code {} due to an exception.\nException data: {}", hashCode() , d.getClass().getName() , d.hashCode() , ex);
                    }
                }
            }
        } finally {
            en.Dispose();
            disposables = null; // Destroy the list contents themselves.
        }
    }
}
