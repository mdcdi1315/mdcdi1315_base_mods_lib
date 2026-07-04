
### The mdcdi1315's Base Mods Library Architecture

This document aims to document down the decisions that driven the development of this library,
as well as how the library core parts are thought out.

1. The basic principles

The library stands as an abstracted layer over patched Minecraft classes.

For the library to work, it needs a mod loader implementation, conventionally called 'mod loader layer'.

All mods managed by the library do have a specific lifetime tied to them, that's why they are disposable objects.
The mod loader layer (and the mod loader itself) is *responsible* for constructing them, managing them, destroying them and communicating with them.

For the mods to actually apply functionality, they must communicate over Minecraft to tell it what they need.
However, most of the Minecraft classes are patched and non-visible to the outside world.
To overcome such patching difficulties, the library provides thin abstracted accessors called 'Contracts'.
Each contract manages the translation to different mod-loader environments, where those loaders have widened
access to those patched Minecraft classes.
How a contract implementation is created, designed, managed and disposed of is entirely mod-loader specific. 
The only behavior that must be retained the same across implementations of the contracts
is on argument validation, and nothing else.

All of the above enforce a single and consistent development model across mod-loaders: Purely targeted vanilla modding.

This allows to save a lot of time writing duplicate code on each mod-loader platform, and it eventually minimizes the code effort 
required to port from one Minecraft vanilla version to another.



~~~


            |----------------------|                  |-----------------|----------|
            |                      |                  |                 \          |              Physical limitations border
            |                      |                  |                 \          |              (Like implementation-defined issues, or critical base code bugs)
------------|      Minecraft       |------------------|   Mod Loader    \  Mixin   |--------------------------------------------
            |                      |                  |                 \          |              Exposed to mods and other classes
            |                      |                  |                 \          |              Mod loader platform border
            |--------\-------------|                  |---\-------------|--\-----\-|
                     |                                    |                |     |
                     |                                    |                |     |-------------------------------|
                     |                                    |       |--------|                                     |
                     |                                    |       |                                              |
                     |                                    |       |                                              |
                     |            |-----------------------\-------\----|------------------|                      |
                     |            |         Mod Loader Layer           \                  |                      |
                     |            |____________________________________\ Mod Lifetime     |                      |
                     |            |      Contract Implementations      \ Management       |                      |
                     |            |------------------------------------| Services         |                      |
                     |            |    mdcdi1315's Base Mods Library   \                  |                      |
                     |------------\     Library features (commands)    \                  |                      |
                     |            |____________________________________\                  |                      |
                     |            |                                    \__________________|                      |
                     |            |        Low-level classes           \                  |                      |
                     |            |         Vanilla patches            \  User space API  |                      |
                     |            |      Library support patches       \                  |                      |
                     |            |------------------------------------|\\\\\\\\\\\\\\\\\\|                      |
                     |            |                                                       |                      |
                     |            |                                                       |                      |
                     |------------\                   Public-Facing API                   |                      |
                     |            |            Contracts, utilities, helpers, etc.        |                      |
                     |            |                                                       |                      |
                     |            |                                                       |                      |
                     |            |---------------------------\---------------------------|                      |
                     |                                        |                                                  |
                     |       |---------------|                |                                                  |    Mod loader platform border
---------------------|-------|               |----------------|--------------------------------------------------|----------------------------
                     |       |  Mod Loader   |                |                                                  |    Vanilla-only modding 
                     |       |  Specific     |                |                                                  |
                     |       |  Code         |                |                                                  |
                     |       |  (Optional)   |                |                                                  |
                     |       |               |                |                                                  |
                     |       |               |                |                                                  |
              |------\-------|\\\\\\\\\\\\\\\|----------------\--------------------------------------------------\-------|
              |                                                                                                          |
              |                               |-----------------------|               |------------------------|         |
              |                               |                       |               |                        |         |
              |                               |       Contract        |               |                        |         |
              |                               |      Consumption      |               |     Mod Instance       |         |
              |                               |                       |               |                        |         |
              |                               |-----------------------|               |------------------------|         |
              |                                                                                                          |
              |                                                                                                          |
              |                                            Dependent Mods                                                |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |                                                                                                          |
              |----------------------------------------------------------------------------------------------------------|
~~~

Based on the diagram above, this driven me to design the library so that to stand as an aggregate between the
mod-loader and the vanilla only modding; a fully controlled environment where you expect it to work the same 
across the mod loaders.

2. The hierarchy of building blocks

- `Minecraft`: The base game we all know. Is just everywhere, and the only one whose physical limitations are present even in vanilla-only modding.
- `Mod Loader`: The engine that discovers and loads mods. This is the one that tries to patch Minecraft so that many mods are compatible with others.
- `Mixin`: The Mixin Framework library, allowing to modify access to Minecraft classes and hijack the code itself.
- `mdcdi1315's Base Mods Library`: The library, acting as the middleman for translating mod-loader code to something accessible to vanilla-only modding.
    - `Mod Loader Layer`: The base classes required for directly interacting with the mod loader, as well as presenting the library as a mod.
    - `Contract Implementations`: Basic mod loader accessors translated to API's understood by the vanilla-only modding.
    - `Mod Lifetime Management Services`: External support and implementations for managing mod instances, as well as providing the contract implementations to those instances.
        - `User space API`: Low level mod instance lookup and description API accessible by both the library and the vanilla-only modding.
    - `Library features`: Features provided by the library itself for all the mods in a modded instance, as well as patches for hooking several public-facing API's.
    - `Public-Facing API`: API the library and the vanilla-only modding actively consumes. For example, the contract definitions belong here.
- `Dependent Mods`: The mods, implemented on top of vanilla-only modding.
    - `Contract Consumption`: Mods communicating with the library's API to achieve the desired effect/behavior.
    - `Mod Instance`: Instances assigned by the library pertaining to those particular mods
