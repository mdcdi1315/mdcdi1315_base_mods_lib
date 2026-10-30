package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Annotation indicating that this class is not meant to be extended by external consumers of the library.
 * @since 1.0.39
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.39")
public @interface NonExtendable { }
