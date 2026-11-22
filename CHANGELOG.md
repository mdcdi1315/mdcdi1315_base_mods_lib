### Now releasing 1.0.42:

-> Licensing change: Project is now bundled with the GNU GPL Version 3 license, and onwards
development effort will continue with this license. The MIT license in the older versions does
not change, but those older versions won't receive any new updates except for bug fixes, if any.

-> Updates to internal collection implementations: They now examine the collection instances
more thoroughly and as such they provide better characteristics from now on.

-> The `ICountableCollection` interface now implements `IReadOnlyCollection`,
and the `ITraversableCollection` interface now implements `IReadOnlyList`.

-> Improved the concatenation support.

-> Bring source code updates from 1.0.41 of older versions

-> Deprecated several old API's and removed several other old API's.

-> Added the Information Lookup API.

This new API allows to query information about depended mods in an
extensible manner, particularly through attributes. Implementations
of the lookup API are free to add more attributes if they want to.

Additionally, the Lookup API provides a new way for reading and accessing
mod files. The `IModResourceLookup` interface was a bit challenging and 
difficult to work with due to the `java.nio.file.Path` requirement.

This new system, bundled with the Lookup API, called as `IModFileSystem`,
overcomes these difficulties by defining its own logic, the file system
abstraction and the file abstraction. You are still able to query all the
files in a mod, and you can now query any number of files you wish.