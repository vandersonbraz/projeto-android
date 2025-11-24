# 🛠️ Decisão de Stack Tecnológica - Optimus Player

**Data:** 23 de Novembro de 2025
**Projeto:** Optimus Player
**Objetivo:** Definir e justificar a stack tecnológica completa para desenvolvimento multiplataforma

---

## 📊 Resumo Executivo

Após análise profunda das tecnologias disponíveis em 2025, definimos uma stack híbrida que maximiza performance, produtividade e compatibilidade multiplataforma:

**Stack Escolhida:**
- 📱 **Mobile (Android):** Kotlin Nativo + Jetpack Compose
- 🖥️ **Backend:** Node.js (Express/NestJS) + PostgreSQL
- 🎨 **Painéis Web:** React.js + TypeScript + Tailwind CSS
- 📺 **Smart TVs:** SDK nativo (Tizen: JS/HTML5, webOS: React, Roku: BrightScript)
- 🍎 **iOS:** Swift + SwiftUI (FASE 5)
- 🎬 **Player:** ExoPlayer (Android), AVPlayer (iOS)
- 📦 **CDN:** BunnyCDN
- ☁️ **Infraestrutura:** Hetzner Cloud VPS

---

## 📱 MOBILE & CROSS-PLATFORM

### Análise: React Native vs Flutter vs Kotlin Multiplatform

#### **Performance Comparisons (2025)**

| Métrica | Flutter | React Native | Kotlin Multiplatform | **Escolha** |
|---------|---------|--------------|---------------------|-------------|
| **UI Performance** | 🥇 Excelente (60fps+) | 🥈 Bom (60fps) | 🥇 Nativo (60fps+) | - |
| **CPU Performance** | 🥈 Bom | 🥉 Médio | 🥇 **+13% vs outros** | ✅ KMP |
| **Cold Start Time** | 🥇 Rápido | 🥈 Médio | 🥇 Muito rápido | - |
| **Memory Efficiency** | 🥈 Bom | 🥉 Médio | 🥇 Excelente | ✅ KMP |
| **Custo MVP** | 🥇 Mais baixo | 🥈 Médio | 🥉 Mais alto | - |
| **Dev Speed** | 🥇 Muito rápido | 🥈 Rápido | 🥉 Médio | - |
| **Native Feel** | 🥉 Customizado | 🥈 Bom | 🥇 100% Nativo | ✅ KMP |

#### **Recomendações por Caso de Uso**

**Flutter:** Startups ou MVPs com foco em UI consistente e desenvolvimento rápido.

**React Native:** Times com expertise em JavaScript/web, necessidade de grande ecossistema de libs.

**Kotlin Multiplatform:** Times com expertise Android, necessidade de performance nativa, apps complexos.

**OTT/IPTV Específico:**
> "Going for a cross-platform tool for web, android and iOS while opting for a separate solution for the web based TV platforms will get you to all the platforms you need, while getting the best performance possible."

### ✅ DECISÃO: KOTLIN NATIVO (Android) + SWIFT (iOS)

**Justificativa:**

Para o Optimus Player, escolhemos **abordagem nativa por plataforma** pelos seguintes motivos:

1. **Performance Crítica:**
   - IPTV streaming requer performance máxima (decodificação de vídeo, parsing de M3U)
   - Kotlin/Swift nativos oferecem acesso direto a APIs de hardware (decoders, Chromecast, AirPlay)

2. **Player Engines Nativos:**
   - **ExoPlayer** (Android) é o melhor player para IPTV (usado por concorrentes)
   - **AVPlayer** (iOS) é otimizado para Apple ecosystem
   - Cross-platform players (VLC, Video.js) têm limitações

3. **Smart TV Compatibility:**
   - Samsung Tizen e LG webOS exigem desenvolvimento separado de qualquer forma
   - Roku usa BrightScript (não compatível com nenhum framework cross-platform)
   - Melhor ter expertise em desenvolvimento nativo desde o início

4. **Longo Prazo:**
   - Manutenibilidade superior (sem dependência de framework terceiro)
   - Performance previsível (sem camada de abstração)
   - Acesso a features nativas no dia 1 de lançamento (OS updates)

**Trade-offs Aceitos:**
- ❌ Desenvolvimento mais lento inicialmente (2 codebases)
- ❌ Custo de desenvolvimento maior
- ✅ Performance superior (crítico para IPTV)
- ✅ Acesso completo a APIs nativas (Chromecast, EPG, PiP)
- ✅ Melhor UX (100% native feel)

### 🔧 Stack Mobile Detalhada

**Android (Kotlin):**
- **Linguagem:** Kotlin 2.0+
- **UI:** Jetpack Compose (declarativo, moderno)
- **Architecture:** MVVM + Repository Pattern
- **Player:** ExoPlayer (Media3 library)
- **Networking:** Retrofit + OkHttp
- **Database:** Room (SQLite wrapper)
- **DI:** Hilt (Dagger)
- **Async:** Coroutines + Flow
- **Chromecast:** Google Cast SDK

**iOS (Swift) - FASE 5:**
- **Linguagem:** Swift 6.0+
- **UI:** SwiftUI
- **Architecture:** MVVM + Combine
- **Player:** AVPlayer + AVKit
- **Networking:** URLSession + Alamofire
- **Database:** Core Data / Realm
- **DI:** Swinject / native
- **Async:** async/await + Combine
- **AirPlay:** Nativo (AVPlayer)

**Fontes:**
- [Flutter vs React Native vs KMP 2025](https://dev.to/forge-stackobea/flutter-react-native-or-kotlin-multiplatform-choosing-the-right-stack-in-2025-22g3)
- [Kotlin Multiplatform vs React Native](https://kotlinlang.org/docs/multiplatform/kotlin-multiplatform-react-native.html)
- [Tech for OTT Apps 2025](https://mlangendijk.medium.com/tech-to-use-for-your-front-end-ott-apps-in-2025-d3e840719484)

---

## 🎬 PLAYER ENGINES

### ExoPlayer (Android) - Escolha Confirmada

**Por que ExoPlayer?**
- ✅ Player oficial recomendado pelo Google
- ✅ Usado pela maioria dos apps IPTV Android (IBO, Bob, Cap Player)
- ✅ Suporte nativo a M3U8 (HLS streaming)
- ✅ Hardware acceleration out-of-the-box
- ✅ Adaptive bitrate streaming (ABR)
- ✅ Extensível (custom decoders, DRM)

**Configuração Recomendada (2025):**
- Use `media3-exoplayer` (versão moderna)
- Enable **ExoPlayer-AV1 decoder** (reduz CPU load)
- Hardware acceleration ON (reduz CPU load em sessões longas)
- Custom buffer size baseado em velocidade da conexão

**Best Practices:**
```kotlin
// ExoPlayer configuration for IPTV
val exoPlayer = ExoPlayer.Builder(context)
    .setLoadControl(
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30_000, // min buffer
                60_000, // max buffer
                2_500,  // playback start
                5_000   // rebuffer
            )
            .build()
    )
    .setSeekBackIncrementMs(10_000)
    .setSeekForwardIncrementMs(10_000)
    .build()
```

**Fontes:**
- [ExoPlayer Official](https://exoplayer.dev/)
- [20 Android IPTV Players 2025](https://www.watchingiptv.com/android-iptv-player/)
- [ExoPlayer M3U8 Best Practices](https://stackoverflow.com/questions/71802603/how-can-i-play-this-m3u8-with-exoplayer)

### AVPlayer (iOS) - FASE 5

**Por que AVPlayer?**
- ✅ Nativo da Apple (performance máxima)
- ✅ Suporte nativo a HLS (M3U8)
- ✅ AirPlay integrado (zero config)
- ✅ Picture-in-Picture nativo
- ✅ Integração com sistema (lock screen controls)

---

## 🖥️ BACKEND

### Análise: Node.js vs Python (FastAPI) vs Go

#### **Performance Benchmarks (2025)**

| Métrica | Node.js | Python FastAPI | Go | **Escolha** |
|---------|---------|----------------|-----|-------------|
| **Requests/sec** | 🥈 ~44% mais rápido que Python | 🥉 ~3,000 req/s | 🥇 **+260% vs Node** | - |
| **Latency** | 🥈 Baixa | 🥉 Média | 🥇 Muito baixa | ✅ Go (ideal) |
| **Concurrency** | 🥈 Event Loop (bom) | 🥉 Asyncio (ok) | 🥇 **Goroutines (milhares)** | ✅ Go |
| **Real-time** | 🥇 Excelente (WebSockets) | 🥉 Limitado | 🥈 Muito bom | ✅ Node |
| **Dev Speed** | 🥇 Rápido (JS/TS) | 🥈 Médio | 🥉 Mais lento | ✅ Node |
| **Ecosystem** | 🥇 NPM (maior) | 🥈 PyPI (grande) | 🥉 Crescendo | ✅ Node |
| **Streaming** | 🥈 Bom | 🥉 Não ideal | 🥇 **Excelente** | ✅ Go |

#### **Casos de Uso Específicos**

**Node.js:** Real-time, microservices, streaming, alta concorrência I/O-bound.

**FastAPI (Python):** ML pipelines, batch processing, automação, integração com IA.

**Go:** Performance crítica, concorrência massiva, streaming de vídeo, deploy lean.

### ✅ DECISÃO: NODE.JS (Express/NestJS)

**Justificativa:**

Embora **Go seja tecnicamente superior** para streaming e concorrência, escolhemos **Node.js** pelos seguintes motivos:

1. **Time to Market:**
   - Desenvolvimento 30-40% mais rápido (JavaScript/TypeScript familiar)
   - Ecossistema NPM enorme (libs prontas)
   - Integração nativa com React (painéis web)

2. **Real-time Capabilities:**
   - WebSockets nativos (notificações de expiração de licença)
   - Server-Sent Events (SSE) para updates em tempo real
   - Event-driven architecture (ideal para webhooks Mercado Pago)

3. **Ecossistema:**
   - Mercado Pago SDK oficial para Node.js
   - PostgreSQL drivers maduros (pg, TypeORM, Prisma)
   - JWT libraries robustas (jsonwebtoken, passport)

4. **Escalabilidade Suficiente:**
   - Node.js aguenta milhares de conexões simultâneas
   - Para IPTV license management (não transcoding), Node.js é mais que suficiente
   - Podemos migrar partes críticas para Go no futuro se necessário (microservices)

**Trade-offs Aceitos:**
- ❌ Performance menor que Go (~40% em CPU-bound tasks)
- ✅ Desenvolvimento muito mais rápido
- ✅ Time já familiar com JavaScript/TypeScript
- ✅ Ecossistema maduro com todas as libs necessárias

### 🔧 Stack Backend Detalhada

**Framework:**
- **NestJS** (preferido) - estrutura enterprise, TypeScript-first, DI, modular
  - OU **Express** (alternativa) - minimalista, flexível, maduro

**Database ORM:**
- **Prisma** (moderno, type-safe) OU **TypeORM** (maduro, decorators)

**Autenticação:**
- **JWT** (jsonwebtoken)
- **Passport.js** (estratégias de auth)

**Validação:**
- **class-validator** + **class-transformer** (NestJS)
- OU **Joi** / **Zod** (Express)

**Pagamentos:**
- **Mercado Pago SDK** oficial (Node.js)

**Real-time:**
- **Socket.io** (WebSockets)
- OU **Server-Sent Events** nativo

**Monitoring:**
- **PM2** (process manager)
- **Winston** / **Pino** (logging)

**Fontes:**
- [Node.js vs Python 2025](https://kanhasoft.com/blog/node-js-vs-python-which-is-best-for-backend-development-in-2025/)
- [FastAPI vs Node.js Performance](https://hostadvice.com/blog/web-hosting/node-js/fastapi-vs-nodejs/)
- [Go vs Node.js Benchmark 2025](https://itnext.io/performance-benchmark-node-js-vs-go-9dbad158c3b0/)
- [Go for Video Streaming](https://memo.d.foundation/golang/golang-for-high-performance-video-streaming)

---

## 🗄️ BANCO DE DADOS

### Análise: PostgreSQL vs MongoDB

#### **Comparação (2025)**

| Aspecto | PostgreSQL | MongoDB | **Escolha** |
|---------|------------|---------|-------------|
| **Tipo** | Relacional (SQL) | NoSQL (Document) | - |
| **ACID** | ✅ Full ACID | ⚠️ Eventual consistency | ✅ PostgreSQL |
| **Relações** | 🥇 Foreign Keys, Joins | 🥉 Embedding/Refs | ✅ PostgreSQL |
| **Integridade** | 🥇 Schema enforced | 🥉 Schema-less | ✅ PostgreSQL |
| **Performance** | 🥈 Excelente (OLTP) | 🥇 Rápido (reads) | - |
| **Escalabilidade** | 🥈 Vertical + sharding | 🥇 Horizontal nativo | - |
| **Licença** | 🥇 PostgreSQL License (livre) | ⚠️ SSPL (restritiva) | ✅ PostgreSQL |
| **Custo** | 🥇 Open-source puro | 🥉 MongoDB Inc. fees | ✅ PostgreSQL |

### ✅ DECISÃO: POSTGRESQL

**Justificativa:**

Para o sistema de licenciamento do Optimus Player, **PostgreSQL é a escolha clara** porque:

1. **Integridade de Dados Crítica:**
   - Licenças, pagamentos, revendedores = dados relacionais com integridade obrigatória
   - Foreign keys garantem consistência (licença sempre tem um usuário válido)
   - Transactions ACID (pagamento Mercado Pago → atualizar licença → tudo ou nada)

2. **Relações Complexas:**
   ```
   Users ← Licenses → ActivationCodes → Resellers → Transactions
   ```
   - Relações many-to-one e one-to-many naturais em SQL
   - Joins eficientes (dashboard de admin mostra dados de múltiplas tabelas)

3. **Schema Bem Definido:**
   - Estrutura de licenças, usuários, revendedores não muda frequentemente
   - Schema enforcement previne bugs (campo obrigatório não pode ser null)

4. **Licença Livre:**
   - PostgreSQL License é permissiva (pode usar comercialmente sem restrições)
   - MongoDB SSPL tem restrições para SaaS

5. **Maturidade:**
   - PostgreSQL é battle-tested há 30+ anos
   - JSON support nativo (caso precise de flexibilidade em alguns campos)
   - Full-text search (buscar licenças por email, nome, etc)

**MongoDB seria melhor se:**
- ❌ Schema mudasse constantemente (não é o caso)
- ❌ Precisássemos escalar horizontalmente para milhões de licenças (não é o caso inicial)
- ❌ Dados fossem hierárquicos/nested (não é o caso)

### 🔧 Schema PostgreSQL

**5 Tabelas Principais:**
1. `users` - Clientes (MAC, Device ID, modelo)
2. `licenses` - Licenças (ativação, expiração, M3U URL, status)
3. `resellers` - Revendedores (créditos, hierarquia)
4. `activation_codes` - Códigos de ativação (código, usado, revendedor)
5. `transactions` - Transações Mercado Pago (valor, créditos, status)

**Extensions Úteis:**
- `uuid-ossp` (UUIDs para IDs)
- `pg_trgm` (full-text search rápido)
- `pgcrypto` (hash de senhas)

**Fontes:**
- [PostgreSQL vs MongoDB 2025](https://www.opticflux.com/mongodb-vs-postgresql-which-database-should-you-use-in-2025/76369/)
- [PostgreSQL for Enterprise 2025](https://xenoss.io/blog/postgresql-mongodb-comparison)
- [Database Comparison 2025](https://www.bytebase.com/blog/postgres-vs-mongodb/)

---

## 🎨 PAINÉIS WEB (Admin + Revendedor)

### ✅ DECISÃO: REACT.JS + TYPESCRIPT + TAILWIND CSS

**Justificativa:**

React é a escolha óbvia para painéis web em 2025:

1. **Ecossistema Maduro:**
   - Maior biblioteca de componentes (Material-UI, Ant Design, Shadcn/UI)
   - Integração perfeita com TypeScript
   - Tooling excelente (Vite, Create React App, Next.js)

2. **Performance:**
   - Virtual DOM eficiente
   - Code-splitting nativo
   - React Server Components (Next.js 15+)

3. **Integração com Backend:**
   - Mesmo time que faz Node.js backend (JavaScript/TypeScript)
   - Axios para HTTP requests (type-safe com TypeScript)
   - React Query para cache/sync de dados

4. **UI/UX:**
   - Tailwind CSS para design system (preto + amarelo)
   - Framer Motion para animações fluidas
   - Recharts/Chart.js para dashboards

**Stack Detalhada:**
- **Framework:** React 19 + TypeScript
- **Bundler:** Vite (mais rápido que Webpack)
- **Styling:** Tailwind CSS + CSS Modules
- **State:** React Context + React Query (server state)
- **Forms:** React Hook Form + Zod (validação)
- **Routing:** React Router v7
- **Charts:** Recharts
- **Animations:** Framer Motion
- **HTTP:** Axios + React Query

---

## 📺 SMART TVS

### ✅ DECISÃO: SDK NATIVO POR PLATAFORMA

**Samsung Tizen:**
- **Linguagem:** HTML5 + CSS3 + JavaScript
- **Framework:** React adaptado (não oficial) OU Vanilla JS
- **Player:** Tizen AVPlay API OU Video.js
- **SDK:** Tizen Studio

**LG webOS:**
- **Linguagem:** Enyo Framework OU React
- **Player:** webOS Media API
- **SDK:** webOS TV SDK

**Roku:**
- **Linguagem:** BrightScript + SceneGraph XML
- **Player:** Roku Video Node (nativo)
- **SDK:** Roku SDK
- **Nota:** Roku é 100% diferente (não há cross-platform)

**Justificativa:**
- Cada plataforma tem SDK próprio e não-compatível
- Performance nativa é crítica para TVs (hardware limitado)
- UI deve seguir guidelines de cada plataforma

---

## ☁️ INFRAESTRUTURA & CDN

### VPS: ✅ HETZNER CLOUD

**Justificativa:**
- 🥇 **Melhor custo-benefício** em 2025
- 🥇 Performance (~13% mais rápido que Contabo)
- 🥇 Preço acessível ($5.50/mês para início)
- 🥇 Bandwidth generoso (20TB incluído)
- 🥇 Localização: Europa (pode expandir para Brasil depois)

**Plano Inicial Recomendado:**
- **CPX21:** 3 vCPU AMD, 4GB RAM, 80GB SSD, 20TB bandwidth
- **Preço:** ~$11/mês
- **Escalabilidade:** Fácil upgrade vertical

**Alternativas:**
- **Contabo:** Mais barato ($4.99), mas performance inferior
- **DigitalOcean/Vultr:** Mais caro ($18-21), melhor para USA

### CDN: ✅ BUNNYCDN

**Justificativa:**
- 🥇 **Preço imbatível** para streaming de vídeo
- 🥇 $60/ano vs Cloudflare $828/ano (exemplo 250GB storage + 500GB traffic)
- 🥇 Excelente para media-heavy applications
- 🥇 "Pay as you go" simples
- 🥇 Streaming optimizado

**BunnyCDN Stream Pricing:**
- **Storage:** $0.005/GB/mês
- **Traffic:** $0.01/GB (América)
- **Encoding:** Opcional

**Cloudflare seria melhor se:**
- Precisássemos de WAF avançado (não é o caso inicial)
- Tivéssemos audiência 100% global (não é o caso - Brasil foco)

**Fontes:**
- [Hetzner vs Competitors 2025](https://www.wpdoze.com/digitalocean-vs-vultr-vs-hetzner/)
- [VPS Comparison 2025](https://www.vpsbenchmarks.com/compare/hetzner_vs_vultr)
- [BunnyCDN vs Cloudflare](https://www.cdnplanet.com/compare/cloudflare/bunnycdn/)
- [CDN for OTT/IPTV](https://blog.blazingcdn.com/en-us/best-cdn-providers-for-ott-and-iptv-platforms)

---

## 📊 STACK COMPLETA - RESUMO

### FASE 2 - Android App
```
┌─────────────────────────────────┐
│   ANDROID APP (Kotlin)          │
├─────────────────────────────────┤
│ • Jetpack Compose (UI)          │
│ • ExoPlayer (Media3)            │
│ • Room (Database)               │
│ • Retrofit + OkHttp (Network)   │
│ • Hilt (DI)                     │
│ • Coroutines + Flow (Async)     │
│ • Google Cast SDK (Chromecast)  │
└─────────────────────────────────┘
```

### FASE 3 - Backend + Painéis
```
┌─────────────────────────────────┐
│   BACKEND (Node.js + NestJS)    │
├─────────────────────────────────┤
│ • NestJS (Framework)            │
│ • PostgreSQL (Database)         │
│ • Prisma (ORM)                  │
│ • JWT (Auth)                    │
│ • Mercado Pago SDK              │
│ • Socket.io (Real-time)         │
└─────────────────────────────────┘
         ↕
┌─────────────────────────────────┐
│   PAINÉIS (React + TypeScript)  │
├─────────────────────────────────┤
│ • React 19 + TypeScript         │
│ • Tailwind CSS (Styling)        │
│ • React Query (Server state)    │
│ • Recharts (Gráficos)           │
│ • Axios (HTTP)                  │
└─────────────────────────────────┘
```

### FASE 4 - Smart TVs
```
┌──────────────────┬──────────────────┬──────────────────┐
│  Tizen (Samsung) │  webOS (LG)      │  Roku            │
├──────────────────┼──────────────────┼──────────────────┤
│ • JS/HTML5       │ • Enyo/React     │ • BrightScript   │
│ • AVPlay         │ • Media API      │ • Video Node     │
│ • Tizen Studio   │ • webOS SDK      │ • Roku SDK       │
└──────────────────┴──────────────────┴──────────────────┘
```

### FASE 5 - iOS
```
┌─────────────────────────────────┐
│   iOS APP (Swift)               │
├─────────────────────────────────┤
│ • SwiftUI (UI)                  │
│ • AVPlayer + AVKit (Player)     │
│ • Core Data (Database)          │
│ • URLSession (Network)          │
│ • Combine (Reactive)            │
│ • AirPlay (Nativo)              │
└─────────────────────────────────┘
```

### Infraestrutura
```
┌─────────────────────────────────┐
│   CLOUD INFRASTRUCTURE          │
├─────────────────────────────────┤
│ • Hetzner Cloud (VPS)           │
│ • BunnyCDN (Video CDN)          │
│ • PostgreSQL 17                 │
│ • Docker + Docker Compose       │
│ • PM2 (Process Manager)         │
│ • Nginx (Reverse Proxy)         │
└─────────────────────────────────┘
```

---

## ✅ CONCLUSÃO

A stack escolhida oferece o melhor equilíbrio entre:

1. **Performance** - Nativo onde importa (mobile players), rápido onde é suficiente (backend)
2. **Produtividade** - Tecnologias modernas com ótimo DX (Developer Experience)
3. **Custo** - Infraestrutura econômica sem sacrificar qualidade
4. **Escalabilidade** - Pode crescer conforme demanda
5. **Manutenibilidade** - Stack madura, bem documentada, comunidade ativa

**Próximo passo:** Planejar infraestrutura detalhada e custos operacionais.

---

**Documento criado em:** 23 de Novembro de 2025
**Autor:** Equipe Optimus Player - Arquitetura & Engenharia
**Versão:** 1.0
