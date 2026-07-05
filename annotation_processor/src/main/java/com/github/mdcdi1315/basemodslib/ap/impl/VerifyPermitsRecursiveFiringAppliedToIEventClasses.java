package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.element.TypeElement;

import javax.tools.Diagnostic;

public final class VerifyPermitsRecursiveFiringAppliedToIEventClasses
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        var tu = processing_env.getTypeUtils();
        TypeMirror event_iface_mirror = processing_env.getElementUtils().getTypeElement(AnnotationImplementationsHandler.BML_BASE_PACKAGE + ".eventapi.IEvent").asType();
        for (Element element : round_env.getElementsAnnotatedWith(annotation))
        {
            if (!tu.isAssignable(element.asType(), event_iface_mirror))
            {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The PermitsRecursiveFiring annotation cannot be applied to the class named as %s because it does not implement the IEvent interface.",
                                        ((TypeElement)element).getQualifiedName()
                                )
                        );
            }
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.BML_BASE_PACKAGE + ".eventapi.PermitsRecursiveFiring";
    }
}
