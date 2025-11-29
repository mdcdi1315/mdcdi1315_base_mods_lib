package com.github.mdcdi1315.basemodslib.integration.modmenu;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public final class ModMenuLazyLoadedMap
    implements Map<String, ConfigScreenFactory<?>>
{
    private final HashMap<String, ConfigScreenFactory<?>> translated_and_saved;
    private Func1<IEnumerable<Pair<String , ConfigurationScreenFactory<?>>>> provider;

    public ModMenuLazyLoadedMap() {
        provider = BaseModsLibClient::GetConfigurationScreens;
        translated_and_saved = new HashMap<>(10);
        BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class , this::OnFinalization);
    }

    private void LoadData()
    {
        if (provider == null) { return; }
        translated_and_saved.clear();
        var en = provider.function().GetEnumerator();
        try {
            Pair<String, ConfigurationScreenFactory<?>> pair;
            while (en.MoveNext()) {
                pair = en.getCurrent();
                translated_and_saved.put(pair.first(), new ConfigurationScreenFactoryToConfigScreenFactory<>(pair.second()));
            }
        } finally {
            en.Dispose();
        }
    }

    private void OnFinalization(ModLoadingCompleteEvent event) {
        LoadData();
        provider = null;
    }

    @Override
    public int size() {
        LoadData();
        return translated_and_saved.size();
    }

    @Override
    public boolean isEmpty() {
        LoadData();
        return translated_and_saved.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return translated_and_saved.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return translated_and_saved.containsValue(value);
    }

    @Override
    public ConfigScreenFactory<?> get(Object key) {
        LoadData();
        return translated_and_saved.get(key);
    }

    @Override
    public @Nullable ConfigScreenFactory<?> put(String key, ConfigScreenFactory<?> value) {
        return translated_and_saved.put(key , value);
    }

    @Override
    public ConfigScreenFactory<?> remove(Object key) {
        return translated_and_saved.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends ConfigScreenFactory<?>> m) {
        translated_and_saved.putAll(m);
    }

    @Override
    public void clear() {
        translated_and_saved.clear();
    }

    @Override
    public @NotNull Set<String> keySet() {
        LoadData();
        return translated_and_saved.keySet();
    }

    @Override
    public @NotNull Collection<ConfigScreenFactory<?>> values() {
        LoadData();
        return translated_and_saved.values();
    }

    @Override
    public @NotNull Set<Entry<String, ConfigScreenFactory<?>>> entrySet() {
        LoadData();
        return translated_and_saved.entrySet();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super ConfigScreenFactory<?>> action) {
        LoadData();
        translated_and_saved.forEach(action);
    }

    public int hashCode() {
        return translated_and_saved.hashCode();
    }
}
