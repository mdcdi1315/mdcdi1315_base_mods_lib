package com.github.mdcdi1315.basemodslib;

/**
 * For Fabric, this does provide an empty mod object to be passed on the Initialize* API's. <br />
 * Use this object in such cases. Failure to use this will result in a runtime exception.
 */
public final class EmptyModObject
{
    public static final EmptyModObject INSTANCE = new EmptyModObject();

    private EmptyModObject() {}
}
