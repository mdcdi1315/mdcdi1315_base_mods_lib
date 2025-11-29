package com.github.mdcdi1315.basemodslib.entity.effect;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.effect.MobEffect;

/**
 * Provides information for registering a new mob effect to Minecraft.
 * @param effect_getter The function that upon invoking, it returns the mob effect to register.
 * @since 1.0.12
 */
public record MobEffectRegistrationInfo(
        @NotNull Func1<MobEffect> effect_getter
) {
    /**
     * Creates a new instance of the {@link MobEffectRegistrationInfo} class.
     * @param effect_getter The function that upon invoking, it returns the mob effect to register.
     * @throws ArgumentNullException {@code effect_getter} is {@code null}.
     */
    public MobEffectRegistrationInfo {
        ArgumentNullException.ThrowIfNull(effect_getter, "effect_getter");
    }
}
