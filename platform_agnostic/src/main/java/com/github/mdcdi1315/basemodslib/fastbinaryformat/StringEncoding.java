package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

public enum StringEncoding
    implements ISynchronized
{
    UTF16_LE(0),
    UTF16_BE(1),
    ASCII(2);

    public final byte encoded_value;

    StringEncoding(int v) { this.encoded_value = (byte) v; }
}
