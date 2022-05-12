package com.github.edisonlil.utils;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * description
 *
 * @author edison
 * @since 2022/05/10 20:36
 */
public class FileIterable implements Iterable<File>{

    File root;

    public static void main(String[] args) {

        new FileIterable(new File("C:\\Users\\edsio\\Documents\\武汉公司资料")).forEach(file -> {

            System.out.println(file.getPath());
        });


    }

    public FileIterable(File file){
        root = file;
    }

    @Override
    public Iterator<File> iterator() {
        throw new RuntimeException();
    }



    @Override
    public void forEach(Consumer<? super File> action) {
        search(root,action);
    }


    public void search(File f,Consumer<? super File> action){

        File root = f;

        if(root == null){
            return;
        }

        for (File file : root.listFiles()) {

            if(file.isDirectory()){
                search(file,action);
            }else {
                action.accept(file);
            }
        }

    }
}
