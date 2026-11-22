package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.basemodslib.mods.information.IModInfo;
import com.github.mdcdi1315.basemodslib.utils.collections.ArrayView;
import com.github.mdcdi1315.basemodslib.mods.information.DeferredFile;
import com.github.mdcdi1315.basemodslib.utils.collections.codec_support.ListCodec;

import com.google.gson.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.Optional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public final class LicenseEntriesLoader
{
    private LicenseEntriesLoader() { }

    @NotNull
    public static CompletableFuture<IEnumerable<ModLicenses>> GetLicenseEntriesAsync()
    { return CompletableFuture.supplyAsync(LicenseEntriesLoader::LoadLicenseEntries); }

    private static IEnumerable<ModLicenses> LoadLicenseEntries()
    {
        var c = BaseModsLib.GetModInstanceCollection();

        Gson gson = new GsonBuilder()
                .setStrictness(Strictness.LENIENT)
                .create();

        List<ModLicenses> list = new List<>(c.getCount() + 1);

        LoadLicenseInfoFor(BaseModsLib.MOD_ID, gson, list::Add);

        try (var mods = c.GetEnumerator())
        {
            while (mods.MoveNext())
            {
                LoadLicenseInfoFor(mods.getCurrent().GetModId(), gson, list::Add);
            }
        }

        list.TrimExcess();

        return list;
    }

    private static String LoadLicenseFileContents(DeferredFile file)
            throws IOException
    {
        try (var reader = file.OpenReader(StandardCharsets.UTF_8))
        {
            return reader.readAllAsString();
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void LoadLicenseInfoFor(String mod_id, Gson gson, Action1<ModLicenses> entry_adder)
    {
        var res_lookup = BaseModsLib.GetModInfo(mod_id);
        if (res_lookup == null) { return; }
        var friendly_name = res_lookup.GetAttribute(IModInfo.FRIENDLY_NAME).map(Extensions::<String>TypeCast).orElse(mod_id);
        var fs = res_lookup.GetFileSystem();
        DeferredFile json_file = fs.TryGetFile("licenses.json");
        try
        {
            if (json_file == null)
            {
                // Attempt defaulted lookup.
                var lf = StringUtils.Concat("LICENSE_", friendly_name);
                var lic_file = fs.TryFileWithAlternatives(
                        "LICENSE",
                        "LICENSE.txt",
                        lf,
                        StringUtils.Concat(lf, ".txt")
                );
                if (lic_file != null)
                {
                    entry_adder.action(
                            new ModLicenses(
                                    mod_id,
                                    friendly_name,
                                    new ArrayView<>(
                                            new LicenseEntry[] {
                                                    new LicenseEntry(
                                                            LoadLicenseFileContents(lic_file),
                                                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.lic_entry.default", friendly_name),
                                                            null
                                                    )
                                            }
                                    )
                            )
                    );
                }
            } else {
                JsonElement element;
                try (var reader = json_file.OpenReader(StandardCharsets.UTF_8))
                {
                    element = gson.fromJson(reader, JsonElement.class);
                }
                var dr = LicenseEntryRef.CODEC.parse(JsonOps.INSTANCE, element);
                if (dr.isError()) {
                    BaseModsLib.LOGGER.warn("[BML]: Failure reading license file {}:\n{}", json_file.GetPath(), dr.error().get().message());
                } else {
                    var list = dr.result().get();
                    List<LicenseEntry> license_entries = new List<>(list.getCount());

                    try (var en = list.GetEnumerator())
                    {
                        DeferredFile lic_file;
                        LicenseEntryRef reference;
                        while (en.MoveNext())
                        {
                            reference = en.getCurrent();
                            lic_file = fs.TryGetFile(reference.license_file());
                            if (lic_file == null) {
                                BaseModsLib.LOGGER.warn("[BML]: Could not find license file {}", reference.license_file());
                            } else {
                                try {
                                    license_entries.Add(new LicenseEntry(LoadLicenseFileContents(lic_file), reference.button_text(), reference.title_text.orElse(null)));
                                } catch (IOException e) {
                                    BaseModsLib.LOGGER.warn("[BML]: Could not load license file {}:\n", reference.license_file(), e);
                                }
                            }
                        }
                    }

                    license_entries.TrimExcess();
                    entry_adder.action(new ModLicenses(mod_id, friendly_name, license_entries));
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
