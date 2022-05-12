package com.github.edisonlil.delegate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.io.File;
import java.nio.charset.Charset;

/**
 * description
 *
 * @author edison
 * @since 2022/05/11 15:47
 */
public class CompilationDelegate{

    CompilationUnit declaration;

    File source;

    public CompilationDelegate(File source, CompilationUnit declaration){
        this.declaration = declaration;
        this.source = source;
    }

    public static CompilationDelegate build(File source,CompilationUnit cu){
        return new CompilationDelegate(source,cu);
    }


    public CompilationUnit compilationUnit() {
        return declaration;
    }

    public void flush(){
        FileUtil.writeBytes(declaration.toString().getBytes(Charset.defaultCharset()),source);
    }


    public String getPackageName(){
        try {
            return declaration.getPackageDeclaration().orElseGet(()->null)
                    .getName().asString();
        }catch (Exception e){
            return null;
        }
    }

    public JavaDocDelegate getJavaDoc(){

        TypeDeclaration type = getTypeDeclaration();
        if(type != null){
            return JavaDocDelegate.build(type.getJavadoc());
        }

        return null;
    }


    public TypeDeclaration getTypeDeclaration(){
        NodeList<TypeDeclaration<?>> type = declaration.getTypes();
        if(!CollUtil.isEmpty(type)){
            return type.get(0);
        }
        return null;
    }

    public AnnotationDelegate addAndGetAnnotation(String name){

        TypeDeclaration type = getTypeDeclaration();
        if(type != null){
            AnnotationDelegate annotation = findAnnotation(name);
            if(annotation != null){
                return annotation;
            }
           return AnnotationDelegate.build(type.addAndGetAnnotation(name));
        }
        return null;
    }

    public AnnotationDelegate findAnnotation(String name){

        TypeDeclaration type = getTypeDeclaration();
        if(type == null) return null;

        AnnotationExpr annotationExpr = (AnnotationExpr) type.getAnnotationByName(name).orElse(null);
        if(annotationExpr == null) return null;
        return AnnotationDelegate.build((NormalAnnotationExpr) annotationExpr);
    }

}
