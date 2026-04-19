package com.github.mdcdi1315.bml_lang_loader;

import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.neoforgespi.language.IModInfo;

public class ModConstructionException
    extends ModLoadingException
{
    private final IModInfo mod_info;
    private final String actual_msg, simple_msg;

    public ModConstructionException(IModInfo modInfo, String message_detailed, String message, Throwable originalException)
    {
        super(
                ModLoadingIssue
                        .error("fml.modloadingissue.failedtoloadmod")
                        .withCause(originalException)
                        .withAffectedMod(modInfo)
        );
        this.initCause(originalException);
        simple_msg = message;
        this.mod_info = modInfo;
        actual_msg = message_detailed;
    }

    @Override
    public String getMessage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Unexpected error encountered:\n");
        builder.append(simple_msg);
        builder.append('\n');
        builder.append("Details: ");
        builder.append(actual_msg);
        builder.append('\n');
        builder.append(super.getMessage());
        return builder.toString();
    }

    public IModInfo getModInfo() { return mod_info; }

    public String getActualMessage() { return actual_msg; }
}
