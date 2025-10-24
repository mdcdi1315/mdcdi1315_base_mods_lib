package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Applied to a package-info.java file to indicate that the package is a translated .NET namespace.
 */
@Documented
@Target(value = ElementType.PACKAGE)
@Retention(RetentionPolicy.CLASS)
public @interface PackageIsDotNetNamespace {

}
