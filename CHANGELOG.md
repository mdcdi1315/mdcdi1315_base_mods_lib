### Now releasing 1.0.31:

-> Updated the Collections API even further! And some bugfixes were employed as well.

-> Function API: Added Bi-Predicate support, extended support to some pre-existing types

-> Reflection Utils: ImplementsInterface method now uses a dedicated recursive method for checking whether a class implements the specified interface.

-> Added the `IModResourceLookup` interface, that does expose of any mod data files directly.

-> Added the `VersionInfo` class, for looking up predefined version data. Now it does include the library's version, a build time stamp, and some compiled against versions of the mod-loaders.

-> `RegistryUtils`: Added resource key construction methods. They are equivalent to the resource location construction methods, but for constructing resource keys.

-> Codecs: Fix: `VersionableCodecV2` not correctly encoding data.

-> Codecs: Added the `ResourceKeyCodec` class, using the now-introduced `RegistryUtils` resource key construction methods.

-> Mod Init: Modify mod and library init exception handling - now it translates some Java to .NET layer ones before handing them to the wrapped exceptions.

