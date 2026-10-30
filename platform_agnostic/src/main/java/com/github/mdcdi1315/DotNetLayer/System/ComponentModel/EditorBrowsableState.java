package com.github.mdcdi1315.DotNetLayer.System.ComponentModel;

/**
 * Specifies the browsable state of a property or method from within an editor.
 */
public enum EditorBrowsableState
{
    /**
     * The property or method is always browsable from within an editor.
     */
    Always,
    /**
     * The property or method is never browsable from within an editor.
     */
    Never,
    /**
     * The property or method is a feature that only advanced users should see.
     * An editor can either show or hide such properties.
     */
    Advanced
}
