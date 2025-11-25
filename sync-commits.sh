#!/bin/bash
# Script para sincronizar commits no Windows

echo "======================================"
echo "  OPTIMUS PLAYER - Sync de Commits"
echo "======================================"
echo ""
echo "Commits pendentes: 7"
echo ""

# Mostrar commits
echo "📋 Commits que serão sincronizados:"
echo ""
git log --oneline HEAD~7..HEAD

echo ""
echo "======================================"
echo "  Executar no Git Bash (Windows):"
echo "======================================"
echo ""
echo "# 1. Navegue até o projeto"
echo "cd C:/Users/vande/Documents/projeto-android"
echo ""
echo "# 2. Puxe este branch com todos os commits"
echo "git pull /caminho/para/optimus-final-bundle.bundle claude/ai-project-prompt-template-01W6iFWAYN6gVZK7r7BDo9SC"
echo ""
echo "# 3. Faça push para o GitHub"
echo "git push origin claude/ai-project-prompt-template-01W6iFWAYN6gVZK7r7BDo9SC"
echo ""
echo "======================================"
