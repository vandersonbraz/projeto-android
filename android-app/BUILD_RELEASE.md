# 📦 Build Release - Optimus Player

Guia completo para gerar APK/AAB para produção (Play Store).

---

## ✅ Configurações Já Feitas

- [x] **Target SDK 35** (Play Store requirement 2025)
- [x] **Ofuscação R8** habilitada (`isMinifyEnabled = true`)
- [x] **Shrink Resources** habilitado (remove recursos não usados)
- [x] **ProGuard Rules** completo e otimizado
- [x] **Desugaring** configurado (APIs modernas em Android antigos)

---

## 🔑 Passo 1: Criar Keystore (Assinatura)

### **Gerar Keystore:**

```bash
# No diretório do projeto
keytool -genkey -v -keystore optimus-player-release.keystore \
  -alias optimus-player \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# Preencha as informações:
# - Password: [ESCOLHA UMA SENHA FORTE]
# - First and Last Name: Optimus Player
# - Organizational Unit: Mobile Development
# - Organization: Optimus Player
# - City: [Sua Cidade]
# - State: [Seu Estado]
# - Country Code: BR
```

### **⚠️ IMPORTANTE:**
- **NUNCA** commite o keystore no git!
- **GUARDE** a senha em local seguro (gerenciador de senhas)
- **BACKUP** do keystore (sem ele você não pode atualizar o app!)

---

## 🔐 Passo 2: Configurar Assinatura

### **Opção A: keystore.properties (Recomendado)**

1. Crie `keystore.properties` na raiz do projeto:

```properties
storePassword=SUA_SENHA_AQUI
keyPassword=SUA_SENHA_AQUI
keyAlias=optimus-player
storeFile=../optimus-player-release.keystore
```

2. Adicione ao `.gitignore`:

```bash
echo "keystore.properties" >> .gitignore
echo "*.keystore" >> .gitignore
```

3. Atualize `app/build.gradle.kts`:

```kotlin
// No topo do arquivo, antes de android {}
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    // ...

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... resto das configurações
        }
    }
}
```

### **Opção B: Android Studio**

1. **Build → Generate Signed Bundle/APK**
2. Selecione **Android App Bundle** (AAB para Play Store)
3. Escolha o keystore ou crie um novo
4. Build!

---

## 🏗️ Passo 3: Build de Produção

### **Build AAB (Android App Bundle) - Para Play Store:**

```bash
# Limpar projeto
./gradlew clean

# Build Release
./gradlew bundleRelease

# Output estará em:
# app/build/outputs/bundle/release/app-release.aab
```

### **Build APK - Para distribuição direta:**

```bash
# Build Release APK
./gradlew assembleRelease

# Output estará em:
# app/build/outputs/apk/release/app-release.apk
```

---

## 📱 Passo 4: Testar o Build

### **Instalar APK no dispositivo:**

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### **Testar funcionalidades:**

- [ ] Splash screen carrega corretamente
- [ ] MAC Address é detectado
- [ ] Device ID é gerado
- [ ] Código de ativação funciona
- [ ] Navegação está fluida
- [ ] Sem crashes ao abrir/fechar
- [ ] Logs de debug foram removidos (ofuscação)

---

## 🚀 Passo 5: Upload para Play Store

### **Requisitos da Play Store:**

- ✅ **Target SDK 35** ✅ (configurado!)
- ✅ **Assinatura com keystore** ✅
- ✅ **App Bundle (AAB)** ✅
- ✅ **Versioning correto** ✅
- ✅ **Ícones em todas resoluções**
- ✅ **Screenshots (mínimo 2)**
- ✅ **Descrição do app**
- ✅ **Política de privacidade**

### **Upload:**

1. Acesse [Google Play Console](https://play.google.com/console)
2. Crie novo app ou selecione existente
3. **Produção → Criar nova versão**
4. Upload do **app-release.aab**
5. Preencha **Release notes**
6. **Revisar** → **Lançar**

---

## 🔍 Verificar Ofuscação

Para confirmar que o código está ofuscado:

```bash
# Extrair APK
unzip app-release.apk -d extracted/

# Ver classes ofuscadas
dex2jar extracted/classes.dex
jd-gui classes-dex2jar.jar

# Deve ver classes como: a.b.c.d em vez de nomes reais
```

---

## 📊 Tamanhos Esperados

| Build Type | Tamanho Aprox | Descrição |
|------------|---------------|-----------|
| **Debug APK** | ~15-20 MB | Sem ofuscação, com logs |
| **Release APK** | ~8-12 MB | Ofuscado, otimizado |
| **Release AAB** | ~6-10 MB | Play Store faz split por dispositivo |

---

## 🐛 Troubleshooting

### **Erro: "Keystore not found"**
```bash
# Verifique o caminho em keystore.properties
storeFile=../optimus-player-release.keystore  # Correto
```

### **Erro: "minSdkVersion XX cannot be smaller than version YY"**
```bash
# Atualize as dependências no build.gradle.kts
# Ou aumente minSdk para 23 se necessário
```

### **App crash em produção mas funciona em debug**
```bash
# Provavelmente ProGuard removeu algo necessário
# Adicione regra no proguard-rules.pro:
-keep class com.sua.classe.problema.** { *; }
```

### **"R8 error: Missing classes"**
```bash
# Adicione ao proguard-rules.pro:
-dontwarn nome.da.classe.problema.**
```

---

## 📝 Checklist Final

Antes de fazer upload:

- [ ] Testei o APK release em dispositivo real
- [ ] Testei no Fire Stick / TV Box
- [ ] Splash screen funciona
- [ ] Ativação funciona
- [ ] Não há crashes
- [ ] Logs foram removidos (ofuscação)
- [ ] Versão (versionCode) foi incrementada
- [ ] Keystore está em local seguro
- [ ] Senha do keystore está salva
- [ ] `.gitignore` tem keystore e passwords
- [ ] Build AAB foi gerado com sucesso
- [ ] Screenshots estão prontos
- [ ] Descrição da Play Store está pronta

---

## 🔄 Atualizações Futuras

Para cada nova versão:

1. **Incrementar versionCode e versionName** em `build.gradle.kts`:
```kotlin
versionCode = 2  // Era 1, agora 2
versionName = "1.0.1"  // Era 1.0.0
```

2. **Build nova versão:**
```bash
./gradlew clean bundleRelease
```

3. **Upload no Play Console** na mesma página do app

---

## 📞 Suporte

Se encontrar problemas:
1. Verifique logs: `adb logcat | grep Optimus`
2. Verifique ProGuard warnings: `app/build/outputs/mapping/release/`
3. Consulte documentação: https://developer.android.com/studio/build/shrink-code

---

**Última Atualização:** 25 de Novembro de 2025
**Versão:** 1.0.0
