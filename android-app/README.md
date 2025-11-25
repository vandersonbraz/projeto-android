# 📱 Optimus Player - Android App

Aplicativo IPTV premium desenvolvido em Kotlin nativo para Android.

---

## 🎯 Status do Projeto

✅ **Fase 1:** Mockups & Design - COMPLETA
🚀 **Fase 2:** App Android - EM DESENVOLVIMENTO

### ✅ O Que Está Pronto:

- [x] Estrutura do projeto MVVM
- [x] Gradle configurado com todas dependências
- [x] Tema Dark Premium (Preto + Dourado)
- [x] Splash Screen com animação
- [x] Tela de Ativação (MAC Address + Device ID + Código)
- [x] Utilitários (PreferenceManager, DeviceUtils)
- [x] AndroidManifest completo

### 🔨 Em Desenvolvimento:

- [ ] MainActivity + Bottom Navigation
- [ ] Fragments (Canais, Filmes, Séries)
- [ ] Player (ExoPlayer + M3U)
- [ ] API Integration (Licenças)
- [ ] EPG (Electronic Program Guide)

---

## 🛠️ Tecnologias

- **Linguagem:** Kotlin
- **Min SDK:** 21 (Android 5.0+) - Fire Stick/TV Box support
- **Target SDK:** 34 (Android 14)
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **View Binding:** Habilitado
- **Coroutines:** Para operações assíncronas

### 📦 Principais Bibliotecas:

| Biblioteca | Versão | Uso |
|------------|--------|-----|
| Material Components | 1.11.0 | UI/UX |
| Navigation Component | 2.7.6 | Navegação entre telas |
| Lifecycle (ViewModel/LiveData) | 2.7.0 | Gerenciamento de estado |
| ExoPlayer | 2.19.1 | Reprodução IPTV/M3U |
| Retrofit | 2.9.0 | Comunicação com API |
| Room | 2.6.1 | Banco de dados local |
| Glide | 4.16.0 | Carregamento de imagens |
| Coroutines | 1.7.3 | Operações assíncronas |

---

## 🚀 Como Compilar e Rodar

### **Requisitos:**

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK** 17
- **Gradle** 8.2+
- Dispositivo Android ou Emulador (API 21+)

### **Passos:**

1. **Clone o repositório:**
```bash
git clone https://github.com/vandersonbraz/projeto-android.git
cd projeto-android/android-app
```

2. **Abra no Android Studio:**
- File → Open → Selecione a pasta `android-app`
- Aguarde o Gradle sync

3. **Build o projeto:**
```bash
./gradlew build
```

4. **Rode no emulador/dispositivo:**
- Clique em "Run" (Shift+F10)
- Ou via linha de comando:
```bash
./gradlew installDebug
```

---

## 📱 Telas Implementadas

### 1. **Splash Screen** (`SplashActivity`)
- Animação do logo hexagonal
- Fade-in do texto "OPTIMUS PLAYER"
- Glow pulse effect
- Verifica ativação e redireciona

### 2. **Activation Screen** (`ActivationActivity`)
- Mostra MAC Address do dispositivo
- Mostra Device ID único
- Botões para copiar (clipboard)
- Input de código formatado (XXXX-XXXX-XXXX)
- Validação de código
- Salva licença localmente

### 3. **Main Screen** (`MainActivity`) - EM DESENVOLVIMENTO
- Bottom Navigation (Início, EPG, MAC, Config)
- Fragments para cada seção
- Tab Layout (Canais, Filmes, Séries)

---

## 🎨 Design System

### **Cores:**

```kotlin
// Gold (Primary)
val goldBright = Color(0xFFFFD700)
val goldSoft = Color(0xFFFFC107)

// Black
val blackDeep = Color(0xFF000000)
val blackCarbon = Color(0xFF121212)
val blackCharcoal = Color(0xFF1E1E1E)

// Gray
val grayMedium = Color(0xFF757575)
val graySilver = Color(0xFFCCCCCC)
```

### **Tipografia:**

- **Títulos:** Poppins SemiBold
- **Corpo:** Roboto Regular
- **Código:** Monospace

### **Espaçamento:**

Grid de 8dp (8, 16, 24, 32, 48)

---

## 📂 Estrutura do Projeto

```
app/src/main/
├── java/com/optimus/player/
│   ├── ui/
│   │   ├── splash/
│   │   │   └── SplashActivity.kt
│   │   ├── activation/
│   │   │   └── ActivationActivity.kt
│   │   ├── home/
│   │   ├── player/
│   │   ├── channels/
│   │   ├── movies/
│   │   ├── series/
│   │   └── MainActivity.kt
│   ├── data/
│   │   ├── remote/      # API calls
│   │   ├── local/       # Room Database
│   │   └── repository/  # Data layer
│   ├── domain/
│   │   ├── model/       # Data classes
│   │   └── usecase/     # Business logic
│   ├── utils/
│   │   ├── PreferenceManager.kt
│   │   ├── DeviceUtils.kt
│   │   └── Constants.kt
│   └── di/              # Dependency Injection
├── res/
│   ├── layout/          # XML Layouts
│   ├── values/
│   │   ├── colors.xml
│   │   ├── strings.xml
│   │   └── themes.xml
│   ├── drawable/        # Icons, backgrounds
│   └── navigation/      # Navigation graph
└── AndroidManifest.xml
```

---

## 🔑 Funcionalidades de Licenciamento

### **MAC Address Detection:**
- Método 1: WiFi Manager (Android <6)
- Método 2: Network Interfaces
- Método 3: Gerado a partir do Device ID (fallback)

### **Device ID Generation:**
- Prioridade 1: Android ID
- Prioridade 2: UUID persistente

### **Armazenamento Local:**
- SharedPreferences criptografadas
- Salva: código ativação, data expiração, tipo usuário

---

## 🌐 API (Próxima Fase)

### **Endpoints Planejados:**

```kotlin
POST /api/v1/license/activate
  Body: { mac_address, device_id, activation_code }
  Response: { license_id, expires_at, user_type }

GET /api/v1/license/validate
  Headers: { Authorization: Bearer <token> }
  Response: { valid, expires_at, days_remaining }

POST /api/v1/playlist/sync
  Response: { channels[], movies[], series[] }
```

---

## 🐛 Debug

### **Logs:**

```kotlin
// Enable logging
android {
    buildTypes {
        debug {
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }
    }
}
```

### **Verificar MAC/Device ID:**

```bash
adb shell
> logcat | grep "Optimus"
```

---

## 📝 TODOs

### **Alta Prioridade:**
- [ ] Implementar MainActivity com Bottom Navigation
- [ ] Criar Fragments (Home, Channels, Movies, Series)
- [ ] Integrar ExoPlayer para reprodução M3U
- [ ] API Client (Retrofit) para validação de licenças

### **Média Prioridade:**
- [ ] EPG (Electronic Program Guide)
- [ ] Favorites system (Room Database)
- [ ] Search functionality
- [ ] Chromecast integration

### **Baixa Prioridade:**
- [ ] Parental Control
- [ ] Download for offline
- [ ] Picture-in-Picture mode

---

## 📄 Licença

© 2024 Optimus Player. Todos os direitos reservados.

---

## 👤 Desenvolvedor

**Projeto:** Optimus Player
**Repositório:** vandersonbraz/projeto-android
**Fase Atual:** 2 (App Android) 🚀

---

**Última Atualização:** 24 de Novembro de 2025
