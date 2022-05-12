package com.github.edisonlil.delegate;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

/**
 * description
 *
 * @author edison
 * @since 2022/05/12 15:54
 */
public class FieldDelegate {

    FieldDeclaration declaration;


    public FieldDelegate(FieldDeclaration declaration){
        this.declaration = declaration;
    }

    public static FieldDelegate build(FieldDeclaration declaration){
        return new FieldDelegate(declaration);
    }

    public AnnotationDelegate addAndGetAnnotation(String name){
        AnnotationDelegate annotation = findAnnotation(name);
        if(annotation != null){
            return annotation;
        }
        return AnnotationDelegate.build(declaration.addAndGetAnnotation(name));
    }

    public AnnotationDelegate findAnnotation(String name){
        AnnotationExpr annotationExpr = declaration.getAnnotationByName(name).orElse(null);
        if(annotationExpr == null) return null;
        return AnnotationDelegate.build((NormalAnnotationExpr) annotationExpr);
    }


}
