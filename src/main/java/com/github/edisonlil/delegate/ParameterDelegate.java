package com.github.edisonlil.delegate;


import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author edison
 * @since 2022/05/11 14:12
 */
public class ParameterDelegate {

    Parameter parameter;


    final static Map<String,Class> classMap = new HashMap<>();

    static {
        classMap.put("Long",Long.class);
        classMap.put("Integer",Integer.class);
        classMap.put("String",String.class);
    }

    public ParameterDelegate(Parameter parameter){
        this.parameter = parameter;
    }

    public AnnotationDelegate findAnnotationByName(String name){

        AnnotationExpr annotation = parameter.getAnnotationByName(name).orElseGet(()-> null);

        if(annotation == null){
            return null;
        }

        if(annotation instanceof NormalAnnotationExpr){
            return new AnnotationDelegate((NormalAnnotationExpr)annotation);
        }
        return null;
    }

    public String getParameterClass(){

        String typeStr = parameter.getTypeAsString();

        if(StrUtil.isBlank(typeStr)){
            return null;
        }

        if(typeStr.indexOf('<') != -1){
            return typeStr.substring(0,typeStr.indexOf('<'))+".class";
        }

        return typeStr+".class";


    }

}
