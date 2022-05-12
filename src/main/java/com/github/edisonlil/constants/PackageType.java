package com.github.edisonlil.constants;

import cn.hutool.core.util.StrUtil;

/**
 * description
 *
 * @author edison
 * @since 2022/05/11 14:53
 */
public enum PackageType {

    CONTROLLER("controller"),

    DOMAIN("domain");


    String name;


    public String getName() {
        return name;
    }

    PackageType(String name){
        this.name = name;
    }
    
    public PackageType get(String name){

        if(StrUtil.isBlank(name)){
            return null;
        }

        PackageType[] values = PackageType.values();

        for (PackageType value : values) {
            if(value.name.equals(name)){
                return value;
            }
        }
        return null;
    }
}
