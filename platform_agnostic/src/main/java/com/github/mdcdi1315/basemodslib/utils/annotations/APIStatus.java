package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Indicates the status of an annotated API. <br />
 * When applied to a package, it applies to all the classes contained in that package. <br />
 * When applied to a module, it applies only to the module itself.
 * @since 1.0.39
 */
@Inherited
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.METHOD,
        ElementType.CONSTRUCTOR,
        ElementType.PACKAGE,
        ElementType.MODULE,
        ElementType.ANNOTATION_TYPE,
        ElementType.TYPE
})
public @interface APIStatus
{
    /**
     * Provides constants that indicate the API's integrity status.
     */
    enum Status
    {
        /**
         * The API is experimental, may be changed or even be removed.
         */
        EXPERIMENTAL,
        /**
         * The API is defined for internal support, and should not be used
         * by external code, because it may change without notice.
         */
        INTERNAL,
        /**
         * The API is stable and can be used. <br />
         * No breaking changes can happen to the API.
         */
        STABLE,
        /**
         * The API is to be removed soon.
         */
        DEPRECATED,
        /**
         * The API is to be removed soon, but it cannot be even used.
         */
        DEPRECATED_UNUSABLE
    }

    /**
     * Indicates the version of the library the API was introduced, or when the API became deprecated.
     * @return The version of the library the API was introduced or deprecated.
     */
    String Since() default "1.0.0";

    /**
     * Indicates the status of the API.
     * @return The status of the API.
     */
    Status Status();
}
