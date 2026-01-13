package com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;

public final class AssignValueConsumer_Short
        extends AssignValueConsumer<Integer>
{
    public AssignValueConsumer_Short(ConfigRecord record, IConfigField<?> field) { super(record, field); }

    @Override
    public void accept(Integer value)
    {
        try {
            field_to_set.set(config, value.shortValue());
        } catch (IllegalAccessException e) {
            ReportSetWarning(e);
        }
    }
}