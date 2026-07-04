package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.QualifiedNameable;

import javax.tools.Diagnostic;

public final class VerifyCorrectMaybeNullInMixinUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            if (Utils.ContainsAnnotationType(e.getEnclosingElement(), AnnotationImplementationsHandler.MIXIN_DECLARATION_ANNOTATION))
            {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "A MaybeNullInMixin annotation was declared into the Mixin class named as %s. This is invalid.\nMember Name: %s",
                                        ((QualifiedNameable)e.getEnclosingElement()).getQualifiedName(),
                                        e.getSimpleName()
                                )
                        );
            }
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.BML_ANNOTATIONS_PACKAGE + ".MaybeNullInMixin";
    }
}
