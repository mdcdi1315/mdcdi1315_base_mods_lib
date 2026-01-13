### Now releasing 1.0.15:

-> Big update!

-> Saved Data: Reimplemented Saved Data logic so that it is easier to be maintained and ported to newer Minecraft versions.

-> Utilities: Added empty iterable/iterator classes

-> Added a sound registrar to register custom sound events to Minecraft.

-> Registries: Added a new way to register registry objects in bulk. See the IBulkRegistryObjectRegister for more information.

-> Fluids: Added the missing 'get-fluid' utility method.

-> Added the sound registry finalized event (due to the fact that the sound registrar was added)

-> Completely revamped the config system

- Now, it is a highly configurable and advanced system with many properties and fixes many issues 
that the old one had due to how it was implemented.

- It is also fully compatible with configs written using the old system

- The basic interface has also not being changed.

