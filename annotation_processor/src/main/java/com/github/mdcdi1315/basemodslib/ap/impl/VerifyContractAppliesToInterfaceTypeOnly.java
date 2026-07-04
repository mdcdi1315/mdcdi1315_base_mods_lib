package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.tools.Diagnostic;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public final class VerifyContractAppliesToInterfaceTypeOnly
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            if (e.getKind() != ElementKind.INTERFACE)
            {
                processing_env.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format(
                                "The Contract annotation can only be applied to interfaces, but it was applied to the class of name %s.",
                                ((TypeElement)e).getQualifiedName()
                        )
                );
            }
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.BML_BASE_PACKAGE + ".Contract";
    }
}
