#!/bin/sh
mvn clean compile && \
ant replace.before.package && \
mvn package && \
ant fdesktop.jar.obfuscate && \
ant run-testing-en