/**
 * Provides a solution for cross-loader configuration files, using the Mojang's serialization library in order to do that. <br />
 *
 * In more depth, this API aims to be a solution for mod configuration files without concerning how these will be managed. <br />
 * It is end-to-end extensible, and it does provide support for most common cases.
 *
 * <h3>Config File Management</h3>
 * The Configuration File Manager manages configuration files for all the mods loaded through the BML. <br />
 * Note that the BML fires the {@link com.github.mdcdi1315.basemodslib.mods.IModInstance#SetupConfigurationFiles(ConfigManager)} method
 * when the Configuration File Manager is ready to process a mod instance. <br />
 * While the Configuration File Manager is a singleton obtained through the {@link com.github.mdcdi1315.basemodslib.config.ConfigManager#INSTANCE} field,
 * it should be noted down that this cannot be retrieved during mod loading as it can cause obscure loading issues due to the library not loaded yet. <br />
 * Note, you can get the value of the field only when the {@link com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent} is firing or after fired by the library.
 *
 * <h3>Annotation-based configuration specification</h3>
 * Each of the configuration files that a mod loads and saves is represented in Java
 * as a class implementing the {@link com.github.mdcdi1315.basemodslib.config.IModConfig}
 * interface and that it contains public fields controlled through annotation types. <br />
 * To further verify that a field is a persistent field (That is, saved to the configuration file),
 * the field needs to be marked with the {@link com.github.mdcdi1315.basemodslib.config.ConfigField} annotation type. <br />
 * This allows the Configuration File Manager to sort which fields are needed when the Configuration Record is written. <br />
 * The Configuration Record is rather a snapshot of the configuration data taken during writing.
 *
 * <h3>Validation of configuration files input</h3>
 * Although that the Mojang's Serialization library is very complete on type check support,
 * this does not stand true for the values of the file itself. The file may be modified
 * when it is saved in a long-term storage. <br />
 * To ensure that the file is loaded and saved correctly, Configuration
 * Constraints are applied to each field of the configuration file. <br />
 * They are declared through the {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.ConfigFieldConstraintCreator}
 * interface and are constructed as {@link com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint}
 * instances loaded through annotations applied to the fields themselves. <br />
 * Apart from the long-term storage part, they also aid to catch run-time invalid cases, such as if a string in a configuration class is {@code null}.
 *
 * <h3>Configuration File Format Extensibility</h3>
 * You may also provide your own file format for saving configuration files, note however that this requires to provide a {@link com.mojang.serialization.DynamicOps} instance. <br />
 * By default, the library adds support for the JSON file format through the GSON library. <br />
 * If you want to develop your own configuration file format, look at the {@link com.github.mdcdi1315.basemodslib.config.IConfigFileFormat} interface for how to do this.
 *
 * <h3>Overall system extensibility</h3>
 * Mod developers can also extend this system by declaring new configuration field mappings at the {@link com.github.mdcdi1315.basemodslib.config.reflect.ConfigFieldCodecRegistry} class. <br />
 * This allows for end-to-end customization and provides complete control over how the configuration file is stored. <br />
 * For simple cases, common, and default configuration field codec mappings are provided for all the mods that depend on the BML itself.
 * For declaring a new custom configuration field, see the {@link com.github.mdcdi1315.basemodslib.config.reflect.ConfigFieldCodecRegistry#AddCodecMapping(Class, com.mojang.serialization.Codec)} method. <br />
 * Additionally, since BML 1.0.26, the mod developers are now able to provide their own {@link com.mojang.serialization.Codec} for de/encoding their configuration data.
 *
 * <h3>Configuration files and GUI's</h3>
 * Apart from all these features, the library does also provide interconnection routines with
 * GUI on Minecraft clients, for editing configuration files in an interactive manner. <br />
 * This is done through a handful of support classes, most notably the
 * {@link com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory} class
 * that is able to arbitrarily create configuration screens. <br />
 * To even spice it up further, the library provides an implementation of the
 * aforementioned class by using the <a href="https://www.curseforge.com/minecraft/mc-mods/cloth-config">Cloth Config API</a> mod.
 * If, however, this is not enough or does not meet the average expectations, the ability to register a custom configuration
 * screen factory class is also available. <br />
 * To also somehow bring a consistent parity across mod-loaders, for Fabric only, the library provides integration with the
 * <a href="https://www.curseforge.com/minecraft/mc-mods/modmenu">ModMenu</a>
 * mod to unify the configuration screen factory access and creation.
 */
package com.github.mdcdi1315.basemodslib.config;