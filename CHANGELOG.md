### Now releasing 1.0.18:

-> Large feature update!

-> Forge/NeoForge: Custom Creative Mode Tab stacks were not appropriately registered, and as such only one was registered for each given mod.
This release fixes that.

-> Most classes have now migrated away from `List` class and now use the `SingleLinkedList` class instead

-> Expanded the collections API package

-> Added miscellaneous additional API's

-> Added more network codecs

-> Development upgrade: Dropping support for Developer Archives, now anyone wanting to consume the library must use GitHub packages from now on.
The template will be appropriately updated for 1.21.1+ variants.

-> Made CodecUtils to not use lambda expressions for CreateCodecDirect API's

-> Added more documentation!

-> Added the weight API introduced in one of my mods here. 

-> Updated copyright in `LICENSE` for 2026