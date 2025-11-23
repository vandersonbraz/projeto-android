# 🧘 CALMARE - App Android de Meditação e Sons Relaxantes

**App completo de alta monetização pronto para publicar na Google Play Store!**

## 📥 **DOWNLOAD DIRETO**

### 🎯 **PROJETO COMPLETO - v1.1 CORRIGIDO (BUILD OK):**

**[⬇️ BAIXAR AQUI - v1.1-FIXED (132 KB)](https://github.com/vandersonbraz/projeto-android/raw/claude/android-monetization-app-01BXuLECEMMS2JfQUs6Cpqsf/CalmareApp/Calmare-App-v1.1-FIXED.zip)**

**✅ Projeto Android Studio completo**
**✅ Badge do sino: "Meditações" (centralizado, sem "Perdidas")**
**✅ Botão de notificações: apenas liga/desliga e pede permissões**
**✅ Novo botão "Configurar Lembretes": escolha horários de meditação**
**✅ Erros de compilação CORRIGIDOS - strings em resources**
**✅ Compila sem erros - pronto para usar!**

---

## 📱 SOBRE O APP

**Calmare** é um aplicativo Android moderno de meditação e sons relaxantes, focado em **máxima monetização** através de:

- ✅ **Google AdMob** (Banner, Interstitial, Rewarded Ads)
- ✅ **Google Play Billing** (Assinaturas mensais/anuais + compras únicas)
- ✅ **Design Premium** (roxo moderno, UX clean)
- ✅ **Nicho Health & Wellness** (alto potencial de ganho)

### 🆕 **NOVIDADES v1.0** (Novembro 2025):

- 🔔 **Sistema de Badge de Notificações**: Sino mostra quantidade de meditações perdidas em tempo real
- ⏰ **Lembretes Múltiplos**: Configure quantos lembretes quiser para meditar
- ⚙️ **Gerenciamento em Settings**: Adicione, visualize e delete lembretes individuais
- 🔐 **Permissões Inteligentes**: Fluxo completo de request de permissões (Android 13+)
- 🔊 **Notificações com Som**: Som padrão + vibração personalizada
- ⏱️ **Sincronização Precisa**: Alarmes exatos sincronizados ao segundo
- 💾 **Armazenamento Persistente**: Lembretes salvos sobrevivem a reinicializações
- 🎵 **Loop-aware Autoplay**: Próximo som só funciona se loop estiver desligado

---

## 🚀 COMO ABRIR E COMPILAR

### 1. IDE NECESSÁRIA:
- **Android Studio** (versão mais recente)
- Download: https://developer.android.com/studio

### 2. ABRIR O PROJETO:
1. Abra o **Android Studio**
2. Clique em **"Open"** (ou File → Open)
3. Navegue até a pasta **`CalmareApp/`**
4. Selecione a pasta e clique em **OK**
5. Aguarde o Android Studio sincronizar o Gradle (primeira vez pode demorar)

### 3. COMPILAR:
- Clique em **"Build" → "Make Project"** (ou Ctrl+F9)
- Aguarde a compilação finalizar
- **NÃO DEVE HAVER ERROS!** ✅

### 4. EXECUTAR:
- Conecte um dispositivo Android via USB (com USB Debugging ativado)
- OU use um emulador Android
- Clique em **"Run"** (botão Play verde) ou Shift+F10

---

## ⚙️ CONFIGURAÇÕES IMPORTANTES ANTES DE PUBLICAR

### 🔴 1. GOOGLE ADMOB - IDs DE ANÚNCIOS

**Localização:** `app/src/main/java/com/calmare/app/managers/AdManager.kt`

**IDs ATUAIS SÃO DE TESTE!** Troque pelos seus IDs reais:

```kotlin
// SUBSTITUA ESTES IDs:
private const val BANNER_AD_UNIT_ID = "ca-app-pub-XXXXXXXX/YYYYYYYYYY"
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXX/YYYYYYYYYY"
private const val REWARDED_AD_UNIT_ID = "ca-app-pub-XXXXXXXX/YYYYYYYYYY"
```

**E também em:** `app/src/main/AndroidManifest.xml`

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXX~YYYYYYYYYY"/>
```

**Como obter seus IDs:**
1. Acesse: https://apps.admob.com/
2. Crie um app
3. Crie unidades de anúncio (Banner, Interstitial, Rewarded)
4. Copie os IDs e substitua no código

---

### 🔴 2. GOOGLE PLAY BILLING - PRODUTOS DE ASSINATURA

**Localização:** `app/src/main/java/com/calmare/app/managers/BillingManager.kt`

**Configure estes produtos no Google Play Console:**

```kotlin
const val PRODUCT_PREMIUM_MONTHLY = "premium_monthly"   // Assinatura Mensal
const val PRODUCT_PREMIUM_YEARLY = "premium_yearly"     // Assinatura Anual
const val PRODUCT_REMOVE_ADS = "remove_ads"             // Remover Anúncios
const val PRODUCT_LIFETIME = "lifetime_premium"         // Premium Vitalício
```

**Como configurar:**
1. Acesse: https://play.google.com/console/
2. Vá em **"Monetization" → "Subscriptions"**
3. Crie os produtos com os IDs acima
4. Defina os preços sugeridos:
   - **premium_monthly**: R$ 19,90/mês
   - **premium_yearly**: R$ 154,80/ano
   - **remove_ads**: R$ 14,90 (compra única)
   - **lifetime_premium**: R$ 89,90 (compra única - opcional)

---

### 🔴 3. PACOTE DO APLICATIVO

**Localização:** `app/build.gradle`

**Troque o applicationId:**

```gradle
defaultConfig {
    applicationId "com.calmare.app"  // ← TROQUE AQUI
    // ...
}
```

Sugestão: `com.seudominio.calmare`

---

### 🔴 4. ÍCONE DO APP

**Localização:** `app/src/main/res/mipmap-*/`

- **TROQUE** os ícones placeholder pelos ícones reais do seu app
- Use uma ferramenta online para gerar todos os tamanhos: https://romannurik.github.io/AndroidAssetStudio/

---

### 🔴 5. KEYSTORE PARA ASSINATURA (Release)

Para publicar na Play Store, você precisa assinar o app:

1. No Android Studio: **Build → Generate Signed Bundle/APK**
2. Selecione **"Android App Bundle"**
3. Crie um novo KeyStore (guarde em local seguro!)
4. Preencha os dados
5. Gere o AAB assinado

---

## 📂 ESTRUTURA DO PROJETO

```
CalmareApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/calmare/app/
│   │   │   ├── managers/
│   │   │   │   ├── AdManager.kt        ← Gerencia AdMob
│   │   │   │   └── BillingManager.kt   ← Gerencia Billing
│   │   │   └── ui/
│   │   │       ├── SplashActivity.kt
│   │   │       ├── MainActivity.kt
│   │   │       ├── PremiumActivity.kt  ← Tela de assinatura
│   │   │       ├── PlayerActivity.kt
│   │   │       └── SettingsActivity.kt
│   │   ├── res/
│   │   │   ├── layout/                 ← Todas as telas XML
│   │   │   ├── values/
│   │   │   │   ├── strings.xml         ← Todos os textos
│   │   │   │   ├── colors.xml          ← Paleta de cores
│   │   │   │   └── themes.xml
│   │   │   └── drawable/               ← Backgrounds, gradientes
│   │   └── AndroidManifest.xml
│   └── build.gradle                    ← Dependências
├── build.gradle                        ← Config principal
└── README.md                           ← Este arquivo
```

---

## 💰 MONETIZAÇÃO IMPLEMENTADA

### ANÚNCIOS (AdMob):
- ✅ **Banner Ads**: Aparecem na Home, Biblioteca, Configurações (apenas free)
- ✅ **Interstitial Ads**: Ao fechar player após 5+ minutos (apenas free)
- ✅ **Rewarded Ads**: Usuário assiste vídeo para desbloquear conteúdo premium por 24h

### ASSINATURAS (Billing):
- ✅ **Plano Mensal**: R$ 19,90/mês
- ✅ **Plano Anual**: R$ 154,80/ano (destaque: economize 35%!)
- ✅ **Remover Anúncios**: R$ 14,90 (compra única)
- ✅ **Premium Vitalício**: R$ 89,90 (compra única - opcional)

### LÓGICA:
- Usuários **Premium** ou que compraram **Remove Ads** → **NÃO veem anúncios**
- Usuários **Free** → Veem todos os anúncios + conteúdo limitado
- Sistema detecta automaticamente via `BillingManager.shouldShowAds()`

---

## 🎨 DESIGN SYSTEM

### Cores Principais:
- **Primary**: #6C63FF (roxo moderno)
- **Accent**: #FF6B9D (rosa para CTAs)
- **Background**: #F8F9FE (off-white relaxante)
- **Success**: #00D2A0 (verde para checkmarks)

### Tipografia:
- **Headings**: Poppins (bold/semibold)
- **Body**: Inter (regular/medium)

---

## 🛠️ TECNOLOGIAS USADAS

| Componente | Versão | Uso |
|------------|--------|-----|
| Android SDK | API 35 (Android 15) | Compilação e Target |
| Kotlin | 1.9.20 | Linguagem principal |
| Google Mobile Ads | 23.6.0 | AdMob (Anúncios) |
| Play Billing Library | 7.0.0 | Assinaturas e Compras |
| Material Design 3 | 1.11.0 | Componentes UI |
| Jetpack Navigation | 2.7.6 | Navegação entre telas |

---

## ✅ CHECKLIST PRÉ-PUBLICAÇÃO

Antes de publicar na Play Store:

- [ ] Trocar IDs de AdMob (App ID + Banner, Interstitial, Rewarded)
- [ ] Criar produtos de Billing no Play Console
- [ ] Trocar applicationId (pacote)
- [ ] Criar ícones do app (todos os tamanhos)
- [ ] Escrever Política de Privacidade completa
- [ ] Escrever Termos de Uso completos
- [ ] Gerar screenshots para Play Store (mínimo 2 por tela)
- [ ] Assinar o app com KeyStore
- [ ] Gerar AAB (Android App Bundle)
- [ ] Testar em dispositivos reais
- [ ] Testar compras em modo sandbox
- [ ] Definir preços dos produtos
- [ ] Criar página na Play Store (descrição, ícone, screenshots)
- [ ] Enviar para revisão

---

## 📄 POLÍTICA DE PRIVACIDADE E TERMOS

**IMPORTANTE:** Os textos base estão em `res/values/strings.xml`, mas você **DEVE expandir** com informações completas sobre:

- Coleta de dados (AdMob coleta dados de anúncios)
- Uso de cookies e identificadores
- Direitos do usuário (LGPD no Brasil, GDPR na Europa)
- Política de cancelamento de assinaturas
- Informações de contato

**Recomendação:** Use geradores online ou consulte um advogado.

---

## 🐛 SOLUÇÃO DE PROBLEMAS

### Erro de compilação:
- Execute: **File → Invalidate Caches → Invalidate and Restart**
- Execute: **Build → Clean Project** depois **Build → Rebuild Project**

### AdMob não mostra anúncios:
- Certifique-se que usou IDs corretos
- Em teste, use IDs de teste fornecidos
- Adicione seu dispositivo como teste no AdMob

### Billing não funciona:
- Certifique-se que o app está assinado
- Produtos devem estar ativos no Play Console
- Teste apenas em modo Internal Testing ou Production

---

## 📞 SUPORTE

Caso tenha dúvidas:

1. **Android Developers**: https://developer.android.com
2. **AdMob Help**: https://support.google.com/admob
3. **Play Billing Docs**: https://developer.android.com/google/play/billing

---

## 📜 LICENÇA

Este projeto é fornecido como está, para fins educacionais e comerciais.

---

## 🎯 PRÓXIMOS PASSOS

1. **Abra no Android Studio**
2. **Compile e teste**
3. **Configure AdMob e Billing**
4. **Personalize visual (ícones, cores se quiser)**
5. **Publique na Play Store**
6. **Comece a monetizar!** 💰

---

**Desenvolvido com foco em monetização máxima - Novembro 2025**

**Stack Atualizada | Políticas Google Play Compliant | Código Limpo e Organizado**

🚀 **BOM LUCRO!**
