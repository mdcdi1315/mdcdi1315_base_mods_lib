package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

/**
 * Determines the environment under which the library runs into.
 */
public enum ModdingEnvironment
    implements ISynchronized
{
    /**
     * The environment could not be determined.
     */
    UNKNOWN,
    /**
     * The library runs in a client distribution.
     */
    CLIENT,
    /**
     * The library runs in a server distribution.
     */
    SERVER
}
