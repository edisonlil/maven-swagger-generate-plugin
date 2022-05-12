package com.github.edisonlil.delegate;

import cn.hutool.core.util.StrUtil;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author edison
 * @since 2022/05/11 14:09
 */
public class AnnotationDelegate {

    NormalAnnotationExpr ann;

    Map<String,MemberValuePair> pairs;


    public static AnnotationDelegate build(NormalAnnotationExpr ann){
        return new AnnotationDelegate(ann);
    }

    public static AnnotationDelegate build(String ann){
        return new AnnotationDelegate(new NormalAnnotationExpr(StaticJavaParser.parseName(ann),new NodeList<>()));
    }

    public AnnotationDelegate(NormalAnnotationExpr ann){
        this.ann = ann;
        this.pairs = pairs();
    }

    public MemberValuePair findPairs(String name){
        return pairs.getOrDefault(name,null);
    }

    private Map<String,MemberValuePair> pairs(){

        Map<String,MemberValuePair> map = new HashMap<>();
        NodeList<MemberValuePair> list = ann.getPairs();

        for (MemberValuePair pair : list) {
            map.put(pair.getName().asString(),pair);
        }
        return map;
    }

    public AnnotationDelegate addPair(String key, String value) {
        if(StrUtil.isBlank(key) || value == null) return this;
        return addPair(key, new NameExpr(value));
    }

    public AnnotationDelegate addPair(String key, Expression value) {
        if(StrUtil.isBlank(key) || value == null) return this;
        if(pairs.containsKey(key)){
            pairs.get(key).setValue(value);
            return this;
        }

        ann.addPair(key,value);
        return this;
    }

    public NormalAnnotationExpr getNormalAnnotationExpr(){
        return ann;
    }


}
