### Now releasing 1.0.25:

-> Further performed performance optimizations

-> The library now uses less Mixins in an effort to use the mod-loader provided ways instead.

-> [1.21.1 ((Neo)Forge)]: Fixed a performance issue with the Item Renderer Mixin that looked up first for the `IClientItemExtensions` object rather than whether the item implements `IBlockEntityItem`.

-> The `ITraversableCollection` interface now implements the IEnumerable interface.

-> Added more API's to the .NET Layer.

-> The library's dev instance now saves each mixed in class to concrete files for further debugging.

-> Minecraft version is now retrieved through the game's `SharedConstants` class.

-> Added a server stopped event.

-> Fabric: Fixed BML accessing client-side stuff on dedicated servers.
