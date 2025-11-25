#!/bin/bash

echo "=========================================="
echo "  OPTIMUS PLAYER - Push Manual"
echo "=========================================="
echo ""

# 1. Copiar ZIP
echo "📦 Copiando ZIP para Windows..."
cp /home/user/projeto-android/optimus-android-app-completo.zip /c/Users/vande/Downloads/ 2>/dev/null && echo "✅ ZIP copiado!" || echo "❌ Erro ao copiar"

# 2. Ir para o projeto
echo ""
echo "📂 Indo para o projeto..."
cd /c/Users/vande/Documents/projeto-android || { echo "❌ Pasta não existe!"; exit 1; }
echo "✅ Na pasta: $(pwd)"

# 3. Extrair ZIP
echo ""
echo "📦 Extraindo ZIP..."
unzip -o /c/Users/vande/Downloads/optimus-android-app-completo.zip && echo "✅ Extraído!" || echo "❌ Erro ao extrair"

# 4. Git add
echo ""
echo "📝 Adicionando arquivos ao git..."
git add -A && echo "✅ Arquivos adicionados!" || echo "❌ Erro no git add"

# 5. Git commit
echo ""
echo "💾 Fazendo commit..."
git commit -m "feat: App Android completo - MainActivity + Fragments + SDK 35" && echo "✅ Commit criado!" || echo "ℹ️  Nada para commitar"

# 6. Git push
echo ""
echo "🚀 Fazendo PUSH para GitHub..."
git push origin claude/ai-project-prompt-template-01W6iFWAYN6gVZK7r7BDo9SC && echo "✅ PUSH REALIZADO COM SUCESSO!" || echo "❌ Erro no push"

echo ""
echo "=========================================="
echo "  CONCLUÍDO!"
echo "=========================================="
