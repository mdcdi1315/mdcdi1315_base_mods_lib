package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.AnnotationList;

public final class AnnotationImplementationsHandler
{
    private AnnotationImplementationsHandler() {}

    public static final String ROOT_PACKAGE = "com.github.mdcdi1315";

    public static final String DOTNET_LAYER_PACKAGE = ROOT_PACKAGE + ".DotNetLayer";

    public static final String BML_BASE_PACKAGE = ROOT_PACKAGE + ".basemodslib";
    public static final String BML_CONFIG_PACKAGE = BML_BASE_PACKAGE + ".config";
    public static final String BML_ANNOTATIONS_PACKAGE = BML_BASE_PACKAGE + ".utils.annotations";

    public static final String MIXIN_DECLARATION_ANNOTATION = "org.spongepowered.asm.mixin.Mixin";

    public static void Initialize()
    {
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectPureUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectIsReadOnlyUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectConfigFieldUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectMaybeNullInMixinUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectOverloadedMethodUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyMixinDoesNotModifyDotNetLayer());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectClassIsDotNetStructUsage());
        AnnotationList.RegisterAnnotationToSearch(new VerifyContractAppliesToInterfaceTypeOnly());
        AnnotationList.RegisterAnnotationToSearch(new VerifyCorrectPackageIsDotNetNamespaceUsage());
    }
}
