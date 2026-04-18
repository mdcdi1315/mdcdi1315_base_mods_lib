package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.EOFException;

/**
 * Provides an {@link InputStream} that wraps an input stream. <br />
 * Implements the {@link IStreamOwner} and {@link IInputStreamWrapper} interfaces.
 */
public class WrappedInputStream
    extends InputStream
    implements IStreamOwner, IInputStreamWrapper
{
    private static final byte FLAG_NONE = 0;
    private static final byte FLAG_OWNER = 1 << 0;
    private static final byte FLAG_DISPOSED = 1 << 1;

    private byte flags;
    private InputStream stream;

    /**
     * Initializes a new instance of the {@link WrappedInputStream} class, by specifying an input stream, and closing the provided stream once the {@link #close()} call is performed on the returned object.
     * @param stream The input data stream to wrap.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    public WrappedInputStream(InputStream stream) throws ArgumentNullException { this(stream, true); }

    /**
     * Initializes a new instance of the {@link WrappedInputStream} class, by specifying an input stream, and specifying whether the input stream should be closed or not upon calling {@link #close()}.
     * @param stream The input data stream to wrap.
     * @param close_stream A value whether to close {@code stream} once the {@link #close()} method is called on the returned object.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    public WrappedInputStream(InputStream stream, boolean close_stream)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(this.stream = stream, "stream");
        flags = close_stream ? FLAG_OWNER : FLAG_NONE;
    }

    @Override
    public final InputStream GetWrapped() { return stream; }

    @Override
    public boolean GetIsOwner() { return (flags & FLAG_OWNER) != 0; }

    @Override
    public void SetIsOwner(boolean value) throws InvalidOperationException { if (value) { flags |= FLAG_OWNER; } else { flags &= ~FLAG_OWNER; } }

    @Override
    public int read() throws IOException { return stream.read(); }

    @Override
    public int read(@NotNull byte[] b) throws IOException { return stream.read(b); }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException { return stream.read(b, off, len); }

    @Override
    public int available() throws IOException { return stream.available(); }

    @Override
    public void mark(int readlimit) { stream.mark(readlimit); }

    @Override
    public void reset() throws IOException { stream.reset(); }

    @Override
    public boolean markSupported() { return stream.markSupported(); }

    /**
     * Reads 'exactly' the specified number of bytes, that is, if the bytes that could be read from the stream is less than {@code n_bytes}, this method fails with {@link EOFException}.
     * @param n_bytes The number of bytes to exactly read.
     * @return The read bytes. The length of this byte buffer always equals the value of {@code n_bytes} parameter.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentOutOfRangeException {@code n_bytes} is a negative value.
     * @throws EOFException If the required number of bytes specified in the {@code n_bytes} parameter are unavailable.
     */
    public byte[] ReadExactly(int n_bytes)
            throws IOException, ArgumentOutOfRangeException
    {
        if (n_bytes < 0) {
            throw new ArgumentOutOfRangeException("n_bytes", "Number of bytes to exactly read is zero!");
        } else {
            return ReadExactlyUnchecked(n_bytes);
        }
    }

    /**
     * Reads the specified number of bytes from the stream and returns them to a newly allocated {@link ByteBuffer}.
     * @param n_bytes The number of bytes to read from the current input stream.
     * @return The allocated {@link ByteBuffer} that holds the read input stream data.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentOutOfRangeException {@code n_bytes} is a negative value.
     * @since 1.0.26
     */
    public ByteBuffer ReadBytes(int n_bytes) throws IOException, ArgumentOutOfRangeException { return ByteBufferUtils.ReadFromStream(this, n_bytes); }

    private byte[] ReadExactlyUnchecked(int n_bytes)
            throws IOException
    {
        byte[] b = new byte[n_bytes];
        int read = 0, r;
        do {
            r = this.read(b, read, n_bytes - read);
            if (r == -1) {
                throw new EOFException(String.format("Stream ended prematurely, expected %d bytes to read while read %d bytes.", n_bytes, read));
            } else {
                read += r;
            }
        } while (read < n_bytes);
        return b;
    }

    /**
     * Reads a byte out of the current data stream. <br />
     * If the data stream does not have any additional data, {@link EOFException} is thrown.
     * @return The byte that was read.
     * @throws IOException An I/O exception was occurred.
     */
    public byte ReadLiteralByte()
            throws IOException
    {
        int r;
        if ((r = this.read()) == -1) { throw new EOFException("Stream ended prematurely!"); } else { return (byte) r; }
    }

    /**
     * Reads a primitive of type {@link Long}, encoded with the {@link java.nio.ByteOrder#BIG_ENDIAN} byte order.
     * @return The read {@link Long} value.
     * @throws IOException An I/O exception was occurred.
     */
    public long ReadLongBE() throws IOException { return ByteArray.GetLong(ReadExactlyUnchecked(Long.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Integer}, encoded with the {@link java.nio.ByteOrder#BIG_ENDIAN} byte order.
     * @return The read {@link Integer} value.
     * @throws IOException An I/O exception was occurred.
     */
    public int ReadIntegerBE() throws IOException { return ByteArray.GetInt(ReadExactlyUnchecked(Integer.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Float}, encoded with the {@link java.nio.ByteOrder#BIG_ENDIAN} byte order.
     * @return The read {@link Float} value.
     * @throws IOException An I/O exception was occurred.
     */
    public float ReadFloatBE() throws IOException { return ByteArray.GetFloat(ReadExactlyUnchecked(Float.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Short}, encoded with the {@link java.nio.ByteOrder#BIG_ENDIAN} byte order.
     * @return The read {@link Short} value.
     * @throws IOException An I/O exception was occurred.
     */
    public short ReadShortBE() throws IOException { return ByteArray.GetShort(ReadExactlyUnchecked(Short.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Double}, encoded with the {@link java.nio.ByteOrder#BIG_ENDIAN} byte order.
     * @return The read {@link Double} value.
     * @throws IOException An I/O exception was occurred.
     */
    public double ReadDoubleBE() throws IOException { return ByteArray.GetDouble(ReadExactlyUnchecked(Double.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Long}, encoded with the {@link java.nio.ByteOrder#LITTLE_ENDIAN} byte order.
     * @return The read {@link Long} value.
     * @throws IOException An I/O exception was occurred.
     */
    public long ReadLongLE() throws IOException { return ByteArrayLE.GetLong(ReadExactlyUnchecked(Long.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Integer}, encoded with the {@link java.nio.ByteOrder#LITTLE_ENDIAN} byte order.
     * @return The read {@link Integer} value.
     * @throws IOException An I/O exception was occurred.
     */
    public int ReadIntegerLE() throws IOException { return ByteArrayLE.GetInt(ReadExactlyUnchecked(Integer.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Float}, encoded with the {@link java.nio.ByteOrder#LITTLE_ENDIAN} byte order.
     * @return The read {@link Float} value.
     * @throws IOException An I/O exception was occurred.
     */
    public float ReadFloatLE() throws IOException { return ByteArrayLE.GetFloat(ReadExactlyUnchecked(Float.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Short}, encoded with the {@link java.nio.ByteOrder#LITTLE_ENDIAN} byte order.
     * @return The read {@link Short} value.
     * @throws IOException An I/O exception was occurred.
     */
    public short ReadShortLE() throws IOException { return ByteArrayLE.GetShort(ReadExactlyUnchecked(Short.BYTES), 0); }

    /**
     * Reads a primitive of type {@link Double}, encoded with the {@link java.nio.ByteOrder#LITTLE_ENDIAN} byte order.
     * @return The read {@link Double} value.
     * @throws IOException An I/O exception was occurred.
     */
    public double ReadDoubleLE() throws IOException { return ByteArrayLE.GetDouble(ReadExactlyUnchecked(Double.BYTES), 0); }

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     * @implSpec The {@link #close()} method of {@link WrappedInputStream} does nothing, if {@link #GetIsOwner()} returns {@code false}.
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
     * Releases all resources used by this {@link WrappedInputStream} instance.
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
            }
        }
    }
}
