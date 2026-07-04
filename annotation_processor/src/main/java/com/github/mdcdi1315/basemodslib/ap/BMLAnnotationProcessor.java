package com.github.mdcdi1315.basemodslib.ap;

import com.github.mdcdi1315.basemodslib.ap.impl.AnnotationImplementationsHandler;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import java.util.Set;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.tools.Diagnostic;

public final class BMLAnnotationProcessor
    extends AbstractProcessor
{
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        AnnotationImplementationsHandler.Initialize();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        boolean owns_them = true;
        for (TypeElement anno_type : annotations)
        {
            String s = anno_type.getQualifiedName().toString();
            IAnnotationProcessorPiece p = AnnotationList.GetPiece(s);
            if (p == null) {
                owns_them = false;
            } else {
                try {
                    p.Process(anno_type, roundEnv, processingEnv);
                } catch (Exception e) {
                    var sw = new StringWriter(100);
                    e.printStackTrace(new PrintWriter(sw));
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.WARNING,
                            String.format("Error processing annotation processor for annotation %s: \n%s", s, sw)
                    );
                }
            }
        }

        return owns_them;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() { return AnnotationList.ConstructSupportedAnnotations(); }
}
