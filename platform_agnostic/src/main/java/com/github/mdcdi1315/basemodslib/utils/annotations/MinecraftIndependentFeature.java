package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Annotation indicating that the feature/API does not depend on Minecraft classes in order to function. <br />
 * When applied to a package, it applies to all the classes of that package.
 * @since 1.0.39
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.39")
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface MinecraftIndependentFeature { }
