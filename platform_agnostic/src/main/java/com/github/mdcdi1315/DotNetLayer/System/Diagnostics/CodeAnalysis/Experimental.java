package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

/**
 * Indicates that an API is experimental and it may change in the future.
 */
@Attribute
@AttributeUsage({
        AttributeTargets.Class,
        AttributeTargets.Constructor,
        AttributeTargets.Enum,
        AttributeTargets.Field,
        AttributeTargets.Interface,
        AttributeTargets.Method
})
public @interface Experimental {

    /**
     * Gets the ID that the compiler will use when reporting a use of the API the attribute applies to.
     * @return The unique diagnostic ID.
     */
    String DiagnosticId();

    /**
     * Gets or sets the URL for corresponding documentation.
     * The API accepts a format string instead of an actual URL, creating a generic URL that includes the diagnostic ID.
     * @return The URL format.
     */
    String UrlFormat();
}
