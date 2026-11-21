package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.BaseAboutScreen;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;

import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class LicensesScreen
    extends BaseAboutScreen
{
    private boolean licenses_appended;
    private final StringWidget header;
    private final CompletableFuture<IEnumerable<KeyValuePair<String, IEnumerable<LicenseEntry>>>> licenses_action;

    public LicensesScreen(Screen parent)
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.title"), parent);
        licenses_action = LicenseEntriesLoader.GetLicenseEntriesAsync();
        header = new StringWidget(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.fetching"), minecraft.font);
        SetWorkAreaSpacing(20);
        AddWorkAreaWidget(header);
    }

    private IEnumerable<KeyValuePair<String, IEnumerable<LicenseEntry>>> UnwrapResult()
    {
        try {
            return licenses_action.get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private void AddButtonsAndArrangeLicenses()
    {
        if (licenses_action.isCompletedExceptionally() || licenses_action.isCancelled())
        {
            header.setMessage(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.failure_fetching"));
            if (licenses_action.isCancelled()) {
                BaseModsLib.LOGGER.error("[BML]: Failure fetching license info: Task was manually cancelled.");
            } else {
                BaseModsLib.LOGGER.error("[BML]: Failure fetching license info", licenses_action.exceptionNow());
            }
        }
        else
        {
            header.setMessage(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.header"));
            try (var en = CollectionManipulations.GetEnumeratorSafe(UnwrapResult()))
            {
                KeyValuePair<String, IEnumerable<LicenseEntry>> mod_entry;
                while (en.MoveNext())
                {
                    mod_entry = en.getCurrent();
                    AddWorkAreaWidget(new StringWidget(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_licenses.entry.header", mod_entry.getKey()), minecraft.font));
                    try (var entry_enum = mod_entry.getValue().GetEnumerator())
                    {
                        LicenseEntry lic_entry;
                        while (entry_enum.MoveNext())
                        {
                            lic_entry = entry_enum.getCurrent();
                            AddWorkAreaWidget(
                                    Button.builder(
                                            lic_entry.license_button_text(),
                                            new LicenseScreenButtonHandler(this, lic_entry)
                                    )
                                            .width(BoundToMaxOfWorkArea(minecraft.font.width(lic_entry.license_button_text()) + 10))
                                            .build()
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        if (licenses_action.isDone() && !licenses_appended)
        {
            licenses_appended = true;
            AddButtonsAndArrangeLicenses();
            HardReinitializeWorkArea();
        }
    }

    @SuppressWarnings("NullableProblems")
    private record LicenseScreenButtonHandler(LicensesScreen parent, LicenseEntry entry)
        implements Button.OnPress
    {
        @Override
        public void onPress(Button button)
        {
            parent.minecraft.setScreen(
                    new LicenseViewerScreen(parent, entry)
            );
        }
    }
}
