package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.ArrayView;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

/**
 * Provides an {@link IModFileSystem} that is being comprised of many {@link IModFileSystem}s.
 */
public final class CompositeFileSystem
    implements IModFileSystem
{
    private final IEnumerable<IModFileSystem> systems;

    /**
     * Initializes a new instance of the {@link CompositeFileSystem} class.
     * @param systems The mod file systems to use.
     * @throws ArgumentNullException {@code systems} is {@code null}.
     */
    public CompositeFileSystem(IModFileSystem... systems)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(systems, "systems");
        this.systems = new ArrayView<>(systems);
    }

    /**
     * Initializes a new instance of the {@link CompositeFileSystem} class.
     * @param systems The mod file systems to use.
     * @throws ArgumentNullException {@code systems} is {@code null}.
     */
    public CompositeFileSystem(IEnumerable<IModFileSystem> systems)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(systems, "systems");
        this.systems = systems;
    }

    @Override
    public DeferredFile TryGetFile(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        DeferredFile file;
        try (var en = systems.GetEnumerator())
        {
            while (en.MoveNext())
            {
                file = en.getCurrent().TryGetFile(path);
                if (file != null) { return file; }
            }
        }
        return null;
    }

    @Override
    public IEnumerable<DeferredFile> GetFiles()
    {
        return CollectionManipulations.ConcatEnumerables(CollectionManipulations.MapTo(
                systems,
                IModFileSystem::GetFiles
        ));
    }
}
