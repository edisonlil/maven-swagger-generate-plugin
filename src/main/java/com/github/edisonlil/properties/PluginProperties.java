package com.github.edisonlil.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author edison
 * @since 2022/05/10 20:32
 */
public class PluginProperties {


    String interimCompileSourceRoots;

    Packages packages = new Packages();


    public String getInterimCompileSourceRoots() {
        return interimCompileSourceRoots;
    }

    public void setInterimCompileSourceRoots(String interimCompileSourceRoots) {
        this.interimCompileSourceRoots = interimCompileSourceRoots;
    }

    public Packages getPackages() {
        return packages;
    }

    public static class Packages {

        List<String> controllers = new ArrayList<>();

        List<String> domains = new ArrayList<>();

        public List<String> getControllers() {
            return controllers;
        }

        public void setControllers(List<String> controllers) {
            this.controllers = controllers;
        }

        public List<String> getDomains() {
            return domains;
        }

        public void setDomains(List<String> domains) {
            this.domains = domains;
        }
    }


}
