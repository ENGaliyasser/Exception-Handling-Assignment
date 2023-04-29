@echo off
javac AutosarXmlProcessor.java
if %errorlevel% neq 0 (
    echo Compilation failed
    pause
    exit /b %errorlevel%
)
echo Compilation successful
echo.

rem Normal case
java AutosarXmlProcessor test1.arxml
echo Normal case successful
echo.

rem Not valid Autosar file case
java AutosarXmlProcessor test2.txt
echo Not valid Autosar file case successful
echo.

rem Empty file case
type nul > test3.arxml
java AutosarXmlProcessor test3.arxml
echo Empty file case successful
echo.

pause