═══════════════════════════════════════════════════════════════════════
🔐 TUTORIAL: CRIAR CHAVE PARA ASSINAR O APP CALMARE
═══════════════════════════════════════════════════════════════════════

## 📋 O QUE É A CHAVE DE ASSINATURA?

Para publicar o app na Google Play Store, você PRECISA assinar o APK/AAB com uma chave digital. Essa chave garante que só você pode atualizar o app no futuro.

⚠️ **IMPORTANTE**: Guarde a chave em local seguro! Se perder, nunca mais poderá atualizar o app na Play Store.

═══════════════════════════════════════════════════════════════════════
🛠️ MÉTODO 1: CRIAR CHAVE NO ANDROID STUDIO (RECOMENDADO)
═══════════════════════════════════════════════════════════════════════

### Passo 1: Abrir o Android Studio
1. Abra o projeto Calmare no Android Studio
2. Aguarde o Gradle sincronizar

### Passo 2: Acessar o Menu Build
1. Clique em **Build** (menu superior)
2. Clique em **Generate Signed Bundle / APK**
3. Selecione **Android App Bundle** (AAB)
4. Clique em **Next**

### Passo 3: Criar Nova Chave
1. Clique em **Create new...** (ao lado de "Key store path")
2. Preencha os campos:

```
┌─────────────────────────────────────────────────────────────┐
│ Key store path:                                             │
│ [Escolha] /home/user/calmare-keystore.jks                  │
│                                                             │
│ Password: ****************  (escolha senha FORTE)          │
│ Confirm:  ****************  (repita a senha)               │
│                                                             │
│ Alias: calmare-key                                         │
│ Password: ****************  (pode ser a mesma senha)       │
│ Confirm:  ****************                                 │
│                                                             │
│ Validity (years): 25                                       │
│                                                             │
│ Certificate:                                               │
│ First and Last Name: Seu Nome                             │
│ Organizational Unit: Calmare App                          │
│ Organization: Sua Empresa (ou deixe em branco)            │
│ City or Locality: Sua Cidade                              │
│ State or Province: Seu Estado                             │
│ Country Code (XX): BR                                     │
└─────────────────────────────────────────────────────────────┘
```

3. Clique em **OK**
4. Clique em **Next**
5. Selecione **release** (build variant)
6. Clique em **Create**

### Passo 4: Salvar Informações
**⚠️ ANOTE ESTAS INFORMAÇÕES EM LOCAL SEGURO:**

```
Arquivo da chave: /home/user/calmare-keystore.jks
Senha do keystore: [sua senha]
Alias: calmare-key
Senha do alias: [sua senha]
```

═══════════════════════════════════════════════════════════════════════
🖥️ MÉTODO 2: CRIAR CHAVE VIA LINHA DE COMANDO
═══════════════════════════════════════════════════════════════════════

Abra o terminal e execute:

```bash
keytool -genkey -v -keystore ~/calmare-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias calmare-key
```

O comando vai pedir:
1. **Senha do keystore**: Digite uma senha forte (ex: Calmare2024@Secure)
2. **Confirme a senha**: Digite novamente
3. **Nome e sobrenome**: Seu nome completo
4. **Nome da unidade organizacional**: Calmare App
5. **Nome da organização**: Sua empresa (ou deixe em branco)
6. **Nome da Cidade**: Sua cidade
7. **Nome do Estado**: Seu estado
8. **Código do país**: BR
9. **Senha para o alias**: Pressione ENTER (usa a mesma senha do keystore)

**Resultado**: Arquivo `calmare-keystore.jks` criado na pasta home

═══════════════════════════════════════════════════════════════════════
📦 CONFIGURAR BUILD.GRADLE PARA USAR A CHAVE
═══════════════════════════════════════════════════════════════════════

### Opção 1: Configurar via keystore.properties (RECOMENDADO)

1. Crie o arquivo `keystore.properties` na raiz do projeto:

```properties
storePassword=SuaSenhaDoKeystore
keyPassword=SuaSenhaDoAlias
keyAlias=calmare-key
storeFile=/home/user/calmare-keystore.jks
```

2. Edite `app/build.gradle` e adicione ANTES de `android {`:

```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

android {
    ...
    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

3. **⚠️ IMPORTANTE**: Adicione ao `.gitignore`:
```
keystore.properties
*.jks
*.keystore
```

### Opção 2: Configurar direto no build.gradle (NÃO RECOMENDADO - expõe senhas)

Edite `app/build.gradle`:

```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file("/home/user/calmare-keystore.jks")
            storePassword "SuaSenha"
            keyAlias "calmare-key"
            keyPassword "SuaSenha"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

═══════════════════════════════════════════════════════════════════════
🚀 GERAR APK/AAB ASSINADO
═══════════════════════════════════════════════════════════════════════

### Via Android Studio:
1. **Build** → **Generate Signed Bundle / APK**
2. Selecione **Android App Bundle** (para Play Store) ou **APK** (para testes)
3. Escolha sua chave existente
4. Selecione **release**
5. Clique em **Create**

**Resultado**: Arquivo em `app/release/app-release.aab` ou `app/release/app-release.apk`

### Via Linha de Comando:

```bash
# Gerar AAB assinado (para Play Store)
cd /home/user/projeto-android/CalmareApp
./gradlew bundleRelease

# Gerar APK assinado (para testes)
./gradlew assembleRelease
```

**Resultado**:
- AAB: `app/build/outputs/bundle/release/app-release.aab`
- APK: `app/build/outputs/apk/release/app-release.apk`

═══════════════════════════════════════════════════════════════════════
✅ VERIFICAR SE O APK/AAB ESTÁ ASSINADO
═══════════════════════════════════════════════════════════════════════

```bash
# Para AAB
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab

# Para APK
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

**Deve aparecer**: "jar verified" ✅

═══════════════════════════════════════════════════════════════════════
🔒 BACKUP DA CHAVE (CRÍTICO!)
═══════════════════════════════════════════════════════════════════════

**⚠️ FAÇA BACKUP IMEDIATAMENTE:**

1. Copie o arquivo `calmare-keystore.jks` para:
   - ☁️ Google Drive
   - ☁️ Dropbox
   - 💾 Pen drive
   - 💾 HD externo

2. Salve as senhas em gerenciador de senhas (LastPass, 1Password, etc.)

3. Imprima as informações e guarde em local seguro físico

**❌ SE PERDER A CHAVE:**
- Nunca mais poderá atualizar o app na Play Store
- Terá que criar um novo app do zero com novo package name
- Perderá todas as avaliações e downloads

═══════════════════════════════════════════════════════════════════════
🎯 RESUMO RÁPIDO
═══════════════════════════════════════════════════════════════════════

```bash
# 1. Criar chave
keytool -genkey -v -keystore ~/calmare-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias calmare-key

# 2. Configurar build.gradle (criar keystore.properties)

# 3. Gerar AAB assinado
./gradlew bundleRelease

# 4. Fazer BACKUP da chave em múltiplos locais

# 5. Publicar na Play Store
# Acesse play.google.com/console
# Upload do arquivo app-release.aab
```

═══════════════════════════════════════════════════════════════════════
