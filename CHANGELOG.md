### Now releasing 1.0.13:

-> This is the final release for 1.20.1 as we have been reached the end of the year.

-> Finalized support for extended screen handlers. 
Now the API properly translates screen handling with custom packet data for 1.20.1 . 
For Fabric 1.21.1, a way was found to implement screen handler support and from now on it is supported.

-> Potential fix for the Fabric rendering API layer that overrides `IBlockEntityItem` execution on NeoForge. This is done by the Sinytra's Forgified Fabric API that is used for e.g. Sodium on NeoForge.
This change does also apply to 1.20.1, if a similar API with similar effects is discovered.

-> Added a new `StringUtils` class to complement static methods found in `System.String` class in .NET .

-> Added some additional documentation for some types

-> Some minor bugfixes and improvements

-> Added helper methods to `IRegistryRegistrar` contract to cope better with the `RegisterObject` method.

