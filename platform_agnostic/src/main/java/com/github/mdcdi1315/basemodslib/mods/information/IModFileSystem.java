package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;

/**
 * Provides a way to access files inside a mod.
 *
 * <h3>Easy access to files, simplified</h3>
 * This interface provides the basis for accessing files of a mod. <br />
 * Callers can very easily find files and open them for reading their contents.
 * Each file retrieved through this interface is conveniently wrapped into a {@link DeferredFile},
 * which does then provide the way to open it and read it.
 * <h4>Path access conventions</h4>
 * If you want to use the {@link #TryGetFile(String)} API, you should expect
 * that the delimiting character for each path component is the forward slash {@code /}.
 * For example, a valid path to pass to it is {@code foo/bar/license.txt}.
 * If the implementations have a file system of a different delimiting character,
 * they should translate to that one from the forward slash.
 * <h4>The empty file system</h4>
 * There is also a default implementation of the {@link IModFileSystem} interface,
 * that provides an empty file system. It is a file system that does not contain
 * any files, and can be used as a placeholder.
 */
public interface IModFileSystem
    extends ISynchronized
{
    /**
     * Represents a {@link IModFileSystem} that is empty: <br />
     * Does not contain any files, and it's {@link #GetFiles()} method
     * returns an empty collection.
     */
    IModFileSystem EMPTY = new IModFileSystem()
    {
        @Override
        public DeferredFile TryGetFile(String path)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(path, "path");
            return null;
        }

        @Override
        public IEnumerable<DeferredFile> GetFiles() { return new EmptyEnumerable<>(); }

        @Override
        public DeferredFile TryFileWithAlternatives(String... alternatives) { return null; }

        @Override
        public String toString() { return "EMPTY"; }
    };

    /**
     * Gets the string that separates path components when using {@link #TryGetFile(String)}. <br />
     * Here, it is the forward slash (/).
     */
    String PATH_SEPARATOR = "/";

    /**
     * Attempts to get the file system of the specified mod information, or it returns the {@link #EMPTY} mod file system.
     * @param info The mod information containing the file system.
     * @return An instance of the {@link IModFileSystem} interface.
     */
    @NotNull
    static IModFileSystem GetFromInfoOrEmpty(@AllowNull IModInfo info) { return (info == null) ? EMPTY : info.GetFileSystem(); }

    /**
     * Attempts to locate a file in this file system.
     * @param path The path to the file that is subject to be located.
     * @return A {@link DeferredFile} providing the located file, or {@code null}
     * if the file could not be found.
     * @throws ArgumentNullException {@code path} is {@code null}.
     */
    @MaybeNull
    default DeferredFile TryGetFile(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (var en = GetFiles().GetEnumerator())
        {
            DeferredFile current;
            while (en.MoveNext())
            {
                current = en.getCurrent();
                if (path.equals(current.GetPath())) { return current; }
            }
        }
        return null;
    }

    /**
     * Attempts to find a file, using fallback paths if a file cannot be located.
     * @param alternatives The path to the file and the fallback paths to use.
     * @return The found file, or {@code null} if all the paths were tested
     * but no file was found.
     * @throws ArgumentNullException {@code alternatives} is {@code null}.
     */
    @MaybeNull
    default DeferredFile TryFileWithAlternatives(String... alternatives)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(alternatives, "alternatives");
        DeferredFile current;
        for (var path : alternatives)
        {
            current = TryGetFile(path);
            if (current != null) { return current; }
        }
        return null;
    }

    /**
     * Gets all the files contained in this mod file system.
     * @return The contained files.
     */
    @NotNull
    IEnumerable<DeferredFile> GetFiles();
}
