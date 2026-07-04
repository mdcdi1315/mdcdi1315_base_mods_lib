package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;

import javax.tools.Diagnostic;

public final class VerifyCorrectPackageIsDotNetNamespaceUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element roots : round_env.getRootElements())
        {
            if (
                roots instanceof PackageElement pe &&
                Utils.ContainsAnnotationType(pe, GetName()) &&
                (!pe.getQualifiedName().toString().startsWith(AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE))
            ) {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "Package %s cannot be a .NET Layer namespace because is not into the DotNetLayer package.",
                                        pe.getQualifiedName()
                                )
                        );
            }
        }
    }

    @Override
    public String GetName() { return AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".PackageIsDotNetNamespace"; }
}
