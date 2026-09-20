package com.github.mdcdi1315.basemodslib.integration.clothconfig.customsupport;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.registries.ResourceLocationConstructionException;

import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Supplier;

public final class ListOfResourceLocationEntry
        extends TextFieldListEntry<Identifier>
{
    public ListOfResourceLocationEntry(Component fieldName, Component resetButtonKey, Optional<Component[]> tooltip) {
        this(fieldName, Identifier.tryBuild(Identifier.DEFAULT_NAMESPACE, "value"), resetButtonKey, new ElementSupplier<>(tooltip), false);
    }

    public ListOfResourceLocationEntry(Component fieldName, Identifier original, Component resetButtonKey, Optional<Component[]> tooltip) {
        this(fieldName, original, resetButtonKey, new ElementSupplier<>(tooltip), false);
    }

    public ListOfResourceLocationEntry(Component fieldName, Identifier original, Component resetButtonKey, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, original, resetButtonKey, null, tooltipSupplier, requiresRestart);

        textFieldWidget.setValue(original.toString());
        textFieldWidget.setCursorPosition(0);
    }

    @Override
    public Optional<Identifier> getDefaultValue() { return Optional.ofNullable(Identifier.tryBuild(Identifier.DEFAULT_NAMESPACE, "value")); }

    @Override
    public Optional<Component> getError()
    {
        try {
            RegistryUtils.ParseIdentifier(textFieldWidget.getValue());
            return Optional.empty();
        } catch (ResourceLocationConstructionException construction) {
            return Optional.of(
                    Component.literal(
                            StringUtils.Format(
                                    "Not a valid resource location; Parsing of \"{0}\" failed:\n{1}",
                                    textFieldWidget.getValue(),
                                    construction.getMessage()
                            )
                    )
            );
        }
    }

    @Override
    public Identifier getValue()
    {
        try {
            return RegistryUtils.ParseIdentifier(textFieldWidget.getValue());
        } catch (ResourceLocationConstructionException e) {
            return null;
        }
    }
}
