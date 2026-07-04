package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.*;

import javax.tools.Diagnostic;

import java.util.Objects;

public final class VerifyCorrectOverloadedMethodUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            ExecutableElement method = (ExecutableElement)e;
            TypeElement declaring_class = (TypeElement)method.getEnclosingElement();
            AnnotationMirror anno = Utils.GetMirrorOfName(method, GetName());
            if (anno == null) {
                throw new IllegalStateException("Cannot find the annotation declaration in method element.");
            } else {
                String method_name = (String) Objects.requireNonNull(Utils.GetElementValueOfName(anno, "value")).getValue();
                if (Utils.GetElementOfClass(declaring_class, ElementKind.METHOD, method_name) == null)
                {
                    processing_env
                            .getMessager()
                            .printMessage(
                                    Diagnostic.Kind.ERROR,
                                    String.format(
                                            "Cannot find method named as %s in class %s, from which the overloaded method %s is declared.",
                                            method_name,
                                            declaring_class.getQualifiedName(),
                                            method.getSimpleName()
                                    )
                            );
                }
            }
        }
    }

    @Override
    public String GetName() { return AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE + ".OverloadedMethod"; }
}
