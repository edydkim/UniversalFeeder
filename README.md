## UniversalFeeder
Universal UpStream Feeder Manager for BI - Data Analysing and Mining.

Supporting for Reactive Platform - Asynchronous and Event Handler, Scalable System - Scale-Out on Multi-Threading (Thread-Safe)

see below for details..

## SDK
JDK 1.8

## Framework
Spring Boot

Drools (**Rule Engine for BI**)

## Extension
JMX

JMS

Google Guava

Google joox

Commons Codec (Base64)

## Build
Maven

## WAS tested
Embeded Tomcat..

## Run
$>mvn spring-boot:run

## Messaging Queue
MQ Service

## CommandLine flags (for low specs), cf. bin/dispatch.sh
-XX:ErrorFile=processor.error -XX:GCLogFileSize=52428800 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:InitialHeapSize=268435456 -XX:MaxDirectMemorySize=536870912 -XX:MaxHeapSize=536870912 -XX:MaxGCPauseMillis=200 -XX:MaxMetaspaceSize=268435456 -XX:MetaspaceSize=268435456 -XX:NumberOfGCLogFiles=5 -XX:OnOutOfMemoryError=onError.sh -XX:+PrintFlagsFinal -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+TraceGen0Time -XX:+TraceGen1Time -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseGCLogFileRotation -XX:+UseParallelGC
