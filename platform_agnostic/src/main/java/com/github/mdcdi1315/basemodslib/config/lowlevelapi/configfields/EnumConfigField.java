package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for enumeration-backed fields.
 * <h4>Notes about using this class</h4>
 * This class is not registered in the {@link ConfigFieldRegistry} because the enum type must be matched exactly. <br />
 * As such, you need to declare any enumeration types you use in your configs into the {@link ConfigFieldRegistry} as follows: <br />
 * <pre>{@code
 *     public enum AnEnumerationType {
 *         Field1,
 *         Field2,
 *         Field3
 *     }
 *
 *     // In your mod's initializer....
 *
 *     public void Initialize() {
 *         ConfigFieldRegistry.RegisterConfigFieldCreator(AnEnumerationType.class, EnumConfigField::new);
 *     }
 * }</pre>
 * @since 1.0.15
 */
public final class EnumConfigField<T extends Enum<T>>
    extends BaseConfigField<T>
{
    private final T value;

    /**
     * Constructs a new instance of the {@link EnumConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param value The enumeration case value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} and/or {@code value} are {@code null}.
     */
    public EnumConfigField(String name, String comment, T value, Iterable<IConfigFieldConstraint<T>> constraints) throws ArgumentNullException {
        super(name, comment, constraints);
        ArgumentNullException.ThrowIfNull(value, "value");
        this.value = value;
    }

    @Override
    public T GetValue() { return value; }
}
