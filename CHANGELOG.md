### Now releasing 1.0.36:

-> Further bugfixes on I/O package, and extended the capabilities of the `MemoryStream` class.

-> Event API: Added `IDestroyedOnUseEvent` interface, that allows to construct events that 
once they are fired, they are removed from the events manager.

-> Event API: Firing events in event handlers is no longer allowed, except if the event that 
is being fired of specifies the `PermitsRecursiveFiring` annotation.
