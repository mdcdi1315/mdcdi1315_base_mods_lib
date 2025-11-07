package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresUnreferencedCode;

import java.lang.reflect.Method;

/**
 * Provides information about a {@link StackFrame}, which represents a function call on the call stack for the current thread.
 */
public class StackFrame
{
    /**
     * Defines the value that is returned from the GetNativeOffset() or GetILOffset() method when the native or Microsoft intermediate language (MSIL) offset is unknown. This field is constant.
     */
    public static final int OFFSET_UNKNOWN = -1;

    /**
     * Special class for translating {@link StackWalker.StackFrame} objects to {@link StackFrame} objects. <br />
     * This is purely Java; does not exist in .NET.
     */
    static final class JavaStackWalkerFrameImpl
        extends StackFrame
    {
        private final int byte_code_offset;

        /**
         * Initializes a new instance of the {@link JavaStackWalkerFrameImpl} class.
         * @param sf The stack frame to construct this object from.
         */
        public JavaStackWalkerFrameImpl(StackWalker.StackFrame sf)
            throws ArgumentNullException
        {
            super();
            ArgumentNullException.ThrowIfNull(sf, "sf");
            try {
                super.method = sf.getDeclaringClass().getMethod(sf.getMethodName());
            } catch (NoSuchMethodException ignored) {}
            byte_code_offset = sf.getByteCodeIndex();
            super.file_name = sf.getFileName();
            super.line_number = sf.getLineNumber();
        }

        @Override
        public int GetILOffset() {
            return byte_code_offset;
        }
    }

    static final class JavaStackTraceElementImpl
        extends StackFrame
    {
        public JavaStackTraceElementImpl(StackTraceElement element)
                throws ArgumentNullException
        {
            super();
            ArgumentNullException.ThrowIfNull(element, "element");
            try {
                super.method = Class.forName(element.getClassName()).getMethod(element.getMethodName());
            } catch (Exception ignored) {}
            super.file_name = element.getFileName();
            super.line_number = element.getLineNumber();
        }
    }

    @AllowNull
    private Method method;
    @AllowNull
    private String file_name;
    private int line_number, col_number;

    /**
     * Initializes a new instance of the {@link StackFrame} class.
     */
    public StackFrame()
    {
        method = null;
        file_name = null;
        line_number = 0;
        col_number = 0;
    }

    /**
     * Initializes a new instance of the {@link StackFrame} class that contains only the given file name and line number.
     * @param fileName The file name.
     * @param lineNumber The line number in the specified file.
     */
    public StackFrame(@MaybeNull String fileName, int lineNumber) {
        this(fileName , lineNumber , 0);
    }

    /**
     * Initializes a new instance of the {@link StackFrame} class that contains only the given file name, line number, and column number.
     * @param fileName The file name.
     * @param lineNumber The line number in the specified file.
     * @param colNumber The column number in the specified file.
     */
    public StackFrame(@MaybeNull String fileName, int lineNumber, int colNumber)
    {
        method = null;
        this.file_name = fileName;
        this.line_number = lineNumber;
        this.col_number = colNumber;
    }

    /**
     * Gets the file name that contains the code that is executing.
     * This information is typically extracted from the debugging symbols for the executable.
     * @return The file name, or {@code null} if the file name cannot be determined.
     */
    @MaybeNull
    public String GetFileName() {
        return file_name;
    }

    /**
     * Gets the line number in the file that contains the code that is executing.
     * This information is typically extracted from the debugging symbols for the executable.
     * @return The file line number, or 0 (zero) if the file line number cannot be determined.
     */
    public int GetFileLineNumber() {
        return line_number;
    }

    /**
     * Gets the column number in the file that contains the code that is executing.
     * This information is typically extracted from the debugging symbols for the executable.
     * @return The file column number, or 0 (zero) if the file column number cannot be determined.
     */
    public int GetFileColumnNumber() {
        return col_number;
    }

    /**
     * Gets the method in which the frame is executing.
     * @return The method in which the frame is executing.
     */
    @MaybeNull
    @RequiresUnreferencedCode(Message = "Metadata for the method might be incomplete or removed. Consider using DiagnosticMethodInfo.Create instead")
    public Method GetMethod() {
        return method;
    }

    /**
     * Gets the offset from the start of the Microsoft intermediate language (MSIL) code for the method that is executing.
     * This offset might be an approximation depending on whether or not the just-in-time (JIT) compiler is generating debugging code.
     * The generation of this debugging information is controlled by the DebuggableAttribute.
     * @return The offset from the start of the MSIL code for the method that is executing.
     */
    public int GetILOffset() {
        return OFFSET_UNKNOWN;
    }

    /**
     * Gets the offset from the start of the native just-in-time (JIT)-compiled code for the method that is being executed.
     * The generation of this debugging information is controlled by the DebuggableAttribute class.
     * @return The offset from the start of the JIT-compiled code for the method that is being executed.
     */
    public int GetNativeOffset() {
        return OFFSET_UNKNOWN;
    }
}
