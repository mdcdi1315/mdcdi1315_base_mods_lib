package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

/**
 * A special implementation of the {@link ParentableScreen} class to only set the parent screen when instructed to by the user. <br />
 * This is useful for classes that depend on user code on which screen they should go after they have been closed.
 */
public abstract class SelectiveParentableScreen
    extends ParentableScreen
{
    private boolean suppress_setting_parent_on_close;

    /**
     * Constructor for allowing to use the {@link SelectiveParentableScreen} class in derived classes.
     * @param title The title of the newly created screen.
     * @param parent The parent screen that will become the current one if and when this screen object is closed. Can be {@code null}, in which case the user will be lead to the game's main menu.
     */
    protected SelectiveParentableScreen(Component title, @MaybeNull Screen parent) {
        super(title, parent);
        suppress_setting_parent_on_close = false;
    }

    /**
     * Suppress setting the parent screen when this screen will be closed.
     */
    public void SuppressSettingParentScreenOnClose() {
        suppress_setting_parent_on_close = true;
    }

    /**
     * Set the parent screen suppression status.
     * @param status The current parent screen set on close suppression status.
     */
    public void SetSettingParentSuppressionStatus(boolean status) {
        suppress_setting_parent_on_close = status;
    }

    /**
     * Get the parent screen suppression status.
     * @return The current parent screen set on close suppression status.
     */
    public boolean GetSettingParentSuppressionStatus() {
        return suppress_setting_parent_on_close;
    }

    @Override
    public void onClose() {
        if (!suppress_setting_parent_on_close) {
            super.onClose();
        }
    }
}
