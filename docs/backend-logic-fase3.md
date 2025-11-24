# 🔐 LÓGICA DE BACKEND - FASE 3 (Sistema de Licenciamento)

**Data:** 23 de Novembro de 2025
**Projeto:** Optimus Player
**Fase:** 3 - Backend + Licenças + Painéis

---

## 🎯 VISÃO GERAL

O sistema de licenciamento do Optimus Player possui **2 NÍVEIS**:

1. **SERVIDOR (Administrador Principal)** - Você
2. **REVENDEDORES** - Seus parceiros que compram créditos

---

## 👤 TIPOS DE USUÁRIOS E FLUXOS

### **1️⃣ CLIENTES DIRETOS DO SERVIDOR (seus clientes)**

#### **ATIVAÇÃO (Primeira vez):**
```
Cliente baixa app → Vê MAC e Device ID → Paga licença anual via Mercado Pago

↓ (Mercado Pago Webhook aprova pagamento)

Sistema AUTOMATICAMENTE:
  1. Gera código de ativação único
  2. Envia código por EMAIL para o cliente
  3. Cria registro em `licenses` (status: pending)

Cliente:
  1. Recebe email com código
  2. Abre app
  3. Insere código na tela de ativação
  4. App chama API /license/activate
  5. Sistema valida código + MAC + Device ID
  6. Licença ativada (status: active, expires_at: NOW() + 1 YEAR)
```

#### **RENOVAÇÃO (Licença expirando/expirada):**
```
App detecta licença próxima de expirar (7 dias) → Mostra notificação

Cliente clica "Renovar" → Redireciona para Mercado Pago

↓ (Cliente paga)

Mercado Pago aprova → Webhook notifica sistema

Sistema AUTOMATICAMENTE:
  1. Atualiza `licenses.expires_at = NOW() + 1 YEAR`
  2. Atualiza `licenses.status = 'active'`
  3. Envia email: "Licença renovada com sucesso!"

✅ Licença liberada IMEDIATAMENTE
```

**Regras:**
- ✅ Servidor tem **CRÉDITOS ILIMITADOS**
- ✅ Renovação é **100% AUTOMÁTICA**
- ✅ Email enviado automaticamente (SendGrid)
- ✅ Não precisa aprovação manual

---

### **2️⃣ CLIENTES DE REVENDEDORES**

#### **ATIVAÇÃO (Primeira vez):**
```
PASSO 1: Revendedor gera código
─────────────────────────────────
Revendedor acessa painel → Clica "Gerar Código"

Sistema verifica:
  IF revendedor.credits >= 1:
    → Gera código (ex: "A1B2-C3D4-E5F6")
    → Insere em `activation_codes` (reseller_id, used: false)
    → Decrementa: UPDATE resellers SET credits = credits - 1
    → Retorna código para revendedor
  ELSE:
    → Retorna erro: "Créditos insuficientes. Compre mais créditos."

Revendedor fornece código para cliente (WhatsApp, Email, etc)

PASSO 2: Cliente ativa
─────────────────────────
Cliente abre app → Insere código → App chama /license/activate

Sistema:
  1. Valida código existe e não foi usado
  2. Cria user (MAC + Device ID)
  3. Cria license (activation_code, reseller_id, expires_at: NOW() + 1 YEAR)
  4. Marca código como usado: UPDATE activation_codes SET used=true
  5. Retorna: license_key (JWT token)

✅ Cliente ativado
```

#### **RENOVAÇÃO (Licença expirando/expirada):**

**CENÁRIO A: Revendedor TEM créditos**
```
Cliente clica "Renovar" → App abre Mercado Pago

Cliente paga → Mercado Pago aprova

↓ Webhook notifica sistema com external_reference: "license-{uuid}"

Sistema:
  1. Busca license por ID
  2. Busca revendedor responsável (licenses.reseller_id)
  3. Verifica: resellers.credits >= 1

  IF credits >= 1:
    → Debita 1 crédito: UPDATE resellers SET credits = credits - 1
    → Renova licença: UPDATE licenses SET expires_at = NOW() + 1 YEAR, status = 'active'
    → Registra transação
    → Envia email para CLIENTE: "Licença renovada!"
    → Envia email para REVENDEDOR: "Cliente X renovou. Crédito debitado. Saldo: Y"

    ✅ Renovação AUTOMÁTICA
```

**CENÁRIO B: Revendedor NÃO TEM créditos**
```
Cliente clica "Renovar" → App abre Mercado Pago

Cliente paga → Mercado Pago aprova

↓ Webhook notifica sistema

Sistema:
  1. Busca license e revendedor
  2. Verifica: resellers.credits < 1

  IF credits < 1:
    → Cria `pending_renewals` (license_id, payment_id, status: 'awaiting_credits')
    → Envia NOTIFICAÇÃO para revendedor:
       - EMAIL: "Cliente X pagou renovação mas você não tem créditos. Compre agora!"
       - PUSH no painel: Badge vermelho "1 renovação pendente"
    → Envia email para CLIENTE: "Pagamento aprovado. Aguardando aprovação do provedor."

    ⏳ Aguarda revendedor comprar créditos...

REVENDEDOR compra créditos:
  → Acessa painel
  → Vê notificação "1 renovação pendente"
  → Clica "Ver Pendentes"
  → Vê: "Cliente X - Pago em DD/MM/YYYY - Aguardando aprovação"

  OPÇÃO 1: Revendedor compra créditos automaticamente
  ──────────────────────────────────────────────────
  → Clica "Comprar Créditos"
  → Escolhe quantidade (10, 50, 100)
  → Paga via Mercado Pago
  → Mercado Pago aprova
  → Sistema adiciona créditos
  → Sistema AUTOMATICAMENTE processa renovações pendentes:
      PARA CADA pending_renewal WHERE status='awaiting_credits':
        IF reseller.credits >= 1:
          → Debita 1 crédito
          → Renova licença
          → Marca pending_renewal como 'processed'
          → Envia email para cliente: "Licença renovada!"

  OPÇÃO 2: Revendedor aprova manualmente
  ──────────────────────────────────────
  (Se já comprou créditos antes)
  → Clica "Aprovar Renovação" no pending_renewal
  → Sistema debita 1 crédito e renova

  ✅ Renovação LIBERADA
```

---

## 💳 SISTEMA DE CRÉDITOS

### **Compra de Créditos (Revendedor → Servidor)**

```
Revendedor acessa painel → "Comprar Créditos" → Escolhe pacote

PACOTES PROMOCIONAIS:
┌────────────────────────────────────────┐
│  10 créditos  = R$ 50,00  (R$5,00/un)  │
│  50 créditos  = R$225,00  (R$4,50/un)  │ 10% desconto
│ 100 créditos  = R$400,00  (R$4,00/un)  │ 20% desconto
│ 200 créditos  = R$700,00  (R$3,50/un)  │ 30% desconto
└────────────────────────────────────────┘

Revendedor escolhe → Clica "Pagar com Mercado Pago"

Sistema:
  1. Cria Mercado Pago Preference
     - title: "Créditos Optimus Player - {quantidade}"
     - unit_price: {valor}
     - external_reference: "credits-{reseller_id}-{quantity}"

  2. Retorna init_point (URL pagamento)
  3. Revendedor paga

  4. Webhook Mercado Pago aprova

Sistema:
  1. Parse external_reference: "credits-abc123-50"
  2. Extrai: reseller_id = "abc123", quantity = 50

  3. BEGIN TRANSACTION:
     → UPDATE resellers SET credits = credits + 50 WHERE id = 'abc123'
     → INSERT INTO transactions (reseller_id, amount, credits_purchased, payment_id, status)
     → Processar pending_renewals (se houver)
     COMMIT

  4. Envia email: "50 créditos adicionados! Saldo atual: {total}"

  ✅ Créditos liberados IMEDIATAMENTE
```

---

## 📧 EMAILS AUTOMÁTICOS (SendGrid)

### **Templates necessários:**

**1. Código de Ativação (Cliente Direto)**
```
Assunto: Seu código de ativação - Optimus Player

Olá!

Seu pagamento foi aprovado! 🎉

Seu código de ativação:
┌─────────────────────┐
│   A1B2-C3D4-E5F6    │
└─────────────────────┘

Como ativar:
1. Abra o app Optimus Player
2. Insira o código acima
3. Aproveite!

Licença válida até: DD/MM/AAAA

Suporte: suporte@optimusplayer.com
```

**2. Licença Renovada (Cliente)**
```
Assunto: Licença renovada - Optimus Player

Olá!

Sua licença foi renovada com sucesso! 🎉

Nova data de expiração: DD/MM/AAAA

Continue aproveitando o melhor player IPTV!
```

**3. Renovação Pendente (Revendedor SEM créditos)**
```
Assunto: ⚠️ Cliente aguardando renovação - Créditos necessários

Olá {nome_revendedor}!

Você tem 1 renovação pendente de aprovação.

Cliente pagou mas você não tem créditos disponíveis.

Saldo atual: 0 créditos

👉 Compre créditos agora: {link_painel}

Cliente aguardando:
- MAC: AA:BB:CC:DD:EE:FF
- Pago em: DD/MM/YYYY
```

**4. Créditos Adicionados (Revendedor)**
```
Assunto: ✅ Créditos adicionados - Optimus Player

Olá {nome}!

Seu pagamento foi aprovado!

+{quantity} créditos adicionados

Saldo atual: {total} créditos

{processados} renovações pendentes foram processadas automaticamente.

Acesse seu painel: {link}
```

**5. Renovação Automática Debitada (Revendedor)**
```
Assunto: 💳 Cliente renovou - 1 crédito debitado

Olá {nome}!

Cliente renovou a licença:
- MAC: AA:BB:CC:DD:EE:FF
- Data: DD/MM/YYYY

1 crédito debitado

Saldo atual: {credits} créditos
```

---

## 🔔 NOTIFICAÇÕES NO PAINEL (Revendedor)

### **Badge de Renovações Pendentes:**

```javascript
// Dashboard do revendedor
{
  "pending_renewals_count": 3,  // Badge vermelho
  "credits": 0,                  // Alerta se 0
  "last_purchase": "2025-11-20",
  "total_licenses_active": 45
}
```

### **Tela "Renovações Pendentes":**

```
┌─────────────────────────────────────────────────┐
│  RENOVAÇÕES AGUARDANDO APROVAÇÃO (3)            │
├─────────────────────────────────────────────────┤
│                                                 │
│  Cliente: AA:BB:CC:DD:EE:FF                    │
│  Pago em: 20/11/2025 15:30                     │
│  Valor: R$ 50,00 ✅ APROVADO                    │
│  Status: ⏳ Aguardando créditos                 │
│  [Aprovar] (requer 1 crédito)                  │
│                                                 │
├─────────────────────────────────────────────────┤
│  ⚠️ Você não tem créditos suficientes          │
│  [Comprar Créditos Agora]                       │
└─────────────────────────────────────────────────┘
```

---

## 🗄️ TABELAS ADICIONAIS (Banco de Dados)

### **pending_renewals** (nova tabela)
```sql
CREATE TABLE pending_renewals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    license_id UUID REFERENCES licenses(id) ON DELETE CASCADE,
    reseller_id UUID REFERENCES resellers(id),
    payment_id VARCHAR(100), -- Mercado Pago payment ID
    amount DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'awaiting_credits',
    -- Status: 'awaiting_credits', 'processed', 'cancelled'
    created_at TIMESTAMP DEFAULT NOW(),
    processed_at TIMESTAMP
);

CREATE INDEX idx_pending_reseller ON pending_renewals(reseller_id, status);
```

### **notifications** (nova tabela)
```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reseller_id UUID REFERENCES resellers(id),
    type VARCHAR(50), -- 'renewal_pending', 'credits_added', 'renewal_auto'
    title VARCHAR(200),
    message TEXT,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_notifications_reseller ON notifications(reseller_id, read);
```

---

## 🔄 ENDPOINT: /license/renew (CRÍTICO)

```typescript
POST /api/v1/license/renew

Headers:
  Authorization: Bearer <license_key>

Body: {
  license_id: "uuid"
}

Response: {
  payment_url: "https://mpago.li/xyz123"
}

// ────────────────────────────────
// FLUXO COMPLETO
// ────────────────────────────────

async function renewLicense(licenseId: string) {
  // 1. Buscar licença
  const license = await db.licenses.findOne({ id: licenseId });
  const user = await db.users.findOne({ id: license.user_id });

  // 2. Identificar se é cliente direto ou de revendedor
  const isDirectClient = license.reseller_id === null;

  // 3. Criar preferência Mercado Pago
  const preference = {
    title: "Renovação Licença Anual - Optimus Player",
    unit_price: 50.00, // R$50
    quantity: 1,
    external_reference: isDirectClient
      ? `renewal-direct-${license.id}`
      : `renewal-reseller-${license.id}`,

    notification_url: `${API_URL}/webhook/mercadopago`,

    back_urls: {
      success: `${APP_URL}/payment/success`,
      failure: `${APP_URL}/payment/failure`,
      pending: `${APP_URL}/payment/pending`
    }
  };

  const mp = new MercadoPago(ACCESS_TOKEN);
  const response = await mp.preferences.create(preference);

  return { payment_url: response.init_point };
}
```

---

## 🔔 WEBHOOK: /webhook/mercadopago (CRÍTICO)

```typescript
POST /api/v1/webhook/mercadopago

Body: {
  type: "payment",
  data: { id: "123456789" }
}

async function handleMercadoPagoWebhook(data: any) {
  if (data.type !== 'payment') return;

  // 1. Buscar detalhes do pagamento
  const mp = new MercadoPago(ACCESS_TOKEN);
  const payment = await mp.payment.get(data.data.id);

  if (payment.status !== 'approved') return;

  const ref = payment.external_reference;

  // ═══════════════════════════════════════
  // TIPO 1: RENOVAÇÃO CLIENTE DIRETO
  // ═══════════════════════════════════════
  if (ref.startsWith('renewal-direct-')) {
    const licenseId = ref.replace('renewal-direct-', '');

    await db.transaction(async (trx) => {
      // Renovar licença IMEDIATAMENTE
      await trx.licenses.update({
        where: { id: licenseId },
        data: {
          expires_at: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000),
          status: 'active'
        }
      });

      // Registrar transação
      await trx.transactions.create({
        license_id: licenseId,
        amount: payment.transaction_amount,
        payment_id: payment.id,
        status: 'approved'
      });
    });

    // Enviar email
    await sendEmail({
      to: user.email,
      template: 'license_renewed',
      data: { expires_at: new Date(...) }
    });

    return;
  }

  // ═══════════════════════════════════════
  // TIPO 2: RENOVAÇÃO CLIENTE REVENDEDOR
  // ═══════════════════════════════════════
  if (ref.startsWith('renewal-reseller-')) {
    const licenseId = ref.replace('renewal-reseller-', '');
    const license = await db.licenses.findOne({ id: licenseId });
    const reseller = await db.resellers.findOne({ id: license.reseller_id });

    // Verificar créditos
    if (reseller.credits >= 1) {
      // ✅ TEM CRÉDITOS: Renovar automaticamente
      await db.transaction(async (trx) => {
        // Debitar crédito
        await trx.resellers.update({
          where: { id: reseller.id },
          data: { credits: { decrement: 1 } }
        });

        // Renovar licença
        await trx.licenses.update({
          where: { id: licenseId },
          data: {
            expires_at: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000),
            status: 'active'
          }
        });

        // Registrar transação
        await trx.transactions.create({
          license_id: licenseId,
          reseller_id: reseller.id,
          amount: payment.transaction_amount,
          payment_id: payment.id,
          status: 'approved'
        });
      });

      // Emails
      await sendEmail({
        to: user.email,
        template: 'license_renewed'
      });

      await sendEmail({
        to: reseller.email,
        template: 'renewal_debited',
        data: {
          mac: user.mac_address,
          credits_remaining: reseller.credits - 1
        }
      });

    } else {
      // ❌ SEM CRÉDITOS: Criar pendência
      await db.pending_renewals.create({
        license_id: licenseId,
        reseller_id: reseller.id,
        payment_id: payment.id,
        amount: payment.transaction_amount,
        status: 'awaiting_credits'
      });

      // Notificar revendedor
      await db.notifications.create({
        reseller_id: reseller.id,
        type: 'renewal_pending',
        title: 'Renovação Aguardando Créditos',
        message: `Cliente ${user.mac_address} pagou renovação mas você não tem créditos.`
      });

      await sendEmail({
        to: reseller.email,
        template: 'renewal_pending_no_credits',
        data: { mac: user.mac_address }
      });

      await sendEmail({
        to: user.email,
        template: 'renewal_awaiting_approval'
      });
    }

    return;
  }

  // ═══════════════════════════════════════
  // TIPO 3: COMPRA DE CRÉDITOS
  // ═══════════════════════════════════════
  if (ref.startsWith('credits-')) {
    const parts = ref.split('-'); // "credits-{reseller_id}-{quantity}"
    const resellerId = parts[1];
    const quantity = parseInt(parts[2]);

    await db.transaction(async (trx) => {
      // Adicionar créditos
      await trx.resellers.update({
        where: { id: resellerId },
        data: { credits: { increment: quantity } }
      });

      // Registrar transação
      await trx.transactions.create({
        reseller_id: resellerId,
        amount: payment.transaction_amount,
        credits_purchased: quantity,
        payment_id: payment.id,
        status: 'approved'
      });

      // PROCESSAR RENOVAÇÕES PENDENTES AUTOMATICAMENTE
      const pending = await trx.pending_renewals.findMany({
        where: {
          reseller_id: resellerId,
          status: 'awaiting_credits'
        }
      });

      let processed = 0;
      const reseller = await trx.resellers.findOne({ id: resellerId });

      for (const renewal of pending) {
        if (reseller.credits - processed >= 1) {
          // Processar renovação
          await processRenewal(renewal, trx);
          processed++;
        } else {
          break; // Sem créditos suficientes
        }
      }
    });

    // Email
    await sendEmail({
      to: reseller.email,
      template: 'credits_added',
      data: {
        quantity,
        total: reseller.credits + quantity,
        processed
      }
    });
  }
}
```

---

## ✅ RESUMO FINAL

### **CLIENTE DIRETO (Servidor):**
- Ativação: Paga → Email com código → Ativa
- Renovação: Paga → Libera AUTOMATICAMENTE

### **CLIENTE REVENDEDOR:**
- Ativação: Revendedor gera código (gasta 1 crédito) → Fornece para cliente → Cliente ativa
- Renovação COM créditos: Paga → Libera AUTOMATICAMENTE
- Renovação SEM créditos: Paga → Fica pendente → Revendedor compra créditos → Libera

### **REVENDEDOR:**
- Compra créditos do servidor
- Gera códigos (gasta créditos)
- Renovações debitam créditos automaticamente
- Notificado quando sem créditos

---

**Documento criado em:** 23 de Novembro de 2025
**Autor:** Equipe Optimus Player - Backend Architecture
**Versão:** 1.0 - Definitivo para Fase 3
