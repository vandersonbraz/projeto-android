# 🚀 COMO BAIXAR E USAR SEU APP ANDROID CALMARE

## 📥 OPÇÃO 1: BAIXAR O ARQUIVO ZIP (MAIS FÁCIL)

### 1. Localize o arquivo ZIP:
```
/home/user/projeto-android/Calmare_Android_App_Completo.zip
```

### 2. No Windows, copie para uma pasta acessível:
- Navegue até `\\wsl$\home\user\projeto-android\` no Windows Explorer
- Copie `Calmare_Android_App_Completo.zip` para seu Desktop ou Documentos

### 3. Extraia o ZIP:
- Clique com botão direito no arquivo
- Escolha "Extrair tudo..." ou use WinRAR/7-Zip
- Extraia para uma pasta de sua escolha (ex: `C:\AndroidProjects\`)

---

## 📥 OPÇÃO 2: CLONAR DO GIT (SE PREFERIR)

```bash
git clone https://github.com/vandersonbraz/projeto-android
cd projeto-android
```

O app está na pasta `CalmareApp/`

---

## 🛠️ ABRIR NO ANDROID STUDIO

### 1. Baixe o Android Studio:
- https://developer.android.com/studio
- Instale a versão mais recente

### 2. Abra o projeto:
1. Abra o **Android Studio**
2. Clique em **"Open"** (ou File → Open)
3. Navegue até a pasta **`CalmareApp`** (não o ZIP!)
4. Clique em **OK**

### 3. Aguarde sincronização:
- O Android Studio vai sincronizar o Gradle automaticamente
- Pode demorar alguns minutos na primeira vez
- Aguarde até ver "Gradle sync finished" na barra inferior

### 4. Compile:
- Menu: **Build → Make Project** (ou Ctrl+F9)
- Aguarde a compilação
- ✅ **Não deve ter erros!**

---

## ▶️ EXECUTAR O APP

### Opção A: Emulador Android
1. No Android Studio: **Tools → Device Manager**
2. Crie um dispositivo virtual (se não tiver)
3. Inicie o emulador
4. Clique no botão **Play verde** (Run)

### Opção B: Dispositivo Real
1. Ative **Depuração USB** no seu celular Android:
   - Configurações → Sobre o telefone → Toque 7x em "Número da versão"
   - Configurações → Opções do desenvolvedor → Ativar "Depuração USB"
2. Conecte o celular no PC via USB
3. Autorize a depuração no celular
4. No Android Studio, selecione seu dispositivo
5. Clique no botão **Play verde** (Run)

---

## ⚙️ ANTES DE PUBLICAR NA PLAY STORE

**LEIA O ARQUIVO `README.md` DENTRO DA PASTA `CalmareApp/`**

Ele contém instruções detalhadas sobre:
- ✅ Como trocar IDs do AdMob pelos seus IDs reais
- ✅ Como configurar produtos de assinatura no Play Console
- ✅ Como gerar ícones do app
- ✅ Como assinar o app para publicação
- ✅ Checklist completo pré-publicação

---

## 📂 ESTRUTURA DOS ARQUIVOS

```
Calmare_Android_App_Completo.zip    ← Arquivo para download
│
└── CalmareApp/                      ← Pasta do projeto (ABRA ESTA NO ANDROID STUDIO)
    ├── README.md                    ← LEIA ESTE ARQUIVO! Instruções completas
    ├── app/
    │   ├── src/main/
    │   │   ├── java/                ← Código Kotlin
    │   │   ├── res/                 ← Layouts XML, cores, strings
    │   │   └── AndroidManifest.xml
    │   └── build.gradle
    ├── build.gradle
    └── settings.gradle
```

---

## ❓ PROBLEMAS COMUNS

### "Gradle sync failed"
- Verifique sua conexão com a internet
- Menu: **File → Invalidate Caches → Invalidate and Restart**

### "SDK not found"
- O Android Studio vai pedir para baixar o SDK
- Clique em "Install missing SDK packages"

### Não consigo abrir o ZIP
- Use WinRAR, 7-Zip ou o extrator nativo do Windows
- Certifique-se que o arquivo foi totalmente baixado

---

## 🎯 PRÓXIMOS PASSOS

1. ✅ Extrair o ZIP
2. ✅ Abrir no Android Studio
3. ✅ Compilar e testar no emulador
4. ✅ Ler o README.md completo
5. ✅ Configurar AdMob e Billing
6. ✅ Personalizar (ícone, cores se quiser)
7. ✅ Publicar na Play Store
8. ✅ **LUCRAR!** 💰

---

## 📞 SUPORTE OFICIAL

- **Android Developers**: https://developer.android.com
- **AdMob**: https://support.google.com/admob
- **Play Console**: https://support.google.com/googleplay/android-developer

---

**Desenvolvido com foco em monetização máxima - Novembro 2025**

🚀 **BOA SORTE E BONS LUCROS!**
