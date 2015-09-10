#!/bin/bash

WHOAMI=`whoami`

export JAVA_HOME=/opt/sunjdk/jdk1.8.0_20_64

TARGET_JAR="/home/${WHOAMI}/projects/org/ufm/target/UniversalFeeder-1.0-SNAPSHOT-jar-with-dependencies.jar"
CP_JAR="/home/${WHOAMI}/projects/org/ufm/out/artifacts/UniversalFeeder_jar/UniversalFeeder.jar"

# Current Flags: ${JAVA_HOME}/bin/java -XX:+PrintFlagsFinal -version

# Whole Dumping Sequence:
# $> gcore or kill -3
# $> sort /tmp/gcore.log | uniq -c | sort -nr | less
# $> strings -a /tmp/gcore.log > /tmp/string.log

# Normal Deployment
${JAVA_HOME}/bin/java -cp ${CP_JAR} -Dfile.encoding=UTF-8 -Xms256m -Xmx512m -verbose:gc -Xloggc:/tmp/gc.log.`date +%Y%m%d%H%M%S` -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:MaxDirectMemorySize=512m -XX:+PrintFlagsFinal -XX:+PrintGCDetails -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=50M -XX:+TraceGen1Time -XX:+TraceGen0Time -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:ErrorFile=dispatcher.error -XX:OnOutOfMemoryError="onError.sh" Main 1>/dev/null &

# Maven Deployment
# <- ${JAVA_HOME}/bin/java -jar ${TARGET_PATH} -Dfile.encoding=UTF-8 -Xms256m -Xmx512m -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dispatcher.dump -XX:ErrorFile=dispatcher.error -XX:OnOutOfMemoryError="touch OnOutOfMemoryError.dispatcher" Main 1>/dev/null &
