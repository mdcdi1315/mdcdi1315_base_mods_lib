package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.codecs.ListCodec;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;

public final class Record
    implements Iterable<SerializedField>
{
    private static Codec<Record> codec;

    private final ImmutableList<SerializedField> fields;

    public static final class Builder
    {
        private ImmutableList.Builder<SerializedField> builder;

        public Builder() {
            builder = ImmutableList.builder();
        }

        public Builder(int size) {
            builder = ImmutableList.builderWithExpectedSize(size);
        }

        public Builder Add(SerializedField field) {
            builder.add(field);
            return this;
        }

        public Record Build() {
            return new Record(builder.build());
        }
    }

    public static Codec<Record> GetCodec()
    {
        if (codec == null) {
            codec = new ListCodec<>(SerializedField.GetCodec()).flatXmap(
                    Record::CreateFromList,
                    Record::GetListFromRecord
            );
        }
        return codec;
    }

    private static DataResult<Record> CreateFromList(List<SerializedField> lt) {
        return DataResult.success(new Record(lt));
    }

    private static DataResult<List<SerializedField>> GetListFromRecord(Record rc) {
        return DataResult.success(rc.fields);
    }

    public Record(Iterable<SerializedField> iterable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(iterable, "iterable");
        if (iterable instanceof ImmutableList<SerializedField> lt) {
            this.fields = lt;
        } else if (iterable instanceof Collection<SerializedField> c) {
            this.fields = ImmutableList.copyOf(c);
        } else {
            this.fields = ImmutableList.copyOf(iterable);
        }
    }

    public int GetCount() {
        return fields.size();
    }

    @Override
    public @NotNull Iterator<SerializedField> iterator() {
        return fields.iterator();
    }
}
