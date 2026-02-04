### Now releasing 1.0.17:

-> Fabric: Loaded mods list was not ever completed and as such the library never knew about the loaded mods. This release fixes that.

-> Added some custom collection types - there will be more in the future!

-> Removed the usage of the List class in most registrars implementing contracts.
They are now instead using the SingleLinkedList class which is faster and occupies less memory than the List class.

-> Updated LICENSE file for 2026

-> NeoForge BML Language Provider: Accelerated mod container creation by looking up for the BML library only once.
