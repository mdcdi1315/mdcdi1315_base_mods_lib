package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Represents the method that defines a set of criteria and determines whether the specified object meets those criteria.
 * @param <T> The type of the object to compare.
 */
@FunctionalInterface
@DotNetDelegateInterface
public interface Predicate<@DotNetByRefParameter(ByRefParameterType.IN) T> {
    /**
     *
     * @param obj The object to compare against the criteria defined within the method represented by this delegate.
     * @return true if obj meets the criteria defined within the method represented by this delegate; otherwise, false.
     */
    boolean predicate(T obj);
}
