package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

public record ModLicenses(String mod_id, String mod_friendly_name, IEnumerable<LicenseEntry> entries) {}