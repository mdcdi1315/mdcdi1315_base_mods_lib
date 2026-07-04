package com.github.mdcdi1315.basemodslib.ap;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.LinkedList;

public final class AnnotationList
{
    private static final LinkedList<IAnnotationProcessorPiece> annotations = new LinkedList<>();

    private AnnotationList() {}

    public static void RegisterAnnotationToSearch(IAnnotationProcessorPiece piece)
    {
        Objects.requireNonNull(piece, "piece cannot be null");
        annotations.add(piece);
    }

    public static IAnnotationProcessorPiece GetPiece(String annotation_name)
    {
        Objects.requireNonNull(annotation_name, "annotation name cannot be null");
        for (IAnnotationProcessorPiece annotation : annotations)
        {
            if (annotation_name.equals(annotation.GetName()))
            {
                return annotation;
            }
        }
        return null;
    }

    public static Set<String> ConstructSupportedAnnotations()
    {
        HashSet<String> annotations = new HashSet<>(AnnotationList.annotations.size());
        for (IAnnotationProcessorPiece p : AnnotationList.annotations)
        {
            annotations.add(p.GetName());
        }
        return annotations;
    }
}
