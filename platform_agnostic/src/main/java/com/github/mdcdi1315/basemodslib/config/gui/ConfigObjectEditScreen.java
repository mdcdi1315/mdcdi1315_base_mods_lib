package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.Record;
import com.github.mdcdi1315.basemodslib.client.gui.ParentableScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;

// This is not completed yet!
final class ConfigObjectEditScreen
    extends ParentableScreen
{
    private Button back_button;
    private final Record cfg_record;
    private SettingsModifierList list;
    private final String[] title_strings;
    private final int[] title_string_lengths;

    public ConfigObjectEditScreen(Component title, @MaybeNull Screen parent, String config_object_name, String cfg_file_name, Record value)
    {
        super(title, parent);
        title_strings = new String[] {
                String.format("Configuration tweaker for %s config file" , cfg_file_name),
                String.format("Modifying %s object", config_object_name)
        };
        title_string_lengths = new int[2];
        back_button = null;
        cfg_record = value;
    }

    private void OnButtonFiredEvent(Button b)
    {
        if (b == back_button) {
            onClose();
        }
    }

    public void init()
    {
        int common_down_y = height - 38;
        if (common_down_y < 0) {
            common_down_y = height;
        }
        int back_button_x = (width / 2) - 45;
        if (back_button_x < 0) {
            back_button_x = 30;
        }
        for (int I = 0; I < title_strings.length; I++) {
            title_string_lengths[I] = font.width(title_strings[I]);
        }
        back_button = Button.builder(Component.literal("Exit"), this::OnButtonFiredEvent)
                .bounds(back_button_x, common_down_y, 90 , 20)
                .build();
        list = new SettingsModifierList(minecraft, width, common_down_y-10, 17 + (title_strings.length * font.lineHeight));
        list.CreateFromRecord("", cfg_record);
        addRenderableWidget(list);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(guiGraphics, mouseX , mouseY , partialTick);

        // Render title

        for (int I = 0, title_y = 7; I < title_strings.length; I++, title_y += font.lineHeight)
        {
            int string_x_pos = (width / 2) - (title_string_lengths[I] / 2);
            // If somehow our width overflown because the string is too large (or our screen became too small), set position to a reasonable value instead.
            if (string_x_pos < 0) { string_x_pos = 10; }

            guiGraphics.drawString(font, title_strings[I], string_x_pos, title_y , 0xFF00FF00);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
