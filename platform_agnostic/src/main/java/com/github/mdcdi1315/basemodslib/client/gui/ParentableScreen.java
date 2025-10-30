package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

/**
 * Defines the base class for all screen classes that can be parented to other screen classes. <br />
 * This does also override the {@link #onClose} method to instead bind the Minecraft window to the parent one, if exists.
 */
public abstract class ParentableScreen
    extends Screen
{
    @AllowNull
    private final Screen parent;

    /**
     * Constructor for allowing to use the {@link ParentableScreen} class in derived classes.
     * @param title The title of the newly created screen.
     * @param parent The parent screen that will become the current one if and when this screen object is closed. Can be {@code null}, in which case the user will be lead to the game's main menu.
     */
    protected ParentableScreen(Component title, @MaybeNull Screen parent) {
        super(title);
        this.parent = parent;
    }

    /**
     * Gets the screen object that the Minecraft instance will be bound to once this screen object is closed.
     * @return The screen object that will become the current one if and when this screen object is closed.
     */
    @MaybeNull
    public Screen GetParent() {
        return parent;
    }

    public void onClose() {
        // When this is called the Minecraft instance is never but never null.
        minecraft.setScreen(parent);
    }
}
