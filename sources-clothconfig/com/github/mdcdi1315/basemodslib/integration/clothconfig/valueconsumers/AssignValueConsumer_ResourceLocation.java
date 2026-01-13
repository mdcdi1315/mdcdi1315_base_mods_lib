package com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public final class AssignValueConsumer_ResourceLocation
    extends AssignValueConsumer<String>
{
    public AssignValueConsumer_ResourceLocation(ConfigRecord record, IConfigField<?> field) { super(record, field); }

    @Override
    public void accept(String value)
    {
        try {
            field_to_set.set(config, ResourceLocation.parse(value));
        } catch (IllegalAccessException e) {
            ReportSetWarning(e);
        } catch (ResourceLocationException rle) {
            BaseModsLib.LOGGER.warn("Cannot assign the value {} to field {}: {}", value, field_to_set.getName(), rle);
        }
    }
}
