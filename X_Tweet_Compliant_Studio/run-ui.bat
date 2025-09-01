@echo off
cd /d %~dp0
mvn -q -DskipTests package && mvn -q exec:java
