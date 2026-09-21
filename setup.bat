@echo off
REM Chepe Rakha Reader — Windows Setup Script
REM Run this after cloning the repository

echo === Chepe Rakha Reader - Initial Setup ===

REM Check if EPUB exists
if not exist "app\src\main\assets\book\book.epub" (
    echo WARNING: EPUB file not found at app\src\main\assets\book\book.epub
    echo Please add your EPUB file there.
    pause
)

REM Initialize git if needed
if not exist ".git" (
    echo Initializing git repository...
    git init
)

echo.
echo To complete setup, run:
echo   git add .
echo   git commit -m "Initial commit: Chepe Rakha Reader"
echo   git push -u origin main

pause
