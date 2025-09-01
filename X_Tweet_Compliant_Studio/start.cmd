@echo off
cd /d %~dp0
call mvn -q -DskipTests package
call mvn -q exec:java
