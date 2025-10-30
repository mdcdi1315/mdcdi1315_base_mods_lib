package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.config.ConfigSaveException;
import com.github.mdcdi1315.basemodslib.client.gui.ParentableScreen;
import com.github.mdcdi1315.basemodslib.client.gui.ConfirmationDialogScreen;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigSerializationHelpers;

import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;

/**
 * Provides a screen for modifying configuration file data. <br />
 * The changes can then be saved as required by the user.
 * @param <TCFG> The type of the mod config to save.
 */
public class DefaultConfigurationScreen<TCFG extends IModConfig>
    extends ParentableScreen
{
    private final TCFG cfg;
    private final String mod_id;
    private int tweak_string_len;
    private SettingsModifierList list;
    private final boolean render_comment;
    private final Component comment, tweak_string;
    private Button back_button, apply_changes_button;

    public DefaultConfigurationScreen(TCFG config, String mod_id, Screen parent)
            throws ArgumentException
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.title"), parent);
        ArgumentNullException.ThrowIfNull(config, "config");
        ArgumentNullException.ThrowIfNullOrEmpty(mod_id, "mod_id");
        cfg = config;
        this.mod_id = mod_id;
        tweak_string_len = 0;
        comment = ConfigGuiUtils.ConstructConfigTranslatableString(cfg.GetComment());
        tweak_string = Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.header" , config.GetName());
        render_comment = !comment.getString().isBlank();
    }

    private void OnButtonFiredEvent(Button b)
    {
        if (b == back_button) {
            onClose();
        } else if (b == apply_changes_button) {
            ConfirmationDialogScreen.CreateDialogSuppressSettingParent(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.save_changes_question" , cfg.GetName()).getString(), this::OnDialogCompleted);
        }
    }

    private void OnDialogCompleted(ConfirmationDialogScreen.DialogResult dr)
    {
        if (dr == ConfirmationDialogScreen.DialogResult.YES) {
            try {
                ConfigSerializationHelpers.ApplyConfigData(cfg, list.ReconstructRecord());
                ConfigManager.INSTANCE.SaveConfigurationFile(cfg);
                InformationalDialogScreen.CreateDialog(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.changes_saved_successfully") , this);
            } catch (ConfigSaveException cse) {
                BaseModsLib.LOGGER.info("Cannot save configuration file. An exception occurred while doing that.", cse);
                InformationalDialogScreen.CreateDialog(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.changes_save_failed"), this);
            } catch (Exception ex) {
                BaseModsLib.LOGGER.info("Cannot save configuration file. An exception occurred while reading a configuration value.", ex);
                InformationalDialogScreen.CreateDialog(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.changes_save_failed"), this);
            }
        } else {
            minecraft.setScreen(this);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void init()
    {
        int common_down_y = height - 38;
        if (common_down_y < 0) {
            common_down_y = height;
        }
        int apply_changes_button_x = (width / 2) - 100;
        if (apply_changes_button_x < 0) {
            apply_changes_button_x = 30;
        }
        tweak_string_len = font.width(tweak_string);
        apply_changes_button = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.save_changes_button"), this::OnButtonFiredEvent)
                .bounds(apply_changes_button_x, common_down_y, 90, 20)
                .build();
        back_button = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.config.default_config_screen.exit_button"), this::OnButtonFiredEvent)
                .bounds(apply_changes_button_x + 90 + 10, common_down_y, 90 , 20)
                .build();
        list = new SettingsModifierList(minecraft, width, common_down_y-10, 25 , common_down_y - 30);
        list.CreateFromRecord(mod_id, ConfigSerializationHelpers.GetConfigData(cfg));
        addRenderableWidget(list);
        addRenderableWidget(back_button);
        addRenderableWidget(apply_changes_button);
    }

    public void tick() {
        list.Tick();
        super.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(guiGraphics);

        // Render title

        int string_x_pos = (width / 2) - (tweak_string_len / 2);
        // If somehow our width overflown because the string is too large (or our screen became too small), set position to a reasonable value instead.
        if (string_x_pos < 0) { string_x_pos = 10; }

        guiGraphics.drawString(font, tweak_string, string_x_pos, 7 , 0xFF00FF00);

        if (render_comment && (
                mouseX >= string_x_pos &&
                mouseX < (string_x_pos + tweak_string_len) &&
                mouseY >= 7 &&
                mouseY < (7 + font.lineHeight)
        )) {
            // Create a tooltip for the config file comment, if the config file has one.
            guiGraphics.renderTooltip(font, comment, mouseX , mouseY);
        }

        // Render the rest stuff
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    @MaybeNull
    public Music getBackgroundMusic() {
        return Musics.MENU;
    }
}
