package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Annotation indicating that the feature/API does depend on Minecraft classes in order to function. <br />
 * When applied to a package, it applies to all the classes of that package.
 * @since 1.0.39
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.39")
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface MinecraftDependentFeature
{
    /**
     * An array of Minecraft versions where the feature/API is known to be well-supported.
     * @return The Minecraft versions where the feature/API is supported.
     * @apiNote If this is defined as an empty array, it indicates that the feature
     * is supported in all Minecraft versions where the BML is supported.
     */
    String[] SupportedVersions() default {};
}
