package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import java.util.Arrays;
import java.util.Objects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PushbackWrappedInputStream
    extends WrappedInputStream
{
    private int pushback_buf_index; // The next index from which to read from the push-back buffer.
    private byte[] pushback_buffer;

    /**
     * Initializes a new instance of the {@link PushbackWrappedInputStream} class, by specifying an input stream, and closing the provided stream once the {@link #close()} call is performed on the returned object. <br />
     * Additionally, the {@code push_back_buf_size} parameter specifies the size of the push-back buffer.
     * @param stream The input data stream to wrap.
     * @param push_back_buf_size The size of the push-back buffer, in bytes.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code push_back_buf_size} is less than 1.
     */
    public PushbackWrappedInputStream(InputStream stream, int push_back_buf_size) throws ArgumentNullException, ArgumentOutOfRangeException { this(stream, push_back_buf_size, true); }

    /**
     * Initializes a new instance of the {@link PushbackWrappedInputStream} class, by specifying an input stream, whether the input stream should be closed or not upon calling {@link #close()}, and the size of the push-back buffer.
     * @param stream       The input data stream to wrap.
     * @param push_back_buf_size The size of the push-back buffer, in bytes.
     * @param close_stream A value whether to close {@code stream} once the {@link #close()} method is called on the returned object.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code push_back_buf_size} is less than 1.
     */
    public PushbackWrappedInputStream(InputStream stream, int push_back_buf_size, boolean close_stream)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        super(stream, close_stream);
        if (push_back_buf_size < 1) {
            throw new ArgumentOutOfRangeException("push_back_buf_size", "Push-back buffer length must be a positive integer");
        } else {
            // Initially, the pushback buffer does not contain any data.
            pushback_buffer = new byte[pushback_buf_index = push_back_buf_size];
        }
    }

    /**
     * Gets the number of bytes that are allocated for the push-back buffer.
     * @return The number of bytes of the push-back buffer.
     */
    public final int GetPushBackBufferSize() { return pushback_buffer.length; }

    /**
     * Gets the number of bytes that are remaining in the push-back buffer.
     * @return The number of bytes remaining to fill to the push-back buffer.
     */
    public final int GetRemainingPushBackBufferBytes() { return pushback_buf_index; }

    @Override
    public int read()
            throws IOException
    {
        return (pushback_buf_index < pushback_buffer.length) ? pushback_buffer[pushback_buf_index++] & 0xFF : super.read();
    }

    @Override
    public int read(byte[] b) throws IOException { return read(b, 0, b.length); }

    public int read(byte[] b, int off, int len)
            throws IOException
    {
        Objects.checkFromIndexSize(off, len, b.length);
        if (len == 0) { return 0; }

        int avail = pushback_buffer.length - pushback_buf_index;
        if (avail > 0) {
            if (len < avail) { avail = len; }
            System.arraycopy(pushback_buffer, pushback_buf_index, b, off, avail);
            pushback_buf_index += avail;
            off += avail;
            len -= avail;
        }
        if (len > 0) {
            len = super.read(b, off, len);
            return (len == -1) ? (avail == 0 ? -1 : avail) : avail + len;
        } else {
            return avail;
        }
    }

    /**
     * Pushes back a byte by copying it to the front of the pushback buffer.
     * After this method returns, the next byte to be read will have the value
     * {@code (byte)b}.
     *
     * @param      b   the {@code int} value whose low-order
     *                  byte is to be pushed back.
     * @throws    IOException If there is not enough room in the pushback
     *            buffer for the byte, or this input stream has been closed by
     *            invoking its {@link #close()} method.
     */
    public void Unread(int b)
            throws IOException
    {
        if (pushback_buf_index == 0) {
            throw new IOException("Push back buffer is full");
        } else {
            pushback_buffer[--pushback_buf_index] = (byte)b;
        }
    }

    /**
     * Pushes back a portion of an array of bytes by copying it to the front
     * of the pushback buffer.  After this method returns, the next byte to be
     * read will have the value {@code b[off]}, the byte after that will
     * have the value {@code b[off+1]}, and so forth.
     *
     * @param     b the byte array to push back.
     * @param     off the start offset of the data.
     * @param     len the number of bytes to push back.
     * @throws    NullPointerException If {@code b} is {@code null}.
     * @throws    IOException If there is not enough room in the pushback
     *            buffer for the specified number of bytes,
     *            or this input stream has been closed by
     *            invoking its {@link #close()} method.
     */
    public void Unread(byte[] b, int off, int len)
            throws IOException
    {
        if (len > pushback_buf_index) {
            throw new IOException("Push back buffer is full");
        } else {
            pushback_buf_index -= len;
            System.arraycopy(b, off, pushback_buffer, pushback_buf_index, len);
        }
    }

    /**
     * Pushes back an array of bytes by copying it to the front of the
     * pushback buffer.  After this method returns, the next byte to be read
     * will have the value {@code b[0]}, the byte after that will have the
     * value {@code b[1]}, and so forth.
     *
     * @param     b the byte array to push back
     * @throws    NullPointerException If {@code b} is {@code null}.
     * @throws    IOException If there is not enough room in the pushback
     *            buffer for the specified number of bytes,
     *            or this input stream has been closed by
     *            invoking its {@link #close()} method.
     * @since     1.1
     */
    public void Unread(byte[] b) throws IOException { Unread(b, 0, b.length); }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * invocation of a method for this input stream. The next invocation might be
     * the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     *
     * <p> The method returns the sum of the number of bytes that have been
     * pushed back and the value returned by {@link
     * java.io.FilterInputStream#available available}.
     *
     * @return     the number of bytes that can be read (or skipped over) from
     *             the input stream without blocking.
     * @throws     IOException  if this input stream has been closed by
     *             invoking its {@link #close()} method,
     *             or an I/O error occurs.
     * @see        InputStream#available()
     */
    public int available()
            throws IOException
    {
        int n = pushback_buffer.length - pushback_buf_index;
        int avail = super.available();
        return n > (Integer.MAX_VALUE - avail) ? Integer.MAX_VALUE : n + avail;
    }

    /**
     * Skips over and discards {@code n} bytes of data from this
     * input stream. The {@code skip} method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes,
     * possibly zero.  If {@code n} is negative, no bytes are skipped.
     *
     * <p> The {@code skip} method of {@code PushbackWrappedInputStream}
     * first skips over the bytes in the pushback buffer, if any.  It then
     * calls the {@code skip} method of the underlying input stream if
     * more bytes need to be skipped.  The actual number of bytes skipped
     * is returned.
     *
     * @param      n  {@inheritDoc}
     * @return     {@inheritDoc}
     * @throws     IOException  if the stream has been closed by
     *             invoking its {@link #close()} method,
     *             {@code in.skip(n)} throws an IOException,
     *             or an I/O error occurs.
     */
    public long skip(long n) throws IOException
    {
        if (n < 1) { return 0; }

        long pskip = pushback_buffer.length - pushback_buf_index;
        if (pskip > 0) {
            if (n < pskip) { pskip = n; }
            pushback_buf_index += (int) pskip;
            n -= pskip;
        }
        if (n > 0) { pskip += super.skip(n); }
        return pskip;
    }

    /**
     * Tests if this input stream supports the {@code mark} and
     * {@code reset} methods, which it does not.
     *
     * @return   {@code false}, since this class does not support the
     *           {@code mark} and {@code reset} methods.
     * @see      InputStream#mark(int)
     * @see      InputStream#reset()
     */
    public final boolean markSupported() { return false; }

    /**
     * Marks the current position in this input stream.
     *
     * <p> The {@code mark} method of {@code PushbackWrappedInputStream}
     * does nothing.
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     InputStream#reset()
     */
    public final void mark(int readlimit) {}

    /**
     * Repositions this stream to the position at the time the
     * {@code mark} method was last called on this input stream.
     *
     * <p> The method {@code reset} for class
     * {@code PushbackWrappedInputStream} does nothing except throw an
     * {@code IOException}.
     *
     * @throws  IOException  if this method is invoked.
     * @see     InputStream#mark(int)
     * @see     IOException
     */
    public final void reset() throws IOException { throw new IOException("mark/reset not supported"); }

    @Override
    public long transferTo(OutputStream out)
            throws IOException
    {
        Objects.requireNonNull(out, "out");
        int avail = pushback_buffer.length - pushback_buf_index;
        if (avail > 0) {
            // Prevent poisoning and leaking of buf
            byte[] buffer = Arrays.copyOfRange(pushback_buffer, pushback_buf_index, pushback_buffer.length);
            out.write(buffer);
            pushback_buf_index = buffer.length;
        }
        try {
            return Math.addExact(avail, GetWrapped().transferTo(out));
        } catch (ArithmeticException ignore) {
            return Long.MAX_VALUE;
        }
    }

    @Override
    protected void CloseCode()
            throws IOException
    {
        try {
            super.CloseCode();
        } finally {
            pushback_buffer = null;
        }
    }
}
