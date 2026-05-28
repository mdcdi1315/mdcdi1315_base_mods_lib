### Now releasing 1.0.34:

-> Event API: Redesign how it is loaded and shut down. Additionally, this fixes issue #1.

-> Collections package: Added `BTreeDictionary`, which is the first `IDictionary` implementation!

-> Added more annotations to the new annotations package.

-> World package: Added several block manipulation helpers, and added the `Area3DEnumerator` for retrieving all the block positions in the defined area.

-> Debug Events Manager: The Debug events manager now properly logs each dispatched event to console. This is meant as an internal improvement for mod developers.

-> Deprecated the `JavaCollectionsCompatibilityHelper` class, as it is incomplete. The `CollectionManipulations` class now provides everything needed for translating collection classes.