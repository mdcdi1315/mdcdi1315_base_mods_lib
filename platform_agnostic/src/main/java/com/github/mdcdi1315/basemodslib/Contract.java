package com.github.mdcdi1315.basemodslib;

import java.lang.annotation.*;

/**
 * Marker annotation interface for defining that the specified interface defines a Base Mods Library contract. <br />
 * Contracts are, by library definition, abstracted interfaces that their actions translate to different mod loader actions,
 * depending on the mod loader that the library itself is loaded as.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Contract { }
