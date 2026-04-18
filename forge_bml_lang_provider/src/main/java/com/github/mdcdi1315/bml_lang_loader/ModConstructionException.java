package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModConstructionException
    extends ModLoadingException
{
    private final String actual_msg, simple_msg;

    public ModConstructionException(IModInfo modInfo, ModLoadingStage errorStage, String message_detailed, String message, Throwable originalException) {
        super(modInfo, errorStage, null, originalException);
        simple_msg = message;
        actual_msg = message_detailed;
    }

    @Override
    public String getMessage() { return simple_msg; }

    @Override
    public String formatToString() { return actual_msg; }

    @Override
    public String getCleanMessage() { return actual_msg; }
}
