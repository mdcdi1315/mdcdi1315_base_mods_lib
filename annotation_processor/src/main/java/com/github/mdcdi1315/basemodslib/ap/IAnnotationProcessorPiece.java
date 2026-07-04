package com.github.mdcdi1315.basemodslib.ap;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.TypeElement;

public interface IAnnotationProcessorPiece
{
    void Process(TypeElement annotation, RoundEnvironment round_env, ProcessingEnvironment processing_env) throws Exception;

    String GetName();
}
