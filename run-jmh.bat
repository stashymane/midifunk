@echo off
call gradlew.bat clean
call gradlew.bat jmh -Dorg.gradle.java.home="C:\Program Files\Java\jdk1.8.0_251" --no-daemon
pause > nul