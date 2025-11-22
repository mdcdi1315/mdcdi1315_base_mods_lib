
### Forge BML Language provider

This is a small language provider for Forge that simplifies initialization of a Base Mods Library dependent mod.

This is shipped with the Forge Base Mods Library (Since only there is required).

This essentially is a helper for those mods which do not need mod-loader specific 
initialization and helps to further isolate client from server instances in a simple manner.

This is like bringing parity with Fabric (where there you declare your mod instances as special entry points with the mod instance classes as well)

Note also that this project is not loaded, nor developed as a Minecraft project since it does not access the game itself somehow.

For older mods or for mods needing to access Forge, those mods can still use the `javafml` language provider and can keep their current initialization code as-is.

Usage:

To use this language provider, you need to instead specify `bml_java_fml` as the `modLoader` property of your `mods.toml` file.

When you specify that, any `[[mods]]` declaration you have defined must declare two more properties. 

If you finally use them both depends on the type of the mod you are working on.

-> The `server_mod_instance_class_name` property:

Use this when you want to declare a mod that is server-side only. 
The value of this property is the fully qualified class name of your mod's `IServerModInstance` interface implementation.

-> The `client_mod_instance_class_name` property:

Use this when you want to declare a mod that is client-side only.
The value of this property is the fully qualified class name of your mod's `IClientModInstance` interface implementation.

> [!NOTE]
If you want to declare a mod that uses both client and server features, you must declare both properties.

> [!NOTE]
Mods not declaring none of the aforementioned properties will never be initialized. The language provider will print a warning in the log if such case is found.

Example:

~~~TOML
modLoader="bml_java_fml"
loaderVersion="[47,)"
license="A license"
issueTrackerURL="<Issue tracker URI Here>"
[[mods]]
modId="your_mod"
version="your_version"
displayName="A friendly name of your mod"
displayURL="The home page of your mod"
logoFile="The icon of your mod.png"
credits="Credits"
authors="You!"
description='''A description '''
server_mod_instance_class_name="your_group.your_mod.ServerModInstance"
client_mod_instance_class_name="your_group.your_mod.ClientModInstance"
[[dependencies.your_mod]]
modId="forge"
mandatory=true
versionRange="[47,)"
ordering="NONE"
side="BOTH"
[[dependencies.your_mod]]
modId="minecraft"
mandatory=true
versionRange="[1.20.1,1.20.2)"
ordering="NONE"
side="BOTH"
[[dependencies.your_mod]]
modId="mdcdi1315_base_mods_lib"
mandatory=true
versionRange="[1.0.11,)"
ordering="AFTER"
side="BOTH"
~~~