package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.eventapi.gameplay.*;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class)
public abstract class PlayerMixin
{
    @Inject(method = "killedEntity", at = @At("RETURN"))
    private void OnEntityKilled(ServerLevel level, LivingEntity entity, DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        if (!level.isClientSide()) {
            EventManager.FireEventSafe(new PlayerKilledEntityEvent((Player)((Object)this), entity));
        }
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void OnKilled(DamageSource cause, CallbackInfo ci)
    {
        Player p = (Player)((Object)this);
        if (!p.level().isClientSide()) {
            EventManager.FireEventSafe(new PlayerWasKilledEvent(p, cause));
        }
    }
}
