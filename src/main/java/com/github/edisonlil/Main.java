package com.github.edisonlil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.github.edisonlil.constants.PackageType;
import com.github.edisonlil.delegate.*;
import com.github.edisonlil.properties.PluginProperties;
import com.github.edisonlil.utils.FileIterable;
import com.github.edisonlil.utils.StringUtils;
import com.github.edisonlil.utils.TaskExecutor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * description
 *
 * @author edison
 * @since 2022/05/10 20:30
 */
@Mojo(name = "start")
public class Main extends AbstractMojo {

//    @Inject
//    private MavenProject project;

    /**
     * The source directories containing the sources to be compiled.
     */
    @Parameter( defaultValue = "${project.compileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    /**
     * 插件配置信息
     */
    @Parameter(name = "properties")
    PluginProperties properties;

//    public Main(MavenProject project) {
////        this.project = project;
//    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {


        for (String compileSourceRoot : compileSourceRoots) {

            String tmpCompileSourceRoots = properties.getInterimCompileSourceRoots();
            if(!StrUtil.isBlank(tmpCompileSourceRoots)){
                FileUtil.copy(compileSourceRoot,properties.getInterimCompileSourceRoots(),true);
                compileSourceRoot = tmpCompileSourceRoots;
            }

            Map<PackageType,List<CompilationDelegate>> packageCusMap = doScanner(compileSourceRoot);

            try {
                TaskExecutor.create(2)
                        .submit(()->contrProcess(packageCusMap.get(PackageType.CONTROLLER)))
                        .submit(()->domainProcess(packageCusMap.get(PackageType.DOMAIN))).call();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean contrProcess(List<CompilationDelegate> cus){

        for (CompilationDelegate delegate : cus) {

            CompilationUnit cu = delegate.compilationUnit();

            cu.addImport("io.swagger.annotations.*");


            AnnotationDelegate apiAnn = delegate.addAndGetAnnotation("Api");
            apiAnn.addPair("value",delegate.getJavaDoc().description());




            //方法注解加入
            cu.findAll(MethodDeclaration.class).forEach(m -> {

                MethodDelegate method = MethodDelegate.build(m);

                String methodJavaDocDesc = method.javaDoc().description();

                if(StrUtil.isNotBlank(methodJavaDocDesc)){
                    method.addAndGetAnnotation("ApiOperation")
                            .addPair("value",methodJavaDocDesc);
                }

                List<JavadocBlockTag> list = method.javaDoc().tag(JavadocBlockTag.Type.PARAM);

                if(!list.isEmpty()){

                    AnnotationDelegate apiImplicitParams = method.addAndGetAnnotation("ApiImplicitParams");

                    NodeList<Expression> values = new NodeList<>();
                    list.forEach(item->{
                        String name = item.getName().orElse(null);

                        AnnotationDelegate ann = AnnotationDelegate.build("ApiImplicitParam")
                                .addPair("name", StringUtils.toLiteral(item.getName().get()))
                                .addPair("value", JavaDocDelegate.build(item.getContent()).description());

                        values.add(ann.getNormalAnnotationExpr());

                        if(!StrUtil.isBlank(name)){
                            ParameterDelegate parameter = method.findParameter(name);
                            if(parameter != null && StrUtil.isBlank(parameter.getParameterClass())){
                                ann.addPair("dataTypeClass",parameter.getParameterClass());
                            }
                        }

                    });
                    ArrayInitializerExpr apiImplicitParam = new ArrayInitializerExpr(values);
                    apiImplicitParams.addPair("value",apiImplicitParam);
                }

            });

            delegate.flush();
        }

        return true;
    }

    private boolean domainProcess(List<CompilationDelegate> cus){

        for (CompilationDelegate delegate : cus) {

            CompilationUnit cu = delegate.compilationUnit();

            cu.addImport("io.swagger.annotations.*");

            cu.findAll(FieldDeclaration.class).forEach(field -> {


                AnnotationDelegate apiModelProperty = FieldDelegate.build(field).addAndGetAnnotation("ApiModelProperty")
                        .addPair("name", JavaDocDelegate.build(field.getJavadoc()).description());

//                if(field.getAnnotationByName("NotNull") != null){
//                    apiModelProperty.addPair("required", new BooleanLiteralExpr(true));
//                }

            });

            delegate.flush();
        }



        return true;
    }

    private Map<PackageType,List<CompilationDelegate>> doScanner(String compileSourceRoot){

        List<CompilationDelegate> controllerSource = new ArrayList<>();
        List<CompilationDelegate> domainSource = new ArrayList<>();

        new FileIterable(new File(compileSourceRoot)).forEach(file -> {

            if(!"java".equals(FileNameUtil.extName(file))){
                return;
            }

            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
            } catch (FileNotFoundException e) {
                return;
            }

            PluginProperties.Packages packages = properties.getPackages();

            if(getPackageName(cu) == null){
                return;
            }
            if(packages.getControllers().contains(getPackageName(cu))){
                controllerSource.add(CompilationDelegate.build(file,cu));
            }else if(packages.getDomains().contains(getPackageName(cu))){
                domainSource.add(CompilationDelegate.build(file,cu));
            }

        });

        Map<PackageType,List<CompilationDelegate>> matchSource = new HashMap<>();
        matchSource.put(PackageType.CONTROLLER,controllerSource);
        matchSource.put(PackageType.DOMAIN,domainSource);

        return matchSource;

    }

    private static String getPackageName(CompilationUnit cu){
        try {
            PackageDeclaration packageDeclaration = cu.getPackageDeclaration().orElseGet(()-> null);
            if(packageDeclaration == null) return null;
            return packageDeclaration.getName().asString();
        }catch (Exception e){
            return null;
        }
    }

}
