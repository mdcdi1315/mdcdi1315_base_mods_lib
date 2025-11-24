package com.github.mdcdi1315.basemodslib.mods.proxy;

/**
 * The exception that is thrown when the proxy constructor throws an exception.
 */
public final class ProxyInstantiationException
        extends ProxyReflectionException
{
    /**
     * Creates a new instance of the {@link ProxyInstantiationException} class with the specified name of the proxy object whose constructor failed and the exception caused by that constructor invocation.
     * @param class_name The name of the proxy class that contains the failing constructor.
     * @param ex The exception thrown by the invocation of that constructor.
     */
   public ProxyInstantiationException(String class_name, Exception ex) {
       super(String.format("Invocation of proxy constructor thrown an exception!\nProxy class: %s" , class_name) , ex);
   }
}
