### Now releasing 1.0.20:

-> Added a new 'function' package in the 'utils' package. It provides helper methods for transforming and using functional interfaces.

-> Added the `DirectlyMappedSet` and the `DirectlyMappedSpliterator` classes.

-> NBT Utilities: Optimized the lookup by directly checking for `instanceof` rather than allocating the tag into a new variable first.

-> `DefaultConfigurationScreenFactory`: Fixed the issue where the Fabric distribution of the library 
failed to instantiate the Cloth Config API screen due to the naming of the mod in Fabric environments.

-> Added networking helpers around Minecraft's `Vec2` type.

-> (Config) `ListElementRegistry`: Fixed an issue with the List Element Registry not able to recognize `ResourceLocation` instances.

-> Event API: Added new registry finalized events, and reordered all the registry finalized 
events so that they are dispatched in the exact same way as they are created at the beginning.
Note, only Forge keeps a custom pattern due to how it conceives the concept of registries and the mods.

-> [1.21.5]: Fixed misreported Minecraft Version on all implementations. 
This is not a problem in typical environments, however.