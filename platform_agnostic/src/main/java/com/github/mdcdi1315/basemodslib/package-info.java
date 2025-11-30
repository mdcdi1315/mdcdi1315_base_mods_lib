/**
 * Defines the base package for the mdcdi1315's Base Mods Library. <br />
 *
 * <h3>What is the Base Mods Library?</h3>
 *
 * OK, it is known that this mod is a library for creating multi-platform mods. <br />
 * The problem, is, how the things are done? <br />
 *
 * <h4>The library from the developer's side</h4>
 *
 * From the developer side, the library is a set of abstracted contracts defining the entire process
 * of how a mod using this library should be built. These contracts do also provide additional things,
 * if they are required for typical development. <br />
 *
 * The starting point is the {@link com.github.mdcdi1315.basemodslib.mods.IClientModInstance} and {@link com.github.mdcdi1315.basemodslib.mods.IServerModInstance} interfaces,
 * that are the base contracts for defining the mods themselves. <br />
 *
 * Finally, the library itself exports some custom base classes known to all the mod instances for flexibility. <br />
 * Those classes are the {@link com.github.mdcdi1315.basemodslib.BaseModsLib} and {@link com.github.mdcdi1315.basemodslib.BaseModsLibClient} classes.
 *
 * <h4>The library from the implementer's side</h4>
 *
 * For an implementer (typically a maintainer or something similar), the library provides the base building blocks and contracts for
 * defining base mods functionality, such as registering blocks and items, and providing base services for all the mods depending on the library. <br />
 *
 * Finally, those contracts define an exact 1-1 translation to what action should have been performed if the mod was declared as mod-loader specific. <br />
 *
 * The implementers have to exactly define logic required by the contracts, and should not omit any contract dispatch. <br />
 * If there is functionality that could be missing due to restrictions on the underlying mod loader, methods should throw {@link com.github.mdcdi1315.DotNetLayer.System.NotSupportedException} or a derivant class.
 */
package com.github.mdcdi1315.basemodslib;