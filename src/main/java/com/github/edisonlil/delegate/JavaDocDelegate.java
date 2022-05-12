package com.github.edisonlil.delegate;

import com.github.edisonlil.utils.StringUtils;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author edison
 * @since 2022/05/10 21:22
 */
public class JavaDocDelegate {


    Javadoc javadoc;

    private JavaDocDelegate(Javadoc javadoc){
        this.javadoc = javadoc;
    }

    public static JavaDocDelegate build(Optional<Javadoc> javadoc){
        return new JavaDocDelegate(javadoc.orElseGet(()-> null));
    }
    public static JavaDocDelegate build(JavadocDescription javadoc){
        return new JavaDocDelegate(new Javadoc(javadoc));
    }


    public String description(){
        if(javadoc == null) return null;
        String text = javadoc.getDescription().toText();
        return StringUtils.toLiteral(text);
    }



    public List<JavadocBlockTag> tag(JavadocBlockTag.Type type){
        if(javadoc == null) return new ArrayList<>();
        return javadoc.getBlockTags().stream().filter((item)->{

            if(item == null){
                return false;
            }
            return type.equals(item.getType());
        }).collect(Collectors.toList());
    }

}
