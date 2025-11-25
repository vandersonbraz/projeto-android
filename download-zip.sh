#!/bin/bash

echo "================================"
echo "  BAIXAR ZIP PARA WINDOWS"
echo "================================"
echo ""

ZIP_FILE="optimus-android-app-completo.zip"
ZIP_PATH="/home/user/projeto-android/$ZIP_FILE"

echo "📦 Arquivo: $ZIP_FILE (133KB)"
echo "📍 Localização Linux: $ZIP_PATH"
echo ""

# Tentar copiar para várias localizações possíveis do Windows
DESTINATIONS=(
    "/mnt/c/Users/vande/Downloads"
    "/mnt/c/Users/vande/Desktop"
    "/mnt/c/Users/vande/Documents"
)

echo "🔄 Tentando copiar para Windows..."
echo ""

SUCCESS=0
for dest in "${DESTINATIONS[@]}"; do
    if [ -d "$dest" ]; then
        echo "✅ Encontrei: $dest"
        if cp "$ZIP_PATH" "$dest/$ZIP_FILE" 2>/dev/null; then
            echo "   ✅ Copiado com sucesso!"
            echo "   📁 Arquivo em: $(echo $dest | sed 's|/mnt/c|C:|')\\$ZIP_FILE"
            SUCCESS=1
        else
            echo "   ❌ Erro ao copiar"
        fi
    fi
done

if [ $SUCCESS -eq 0 ]; then
    echo "❌ Nenhuma pasta do Windows encontrada"
    echo ""
    echo "📋 SOLUÇÃO MANUAL:"
    echo ""
    echo "1. Abra o Explorador de Arquivos do Windows"
    echo "2. Digite na barra de endereço:"
    echo "   \\\\wsl\$\\Ubuntu\\home\\user\\projeto-android"
    echo ""
    echo "3. Copie o arquivo: $ZIP_FILE"
    echo "   para: C:\\Users\\vande\\Downloads"
fi

echo ""
echo "================================"
