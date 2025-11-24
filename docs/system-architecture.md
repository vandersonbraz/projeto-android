# 🏗️ Arquitetura do Sistema - Optimus Player

**Data:** 23 de Novembro de 2025
**Projeto:** Optimus Player
**Objetivo:** Documentar arquitetura completa com diagramas, fluxos e integrações

---

## 📊 Visão Geral do Sistema

O Optimus Player é composto por **5 componentes principais** que trabalham integrados:

1. **Apps Cliente** (Android, iOS, Smart TVs)
2. **API Backend** (Node.js + PostgreSQL)
3. **Painel Admin** (React - gerenciar sistema)
4. **Painel Revendedor** (React - vender licenças)
5. **Integrações Externas** (Mercado Pago, M3U Providers, EPG)

---

## 🎯 ARQUITETURA GERAL (HIGH-LEVEL)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          USUÁRIOS FINAIS                                 │
└───────────┬──────────────────┬──────────────────┬────────────────────────┘
            │                  │                  │
    ┌───────▼────────┐ ┌──────▼────────┐ ┌──────▼──────────┐
    │  Android App   │ │   iOS App     │ │  Smart TV Apps  │
    │   (Kotlin)     │ │   (Swift)     │ │  (Tizen/webOS)  │
    └───────┬────────┘ └──────┬────────┘ └──────┬──────────┘
            │                  │                  │
            └──────────────────┴──────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │    HTTPS/REST       │
                    │  (SSL/TLS 1.3)      │
                    └──────────┬──────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                      API BACKEND                             │
│                    (Node.js + NestJS)                        │
├──────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │   License   │  │  Reseller    │  │    Admin         │   │
│  │  Controller │  │  Controller  │  │   Controller     │   │
│  └─────────────┘  └──────────────┘  └──────────────────┘   │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              PostgreSQL 17 Database                   │  │
│  │  (users, licenses, resellers, codes, transactions)    │  │
│  └───────────────────────────────────────────────────────┘  │
└────────┬────────────────────────────┬──────────────────────┘
         │                            │
         │                   ┌────────▼────────┐
         │                   │  Mercado Pago   │
         │                   │     Webhook     │
         │                   └─────────────────┘
         │
┌────────▼──────────────────────────────────────────────────┐
│                    WEB DASHBOARDS                         │
├────────────────────────────────┬──────────────────────────┤
│      Admin Panel (React)       │  Reseller Panel (React)  │
│  - Criar revendedores          │  - Gerar códigos         │
│  - Gerenciar licenças          │  - Ver licenças          │
│  - Upload M3U                  │  - Comprar créditos      │
│  - Estatísticas                │  - Dashboard             │
└────────────────────────────────┴──────────────────────────┘
```

---

## 🔄 FLUXOS PRINCIPAIS

### FLUXO 1: Ativação de Licença (Primeira Vez)

```
┌──────────────────────────────────────────────────────────────────────┐
│ FASE 1: Admin cria revendedor                                        │
└──────────────────────────────────────────────────────────────────────┘

ADMIN PANEL                    API BACKEND                DATABASE
     │                              │                          │
     │ POST /admin/reseller/create  │                          │
     ├─────────────────────────────>│                          │
     │ { name, email, password,     │   INSERT INTO resellers  │
     │   initial_credits: 10 }      │   (name, email, ...)     │
     │                              ├─────────────────────────>│
     │                              │<─────────────────────────┤
     │<─────────────────────────────┤   reseller_id: UUID      │
     │ { reseller_id, login_url }   │                          │
     │                              │                          │

┌──────────────────────────────────────────────────────────────────────┐
│ FASE 2: Revendedor gera código de ativação                          │
└──────────────────────────────────────────────────────────────────────┘

RESELLER PANEL                 API BACKEND                DATABASE
     │                              │                          │
     │ POST /reseller/generate-code │                          │
     ├─────────────────────────────>│                          │
     │ Header: Bearer <token>       │   SELECT credits         │
     │                              ├─────────────────────────>│
     │                              │<─────────────────────────┤
     │                              │   credits = 10           │
     │                              │                          │
     │                              │   IF credits > 0:        │
     │                              │   code = generateCode()  │
     │                              │   // Ex: "A1B2-C3D4-E5F6"│
     │                              │                          │
     │                              │   BEGIN TRANSACTION      │
     │                              │   INSERT activation_codes│
     │                              │   UPDATE resellers       │
     │                              │   SET credits = credits-1│
     │                              │   COMMIT                 │
     │                              ├─────────────────────────>│
     │<─────────────────────────────┤<─────────────────────────┤
     │ { activation_code:           │                          │
     │   "A1B2-C3D4-E5F6",          │                          │
     │   credits_remaining: 9 }     │                          │
     │                              │                          │

┌──────────────────────────────────────────────────────────────────────┐
│ FASE 3: Cliente ativa licença no app                                │
└──────────────────────────────────────────────────────────────────────┘

ANDROID APP                    API BACKEND                DATABASE
     │                              │                          │
     │ [Abre app pela 1ª vez]       │                          │
     │ Tela: Ativação               │                          │
     │ Exibe MAC: AA:BB:CC:DD:EE:FF │                          │
     │ Exibe Device ID: android-123 │                          │
     │                              │                          │
     │ [Usuário digita código]      │                          │
     │ Código: A1B2-C3D4-E5F6       │                          │
     │                              │                          │
     │ POST /license/activate       │                          │
     ├─────────────────────────────>│                          │
     │ {                            │                          │
     │   activation_code: "A1B2...", │  SELECT FROM codes      │
     │   mac_address: "AA:BB...",   │  WHERE code = "A1B2..."  │
     │   device_id: "android-123",  ├─────────────────────────>│
     │   device_model: "Samsung S21"│<─────────────────────────┤
     │   platform: "android"        │  code exists, used=false │
     │ }                            │                          │
     │                              │  BEGIN TRANSACTION       │
     │                              │                          │
     │                              │  1. INSERT users         │
     │                              │     (mac, device_id,...)  │
     │                              │                          │
     │                              │  2. INSERT licenses      │
     │                              │     user_id, license_key,│
     │                              │     expires_at = NOW()+1Y│
     │                              │     status = 'active'    │
     │                              │                          │
     │                              │  3. UPDATE activation_codes│
     │                              │     SET used=true,       │
     │                              │     used_at=NOW()        │
     │                              │                          │
     │                              │  COMMIT                  │
     │                              ├─────────────────────────>│
     │<─────────────────────────────┤<─────────────────────────┤
     │ {                            │                          │
     │   status: "success",         │                          │
     │   license_key: "eyJhbG...",  │                          │
     │   expires_at: "2026-11-23",  │                          │
     │   m3u_url: null,             │                          │
     │   epg_url: null              │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Salva license_key em        │                          │
     │  EncryptedSharedPreferences] │                          │
     │                              │                          │
     │ [Navega para tela Home]      │                          │
     │ [Exibe: "Configure seu M3U"] │                          │
     │                              │                          │
```

---

### FLUXO 2: Validação de Licença (Ao Abrir App)

```
ANDROID APP                    API BACKEND                DATABASE
     │                              │                          │
     │ [Usuário abre app]           │                          │
     │                              │                          │
     │ [Lê license_key do storage]  │                          │
     │ license_key = "eyJhbG..."    │                          │
     │ mac_address = "AA:BB..."     │                          │
     │ device_id = "android-123"    │                          │
     │                              │                          │
     │ POST /license/validate       │                          │
     ├─────────────────────────────>│                          │
     │ Header: Bearer eyJhbG...     │                          │
     │ {                            │  SELECT licenses.*,      │
     │   mac_address: "AA:BB...",   │         users.*          │
     │   device_id: "android-123"   │  FROM licenses           │
     │ }                            │  JOIN users ON ...       │
     │                              │  WHERE license_key=...   │
     │                              │  AND mac_address=...     │
     │                              │  AND device_id=...       │
     │                              ├─────────────────────────>│
     │                              │<─────────────────────────┤
     │                              │  license found           │
     │                              │  expires_at="2026-11-23" │
     │                              │  status="active"         │
     │                              │                          │
     │                              │  CHECK:                  │
     │                              │  IF NOW() > expires_at:  │
     │                              │    UPDATE status='expired'│
     │                              │    RETURN valid=false    │
     │                              │  ELSE:                   │
     │                              │    RETURN valid=true     │
     │                              │                          │
     │<─────────────────────────────┤                          │
     │ {                            │                          │
     │   valid: true,               │                          │
     │   expires_at: "2026-11-23",  │                          │
     │   days_remaining: 365,       │                          │
     │   m3u_url: "https://...",    │                          │
     │   epg_url: "https://..."     │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Se valid=true]              │                          │
     │ [Navega para Home]           │                          │
     │                              │                          │
     │ [Se valid=false]             │                          │
     │ [Exibe tela de expiração]    │                          │
     │ "Sua licença expirou.        │                          │
     │  Entre em contato com seu    │                          │
     │  provedor para renovar."     │                          │
     │ [Botão: Renovar Agora]       │                          │
     │                              │                          │
```

---

### FLUXO 3: Renovação de Licença via Mercado Pago

```
┌──────────────────────────────────────────────────────────────────────┐
│ USUÁRIO: Clica em "Renovar Agora" no app                            │
└──────────────────────────────────────────────────────────────────────┘

ANDROID APP                    API BACKEND                MERCADO PAGO
     │                              │                          │
     │ POST /license/renew          │                          │
     ├─────────────────────────────>│                          │
     │ Header: Bearer eyJhbG...     │                          │
     │ { license_id: "uuid..." }    │                          │
     │                              │                          │
     │                              │  CREATE Preference       │
     │                              │  (Mercado Pago SDK)      │
     │                              │                          │
     │                              │  preference = {          │
     │                              │    title: "Licença Anual"│
     │                              │    quantity: 1,          │
     │                              │    unit_price: 50.00,    │
     │                              │    external_reference:   │
     │                              │      license_id          │
     │                              │  }                       │
     │                              │                          │
     │                              │  POST /checkout/preferences│
     │                              ├─────────────────────────>│
     │                              │<─────────────────────────┤
     │                              │  { init_point: "https://│
     │                              │    mpago.li/xyz123" }    │
     │<─────────────────────────────┤                          │
     │ {                            │                          │
     │   payment_url:               │                          │
     │   "https://mpago.li/xyz123"  │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Abre browser com payment_url]                          │
     │ [Usuário paga]               │                          │
     │                              │                          │
     │                              │  [ASYNC - Webhook]       │
     │                              │  POST /webhook/mercadopago│
     │                              │<─────────────────────────┤
     │                              │  {                       │
     │                              │    type: "payment",      │
     │                              │    data: { id: "123" }   │
     │                              │  }                       │
     │                              │                          │
     │                              │  GET /v1/payments/123    │
     │                              ├─────────────────────────>│
     │                              │<─────────────────────────┤
     │                              │  {                       │
     │                              │    status: "approved",   │
     │                              │    external_reference:   │
     │                              │      "license-uuid"      │
     │                              │  }                       │
     │                              │                          │
     │                              │  UPDATE licenses         │
     │                              │  SET expires_at =        │
     │                              │      NOW() + INTERVAL 1Y,│
     │                              │      status = 'active'   │
     │                              │  WHERE id = license-uuid │
     │                              │                          │
     │                              │  INSERT transactions     │
     │                              │  (payment_id, amount,...)│
     │                              │                          │
     │                              │  SEND EMAIL (SendGrid)   │
     │                              │  "Licença renovada!"     │
     │                              │                          │
     │ [Ao abrir app novamente]     │                          │
     │ POST /license/validate       │                          │
     ├─────────────────────────────>│                          │
     │<─────────────────────────────┤                          │
     │ { valid: true,               │                          │
     │   expires_at: "2027-11-23",  │                          │
     │   days_remaining: 365 }      │                          │
     │                              │                          │
     │ [Acesso liberado! 🎉]        │                          │
     │                              │                          │
```

---

### FLUXO 4: Upload de M3U pelo Admin

```
┌──────────────────────────────────────────────────────────────────────┐
│ ADMIN: Upload de M3U para licença específica                        │
└──────────────────────────────────────────────────────────────────────┘

ADMIN PANEL                    API BACKEND                DATABASE
     │                              │                          │
     │ [Seleciona licença]          │                          │
     │ License ID: abc-123          │                          │
     │ [Upload arquivo M3U]         │                          │
     │ File: playlist.m3u (2MB)     │                          │
     │ Duração: 30 dias             │                          │
     │                              │                          │
     │ POST /admin/m3u/upload       │                          │
     ├─────────────────────────────>│                          │
     │ Header: Bearer <admin_token> │                          │
     │ Content-Type: multipart/form │                          │
     │ Body: FormData {             │                          │
     │   license_id: "abc-123",     │  VERIFY admin token      │
     │   m3u_file: <file>,          │                          │
     │   duration_days: 30          │  SAVE file to:           │
     │ }                            │  /storage/m3u/abc-123.m3u│
     │                              │                          │
     │                              │  OR upload to CDN        │
     │                              │  (BunnyCDN Storage API)  │
     │                              │                          │
     │                              │  cdn_url = await         │
     │                              │    uploadToBunnyCDN(file)│
     │                              │                          │
     │                              │  UPDATE licenses         │
     │                              │  SET m3u_url = cdn_url,  │
     │                              │      m3u_uploaded_at =   │
     │                              │        NOW(),            │
     │                              │      m3u_expires_at =    │
     │                              │        NOW() + 30 days   │
     │                              │  WHERE id = "abc-123"    │
     │                              ├─────────────────────────>│
     │<─────────────────────────────┤<─────────────────────────┤
     │ {                            │                          │
     │   m3u_url: "https://cdn...", │                          │
     │   uploaded_at: "2025-11-23", │                          │
     │   expires_at: "2025-12-23"   │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Admin vê confirmação]       │                          │
     │ "M3U carregado com sucesso!" │                          │
     │                              │                          │

┌──────────────────────────────────────────────────────────────────────┐
│ USUÁRIO: Atualiza M3U no app                                        │
└──────────────────────────────────────────────────────────────────────┘

ANDROID APP                    API BACKEND                DATABASE
     │                              │                          │
     │ [Usuário clica "Atualizar"]  │                          │
     │ POST /license/validate       │                          │
     ├─────────────────────────────>│                          │
     │<─────────────────────────────┤  SELECT m3u_url, ...     │
     │ {                            │  FROM licenses ...       │
     │   valid: true,               │                          │
     │   m3u_url: "https://cdn...", │                          │
     │   m3u_expires_at: "2025-12-23"│                          │
     │ }                            │                          │
     │                              │                          │
     │ GET <m3u_url>                │                          │
     │ (via HTTP client)            │                          │
     │                              │                          │
     │ [Download M3U file]          │                          │
     │ [Parse M3U]                  │                          │
     │ [Salva canais no Room DB]    │                          │
     │                              │                          │
     │ [Exibe: "Playlist atualizada!│                          │
     │  Expira em: 2025-12-23"]     │                          │
     │                              │                          │
```

---

### FLUXO 5: Revendedor Compra Créditos

```
RESELLER PANEL                 API BACKEND                MERCADO PAGO
     │                              │                          │
     │ [Seleciona quantidade]       │                          │
     │ Quantity: 10 créditos        │                          │
     │ Preço: R$5/crédito           │                          │
     │ Total: R$50                  │                          │
     │                              │                          │
     │ POST /reseller/buy-credits   │                          │
     ├─────────────────────────────>│                          │
     │ Header: Bearer <token>       │                          │
     │ { quantity: 10 }             │                          │
     │                              │                          │
     │                              │  CREATE Preference       │
     │                              │  preference = {          │
     │                              │    title: "10 Créditos", │
     │                              │    unit_price: 5.00,     │
     │                              │    quantity: 10,         │
     │                              │    external_reference:   │
     │                              │      reseller_id+quantity│
     │                              │  }                       │
     │                              │                          │
     │                              │  POST /checkout/preferences│
     │                              ├─────────────────────────>│
     │<─────────────────────────────┤<─────────────────────────┤
     │ {                            │                          │
     │   payment_url: "https://..."  │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Redireciona para Mercado Pago]                         │
     │ [Usuário paga]               │                          │
     │                              │                          │
     │                              │  POST /webhook/mercadopago│
     │                              │<─────────────────────────┤
     │                              │  { status: "approved" }  │
     │                              │                          │
     │                              │  PARSE external_reference│
     │                              │  reseller_id = "xyz"     │
     │                              │  quantity = 10           │
     │                              │                          │
     │                              │  BEGIN TRANSACTION       │
     │                              │  UPDATE resellers        │
     │                              │  SET credits =           │
     │                              │      credits + 10        │
     │                              │  WHERE id = "xyz"        │
     │                              │                          │
     │                              │  INSERT transactions     │
     │                              │  (reseller_id, amount,   │
     │                              │   credits_purchased: 10) │
     │                              │  COMMIT                  │
     │                              │                          │
     │                              │  SEND EMAIL              │
     │                              │  "10 créditos adicionados!"│
     │                              │                          │
     │ [Recarrega dashboard]        │                          │
     │ GET /reseller/dashboard      │                          │
     ├─────────────────────────────>│                          │
     │<─────────────────────────────┤  SELECT credits FROM ... │
     │ {                            │                          │
     │   credits: 20 (10+10)        │                          │
     │ }                            │                          │
     │                              │                          │
     │ [Exibe: "Créditos: 20"]      │                          │
     │                              │                          │
```

---

## 🗄️ DATABASE SCHEMA (Detalhado)

### Diagrama ER (Entity Relationship)

```
┌─────────────────────┐
│      RESELLERS      │
├─────────────────────┤
│ 🔑 id (UUID PK)     │
│    name             │
│    email (UNIQUE)   │
│    password_hash    │
│    credits (INT)    │
│    parent_id (FK)   │──┐ Hierarquia
│    created_at       │  │ (opcional)
└──────────┬──────────┘  │
           │             │
           └─────────────┘
           │
           │ 1:N
           │
┌──────────▼────────────────┐
│   ACTIVATION_CODES        │
├───────────────────────────┤
│ 🔑 id (UUID PK)           │
│    code (VARCHAR UNIQUE)  │
│ 🔗 reseller_id (FK)       │
│    used (BOOLEAN)         │
│    used_at (TIMESTAMP)    │
│    created_at             │
└──────────┬────────────────┘
           │
           │ 1:1 (quando usado)
           │
┌──────────▼──────────┐         ┌──────────────────┐
│       USERS         │    N:1  │    LICENSES      │
├─────────────────────┤────────>├──────────────────┤
│ 🔑 id (UUID PK)     │         │ 🔑 id (UUID PK)  │
│    mac_address      │<────────│ 🔗 user_id (FK)  │
│    device_id        │   1:N   │    license_key   │
│    device_model     │         │    activation_code│
│    platform         │         │    activated_at  │
│    created_at       │         │    expires_at    │
└─────────────────────┘         │    m3u_url       │
                                │    m3u_uploaded_at│
                                │    m3u_expires_at│
                                │    epg_url       │
                                │    status        │
                                │    created_at    │
                                └──────────────────┘

┌─────────────────────────┐
│     TRANSACTIONS        │
├─────────────────────────┤
│ 🔑 id (UUID PK)         │
│ 🔗 reseller_id (FK)     │──┐
│    amount (DECIMAL)     │  │ N:1
│    credits_purchased    │  │
│    payment_id (MP ID)   │  │
│    status               │  │
│    created_at           │  │
└─────────────────────────┘  │
                             │
            ┌────────────────┘
            │
            └──> RESELLERS
```

### SQL Schema Completo

```sql
-- EXTENSÕES
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- Full-text search

-- TABELA: resellers
CREATE TABLE resellers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    credits INT DEFAULT 0 CHECK (credits >= 0),
    parent_id UUID REFERENCES resellers(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_resellers_email ON resellers(email);
CREATE INDEX idx_resellers_parent ON resellers(parent_id);

-- TABELA: activation_codes
CREATE TABLE activation_codes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) UNIQUE NOT NULL,
    reseller_id UUID REFERENCES resellers(id) ON DELETE CASCADE,
    used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_codes_code ON activation_codes(code);
CREATE INDEX idx_codes_reseller ON activation_codes(reseller_id);
CREATE INDEX idx_codes_used ON activation_codes(used);

-- TABELA: users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mac_address VARCHAR(17) UNIQUE NOT NULL,
    device_id VARCHAR(255) UNIQUE NOT NULL,
    device_model VARCHAR(100),
    platform VARCHAR(20) CHECK (platform IN ('android', 'ios', 'tizen', 'webos', 'roku')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_mac ON users(mac_address);
CREATE INDEX idx_users_device ON users(device_id);

-- TABELA: licenses
CREATE TABLE licenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    license_key TEXT NOT NULL,
    activation_code VARCHAR(20) UNIQUE NOT NULL,
    activated_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    m3u_url TEXT,
    m3u_uploaded_at TIMESTAMP,
    m3u_expires_at TIMESTAMP,
    epg_url TEXT,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'expired', 'suspended')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_licenses_user ON licenses(user_id);
CREATE INDEX idx_licenses_status ON licenses(status);
CREATE INDEX idx_licenses_expires ON licenses(expires_at);
CREATE INDEX idx_licenses_code ON licenses(activation_code);

-- TABELA: transactions
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reseller_id UUID REFERENCES resellers(id) ON DELETE SET NULL,
    license_id UUID REFERENCES licenses(id) ON DELETE SET NULL,
    amount DECIMAL(10, 2) NOT NULL,
    credits_purchased INT DEFAULT 0,
    payment_id VARCHAR(100), -- Mercado Pago payment ID
    payment_method VARCHAR(50),
    status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected', 'refunded')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_transactions_reseller ON transactions(reseller_id);
CREATE INDEX idx_transactions_payment ON transactions(payment_id);
CREATE INDEX idx_transactions_status ON transactions(status);

-- TRIGGER: Atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_resellers_updated_at BEFORE UPDATE ON resellers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_licenses_updated_at BEFORE UPDATE ON licenses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- FUNÇÃO: Verificar e atualizar licenças expiradas (rodar via cron)
CREATE OR REPLACE FUNCTION check_expired_licenses()
RETURNS INT AS $$
DECLARE
    updated_count INT;
BEGIN
    UPDATE licenses
    SET status = 'expired'
    WHERE status = 'active'
    AND expires_at < NOW();

    GET DIAGNOSTICS updated_count = ROW_COUNT;
    RETURN updated_count;
END;
$$ LANGUAGE plpgsql;

-- Cron job (via pg_cron extension ou script externo):
-- */5 * * * * SELECT check_expired_licenses();
```

---

## 🔐 SEGURANÇA

### Autenticação & Autorização

**JWT Tokens:**
```typescript
// Structure
{
  sub: "user-uuid" | "reseller-uuid" | "admin-uuid",
  type: "user" | "reseller" | "admin",
  iat: 1700000000,
  exp: 1700086400 // 24h
}
```

**Endpoints Protegidos:**
| Endpoint | Auth Required | Role |
|----------|---------------|------|
| `/license/activate` | ❌ Público | - |
| `/license/validate` | ✅ Bearer Token | user |
| `/license/renew` | ✅ Bearer Token | user |
| `/reseller/*` | ✅ Bearer Token | reseller |
| `/admin/*` | ✅ Bearer Token | admin |

**Password Hashing:**
- Algorithm: **bcrypt**
- Salt rounds: **12**
- Never store plain passwords

**API Rate Limiting:**
```javascript
// Express rate limiter
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // 100 requests per window
  message: 'Too many requests, please try again later.'
});

app.use('/api/', limiter);
```

**CORS Configuration:**
```javascript
const cors = require('cors');

app.use(cors({
  origin: [
    'https://admin.optimusplayer.com',
    'https://reseller.optimusplayer.com'
  ],
  credentials: true
}));
```

---

## 📊 ESCALABILIDADE

### Estratégias de Escala

**1. Vertical Scaling (até 10k licenças):**
- Upgrade VPS (mais CPU/RAM)
- Simples e rápido
- Downtime mínimo (10-15min)

**2. Horizontal Scaling (10k+ licenças):**

```
                    ┌────────────────────┐
                    │  Load Balancer     │
                    │  (Hetzner LB)      │
                    └─────────┬──────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
    ┌───────▼───────┐ ┌──────▼──────┐ ┌───────▼───────┐
    │   API Node 1  │ │  API Node 2 │ │  API Node 3   │
    │   (Stateless) │ │  (Stateless)│ │  (Stateless)  │
    └───────┬───────┘ └──────┬──────┘ └───────┬───────┘
            │                │                 │
            └────────────────┼─────────────────┘
                             │
                   ┌─────────▼──────────┐
                   │  PostgreSQL        │
                   │  (Primary)         │
                   └─────────┬──────────┘
                             │
                   ┌─────────▼──────────┐
                   │  PostgreSQL        │
                   │  (Read Replica)    │
                   └────────────────────┘
```

**3. Database Optimization:**
- **Connection Pooling** (pg pool, max 20 connections)
- **Read Replicas** (leituras vs escritas separadas)
- **Caching** (Redis para sessões, rate limiting)

**4. CDN Scaling:**
- BunnyCDN escala automaticamente (global edge servers)
- Zero configuração necessária

---

## 📈 MONITORAMENTO

### Métricas Chave (KPIs)

**API Performance:**
- Response time (p50, p95, p99)
- Requests per second (RPS)
- Error rate (4xx, 5xx)

**Database:**
- Query time (slow queries > 100ms)
- Connections ativas
- Lock waits

**Negócio:**
- Licenças ativas vs expiradas
- Taxa de renovação (churn)
- Créditos vendidos (revendedores)
- Revenue (MRR, ARR)

**Stack de Monitoring:**
```
┌─────────────────────────────────────┐
│  Grafana Dashboard (Free Tier)     │
│  - API Metrics                      │
│  - DB Metrics                       │
│  - Business KPIs                    │
└─────────────────────────────────────┘
           ▲
           │
┌──────────┴─────────────────────────┐
│  Prometheus (Metrics Collection)   │
│  - Node.js exporter                │
│  - PostgreSQL exporter             │
└────────────────────────────────────┘
           ▲
           │
┌──────────┴─────────────────────────┐
│  Application Logs                  │
│  - Winston (JSON logs)             │
│  - PM2 logs                        │
└────────────────────────────────────┘
```

---

## ✅ CONCLUSÃO

A arquitetura do Optimus Player foi projetada para ser:

1. **Escalável** - Cresce de 0 a 10k+ licenças sem refactoring
2. **Segura** - JWT, bcrypt, HTTPS, rate limiting
3. **Performante** - Response times < 100ms, streaming otimizado
4. **Confiável** - Backups automáticos, monitoring, high availability
5. **Econômica** - $17/mês inicial, cresce linearmente com receita

**Próximo passo:** Criar GitHub Release v0.1-research com toda a documentação.

---

**Documento criado em:** 23 de Novembro de 2025
**Autor:** Equipe Optimus Player - Arquitetura de Software
**Versão:** 1.0
