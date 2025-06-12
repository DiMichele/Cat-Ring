#!/bin/bash
echo "Avvio rapido Cat e Ring..."
echo

echo "[1/2] Compilazione veloce (solo se necessario)..."
mvn compile
if [ $? -ne 0 ]; then
    echo "ERRORE: Compilazione fallita!"
    read -p "Premi Enter per continuare..."
    exit 1
fi

echo
echo "[2/2] Avvio dell'applicazione..."
echo
mvn javafx:run

echo
echo "Applicazione terminata."
read -p "Premi Enter per continuare..." 