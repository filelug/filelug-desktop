#!/bin/sh
mvn clean compile && \
ant replace.before.package && \
mvn package && \
ant fdesktop.jar.obfuscate && \
ant export-jre && \
ant installer-macAndLinux
