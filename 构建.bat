@echo off
chcp 65001 >nul
set "JAVA_HOME=C:\Users\adminstrator\Desktop\test\jdk21\jdk-21.0.13+9"
set "GRADLE_USER_HOME=C:\Users\adminstrator\Desktop\test\gradle-home"
set "TMP=C:\Users\adminstrator\Desktop\test\tmp"
set "TEMP=C:\Users\adminstrator\Desktop\test\tmp"
cd /d "%~dp0"
call gradlew.bat buildPlugin
if errorlevel 1 (
  echo.
  echo [BUILD FAILED] see errors above
) else (
  echo.
  echo [BUILD OK] plugin zip in build\distributions:
  dir /b build\distributions\*.zip
)
pause
