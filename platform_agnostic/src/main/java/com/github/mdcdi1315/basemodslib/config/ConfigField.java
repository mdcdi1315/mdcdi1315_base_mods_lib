package com.github.mdcdi1315.basemodslib.config;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation interface for marking fields in a {@link IModConfig} class as fields that should be de/serialized. <br />
 * Additional settings for such fields are provided below.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField
{
    /**
     * Returns the name of the field under which this class field should be represented in the serialized configuration file.
     * @return The name of the serialized field.
     */
    String field_name() default "";

    /**
     * Returns the comment value for this field. This is to assist modpack developers to modify the value appropriately.
     * @return The value of the comment associated with this field.
     */
    String comment() default "";
}
