package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.ArrayView;
import com.github.mdcdi1315.basemodslib.utils.collections.codec_support.ListCodec;

import com.google.gson.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class LicenseEntriesLoader
{
    private LicenseEntriesLoader() { }

    @NotNull
    public static CompletableFuture<IEnumerable<KeyValuePair<String, IEnumerable<LicenseEntry>>>> GetLicenseEntriesAsync()
    { return CompletableFuture.supplyAsync(LicenseEntriesLoader::LoadLicenseEntries); }

    private static IEnumerable<KeyValuePair<String, IEnumerable<LicenseEntry>>> LoadLicenseEntries()
    {
        Dictionary<String, IEnumerable<LicenseEntry>> dictionary = new Dictionary<>();

        Gson gson = new GsonBuilder()
                .setStrictness(Strictness.LENIENT)
                .create();

        LoadLicenseInfoFor(BaseModsLib.MOD_ID, gson, dictionary);

        try (var mods = BaseModsLib.GetModInstanceCollection().GetEnumerator())
        {
            while (mods.MoveNext())
            {
                LoadLicenseInfoFor(mods.getCurrent().GetModId(), gson, dictionary);
            }
        }

        return dictionary;
    }

    private static String LoadLicenseFileContents(Path path)
            throws IOException
    {
        try (var reader = Files.newBufferedReader(path))
        {
            return reader.readAllAsString();
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void LoadLicenseInfoFor(String mod_id, Gson gson, Dictionary<String, IEnumerable<LicenseEntry>> dictionary)
    {
        var res_lookup = BaseModsLib.GetModResourceLookup(mod_id);
        if (res_lookup == null) { return; }
        try (res_lookup)
        {
            Path json_file = res_lookup.GetResource("licenses.json");
            if (json_file == null)
            {
                // Attempt defaulted lookup.
                Path lic_file = res_lookup.GetResource("LICENSE");
                if (lic_file == null) { lic_file = res_lookup.GetResource("LICENSE.txt"); }
                if (lic_file != null)
                {
                    dictionary.Add(mod_id, new ArrayView<>(new LicenseEntry[] { new LicenseEntry(LoadLicenseFileContents(lic_file), Component.literal(mod_id + " license"), null) }));
                }
            } else {
                JsonElement element;
                try (var reader = Files.newBufferedReader(json_file))
                {
                    element = gson.fromJson(reader, JsonElement.class);
                }
                var dr = LicenseEntryRef.CODEC.parse(JsonOps.INSTANCE, element);
                if (dr.isError()) {
                    BaseModsLib.LOGGER.warn("[BML]: Failure reading license file {}:\n{}", json_file, dr.error().get().message());
                } else {
                    var list = dr.result().get();
                    List<LicenseEntry> license_entries = new List<>(list.getCount());

                    try (var en = list.GetEnumerator())
                    {
                        Path path_to_lic;
                        LicenseEntryRef reference;
                        while (en.MoveNext())
                        {
                            reference = en.getCurrent();
                            path_to_lic = res_lookup.GetResource(reference.license_file());
                            if (path_to_lic == null) {
                                BaseModsLib.LOGGER.warn("[BML]: Could not find license file {}", reference.license_file());
                            } else {
                                try {
                                    license_entries.Add(new LicenseEntry(LoadLicenseFileContents(path_to_lic), reference.button_text(), reference.title_text.orElse(null)));
                                } catch (IOException e) {
                                    BaseModsLib.LOGGER.warn("[BML]: Could not load license file {}:\n", reference.license_file(), e);
                                }
                            }
                        }
                    }

                    license_entries.TrimExcess();
                    dictionary.Add(mod_id, license_entries);
                }
            }
        } catch (IOException e) {
            BaseModsLib.LOGGER.warn("[BML]: Failure reading license data for mod {}: {}", mod_id, e);
        } catch (JsonSyntaxException e) {
            BaseModsLib.LOGGER.warn("[BML]: Failure reading license data file for mod {}: {}", mod_id, e);
        }
    }

    private record LicenseEntryRef(String license_file, Component button_text, Optional<Component> title_text)
    {
        public static Codec<List<LicenseEntryRef>> CODEC = new ListCodec<>(
                CodecUtils.CreateCodecDirect(
                        Codec.STRING.fieldOf("license_file").forGetter(LicenseEntryRef::license_file),
                        ComponentSerialization.CODEC.fieldOf("license_button_text").forGetter(LicenseEntryRef::button_text),
                        ComponentSerialization.CODEC.optionalFieldOf("license_title_screen_text").forGetter(LicenseEntryRef::title_text),
                        LicenseEntryRef::new
                )
        );
    }
}
