package com.github.mdcdi1315.basemodslib.mods.reporting;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * Provides a message that can be reported.
 */
public interface IMessage
{
    /**
     * Gets a {@link Component} that provides the message to be reported.
     * @return The (possibly translated) message to report.
     */
    @NotNull
    Component GetMessage();

    /**
     * Gets an attribute of the specified name, and returns it's value.
     * @param name The name of the attribute to retrieve.
     * @return The attribute value for {@code name}, if exists; otherwise, {@code false}.
     * @throws ArgumentNullException {@code name} is {@code null}.
     * @apiNote Note that message attributes are case-sensitive; for example:
     * <pre>{@code
     * // Let's assume two attributes, MOD_NAME and mod_name:
     * message.GetAttribute("MOD_NAME") -> "mod"
     * message.GetAttribute("mod_name") -> "Your Mod!"
     * }</pre>
     */
    @NotNull
    Optional<Object> GetAttribute(String name) throws ArgumentNullException;

    /**
     * Gets all the keys defined as attributes in this instance.
     * @return The keys that providing to the {@link #GetAttribute(String)} method will yield valid values.
     */
    @NotNull
    IEnumerable<String> GetAttributeKeys();

    /**
     * Gets a URL that further describes this message and what the users can do about it.
     * @return A valid URL to a website describing in depth this message.
     * @apiNote This method should not return, under any circumstance, {@code null}. <br />
     * The empty URL is indicated by using the empty (&quot;&quot;) string.
     */
    @NotNull
    default String GetAssociatedIssueURL() { return StringUtils.Empty; }

    /**
     * Gets a {@link MessageSeverity} that indicates the severity of this message.
     * @return A {@link MessageSeverity} indicating the severity of this message.
     * @apiNote The default value returned by this method is {@link MessageSeverity#INFO}.
     */
    @NotNull
    default MessageSeverity GetSeverity() { return MessageSeverity.INFO; }
}
