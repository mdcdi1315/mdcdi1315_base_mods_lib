package com.github.mdcdi1315.basemodslib;

import java.lang.annotation.*;

/**
 * Marker annotation interface used for classes to indicate that they can only be used in client-side Minecraft instances. <br />
 * When applied to packages, this annotation and its semantics applies for all the classes of the in question package,
 * unlike many other annotations. <br />
 * Note also that the annotation does not apply for child packages when applied in a package; you must re-declare it for every package.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface ClientOnlyEnvironment { }
