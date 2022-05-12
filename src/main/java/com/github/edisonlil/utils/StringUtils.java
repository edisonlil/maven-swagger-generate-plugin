package com.github.edisonlil.utils;

import cn.hutool.core.util.StrUtil;

/**
 * description
 *
 * @author edison
 * @since 2022/05/12 15:09
 */
public class StringUtils {

    public static String toLiteral(String str){
        if(StrUtil.isBlank(str)) return null;

        str = str.replace("\r\n","")
                .replace("\n","")
                .replace("\"","'");
        return "\""+str+"\"";
    }
}
