#!/usr/bin/env bash
#####
##### system property that prefix with 'filelugd.'
##### will pass to the real application as system property with the key without 'filelugd.'
#####
##### Set country and language other than OS settings by using:
#####
##### -Dfilelugd.user.language=zh \
##### -Dfilelugd.user.region=TW \
#####
##### Set file encoding by using:
#####
##### -D-Dfilelugd.file.encoding=UTF-8 \
#####
./jre/Contents/Home/bin/java \
-Dbundle.jre=./jre/Contents/Home \
-Dbundle.lib=./flib \
-Dfilelugd.javax.swing.adjustPopupLocationToFit=true \
-Dfilelugd.apple.awt.application.name=Filelug \
-Dfilelugd.apple.laf.useScreenMenuBar=true \
-Dfilelugd.com.apple.mrj.application.apple.menu.about.name=Filelug \
-Dfilelugd.apple.awt.showGrowBox=false \
-classpath \
./flib/fdesktop.jar:\
./flib/commons-lang3-3.1.jar:\
./flib/logback-classic-1.1.3.jar:\
./flib/logback-core-1.1.3.jar:\
./flib/slf4j-api-1.7.13.jar:\
./flib/commons-configuration-1.10.jar:\
./flib/commons-logging-1.1.1.jar:\
./flib/commons-lang-2.6.jar:\
./flib/commons-collections-3.2.1.jar: \
com.filelug.desktop.Starter &