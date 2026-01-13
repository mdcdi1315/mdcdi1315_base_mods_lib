package com.github.mdcdi1315.basemodslib.sounds;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.Registries;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeSoundRegistrar
    implements ISoundRegistrar
{
    private DeferredRegister<SoundEvent> SOUND_EVENT_REGISTER;

    public NeoForgeSoundRegistrar(String mod_id) {
        SOUND_EVENT_REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, mod_id);
    }

    @Override
    public void RegisterSoundEvent(SoundEvent event, String name)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(event, "event");
        SOUND_EVENT_REGISTER.register(name, new ElementSupplier<>(event));
    }

    public void RegisterToEventBus(IEventBus event_bus)
    {
        SOUND_EVENT_REGISTER.register(event_bus);
        SOUND_EVENT_REGISTER = null;
    }
}
