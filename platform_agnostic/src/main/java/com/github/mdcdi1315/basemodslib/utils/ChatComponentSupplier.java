package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.network.chat.Component;

/**
 * Provides a specialized element supplier for chat components. <br />
 * This is conveniently used in commands to directly define your chat components without the lambda expression hassle.
 * @since 1.0.5
 */
public final class ChatComponentSupplier
    extends ElementSupplier<Component>
{
    /**
     * Constructs a new instance of the {@link ChatComponentSupplier} class from the specified chat component.
     * @param item The chat component to be returned by the supplier. Can also be {@code null}.
     */
    public ChatComponentSupplier(@MaybeNull Component item) { super(item); }

    /**
     * Constructs a new instance of the {@link ChatComponentSupplier} from an empty chat component.
     * @return A new instance of the {@link ChatComponentSupplier} containing an empty chat component.
     */
    public static ChatComponentSupplier FromEmpty() {
        return new ChatComponentSupplier(Component.empty());
    }

    /**
     * Constructs a new instance of the {@link ChatComponentSupplier} from the specified string.
     * @param literal The string that this chat component will hold.
     * @return A new instance of the {@link ChatComponentSupplier} containing the contents of the {@code literal} argument.
     */
    public static ChatComponentSupplier FromLiteral(String literal) {
        return new ChatComponentSupplier(Component.literal(literal));
    }

    /**
     * Constructs a new instance of the {@link ChatComponentSupplier} from the specified translatable string. <br />
     * This method searches in the defined resource packs for the specified translation key and when it's string value is requested,
     * the translated string is instead returned.
     * @param translation_key The translation key to use for translating the chat component value.
     * @return A new instance of the {@link ChatComponentSupplier} created from the translated value of the {@code translation_key} argument.
     */
    public static ChatComponentSupplier FromTranslatable(String translation_key) {
        return new ChatComponentSupplier(Component.translatable(translation_key));
    }

    /**
     * Constructs a new instance of the {@link ChatComponentSupplier} from the specified translatable string. <br />
     * This method searches in the defined resource packs for the specified translation key and when it's string value is requested,
     * the translated string is instead returned.
     * @param translation_key The translation key to use for translating the chat component value.
     * @param arguments Additional formatting arguments to be passed for additionally formatting the translated string.
     * @return A new instance of the {@link ChatComponentSupplier} created from the translated value of the {@code translation_key} argument.
     */
    public static ChatComponentSupplier FromTranslatable(String translation_key, Object... arguments) {
        return new ChatComponentSupplier(Component.translatable(translation_key , arguments));
    }
}
