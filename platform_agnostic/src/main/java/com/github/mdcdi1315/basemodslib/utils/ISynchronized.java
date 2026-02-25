package com.github.mdcdi1315.basemodslib.utils;

/**
 * Base marker interface for all classes that are thread-safe.
 * <h4>Meaning of this interface</h4>
 * Classes implementing this marker interface indicate that can, either full, or partially, implement thread safety while accessing them. <br />
 * This practically means that you can call methods from any thread. <br />
 * Because an object may not fully implement thread safety, please refer to its documentation to verify that the methods you want to call from it are thread-safe.
 * @since 1.0.18
 */
public interface ISynchronized { }
