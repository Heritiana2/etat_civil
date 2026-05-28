#!/bin/bash

# Script de lancement CUA - Gestion de l'État Civil
# Inclut automatiquement toutes les dépendances du répertoire lib/

cd "$(dirname "$0")" || exit 1

echo "🚀 Lancement de l'application CUA..."
echo "Classpath : bin:lib/*"

java -cp "bin:lib/*" mg.view.LoginFrame

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors du lancement de l'application."
    exit 1
else
    echo "✅ Application terminée."
fi
