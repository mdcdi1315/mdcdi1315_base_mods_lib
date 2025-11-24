package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.gameplay.*;

import net.minecraft.stats.Stat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class)
public abstract class PlayerMixin
{
    @Inject(method = "killedEntity", at = @At("RETURN"))
    private void OnEntityKilled(ServerLevel level, LivingEntity entity, CallbackInfoReturnable<Boolean> cir)
    {
        if (!level.isClientSide) {
            BaseModsLib.GetEventsManager().FireEvent(new PlayerKilledEntityEvent((Player)((Object)this), entity));
        }
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void OnKilled(DamageSource cause, CallbackInfo ci)
    {
        Player p = (Player)((Object)this);
        if (!p.level().isClientSide) {
            BaseModsLib.GetEventsManager().FireEvent(new PlayerWasKilledEvent(p, cause));
        }
    }

    @Inject(method = "respawn", at = @At("HEAD"))
    private void OnRespawn(CallbackInfo ci) {
        BaseModsLib.GetEventsManager().FireEvent(new PlayerRequestedRespawnEvent((Player)((Object)this)));
    }

    @Inject(method = "awardStat(Lnet/minecraft/stats/Stat;I)V", at = @At("HEAD"))
    private void OnAwardedStat(Stat<?> stat, int increment, CallbackInfo ci) {
        BaseModsLib.GetEventsManager().FireEvent(new PlayerWillBeRewardedWithStatEvent((Player)((Object)this), stat, increment));
    }
}
