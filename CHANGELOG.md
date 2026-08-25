### Now releasing 1.0.37:

-> Expanded the I/O package (Again!)

-> Collections package: Added several API's:
    - `FixedArrayBasedList`: An non-expandable list, based by the `ArrayBasedList` class.
    - Added better empty collection handling
    - `ArrayBasedList`: Several code cleanups
    - Added sorting algorithms, plus checker whether an enumerable is sorted.
    - Added common comparer implementations, accessible under the `comparers` sub-package.

-> .NET Layer additions:
  - Most notable: Added the `Dictionary` implementation
  - Completed the exception classes
  - Added the non-generic `ArrayList` implementation
  - Updated annotation processor to better handle the `ClassIsDotNetStruct` annotation

-> Further cleanups on `BaseModsLib` and `BaseModsLibClient` classes,
and they now use a `Dictionary` instance to store the mod instances.

-> Function package: Added Provides* function signatures