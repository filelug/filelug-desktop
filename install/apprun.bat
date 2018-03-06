@echo off

SETLOCAL

set FILELUG_HOME=.
set FILELUG_LIB_NAME=flib
set FILELUG_LIB=%FILELUG_HOME%\%FILELUG_LIB_NAME%

set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\commons-lang3-3.1.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\fdesktop.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\logback-classic-1.1.3.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\logback-core-1.1.3.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\slf4j-api-1.7.13.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\commons-configuration-1.10.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\commons-logging-1.1.1.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\commons-lang-2.6.jar
set NEWCLASSPATH=%NEWCLASSPATH%;%FILELUG_LIB%\commons-collections-3.2.1.jar

set FILELUG_JRE_NAME=jre
set JRE_HOME=%FILELUG_HOME%\%FILELUG_JRE_NAME%
set PATH=%JRE_HOME%\bin;%PATH%

REM
REM system property that prefix with 'filelugd.'
REM will pass to the real application as system property with the key without 'filelugd.'
REM
REM Ex. Set country and language other than OS settings by using:
REM -Dfilelugd.user.language=zh -Dfilelugd.user.region=TW
REM

@start javaw -Dbundle.jre=%FILELUG_JRE_NAME% -Dbundle.lib=%FILELUG_LIB_NAME% -Dfilelugd.awt.useSystemAAFontSettings=lcd -Dfilelugd.javax.swing.adjustPopupLocationToFit=true -classpath %NEWCLASSPATH% com.filelug.desktop.Starter

ENDLOCAL