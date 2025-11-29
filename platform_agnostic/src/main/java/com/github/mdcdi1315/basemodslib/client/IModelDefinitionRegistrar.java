package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for registering additional model definitions to Minecraft.
 */
@Contract
public interface IModelDefinitionRegistrar
{
    /**
     * Registers a model definition to the Minecraft client.
     * @param info The model definition registration information.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void Register(ModelDefinitionRegistrationInfo info) throws ArgumentNullException;
}
