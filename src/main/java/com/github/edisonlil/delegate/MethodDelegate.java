package com.github.edisonlil.delegate;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author edison
 * @since 2022/05/11 13:33
 */
public class MethodDelegate {

    MethodDeclaration declaration;

    Map<String, Parameter> parameters;


    public MethodDelegate(MethodDeclaration declaration){
        this.declaration = declaration;
        parameters = parameters();
    }

    public static MethodDelegate build(MethodDeclaration declaration){
        return new MethodDelegate(declaration);
    }

    public JavaDocDelegate javaDoc(){
        return JavaDocDelegate.build(declaration.getJavadoc());
    }

    public ParameterDelegate findParameter(String name){

        Parameter parameter = parameters.get(name);
        if(parameter == null) return null;

        return new ParameterDelegate(parameter);
    }

    public AnnotationDelegate findAnnotation(String name){
        AnnotationExpr annotationExpr = declaration.getAnnotationByName(name).orElse(null);
        if(annotationExpr == null) return null;
        return AnnotationDelegate.build((NormalAnnotationExpr) annotationExpr);
    }


    private Map<String, Parameter> parameters(){

        Map<String,Parameter> map = new HashMap<>();
        List<Parameter> parameterList = declaration.getParameters();

        for (Parameter parameter : parameterList) {
            map.put(parameter.getName().asString(),parameter);
        }
        return map;
    }


    public Node setAnnotations(NodeList annotations) {
        return declaration.setAnnotations(annotations);
    }

    public AnnotationDelegate addAndGetAnnotation(String name){
        AnnotationDelegate annotation = findAnnotation(name);
        if(annotation != null){
            return annotation;
        }
        return AnnotationDelegate.build(declaration.addAndGetAnnotation(name));
    }

    public NodeList<Parameter> getParameters() {
        return declaration.getParameters();
    }

    public Node setParameters(NodeList nodeList) {
        return declaration.setParameters(nodeList);
    }
}
