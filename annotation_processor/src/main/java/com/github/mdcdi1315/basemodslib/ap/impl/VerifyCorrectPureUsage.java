package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;

import javax.tools.Diagnostic;

public final class VerifyCorrectPureUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            if (e instanceof TypeElement te) {
                for (Element class_e : te.getEnclosedElements())
                {
                    if (class_e.getKind() == ElementKind.METHOD)
                    {
                        ExecutableElement method = (ExecutableElement) class_e;
                        if (!Utils.ContainsAnnotationType(method, GetName())) {
                            processing_env.getMessager()
                                    .printMessage(
                                            Diagnostic.Kind.ERROR,
                                            String.format(
                                                    "Declared the class %s as pure, but the method %s is not pure.",
                                                    te.getQualifiedName(),
                                                    method.getSimpleName()
                                            )
                                    );
                        } else {
                            ExecutableElementIsPure(method, processing_env);
                        }
                    }
                }
            } else if (e instanceof ExecutableElement ee) {
                ExecutableElementIsPure(ee, processing_env);
            }
        }
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    private void ExecutableElementIsPure(ExecutableElement ee, ProcessingEnvironment env)
    {
        if (ee.getThrownTypes().size() > 0)
        {
            String class_name = ((TypeElement)ee.getEnclosingElement()).getQualifiedName().toString();
            env.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    String.format(
                            "The method with name %s in class %s cannot be pure because it throws custom exceptions.",
                            ee.getSimpleName(),
                            class_name
                    )
            );
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.BML_ANNOTATIONS_PACKAGE + ".Pure";
    }
}
