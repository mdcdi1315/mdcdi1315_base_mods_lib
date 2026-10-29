/**
 * Provides a communication layer over the Minecraft command system. <br />
 * The basic abstraction here is the {@link com.github.mdcdi1315.basemodslib.commands.AbstractCommand}
 * class, which is an abstract class that mods can depend on to build their commands.
 */
@MinecraftDependentFeature
@APIStatus(Status = APIStatus.Status.STABLE)
package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.utils.annotations.MinecraftDependentFeature;
