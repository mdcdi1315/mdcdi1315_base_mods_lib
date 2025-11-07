package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.function.Function;

public class StackTrace
{
    /**
     * Defines the default for the number of methods to omit from the stack trace. This field is constant.
     */
    public static final int METHODS_TO_SKIP = 1; // Always omit the creation of this object.

    private final StackFrame[] elements;

    private record StackFrameConstructor(int frames_to_skip)
        implements Function<Stream<StackWalker.StackFrame>, StackFrame[]>
    {
        @Override
        public StackFrame[] apply(Stream<StackWalker.StackFrame> stream)
        {
            List<StackFrame> stack_frames = new List<>(10);
            Iterator<StackWalker.StackFrame> si = stream.iterator();
            int skipped = 0;
            StackFrame frame;
            while (si.hasNext()) {
                if (++skipped < frames_to_skip) { continue; }
                frame = new StackFrame.JavaStackWalkerFrameImpl(si.next());
                if (IsEligibleForRemoving(frame)) { continue; }
                stack_frames.Add(frame);
            }
            StackFrame[] frames = new StackFrame[stack_frames.getCount()];
            stack_frames.CopyTo(frames , 0);
            return frames;
        }
    }

    private static StackFrame[] ConstructStackTraceFromException(Exception exception , int methods_to_skip)
    {
        var elements = exception.getStackTrace();
        if (elements == null) { throw new NotSupportedException("Cannot get stack trace frames from an uninitialized exception object."); }
        List<StackFrame> frames = new List<>(elements.length);
        StackFrame constructed;
        int skipped = 0;
        for (var e : elements)
        {
            if (++skipped < methods_to_skip) { continue; }
            constructed = new StackFrame.JavaStackTraceElementImpl(e);
            if (!IsEligibleForRemoving(constructed)) {
                frames.Add(constructed);
            }
        }
        StackFrame[] fs = new StackFrame[frames.getCount()];
        frames.CopyTo(fs , 0);
        return fs;
    }

    private static boolean IsEligibleForRemoving(StackFrame frame)
    {
        var method = frame.GetMethod();
        if (method == null) {
            return false;
        } else {
            if (method.getAnnotation(StackTraceHidden.class) != null) {
                return true;
            } else {
                return method.getDeclaringClass().getAnnotation(StackTraceHidden.class) != null;
            }
        }
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class from the caller's frame.
     */
    public StackTrace() {
        this(true);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class from the caller's frame, optionally capturing source information.
     * @param fNeedFileInfo {@code true} to capture the file name, line number, and column number; otherwise, {@code false}.
     */
    public StackTrace(boolean fNeedFileInfo) {
        elements = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(new StackFrameConstructor(METHODS_TO_SKIP));
    }

    /**
     * Constructs a stack trace from a set of {@link StackFrame} objects.
     * @param frames The set of stack frames that should be present in the stack trace.
     */
    public StackTrace(IEnumerable<StackFrame> frames) {
        var lt = new List<>(frames);
        elements = new StackFrame[lt.getCount()];
        lt.CopyTo(elements , 0);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class that contains a single frame.
     * @param frame The frame that the {@link StackTrace} object should contain.
     */
    public StackTrace(StackFrame frame) {
        ArgumentNullException.ThrowIfNull(frame, "frame");
        elements = new StackFrame[] { frame };
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class using the provided exception object.
     * @param e The exception object from which to construct the stack trace.
     * @throws ArgumentNullException {@code e} is {@code null}.
     */
    public StackTrace(Exception e)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(e, "e");
        elements = ConstructStackTraceFromException(e, METHODS_TO_SKIP);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class using the provided exception object.
     * @param e The exception object from which to construct the stack trace.
     * @param fNeedFileInfo {@code true} to capture the file name, line number, and column number; otherwise, {@code false}.
     * @throws ArgumentNullException {@code e} is {@code null}.
     */
    public StackTrace(Exception e, boolean fNeedFileInfo)
            throws ArgumentNullException
    {
        this(e);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class using the provided exception object and skipping the specified number of frames.
     * @param e The exception object from which to construct the stack trace.
     * @param skipFrames The number of frames up the stack from which to start the trace.
     * @throws ArgumentOutOfRangeException The {@code skipFrames} parameter is negative.
     * @throws ArgumentNullException The parameter {@code e} is {@code null}.
     */
    public StackTrace(Exception e, int skipFrames)
            throws ArgumentOutOfRangeException , ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(e, "e");
        if (skipFrames < 0) {
            throw new ArgumentOutOfRangeException("skipFrames", "Parameter must not be a negative number.");
        }
        elements = ConstructStackTraceFromException(e, METHODS_TO_SKIP + skipFrames);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class using the provided exception object, skipping the specified number of frames and optionally capturing source information.
     * @param e The exception object from which to construct the stack trace.
     * @param skipFrames The number of frames up the stack from which to start the trace.
     * @param fNeedFileInfo {@code true} to capture the file name, line number, and column number; otherwise, {@code false}.
     * @throws ArgumentOutOfRangeException The {@code skipFrames} parameter is negative.
     * @throws ArgumentNullException The parameter {@code e} is {@code null}.
     */
    public StackTrace(Exception e, int skipFrames, boolean fNeedFileInfo)
            throws ArgumentOutOfRangeException , ArgumentNullException
    {
        this(e, skipFrames);
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class from the caller's frame, skipping the specified number of frames.
     * @param skipFrames The number of frames up the stack from which to start the trace.
     * @throws ArgumentOutOfRangeException The {@code skipFrames} parameter is negative.
     */
    public StackTrace(int skipFrames)
        throws ArgumentOutOfRangeException
    {
        if (skipFrames < 0) {
            throw new ArgumentOutOfRangeException("skipFrames", "Parameter must not be a negative number.");
        }
        elements = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(new StackFrameConstructor(METHODS_TO_SKIP + skipFrames));
    }

    /**
     * Initializes a new instance of the {@link StackTrace} class from the caller's frame, skipping the specified number of frames and optionally capturing source information.
     * @param skipFrames The number of frames up the stack from which to start the trace.
     * @param fNeedFileInfo {@code true} to capture the file name, line number, and column number; otherwise, {@code false}.
     * @throws ArgumentOutOfRangeException The {@code skipFrames} parameter is negative.
     */
    public StackTrace(int skipFrames, boolean fNeedFileInfo)
            throws ArgumentOutOfRangeException
    {
        this(skipFrames);
    }

    /**
     * Gets the number of frames in the stack trace.
     * @return The number of frames in the stack trace.
     */
    public int GetFrameCount() {
        return elements.length;
    }

    /**
     * Gets the specified stack frame.
     * @param index The index of the stack frame requested.
     * @return The specified stack frame.
     */
    @MaybeNull
    public StackFrame GetFrame(int index) {
        return (index > -1 && index < elements.length) ? elements[index] : null;
    }

    /**
     * Returns a copy of all stack frames in the current stack trace.
     * @return An array of type {@link StackFrame} representing the function calls in the stack trace.
     */
    @MaybeNull
    public StackFrame[] GetFrames() {
        StackFrame[] new_frames = new StackFrame[elements.length];
        System.arraycopy(elements , 0 , new_frames , 0 , elements.length);
        return new_frames;
    }
}
