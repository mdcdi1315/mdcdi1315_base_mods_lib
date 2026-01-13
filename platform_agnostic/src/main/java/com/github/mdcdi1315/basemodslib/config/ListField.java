package com.github.mdcdi1315.basemodslib.config;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation interface marking a field in a {@link IModConfig} instance that is of type {@link ConfigList}. <br />
 * When fields of such type are declared, this annotation must be specified as well so that class resolving can be efficiently done. <br />
 * If you want to serialize custom elements in a list, you must also register a serialization codec in the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.listsupport.ListElementRegistry} class.
 * @since 1.0.15
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListField
{
    /**
     * Gets the class over single elements of the declared list field.
     * @return The class for each element contained in an {@link ConfigList} instance.
     */
    Class<?> ElementClass();
}
