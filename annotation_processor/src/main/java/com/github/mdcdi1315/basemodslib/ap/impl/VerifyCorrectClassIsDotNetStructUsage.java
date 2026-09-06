package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.*;

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
            Name name = e instanceof QualifiedNameable qe ? qe.getQualifiedName() : e.getSimpleName();
            if (Utils.HasNotModifier(e, Modifier.FINAL)) {
                processing_env.getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The class named as %s is not marked as 'final'. .NET structures cannot be further extended.",
                                        name
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
                                        name
                                )
                        );
            } else if (!Utils.TypeMirrorIsDeclaredTypeAndIsType(((TypeElement)e).getSuperclass(), AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".System.ValueType"))
            {
                processing_env.getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The class named as %s does not extend from ValueType. All .NET structures must extend from the System.ValueType class.",
                                        name
                                )
                        );
            } else {
                boolean at_least_one_ctor = false;
                boolean found_parameterless_ctor = false;
                var sub_elements = e.getEnclosedElements();
                for (var sub_element : sub_elements)
                {
                    if (sub_element.getKind() == ElementKind.CONSTRUCTOR)
                    {
                        at_least_one_ctor = true;
                        if (Utils.HasModifier(sub_element, Modifier.PUBLIC) && ((ExecutableElement)sub_element).getParameters().isEmpty())
                        {
                            found_parameterless_ctor = true;
                        }
                    }
                }
                if (at_least_one_ctor && (!found_parameterless_ctor))
                {
                    processing_env.getMessager()
                            .printMessage(
                                    Diagnostic.Kind.ERROR,
                                    String.format(
                                            "The class named as %s does not declare a public, parameterless constructor. All .NET structures must provide a public parameterless constructor.",
                                            name
                                    )
                            );
                }
            }
        }
    }

    @Override
    public String GetName() { return AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".ClassIsDotNetStruct"; }
}
