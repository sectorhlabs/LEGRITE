@echo off
echo === Instalando Agente Catastro ===

echo [1/3] Dependencias Python...
pip install requests beautifulsoup4 lxml

echo [2/3] Dependencias Node.js...
npm init -y
npm install playwright

echo [3/3] Instalando Chromium...
npx playwright install chromium

echo.
echo === Instalacion completada ===
echo.
echo Para usar con Claude Code:
echo   cd %~dp0
echo   claude
echo   ^> Busca la CDG de Calle Gran Via 1, Madrid
pause
