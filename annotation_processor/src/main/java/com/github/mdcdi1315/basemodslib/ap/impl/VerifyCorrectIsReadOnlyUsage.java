package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.*;

import javax.tools.Diagnostic;

public final class VerifyCorrectIsReadOnlyUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element element : round_env.getElementsAnnotatedWith(annotation))
        {
            for (Element member : element.getEnclosedElements())
            {
                if (
                        member.getKind() == ElementKind.FIELD &&
                        Utils.HasNotModifier(member, Modifier.STATIC) &&
                        Utils.HasNotModifier(member, Modifier.FINAL)
                ) {
                    processing_env
                            .getMessager()
                            .printMessage(
                                    Diagnostic.Kind.ERROR,
                                    String.format(
                                            "The field %s in class %s is not 'final'.",
                                            member.getSimpleName(),
                                            ((QualifiedNameable)element).getQualifiedName()
                                    )
                            );
                }
            }
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".System.Runtime.CompilerServices.IsReadOnly";
    }
}
