/**
 * Provides the Constraints API subsystem for the default de/serialization mechanism.
 * <h3>What is the Constraints Subsystem?</h3>
 *
 * The Constraints Subsystem defines a series of classes to specify that a configuration
 * field must have a specified value - or being in a range of values if so required. <br />
 * To implement such support, the Constraints API provides the support in three parts: <br />
 * The declaration side.
 * Declarations are specified directly to fields using annotations which are marked with the {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.ModConfigConstraint} annotation.
 * These are applied to configuration fields marked with the {@link com.github.mdcdi1315.basemodslib.config.ConfigField} annotation. <br />
 * The code side.
 * For the constraints to actually take effect, they do need executable pieces. <br />
 * Additionally, validation for their application is also performed.  <br />
 * This is represented by implementations of the {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint} interface. <br />
 * The registration side.
 * For converting an annotation to a {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint} instance,
 * the {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.ConfigFieldConstraintRegistry} handles the mapping from that
 * specific annotation to a concrete {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint} instance. <br /> <br />
 *
 * The Constraints API is used together with the {@link com.github.mdcdi1315.basemodslib.config.reflect.ConfigCodec} class, which it does provide the default de/serialization mechanism. <br />
 * Finally, the API provides some built-in constraints and their implementations that are implicitly registered to the
 * {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.ConfigFieldConstraintRegistry}, which can be found
 * at the {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided} package.
 * @since 1.0.26
 */
package com.github.mdcdi1315.basemodslib.config.reflect.constraints;