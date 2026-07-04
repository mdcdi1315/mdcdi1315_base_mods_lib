package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

import javax.tools.Diagnostic;

public final class VerifyCorrectClassIsDotNetStructUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            if (Utils.HasNotModifier(e, Modifier.FINAL)) {
                processing_env.getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The class named as %s is not marked as 'final'. .NET structures cannot be further extended.",
                                        e.getSimpleName()
                                )
                        );
            } else if (
                    ((TypeElement)e).getNestingKind() == NestingKind.MEMBER &&
                    Utils.HasNotModifier(e, Modifier.STATIC)
            ) {
                processing_env.getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The class named as %s is not marked as 'static'. .NET structures cannot be further extended, and cannot inherit properties from their nested classes.",
                                        e.getSimpleName()
                                )
                        );
            } else if (!Utils.TypeMirrorIsDeclaredTypeAndIsType(((TypeElement)e).getSuperclass(), AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".System.ValueType"))
            {
                processing_env.getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The class named as %s does not extend from ValueType. All .NET structures must extend from the System.ValueType class.",
                                        e.getSimpleName()
                                )
                        );
            }
        }
    }

    @Override
    public String GetName() { return AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".ClassIsDotNetStruct"; }
}
