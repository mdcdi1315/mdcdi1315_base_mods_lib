package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;

import java.util.Optional;

/**
 * Provides a base {@link IModInfo} scaffolding class simplifying working with the attribute logic.
 */
public abstract class BaseModInfo
    implements IModInfo
{
    /**
     * Provides the attribute for registering the Mod ID of a
     * mod, if the deriving class has not overridden the {@link #GetModID()} method.
     */
    protected static final String MOD_ID_ATTRIBUTE = "II_ModID";

    private final Dictionary<String, Object> attributes;

    /**
     * Initializes a new instance of the {@link BaseModInfo} class.
     */
    public BaseModInfo()
    {
        this.attributes = new Dictionary<>(16);
    }

    /**
     * Registers a new attribute to this mod information.
     * @param name The name of the new attribute.
     * @param value The value of the new attribute.
     * @throws ArgumentNullException {@code name} and/or {@code value} are {@code null}.
     * @throws ArgumentException The specified attribute with the specified name does already exist.
     */
    protected final void RegisterAttribute(String name, Object value)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(value, "value");
        attributes.Add(name, value);
    }

    /**
     * Ensures that the attributes dictionary occupies as less memory as possible.
     */
    protected final void FinalizeAttributes() { attributes.TrimExcess(); }

    @Override
    public final Optional<Object> GetAttribute(String name)
            throws ArgumentNullException
    {
        ByRefParameter<Object> p = new  ByRefParameter<>();
        if (attributes.TryGetValue(name, p)) {
            return Optional.of(p.Value);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public abstract IModFileSystem GetFileSystem();

    @Override
    public String GetModID() { return (String) attributes.getItem(MOD_ID_ATTRIBUTE); }
}
