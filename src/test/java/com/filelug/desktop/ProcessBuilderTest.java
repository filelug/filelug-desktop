package com.filelug.desktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ProcessBuilderTest {

    public static void main(String[] args) {
        File outputFile = new File("test001.txt");

        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outputFile.deleteOnExit();

        List<String> commands = new ArrayList<>();

//        commands.add("java");
////        commands.add("/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug.app/Contents/PlugIns/jre1.7.0_60.jre/Contents/Home/jre/bin/java");
//        commands.add("-Xms256M");
//        commands.add("-Xmx512M");
//        commands.add("-Dfile.encoding=UTF-8");
//        commands.add("-Duser.language=zh");
//        commands.add("-Duser.region=TW");
//        commands.add("-Dcom.apple.mrj.application.apple.menu.about.name=Filelug");
//        commands.add("-Dapple.laf.useScreenMenuBar=true");
//        commands.add("-Dapple.awt.application.name=Filelug");
//        commands.add("-classpath");
//        commands.add(".:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug.app/Contents/Java/appframework-1.0.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/asm-all-repackaged-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/cglib-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-beanutils-1.8.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-codec-1.6.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-configuration-1.10.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-dbcp2-2.0.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-io-2.4.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-lang-2.6.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-lang3-3.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-logging-1.1.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-pool2-2.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/commons-vfs2-2.0.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/dom4j-1.6.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/fdesktop.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/guava-11.0.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/hk2-api-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/hk2-locator-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/hk2-utils-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/hsqldb-2.3.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/httpasyncclient-4.0.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/httpclient-4.3.5.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/httpcore-4.3.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/httpcore-nio-4.3.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jackson-annotations-2.2.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jackson-core-2.2.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jackson-databind-2.2.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jackson-dataformat-xml-2.1.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jackson-module-jaxb-annotations-2.1.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/javassist-3.12.1.GA.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/javax.annotation-api-1.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/javax.inject-2.2.0-b14.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/javax.servlet-api-3.1.0.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/javax.ws.rs-api-2.0.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jersey-common-2.2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jetty-io-9.1.5.v20140505.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jetty-util-9.1.5.v20140505.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jna-4.0.0.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jna-platform-4.0.0.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/jsr305-1.3.9.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/logback-classic-1.1.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/logback-core-1.1.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/maven-scm-api-1.4.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/maven-scm-provider-svn-commons-1.4.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/maven-scm-provider-svnexe-1.4.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/osgi-resource-locator-1.0.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/plexus-utils-1.5.6.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/reflections-0.9.8.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/regexp-1.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/serializer-2.7.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/slf4j-api-1.7.13.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/stax-api-1.0-2.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/stax2-api-3.1.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/swing-worker-1.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/tika-core-1.3.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/websocket-api-9.1.5.v20140505.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/websocket-client-9.1.5.v20140505.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/websocket-common-9.1.5.v20140505.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/woodstox-core-asl-4.1.4.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/xalan-2.7.1.jar:/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug Desktop.app/Contents/Java/xml-apis-1.0.b2.jar");
//        commands.add("com.filelug.desktop.Application");

        commands.add("/Users/masonhsieh/Downloads/temp/Filelug Demo/jdk1.7.0_67.jdk/Contents/Home/jre/bin/java");
        commands.add("-Dabc.25=Filelug Demo");
        commands.add("-version");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        try {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputFile));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectError(ProcessBuilder.Redirect.to(outputFile));
//            processBuilder.directory(new File("/Users/masonhsieh/Documents/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/dist/Filelug.app/Contents/PlugIns/jre1.7.0_60.jre/Contents/Home/jre/bin"));

            Process process = processBuilder.start();

            int result = process.waitFor();

            System.out.println("result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.exit(0);
    }
}
