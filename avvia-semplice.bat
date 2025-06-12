@echo off
echo Avvio rapido Cat e Ring...
echo.

echo [1/2] Compilazione veloce (solo se necessario)...
call mvn compile
if %ERRORLEVEL% neq 0 (
    echo ERRORE: Compilazione fallita!
    pause
    exit /b 1
)

echo.
echo [2/2] Avvio dell'applicazione...
echo.
call mvn javafx:run

echo.
echo Applicazione terminata.
pause 