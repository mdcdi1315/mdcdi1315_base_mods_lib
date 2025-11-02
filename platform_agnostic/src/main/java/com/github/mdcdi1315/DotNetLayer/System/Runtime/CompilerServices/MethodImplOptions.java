package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.System.Flags;

/**
 * Specifies constants that define the details of how a method is implemented. <br /> <br />
 *
 * This enumeration supports a bitwise combination of its member values.
 */
@Flags
public enum MethodImplOptions
{
    /**
     * The method is implemented in unmanaged code.
     */
    Unmanaged(4),
    /**
     * The method cannot be inlined.
     * Inlining is an optimization by which a method call is replaced with the method body.
     */
    NoInlining(8),
    /**
     * The method is declared, but its implementation is provided elsewhere.
     */
    ForwardRef(16),
    /**
     * The method can be executed by only one thread at a time.
     * Static methods lock on the type, whereas instance methods lock on the instance.
     * Only one thread can execute in any of the instance functions, and only one thread can execute in any of a class's static functions.
     */
    Synchronized(32),
    /**
     * The method is not optimized by the just-in-time (JIT) compiler or by native code generation (see <a href="https://learn.microsoft.com/en-us/dotnet/framework/tools/ngen-exe-native-image-generator">Ngen.exe</a>) when debugging possible code generation problems.
     */
    NoOptimization(64),
    /**
     * The method signature is exported exactly as declared.
     */
    PreserveSig(128),
    /**
     * The method should be inlined if possible. <br /> <br />
     *
     * Unnecessary use of this attribute can reduce performance. The attribute might cause implementation limits to be encountered that will result in slower generated code. Always measure performance to ensure it's helpful to apply this attribute.
     */
    AggressiveInlining(256),
    /**
     * The method contains code that should always be optimized for performance. <br /> <br />
     *
     * It's rarely appropriate to use this attribute. Methods that apply this attribute bypass the first tier of tiered compilation and therefore don't benefit from optimizations that rely on tiered compilation. Those optimizations include dynamic PGO and optimizations based on initialized classes. Use of this attribute might also increase memory use. Always measure performance to ensure it's helpful to apply this attribute.
     */
    AggressiveOptimization(512),
    /**
     * The call is internal, that is, it calls a method that's implemented within the common language runtime.
     */
    InternalCall(4096);

    private final int value__;

    MethodImplOptions(int value) {
        value__ = value;
    }
}
