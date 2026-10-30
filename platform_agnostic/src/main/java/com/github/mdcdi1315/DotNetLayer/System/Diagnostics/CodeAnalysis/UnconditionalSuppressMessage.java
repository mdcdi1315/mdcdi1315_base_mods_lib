package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Suppresses reporting of a specific rule violation, allowing multiple suppressions on a single code artifact.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = AttributeTargets.All, AllowMultiple = true)
@Repeatable(UnconditionalSuppressMessage.RepeatableDefinition.class)
public @interface UnconditionalSuppressMessage
{
    /// <summary>
    /// Gets the category identifying the classification of the attribute.
    /// </summary>
    /// <remarks>
    /// The <see cref="Category"/> property describes the tool or tool analysis category
    /// for which a message suppression attribute applies.
    /// </remarks>
    @NotNull
    String Category();

    /// <summary>
    /// Gets the identifier of the analysis tool rule to be suppressed.
    /// </summary>
    /// <remarks>
    /// Concatenated together, the <see cref="Category"/> and <see cref="CheckId"/>
    /// properties form a unique check identifier.
    /// </remarks>
    @NotNull
    String CheckId();

    /// <summary>
    /// Gets or sets the scope of the code that is relevant for the attribute.
    /// </summary>
    /// <remarks>
    /// The Scope property is an optional argument that specifies the metadata scope for which
    /// the attribute is relevant.
    /// </remarks>
    @AllowNull
    String Scope() default "";

    /// <summary>
    /// Gets or sets a fully qualified path that represents the target of the attribute.
    /// </summary>
    /// <remarks>
    /// The <see cref="Target"/> property is an optional argument identifying the analysis target
    /// of the attribute. An example value is "System.IO.Stream.ctor():System.Void".
    /// Because it is fully qualified, it can be long, particularly for targets such as parameters.
    /// The analysis tool user interface should be capable of automatically formatting the parameter.
    /// </remarks>
    @AllowNull
    String Target() default "";

    /// <summary>
    /// Gets or sets an optional argument expanding on exclusion criteria.
    /// </summary>
    /// <remarks>
    /// The <see cref="MessageId "/> property is an optional argument that specifies additional
    /// exclusion where the literal metadata target is not sufficiently precise. For example,
    /// the <see cref="UnconditionalSuppressMessageAttribute"/> cannot be applied within a method,
    /// and it may be desirable to suppress a violation against a statement in the method that will
    /// give a rule violation, but not against all statements in the method.
    /// </remarks>
    @AllowNull
    String MessageId() default "";

    /// <summary>
    /// Gets or sets the justification for suppressing the code analysis message.
    /// </summary>
    @AllowNull
    String Justification() default "";

    @Documented
    @Retention(RetentionPolicy.CLASS)
    @interface RepeatableDefinition
    {
        UnconditionalSuppressMessage[] value();
    }
}
