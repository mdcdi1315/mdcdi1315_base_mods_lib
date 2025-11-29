package com.github.mdcdi1315.basemodslib.alchemy;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.item.alchemy.Potion;

/**
 * Provides information for registering new potions to Minecraft.
 * @param potion_getter The function that upon invoking, it returns the potion to register.
 */
public record PotionRegistrationInfo(
        @NotNull Func1<Potion> potion_getter
) {
    /**
     * Creates a new instance of the {@link PotionRegistrationInfo} class.
     * @param potion_getter The function that upon invoking, it returns the potion to register.
     * @throws ArgumentNullException {@code potion_getter} is {@code null}.
     */
    public PotionRegistrationInfo {
        ArgumentNullException.ThrowIfNull(potion_getter, "potion_getter");
    }
}
