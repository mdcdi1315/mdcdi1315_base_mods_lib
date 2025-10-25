package com.github.mdcdi1315.basemodslib;

/**
 * Determines the environment under which the library runs into.
 */
public enum ModdingEnvironment
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
