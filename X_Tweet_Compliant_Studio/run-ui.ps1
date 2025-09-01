Set-Location $PSScriptRoot
mvn -q -DskipTests package
mvn -q exec:java
