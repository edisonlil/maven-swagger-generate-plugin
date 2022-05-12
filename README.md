# maven-swagger-generate-plugin

自动生成swagger注解插件

## 安装

```xml
    <plugin>
       <groupId>com.github.edisonlil</groupId>
       <artifactId>maven-swagger-generate-plugin</artifactId>
       <version>1.0.0-SNAPSHOT</version>
       <configuration>
           <properties>
               <packages>
                    <!-- 控制器包名 -->
                   <controllers>
                       <contrl>com.example.swaggerdemo.controller</contrl>
                   </controllers>
                    <!-- 实体类包名 -->
                   <domains>
                       <domain>com.example.swaggerdemo.domain</domain>
                   </domains>
               </packages>
           </properties>
       </configuration>
       <executions>
           <execution>
               <id>generate</id>
               <goals>
                   <goal>start</goal>
               </goals>
               <phase>validate</phase>
           </execution>
       </executions>
    </plugin>
```