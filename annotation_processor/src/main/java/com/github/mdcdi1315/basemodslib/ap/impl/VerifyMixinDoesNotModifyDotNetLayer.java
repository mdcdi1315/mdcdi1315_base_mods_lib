package com.github.mdcdi1315.basemodslib.ap.impl;

import com.github.mdcdi1315.basemodslib.ap.Utils;
import com.github.mdcdi1315.basemodslib.ap.IAnnotationProcessorPiece;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.*;

import javax.lang.model.type.DeclaredType;

import javax.tools.Diagnostic;

import java.util.List;

public final class VerifyMixinDoesNotModifyDotNetLayer
    implements IAnnotationProcessorPiece
{
    @Override
    @SuppressWarnings("unchecked")
    public void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env)
            throws Exception
    {
        for (Element element : round_env.getElementsAnnotatedWith(annotation))
        {
            Name class_name = element instanceof QualifiedNameable qn ? qn.getQualifiedName() : element.getSimpleName();
            AnnotationMirror mirror = Utils.GetMirrorOfName(element, GetName());
            if (mirror == null) {
                processing_env
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.WARNING,
                                String.format(
                                        "Couldn't find Mixin annotation in class %s - probably the environment is corrupt!",
                                        class_name
                                )
                        );
            } else {
                boolean emitted_error = false;
                AnnotationValue v = Utils.GetElementValueOfName(mirror, "value");
                if (v != null)
                {
                    for (AnnotationValue class_value : (List<AnnotationValue>) v.getValue())
                    {
                        if (
                                class_value.getValue() instanceof DeclaredType dt &&
                                ((TypeElement)dt.asElement()).getQualifiedName().toString().startsWith(AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE)
                        ) {
                            emitted_error = true;
                            EmitError(class_name, processing_env);
                            break;
                        }
                    }
                }
                if (emitted_error) { continue; }
                v = Utils.GetElementValueOfName(mirror, "targets");
                if (v != null)
                {
                    for (AnnotationValue class_value : (List<AnnotationValue>) v.getValue())
                    {
                        if (((String)class_value.getValue()).startsWith(AnnotationImplementationsHandler.DOTNET_LAYER_PACKAGE))
                        {
                            EmitError(class_name, processing_env);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void EmitError(Name class_name, ProcessingEnvironment processing_env)
    {
        processing_env
                .getMessager()
                .printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format(
                                "The class %s attempted to define a Mixin against the .NET Layer system. This is not allowed as it may corrupt the state of the layer at run-time.",
                                class_name
                        )
                );
    }

    @Override
    public String GetName()
    {
        return AnnotationImplementationsHandler.MIXIN_DECLARATION_ANNOTATION;
    }
}
