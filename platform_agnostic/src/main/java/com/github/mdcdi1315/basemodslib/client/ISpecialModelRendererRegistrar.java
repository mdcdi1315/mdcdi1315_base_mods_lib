package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way to register additional special model renderers to Minecraft.
 * @since 1.0.11
 */
@Contract
public interface ISpecialModelRendererRegistrar
{
    /**
     * Registers a new special model renderer codec for a given Minecraft block.
     * @param info The registration information to use so that Minecraft can acknowledge your special renderer codec.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void RegisterCodec(SpecialModelRendererCodecRegistrationInfo info) throws ArgumentNullException;

    /**
     * Registers a new special model renderer for a given Minecraft block.
     * @param info The registration information to use so that Minecraft can acknowledge your special renderer.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void Register(SpecialModelRendererRegistrationInfo info) throws ArgumentNullException;
}
