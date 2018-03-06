#!/bin/sh
mvn clean compile && \
ant replace.before.package && \
mvn package && \
ant fdesktop.jar.obfuscate && \
ant run-production-zh_TW