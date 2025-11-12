package com.github.mdcdi1315.basemodslib.entity.sensing;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

/**
 * Provides a way for registering new sensor types to Minecraft.
 * @param sensor_type_getter A function that provides the type of the sensor to register.
 * @param <T> The type of the sensor to be registered. Can be a custom or existing type.
 * @since 1.0.5
 */
public record SensorTypeRegistrationInfo<T extends Sensor<?>>(
        @NotNull Func1<SensorType<T>> sensor_type_getter
) {
    /**
     * Constructs a new instance of the {@link SensorTypeRegistrationInfo} class.
     * @param sensor_type_getter The function that provides the type of the sensor to register.
     * @throws ArgumentNullException {@code sensor_type_getter} is {@code null}.
     */
    public SensorTypeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(sensor_type_getter, "sensor_type_getter");
    }
}
