### Now releasing 1.0.38:

-> Fix copy-paste docs in ByteArray classes in the I/O package.

-> Reworked random support and a new `random` package was added.
Note: several API's using the old Minecraft logic are now deprecated, as
well as the `weight` utility package, which was also reworked as `random.weighted`. 

-> Collections package: Added:
    - HeapSort sorting algorithm
    - Support for wrapping Java maps to `IDictionary` instances.
    - The `ICountableCollection` interface.
    - Reversed primitive array enumerators
    - Mojang `Codec` support

-> Function package: Added primitive transformation function signatures,
that is, given an input number, it provides the same number type but might of a different value.

-> First attempt for the 26.1 base Minecraft Version!