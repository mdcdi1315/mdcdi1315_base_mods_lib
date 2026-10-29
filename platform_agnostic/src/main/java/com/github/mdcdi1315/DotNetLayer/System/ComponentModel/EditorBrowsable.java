package com.github.mdcdi1315.DotNetLayer.System.ComponentModel;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that a class or member is viewable in an editor.
 * This class cannot be inherited.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = {
        AttributeTargets.Class,
        AttributeTargets.Enum,
        AttributeTargets.Constructor,
        AttributeTargets.Method,
        AttributeTargets.Field,
        AttributeTargets.Interface
})
public @interface EditorBrowsable
{
    /**
     * Gets the browsable state of the property or method.
     * @return An {@link EditorBrowsableState} that is the browsable state of the property or method.
     */
    EditorBrowsableState State() default EditorBrowsableState.Always;
}
