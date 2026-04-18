### Now releasing 1.0.26:

-> Another large feature release!

-> Simplified the Configuration API at a further extent - breaks the old one, but it is better.

-> Deprecated the `ConfigList` class.

-> Configuration API: Mod developers can now declare their own de/serializers for their configuration file.

-> Simplified code in several library-provided `Codec` implementations

-> Simplified the Cloth Config API integration and added support for `List` fields.

-> Additions and refactorations in the `utils` package:

-> Fabric: Fixed mod info packets registration that was happening after the library is finalized.

1. Severed the `EmptyEnumerator` class from the `EmptyEnumerable` class and moved to its own class
2. Extensions class: Now every method is completely documented.
3. I/O support: Added several helper classes and methods.
4. Collections support: Added `ISupportsDirectConversionTo`, `ISupportsFiltering` and `ISupportsSlicing` interfaces - these allow generalized lookup on the features that each collection class implements.
5. Collections support: Added several wrapping enumerator implementations.
6. Collections support: Added the `SingletonEnumeratorEnumerable` class, a class implementing `IEnumerable` and provides a single `IEnumerator` instance provided through the `GetEnumerator` method.
7. Function manipulations support: Added several new functional interfaces and extended the `always true` and `always false` predicate support.
