package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import javax.tools.Diagnostic;

public final class VerifyCorrectConfigFieldUsage
    implements IAnnotationProcessorPiece
{
    @Override
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element e : round_env.getElementsAnnotatedWith(annotation))
        {
            if (Utils.HasNotModifier(e, Modifier.PUBLIC)) {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The specified config class named as \"%s\" has the configuration field \"%s\" non-public. All configuration fields must be public.",
                                        e.getEnclosingElement().getSimpleName(),
                                        e.getSimpleName()
                                )
                        );
            } else if (Utils.HasModifier(e, Modifier.STATIC)) {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The specified config class named as \"%s\" has the configuration field \"%s\" as static. Static fields cannot be used as configuration fields.",
                                        e.getEnclosingElement().getSimpleName(),
                                        e.getSimpleName()
                                )
                        );
            } else if (
                    Utils.AsTypeIsDeclaredTypeAndIsType(e, "java.util.List") &&
                    Utils.DoesNotContainAnnotationType(e, AnnotationImplementationsHandler.BML_CONFIG_PACKAGE + ".ListField")
            ) {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format(
                                        "The specified config class named as \"%s\" has the configuration field \"%s\" of list type, but does not declare the ListField annotation.",
                                        e.getEnclosingElement().getSimpleName(),
                                        e.getSimpleName()
                                )
                        );
            }
        }
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.BML_CONFIG_PACKAGE + ".ConfigField";
    }
}
