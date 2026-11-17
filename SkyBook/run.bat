@echo off
title SkyBook Flight Reservation System

echo ==========================================
echo       SKYBOOK FLIGHT RESERVATION
echo ==========================================

if not exist out mkdir out

echo.
echo Compiling SkyBook...

javac -d out src\*.java

if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo Starting SkyBook...
echo.

java -cp out Main

pause