package com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;

public final class AssignValueConsumer_ByteList
    extends AssignValueConsumer_List<Integer>
{
    public AssignValueConsumer_ByteList(ConfigRecord record, IConfigField<?> field) { super(record, field); }

    @Override
    protected Object Transform(Integer entry) { return entry.byteValue(); }
}
