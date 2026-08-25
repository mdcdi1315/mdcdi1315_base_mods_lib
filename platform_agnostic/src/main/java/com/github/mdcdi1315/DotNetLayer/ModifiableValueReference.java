package com.github.mdcdi1315.DotNetLayer;

import java.util.Objects;

@PrivateImplementationDetail
public abstract class ModifiableValueReference<T>
{
    private T current_value;

    public ModifiableValueReference() { current_value = null; }

    public abstract void SetValue(T value);

    public T GetValue() { return current_value; }

    protected final void UpdateValue(T value) { current_value = value; }

    public void SetToByReferenceParameter(ByRefParameter<T> parameter) { parameter.Value = current_value; }

    public static <T> ModifiableValueReference<T> Create() { return new Default<>(); }

    public static <T> ModifiableValueReference<T> Create(T value) { return new Default<>(value); }

    public static <T> ModifiableValueReference<T> GetReferenceByArray(T[] array, int index) { return new ByArray<>(index, Objects.requireNonNull(array)); }

    public static ModifiableValueReference<Integer> GetReferenceByArray(int[] array, int index) { return new ByIntArray(index, Objects.requireNonNull(array)); }

    public static <T> ModifiableValueReference<T> WrapByReferenceParameter(ByRefParameter<T> value) { return new ByARefParameter<>(Objects.requireNonNull(value)); }

    private static final class Default<T>
        extends ModifiableValueReference<T>
    {
        public Default() { super(); }

        public Default(T value) { super(); UpdateValue(value); }

        @Override
        public void SetValue(T value) { UpdateValue(value); }
    }

    private static final class ByArray<T>
        extends ModifiableValueReference<T>
    {
        private final int index;
        private final T[] values_array;

        public ByArray(int index, T[] values_array)
        {
            UpdateValue(
                    (this.values_array = values_array)[Objects.checkIndex(this.index = index, values_array.length)]
            );
        }

        @Override
        public void SetValue(T value)
        {
            values_array[index] = value;
            UpdateValue(value);
        }
    }

    private static final class ByIntArray
        extends ModifiableValueReference<Integer>
    {
        private final int index;
        private final int[] values_array;

        public ByIntArray(int index, int[] values_array)
        {
            UpdateValue(
                    (this.values_array = values_array)[Objects.checkIndex(this.index = index, values_array.length)]
            );
        }

        @Override
        public void SetValue(Integer value)
        {
            values_array[index] = value;
            UpdateValue(value);
        }
    }

    private static final class ByARefParameter<T>
        extends ModifiableValueReference<T>
    {
        private final ByRefParameter<T> parameter;

        public ByARefParameter(ByRefParameter<T> parameter) { this.parameter = parameter; }

        @Override
        public T GetValue() { return parameter.Value; }

        @Override
        public void SetValue(T value) { parameter.Value = value; }

        @Override
        public void SetToByReferenceParameter(ByRefParameter<T> parameter) { parameter.Value = this.parameter.Value; }
    }
}
