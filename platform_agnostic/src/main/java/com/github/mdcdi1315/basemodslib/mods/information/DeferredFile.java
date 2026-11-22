package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.InputStreamReader;

/**
 * Provides the way to open a file retrieved through {@link IModFileSystem}.
 */
public abstract class DeferredFile
{
    private final String path;
    private final IModFileSystem fs;

    /**
     * Initializes a new instance of the {@link DeferredFile} class.
     * @param fs The {@link IModFileSystem} where this {@link DeferredFile} belongs to.
     * @param path The path of the file given as input to the {@link IModFileSystem#TryGetFile(String)}.
     * @throws ArgumentNullException {@code fs} and/or {@code path} are {@code null}.
     */
    public DeferredFile(IModFileSystem fs, String path)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(fs, "fs");
        ArgumentNullException.ThrowIfNull(path, "path");
        this.fs = fs;
        this.path = path;
    }

    // Two deferred files are the exact same when:
    // 1. They both reference the same file system AND
    // 2. Their path strings match.
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    private static boolean EqualsCore(DeferredFile source, DeferredFile other) { return source.fs == other.fs && source.path.equals(other.path); }

    /**
     * Gets the path to the file, virtualized for the
     * mod file system where it was retrieved from.
     * @return The path to the file.
     */
    @Pure
    @NotNull
    public final String GetPath() { return path; }

    /**
     * Gets the file system where this deferred file
     * belongs to.
     * @return The file system where this file belongs to.
     */
    @Pure
    @NotNull
    public final IModFileSystem GetFileSystem() { return fs; }

    /**
     * Opens the file to read its contents.
     * @return A stream to the opened file.
     * @throws IOException If any I/O issues arise during opening the file.
     */
    @NotNull
    public abstract InputStream Open() throws IOException;

    /**
     * Opens a reader to the file to read its contents as a text file.
     * @param charset The character encoding to use.
     * @return A {@link Reader} that can read the contents of the file as characters.
     * @throws IOException If any I/O issues arise during opening the file.
     * @throws ArgumentNullException {@code charset} is {@code null}.
     */
    @NotNull
    public Reader OpenReader(Charset charset)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(charset, "charset");
        return new InputStreamReader(Open(), charset);
    }

    /**
     * Opens a reader to the file to read its contents as a text file.
     * @return A {@link Reader} that can read the contents of the file as characters.
     * @throws IOException If any I/O issues arise during opening the file.
     * @apiNote The reader will be opened with the encoding specified by the {@link Charset#defaultCharset()} method.
     */
    @NotNull
    public Reader OpenReader() throws IOException { return OpenReader(Charset.defaultCharset()); }

    /**
     * Gets a value whether this {@link DeferredFile} and other {@link DeferredFile} do represent the same file.
     * @param other The other {@link DeferredFile} to test against this file.
     * @return A value whether the two files are the same file.
     */
    public final boolean Equals(@AllowNull DeferredFile other) { return other != null && EqualsCore(this, other); }

    @Override
    public final boolean equals(@AllowNull Object obj) { return (obj instanceof DeferredFile df) && EqualsCore(this, df); }

    @NotNull
    @Override
    public String toString()
    {
        return StringUtils.Concat(
                "Deferred file: ",
                path
        );
    }
}
