#!/bin/bash
echo "=== Instalando Agente Catastro ==="

# Python deps
echo "[1/3] Dependencias Python..."
pip install --break-system-packages requests beautifulsoup4 lxml 2>/dev/null || \
pip install requests beautifulsoup4 lxml

# Node deps
echo "[2/3] Dependencias Node.js..."
cd "$(dirname "$0")"
npm init -y 2>/dev/null
npm install playwright

# Chromium para Playwright
echo "[3/3] Instalando Chromium..."
npx playwright install chromium

echo ""
echo "=== Instalacion completada ==="
echo ""
echo "Para usar con Claude Code:"
echo "  cd $(pwd)"
echo "  claude"
echo "  > Busca la CDG de Calle Gran Via 1, Madrid"
