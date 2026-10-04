
## mdcdi1315's Mods Base Library

This is a library for use by my Minecraft Java mods and provides the basic tools that I use to all of them in a way or another.

## How it works

The library is an abstraction over Minecraft and mod loaders. 
It projects to mods all the required interfaces to communicate to Minecraft and register whatever the mods do need.
To achieve this, there are interfaces called Contracts that are called during a specified time by the mod loader itself.

And all of this, while being in common (mod-loader agnostic) code, so the mod code is no longer duplicated.

Apart from these it provides utilities around many Minecraft subsystems and more. 

Most important are the Collections package providing custom collection classes, 
the Functions package providing efficient function signatures,
and since 1.0.38, a new Random package providing random number generators and utilities around them.

Finally, the library provides a subset of .NET API's, that are much more efficient, type-safe, and much
more convenient to use. Most important is the collection abstractions that it provides, such as `IEnumerable` and `IDictionary`.

To be noted, the subset is far from complete, but continued effort is also done to ensure that most of the API's are supported.
There are cases that not everything is supported because of Java restrictions, such as type erasure.
