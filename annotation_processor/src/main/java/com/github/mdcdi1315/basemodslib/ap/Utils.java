package com.github.mdcdi1315.basemodslib.ap;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public final class Utils
{
    private Utils() { }

    public static boolean HasNotModifier(Element e, Modifier modifier) { return !HasModifier(e, modifier); }

    public static boolean HasModifier(Element e, Modifier modifier) { return e.getModifiers().contains(modifier); }

    public static boolean DoesNotContainAnnotationType(Element e, String annotation_class_name) { return !ContainsAnnotationType(e, annotation_class_name); }

    public static boolean ContainsAnnotationType(Element e, String annotation_class_name) { return GetMirrorOfName(e, annotation_class_name) != null; }

    public static AnnotationMirror GetMirrorOfName(Element e, String annotation_class_name)
    {
        for (AnnotationMirror annotationMirror : e.getAnnotationMirrors())
        {
            if (((TypeElement)annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(annotation_class_name))
            {
                return annotationMirror;
            }
        }
        return null;
    }

    public static AnnotationValue GetElementValueOfName(AnnotationMirror mirror, String element_name)
    {
        for (var kvp : mirror.getElementValues().entrySet())
        {
            if (kvp.getKey().getSimpleName().toString().equals(element_name))
            {
                return kvp.getValue();
            }
        }
        return null;
    }

    public static boolean AsTypeIsDeclaredTypeAndIsType(Element e, String type_class_name)
    {
        return e != null && TypeMirrorIsDeclaredTypeAndIsType(e.asType(), type_class_name);
    }

    public static boolean TypeMirrorIsDeclaredTypeAndIsType(TypeMirror e, String type_class_name)
    {
        return e instanceof DeclaredType dt &&
                dt.asElement() instanceof QualifiedNameable qe &&
                qe.getQualifiedName().toString().equals(type_class_name);
    }

    private static Element FindClassElement(TypeElement class_element, ElementKind kind, String member_name)
    {
        Element temp;
        TypeElement e;
        do {
            e = null;
            for (Element g : class_element.getEnclosedElements())
            {
                if (g.getKind().equals(kind) && g.getSimpleName().toString().equals(member_name)) { return g; }
            }
            if (
                    class_element.getSuperclass() instanceof DeclaredType dt &&
                    (temp = FindClassElement(e = ((TypeElement)dt.asElement()), kind, member_name)) != null
            ) { return temp; }
            for (TypeMirror tm : class_element.getInterfaces())
            {
                if (
                    tm instanceof DeclaredType dt &&
                    (temp = FindIFaceElement((TypeElement) dt.asElement(),  kind, member_name)) != null
                ) { return temp; }
            }
        } while (e != null);
        return null;
    }

    private static Element FindIFaceElement(TypeElement iface_element, ElementKind kind, String member_name)
    {
        Element temp;
        for (Element g : iface_element.getEnclosedElements())
        {
            if (g.getKind().equals(kind) && g.getSimpleName().toString().equals(member_name)) { return g; }
        }
        for (TypeMirror tm : iface_element.getInterfaces())
        {
            if (tm instanceof DeclaredType dt &&
                    (temp = FindIFaceElement((TypeElement)dt.asElement(), kind, member_name)) != null)
            {
                return temp;
            }
        }
        return null;
    }

    public static Element GetElementOfClass(TypeElement te, ElementKind kind, String member_name)
    {
        if (te.getKind() == ElementKind.CLASS || te.getKind() == ElementKind.RECORD) {
            return FindClassElement(te, kind, member_name);
        } else if (te.getKind() == ElementKind.INTERFACE) {
            return FindIFaceElement(te, kind, member_name);
        } else {
            return null;
        }
    }
}
