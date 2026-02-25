package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;

import java.util.Objects;

/**
 * Represents a position in a non-contiguous set of memory.
 * Properties of this type should not be interpreted by anything but the type that created it.
 */
@ClassIsDotNetStruct
public final class SequencePosition
    extends ValueType
{
    @AllowNull
    private final Object _object;
    private final int _integer;

    public SequencePosition()
    {
        super();
        _object = null;
        _integer = 0;
    }

    /**
     * Initializes a new instance of the {@link SequencePosition} struct.
     * @param object A non-contiguous set of memory.
     * @param integer The position in {@code object}.
     */
    public SequencePosition(@AllowNull Object object, int integer)
    {
        super();
        _object = object;
        _integer = integer;
    }

    /**
     * Returns the integer part of this {@link SequencePosition}.
     * @return The integer part of this sequence position.
     */
    public int GetInteger() { return _integer; }

    /**
     * Returns the object part of this {@link SequencePosition}.
     * @return The object part of this sequence position.
     */
    @MaybeNull
    public Object GetObject() { return _object; }

    @Override
    public int GetHashCode() { return Objects.hash(_object, _integer); }

    public boolean Equals(SequencePosition sq) { return sq._object == _object && sq._integer == _integer; }

    @Override
    public boolean Equals(@NotNullWhen(ReturnValue = true) Object any)
    {
        if (any instanceof SequencePosition s) {
            return Equals(s);
        } else {
            return false;
        }
    }
}
