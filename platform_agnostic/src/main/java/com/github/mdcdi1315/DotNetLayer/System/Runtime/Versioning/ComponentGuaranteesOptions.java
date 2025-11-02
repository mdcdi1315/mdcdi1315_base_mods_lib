package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Flags;

/**
 * Describes the compatibility guarantee of a component, type, or type member that may span multiple versions. <br /> <br />
 *
 * This enumeration supports a bitwise combination of its member values.
 */
@Flags
public enum ComponentGuaranteesOptions
{
    /**
     * The developer does not guarantee compatibility across versions.
     * Consumers of the component, type, or member can expect future versions to break the existing client.
     */
    None(0),
    /**
     * The developer promises multi-version exchange compatibility for the type.
     * Consumers of the type can expect compatibility across future versions and can use the type in all their interfaces.
     * Versioning problems cannot be fixed by side-by-side execution.
     */
    Exchange(1),
    /**
     * The developer promises stable compatibility across versions.
     * Consumers of the type can expect that future versions will not break the existing client.
     * However, if they do and if the client has not used the type in its interfaces, side-by-side execution may fix the problem.
     */
    Stable(2),
    /**
     * The component has been tested to work when more than one version of the assembly is loaded into the same application domain.
     * Future versions can break compatibility. However, when such breaking changes are made, the old version is not modified but continues to exist alongside the new version.
     */
    SideBySide(4);

    private final int value__;

    ComponentGuaranteesOptions(int value) {
        value__ = value;
    }
}
