package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.YieldingEnumerable;

import java.nio.file.Path;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.IOException;

/**
 * An {@link IModFileSystem} implementation based on {@link Path} instances.
 */
public final class NIOPathBasedFileSystem
    implements IModFileSystem
{
    private static final int MAX_SEARCH_DEPTH = 128;

    private final Path root_path;

    /**
     * Initializes a new instance of the {@link NIOPathBasedFileSystem} class.
     * @param root_path The {@link Path} acting as the logical root path of this file system.
     * @throws ArgumentNullException {@code root_path} is {@code null}.
     */
    public NIOPathBasedFileSystem(Path root_path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(root_path, "root_path");
        this.root_path = root_path;
    }

    private static final class FileImpl
        extends DeferredFile
    {
        private final Path path;

        public FileImpl(NIOPathBasedFileSystem fs, Path path)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(path, "path");
            super(fs, fs.root_path.relativize(path).toString().replace(path.getFileSystem().getSeparator(), PATH_SEPARATOR));
            this.path = path;
        }

        @Override
        public InputStream Open() throws IOException { return Files.newInputStream(path); }
    }

    private static void EnumerateFiles(YieldingEnumerable.IYieldActionData<DeferredFile, NIOPathBasedFileSystem> action)
    {
        var fs = action.GetState();
        try (var stream = Files.walk(fs.root_path, MAX_SEARCH_DEPTH)) {
            var en = stream.iterator();
            Path path;
            while (en.hasNext())
            {
                path = en.next();
                if (Files.isReadable(path)) { action.Yield(new FileImpl(fs, path)); }
            }
        } catch (IOException _) { }
    }

    private String ConvertToNativeFsPath(String path) { return path.replace(PATH_SEPARATOR, root_path.getFileSystem().getSeparator()); }

    @Override
    public DeferredFile TryGetFile(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        var path_constructed = root_path.resolve(ConvertToNativeFsPath(path));
        return Files.exists(path_constructed) ? new FileImpl(this, path_constructed) : null;
    }

    @Override
    public IEnumerable<DeferredFile> GetFiles() { return new YieldingEnumerable<>(this, NIOPathBasedFileSystem::EnumerateFiles); }
}
