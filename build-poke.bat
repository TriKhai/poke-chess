@echo off
setlocal

echo ========================================
echo        Poke Auto Chess ME - Build
echo ========================================

set "JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_503"
set "KVEM_HOME=C:\Java_ME_platform_SDK_3.0"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

if not exist "build.xml" (
    echo ERROR: build.xml not found in %CD%
    pause
    exit /b 1
)

call "%KVEM_HOME%\toolbar\java2\ant\bin\ant.bat" clean test dist

if errorlevel 1 (
    echo.
    echo ============ BUILD FAILED ============
    pause
    exit /b 1
)

echo.
echo ============ BUILD SUCCESSFUL ============
echo Output: %CD%\build\dist\PokeAutoChess.jar / .jad
pause
endlocal
