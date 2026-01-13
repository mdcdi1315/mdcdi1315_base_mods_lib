package com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers;

import com.github.mdcdi1315.basemodslib.config.ConfigList;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;

import java.util.List;

public class AssignValueConsumer_List<T>
    extends AssignValueConsumer<List<T>>
{
    public AssignValueConsumer_List(ConfigRecord record, IConfigField<?> field) { super(record, field); }

    protected Object Transform(T entry) { return entry; }

    @Override
    public final void accept(List<T> value)
    {
        ConfigList list = new ConfigList(value.size());

        for (T v : value) { list.Add(Transform(v)); }

        try {
            field_to_set.set(config, list);
        } catch (IllegalAccessException e) {
            ReportSetWarning(e);
        }
    }
}
