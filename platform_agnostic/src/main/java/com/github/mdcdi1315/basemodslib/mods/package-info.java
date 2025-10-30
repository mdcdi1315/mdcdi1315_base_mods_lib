/**
 * Provides the base interoperability ways and API's to interact with a platform-agnostic way to a mod loader.
 *
 * <h3>How the library treats the mods</h3>
 *
 * The library itself considers mods to be disposable objects - that is, they do have a specified lifetime. <br />
 * Typically and by convention, their lifetime ends once Minecraft is in the process of closing. <br />
 * The base container for that purpose is the {@link com.github.mdcdi1315.basemodslib.mods.IModInstance} interface,
 * providing base services applying to both client and server side mods. <br />
 * However, a mod developer never uses directly the {@code IModInstance} interface itself;
 * It does not provide anything useful other than the loading process of the mod instance itself. <br />
 * Instead the {@link com.github.mdcdi1315.basemodslib.mods.IServerModInstance}
 * and {@link com.github.mdcdi1315.basemodslib.mods.IClientModInstance} interfaces are implemented by the developer,
 * representing server and client side mods respectively. <br />
 * Then, by appropriate Initialize* methods on the Base Mods Library root classes
 * called by mod-loader specific code, the developer can initialize any desired number of mods.
 *
 * <h3>The 'Request for what you need' model.</h3>
 *
 * The interfaces are designed around the concept that only and only resources will be allocated if the mod needs a functionality,
 * for example, to register new blocks. <br />
 * Although rare but valid, a mod may not need blocks to register. <br />
 * Thus, no mod-loader data structures will be allocated for supporting that interface. <br />
 * Each of the provided services is considered as 'inaccessible', once a specific interface method completes execution. <br />
 * 'Inaccessible' means that you should not call any of the service-provided methods after the service invocation has been completed.
 * Doing so can lead to undefined execution and possibly a mod loading failure later in the mod loader mod loading chain. <br />
 * There are also and exceptions to this rule, for example, if the networking manager is created, it must be retained by the in question
 * mod to dispatch networking events.
 */
package com.github.mdcdi1315.basemodslib.mods;