package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Declares that the annotated method was in .NET an explicit
 * interface declaration brought in by an interface class.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Repeatable(ExplicitInterfaceDeclaration.RepeatableDefinition.class)
public @interface ExplicitInterfaceDeclaration
{
    /**
     * The interface that declares the method that must be implemented.
     * @return The interface class value.
     */
    Class<?> value();

    /**
     * Defines whether the annotated method should be privately implemented, but that is not possible due to Java's restrictions.
     * @return Whether the annotated method should be privately implemented.
     */
    boolean ShouldBePrivate() default false;

    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @interface RepeatableDefinition { ExplicitInterfaceDeclaration[] value(); }
}
