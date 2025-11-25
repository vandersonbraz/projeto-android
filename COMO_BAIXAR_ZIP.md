# 📥 Como Baixar o ZIP para o Windows

## Caminho do Arquivo no Linux:
```
/home/user/projeto-android/optimus-android-app-completo.zip
```

---

## ✅ Opção 1: Via Explorador de Arquivos do Windows (MAIS FÁCIL)

1. Abra o **Explorador de Arquivos** do Windows
2. Na barra de endereço, digite:
   ```
   \\wsl$\Ubuntu\home\user\projeto-android
   ```
3. Pressione **Enter**
4. Você verá a pasta do projeto!
5. Copie o arquivo **optimus-android-app-completo.zip** para sua pasta Windows:
   ```
   C:\Users\vande\Downloads
   ```

---

## ✅ Opção 2: Via Comando (WSL → Windows)

Abra o **PowerShell** ou **CMD** no Windows:

```cmd
wsl cp /home/user/projeto-android/optimus-android-app-completo.zip /mnt/c/Users/vande/Downloads/
```

Depois o arquivo estará em: `C:\Users\vande\Downloads\optimus-android-app-completo.zip`

---

## ✅ Opção 3: Via WSL (Dentro do Linux)

Se você já estiver dentro do WSL/Ubuntu:

```bash
cp /home/user/projeto-android/optimus-android-app-completo.zip /mnt/c/Users/vande/Downloads/
```

---

## 📦 O que tem no ZIP?

- `android-app/` - Projeto Android completo
- `docs/` - Documentação (backend, arquitetura)
- `mockups/` - 12 mockups HTML

**Tamanho:** 133KB  
**Contém:** ~3500 linhas de código

---

## 🚀 Depois de Baixar:

1. Extraia o ZIP em:
   ```
   C:\Users\vande\Documents\projeto-android
   ```

2. Abra o **Git Bash** e navegue até lá:
   ```bash
   cd C:/Users/vande/Documents/projeto-android
   ```

3. Commit tudo:
   ```bash
   git add -A
   git commit -m "feat: App completo - MainActivity + Fragments + SDK 35"
   ```

4. Push para GitHub:
   ```bash
   git push origin claude/ai-project-prompt-template-01W6iFWAYN6gVZK7r7BDo9SC
   ```

---

## ❓ Problemas?

Se o caminho `\\wsl$\Ubuntu` não funcionar, tente:
- `\\wsl$\Ubuntu-20.04`
- `\\wsl.localhost\Ubuntu`

Se nada funcionar, me avise que te ajudo!
