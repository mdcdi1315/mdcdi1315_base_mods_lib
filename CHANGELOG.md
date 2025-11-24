### Now releasing 1.0.11:

-> (Neo)Forge: Added a language provider for hiding the initialization details of mods using the library. It looks now like the Fabric usage case.

-> From now on, java docs are not provided with the developer package. Instead, the sources are provided which can be imported by your IDE and use them.

-> The library will now fail cleanly with a special exception upon catastrophic failure during initialization.

-> Events manager API: A complete overhaul of the API was performed, thereby eliminating the issues being around before this happens.

-> A new Proxy manager API is now introduced! It can be used by the developers to instantiate mod loader-specific objects and consume them through their platform-agnostic project.

-> A new enumeration type added for presenting common mod loaders. You can use this instead now to compare against mod loaders.

-> Added lots of documentation to better describe things in the library!

-> Added additional events for common gameplay situations. More will be added in the future!

-> 1.21.5: Added support for the new 'special model renderers' subsystem of the game.