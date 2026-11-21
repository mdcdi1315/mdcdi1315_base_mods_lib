package com.github.mdcdi1315.basemodslib.mods.reporting.messages;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import net.minecraft.network.chat.Component;

/**
 * Provides a way for mods to indicate that they recommend installing a specific mod alongside their mod.
 */
public class ModRecommendedMessage
    extends RecommendationalMessage
{
    private final String reporting_mod_id;
    private final String recommended_mod_id;

    /**
     * Initializes a new instance of the {@link ModRecommendedMessage} class.
     * @param reporting_mod_id The ID of the mod reporting this message.
     * @param recommended_mod_id The ID of the mod that the {@code reporting_mod_id} recommends.
     * @throws ArgumentNullException {@code reporting_mod_id} and/or {@code recommended_mod_id} are {@code null}.
     */
    public ModRecommendedMessage(String reporting_mod_id, String recommended_mod_id)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(reporting_mod_id, "reporting_mod_id");
        ArgumentNullException.ThrowIfNull(recommended_mod_id, "recommended_mod_id");
        super(Component.translatable("mdcdi1315_base_mods_lib.mod_recommended", reporting_mod_id, recommended_mod_id));
        this.reporting_mod_id = reporting_mod_id;
        this.recommended_mod_id = recommended_mod_id;
        RegisterAttribute("REPORTING_MOD_ID", reporting_mod_id);
        RegisterAttribute("RECOMMENDED_MOD_ID", recommended_mod_id);
        FinalizeAttributes();
    }

    /**
     * Gets the ID of the mod reporting this message.
     * @return The ID of the mod reporting this message.
     */
    @Pure
    @NotNull
    public final String ReportingModID() { return this.reporting_mod_id; }

    /**
     * Gets the ID of the mod that the {@link #ReportingModID()} recommends.
     * @return The ID of the mod that the {@link #ReportingModID()} recommends.
     */
    @Pure
    @NotNull
    public final String RecommendedModID() { return this.recommended_mod_id; }
}
