package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

/**
 * Defines APIs to determine whether specific features are supported by the common language runtime.
 */
public final class RuntimeFeature
{
    /**
     * Name of the Portable PDB feature.
     */
    public static final String PortablePdb = "PortablePdb";

    /**
     * Indicates that this version of runtime supports default interface method implementations.
     */
    public static final String DefaultImplementationsOfInterfaces = "DefaultImplementationsOfInterfaces";

    /**
     * Indicates that this version of runtime supports the Unmanaged calling convention value.
     */
    public static final String UnmanagedSignatureCallingConvention = "UnmanagedSignatureCallingConvention";

    /**
     * Indicates that this version of runtime supports covariant returns in overrides of methods declared in classes.
     */
    public static final String CovariantReturnsOfClasses = "CovariantReturnsOfClasses";

    /**
     * Represents a runtime feature where types can define ref fields.
     */
    public static final String ByRefFields = "ByRefFields";

    /**
     * Represents a runtime feature where byref-like types can be used in Generic parameters.
     */
    public static final String ByRefLikeGenerics = "ByRefLikeGenerics";

    /**
     * Indicates that this version of runtime supports virtual static members of interfaces.
     */
    public static final String VirtualStaticsInInterfaces = "VirtualStaticsInInterfaces";

    /**
     * Indicates that this version of runtime supports {@code System.IntPtr} and {@code System.UIntPtr} as numeric types.
     */
    public static final String NumericIntPtr = "NumericIntPtr";

    /**
     * Checks whether a certain feature is supported by the Runtime.
     */
    public static boolean IsSupported(String feature)
    {
        return switch (feature)
        {
            case DefaultImplementationsOfInterfaces,
                 CovariantReturnsOfClasses -> true;
            default -> false;
        };
    }

    /**
     * Gets a value that indicates whether the runtime compiles dynamic code.
     * @return {@code true} if the runtime compiles dynamic code; {@code false} if it doesn't compile dynamic code or doesn't know about this property.
     */
    public static boolean IsDynamicCodeCompiled() { return true; }

    /**
     * Gets a value that indicates whether the runtime supports dynamic code.
     * @return {@code true} if the runtime supports dynamic code; {@code false} if it doesn't support dynamic code or doesn't know about this property.
     */
    public static boolean IsDynamicCodeSupported() { return true; }
}
