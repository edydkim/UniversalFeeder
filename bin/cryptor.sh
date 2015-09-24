#!/bin/bash

WHOAMI=`whoami`

export JAVA_HOME=/opt/sunjdk/jdk1.8.0_20_64

TARGET_JAR="/home/${WHOAMI}/projects/org/ufm/target/UniversalFeeder-1.0-SNAPSHOT-jar-with-dependencies.jar"
CP_JAR="/home/${WHOAMI}/projects/org/ufm/out/artifacts/UniversalFeeder_jar/UniversalFeeder.jar"

# Current Flags: ${JAVA_HOME}/bin/java -XX:+PrintFlagsFinal -version

DECODE=$1

if [ "x$1" == "x" ]; then
    echo "Usage: $0 <plain text>"
    exit 1
fi

# Normal Deployment
${JAVA_HOME}/bin/java -cp ${CP_JAR} -Dfile.encoding=UTF-8 org.ufm.util.Cryptor $1

# Maven Deployment
# <- ${JAVA_HOME}/bin/java -jar ${TARGET_JAR} -Dfile.encoding=UTF-8 org.ufm.util.Cryptor $1

exit 0
