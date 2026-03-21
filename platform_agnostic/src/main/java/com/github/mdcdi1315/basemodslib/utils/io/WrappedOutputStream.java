package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides an {@link OutputStream} that wraps an output stream. <br />
 * Implements the {@link IStreamOwner} and {@link IOutputStreamWrapper} interfaces.
 */
public class WrappedOutputStream
    extends OutputStream
    implements IStreamOwner, IOutputStreamWrapper
{
    private static final byte FLAG_NONE = 0;
    private static final byte FLAG_OWNER = 1 << 0;
    private static final byte FLAG_DISPOSED = 1 << 1;

    private byte flags;
    private OutputStream stream;
    private byte[] primitives_buffer;

    /**
     * Initializes a new instance of the {@link WrappedOutputStream} class, by specifying an output stream, and closing the provided stream once the {@link #close()} call is performed on the returned object.
     * @param stream The output data stream to wrap.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    public WrappedOutputStream(OutputStream stream) throws ArgumentNullException { this(stream, true); }

    /**
     * Initializes a new instance of the {@link WrappedOutputStream} class, by specifying an output stream, and specifying whether the output stream should be closed or not upon calling {@link #close()}.
     * @param out The output data stream to wrap.
     * @param close_stream A value whether to close {@code stream} once the {@link #close()} method is called on the returned object.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    public WrappedOutputStream(OutputStream out, boolean close_stream)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(this.stream = out, "out");
        primitives_buffer = new byte[Long.BYTES | Double.BYTES];
        flags = close_stream ? FLAG_OWNER : FLAG_NONE;
    }

    @Override
    public final OutputStream GetWrapped() { return stream; }

    @Override
    public boolean GetIsOwner() { return (flags & FLAG_OWNER) != 0; }

    @Override
    public void SetIsOwner(boolean value) throws InvalidOperationException { if (value) { flags |= FLAG_OWNER; } else { flags &= ~FLAG_OWNER; } }

    @Override
    public void flush() throws IOException { stream.flush(); }

    @Override
    public void write(int b) throws IOException { stream.write(b); }

    @Override
    public void write(@NotNull byte[] b) throws IOException { stream.write(b); }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException { stream.write(b, off, len); }

    public final void WriteShortBE(short value)
            throws IOException
    {
        ByteArray.SetShort(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Short.BYTES);
    }

    public final void WriteIntegerBE(int value)
            throws IOException
    {
        ByteArray.SetInt(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Integer.BYTES);
    }

    public final void WriteLongBE(long value)
            throws IOException
    {
        ByteArray.SetLong(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Long.BYTES);
    }

    public final void WriteFloatBE(float value)
            throws IOException
    {
        ByteArray.SetFloat(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Float.BYTES);
    }

    public final void WriteDoubleBE(double value)
            throws IOException
    {
        ByteArray.SetDouble(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Double.BYTES);
    }

    public final void WriteShortLE(short value)
            throws IOException
    {
        ByteArrayLE.SetShort(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Short.BYTES);
    }

    public final void WriteIntegerLE(int value)
            throws IOException
    {
        ByteArrayLE.SetInt(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Integer.BYTES);
    }

    public final void WriteLongLE(long value)
            throws IOException
    {
        ByteArrayLE.SetLong(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Long.BYTES);
    }

    public final void WriteFloatLE(float value)
            throws IOException
    {
        ByteArrayLE.SetFloat(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Float.BYTES);
    }

    public final void WriteDoubleLE(double value)
            throws IOException
    {
        ByteArrayLE.SetDouble(primitives_buffer, 0, value);
        write(primitives_buffer, 0, Double.BYTES);
    }

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     * @implSpec The {@link #close()} method of {@link WrappedOutputStream} does nothing, if {@link #GetIsOwner()} returns {@code false}.
     * @throws IOException If an I/O error occurs.
     * @implNote If you want to add disposal code, you do so by overriding this method.
     * However, when overriding this method, make sure that you call first this implementation by using the {@code super} convention.
     */
    protected void CloseCode()
            throws IOException
    {
        if (((flags & FLAG_OWNER) != 0) && stream != null) { stream.close(); }
    }

    /**
     * Releases all resources used by this {@link WrappedOutputStream} instance.
     * @throws IOException Thrown if an I/O error occurs.
     */
    @Override
    public final void close()
            throws IOException
    {
        synchronized (this)
        {
            if ((flags & FLAG_DISPOSED) != 0) { return; }
            try {
                CloseCode();
            } finally {
                stream = null;
                flags |= FLAG_DISPOSED;
                primitives_buffer = null;
            }
        }
    }
}
