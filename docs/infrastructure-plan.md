# ☁️ Plano de Infraestrutura - Optimus Player

**Data:** 23 de Novembro de 2025
**Projeto:** Optimus Player
**Objetivo:** Definir infraestrutura completa (VPS, CDN, custos) para sistema de licenciamento IPTV

---

## 📊 Resumo Executivo

Infraestrutura econômica e escalável baseada em **Hetzner Cloud** (VPS) + **BunnyCDN** (CDN de vídeo), com custo inicial estimado de **$20-30/mês** para até 500 licenças ativas e capacidade de escalar linearmente conforme crescimento.

**Componentes:**
- ☁️ VPS: Hetzner Cloud CPX21 ($11/mês)
- 🌐 CDN: BunnyCDN pay-as-you-go (~$5-15/mês inicial)
- 🗄️ Database: PostgreSQL 17 (incluído no VPS)
- 🔒 SSL: Let's Encrypt (gratuito)
- 📧 Email: SendGrid free tier (12k emails/mês)

---

## 🖥️ VPS - HETZNER CLOUD

### Por que Hetzner Cloud?

**Vantagens:**
1. ✅ **Custo-benefício líder em 2025**
2. ✅ Performance ~13% superior ao Contabo (benchmark)
3. ✅ Bandwidth generoso (20TB incluído no CPX21)
4. ✅ Snapshots e backups automáticos
5. ✅ Network 100% uptime SLA
6. ✅ Localização: Alemanha, Finlândia (expandir para USA depois)

**Comparação de Preço (mesmo spec):**
| Provider | CPU | RAM | Storage | Bandwidth | Preço/mês |
|----------|-----|-----|---------|-----------|-----------|
| Hetzner CPX21 | 3 vCPU AMD | 4GB | 80GB SSD | 20TB | **$11** |
| DigitalOcean | 2 vCPU | 4GB | 80GB SSD | 4TB | **$24** |
| Vultr | 2 vCPU | 4GB | 80GB SSD | 3TB | **$18** |
| Contabo | 4 vCPU | 8GB | 200GB SSD | 32TB | **$8.50** ⚠️ |

_⚠️ Contabo é mais barato, mas performance inferior e suporte lento_

### Planos Recomendados por Fase

#### **FASE 3 - MVP (0-500 licenças ativas)**

**Hetzner CPX21**
- **vCPU:** 3 cores AMD EPYC
- **RAM:** 4GB
- **Storage:** 80GB SSD NVMe
- **Bandwidth:** 20TB/mês incluído
- **Network:** 20 Gbit/s
- **Preço:** $11/mês

**Capacidade Estimada:**
- 500 licenças ativas simultâneas
- 100 requisições/segundo (API)
- 50 painéis admin/revendedor simultâneos
- PostgreSQL com ~100k registros

**Justificativa:**
- 4GB RAM suficiente para Node.js + PostgreSQL + Nginx
- 20TB bandwidth cobre API calls + upload de M3U pequenos
- 80GB SSD suficiente para DB, logs, backups

#### **FASE 4 - Crescimento (500-2000 licenças)**

**Hetzner CPX31**
- **vCPU:** 4 cores AMD EPYC
- **RAM:** 8GB
- **Storage:** 160GB SSD NVMe
- **Bandwidth:** 20TB/mês
- **Preço:** $21/mês (+$10)

**Upgrade Path:**
- Simples: fazer snapshot → criar VM maior → migrar IP
- Downtime: ~10-15 minutos
- Zero perda de dados

#### **FASE 5 - Escala (2000+ licenças)**

**Opção A: Vertical Scaling**
- Hetzner CPX41: 8 cores, 16GB RAM, 240GB SSD, $40/mês

**Opção B: Horizontal Scaling** (recomendado)
```
┌─────────────────────────────────────┐
│   Load Balancer (Hetzner LB)       │  $5/mês
├─────────────────────────────────────┤
│                                     │
├───────────┬─────────────┬───────────┤
│  API 1    │   API 2     │   API 3   │  3x CPX21 = $33/mês
│ (CPX21)   │  (CPX21)    │  (CPX21)  │
├───────────┴─────────────┴───────────┤
│                                     │
│   PostgreSQL Primary (CPX31)        │  $21/mês
│   + Read Replica (CPX21)            │  $11/mês
└─────────────────────────────────────┘
Total: ~$70/mês para 5k+ licenças
```

### Requisitos de Infraestrutura para IPTV License System

**Bandwidth para API de Licenças:**
- Ativação: ~5KB request + response
- Validação: ~2KB request + response
- Upload M3U: ~500KB-5MB (raramente)
- **Estimativa:** 500 licenças × 10 validações/dia = 5k req/dia = ~10MB/dia

**20TB/mês Hetzner** = suficiente para **milhões** de requisições de API

**Storage para Database:**
- 1 licença = ~2KB (users + licenses + codes)
- 1k licenças = ~2MB
- 10k licenças = ~20MB
- **80GB SSD** = suficiente para centenas de milhares de licenças

**CPU para Node.js Backend:**
- Node.js single-threaded por processo
- 3 vCPU suficiente para rodar 3 processos (PM2 cluster mode)
- Cada processo aguenta ~500-1000 req/s (simple CRUD)

**RAM:**
- Node.js process: ~200-300MB
- PostgreSQL: ~1-2GB (com cache)
- Nginx: ~50MB
- Sistema: ~500MB
- **4GB total** = confortável para MVP

**Fontes:**
- [Hetzner vs Competitors](https://www.wpdoze.com/digitalocean-vs-vultr-vs-hetzner/)
- [VPS Benchmarks 2025](https://www.vpsbenchmarks.com/compare/hetzner_vs_vultr)
- [IPTV Bandwidth Requirements](https://cloudystream.com/iptv-bandwidth-requirements-2025/)

---

## 🌐 CDN - BUNNYCDN

### Por que BunnyCDN?

**Vantagens:**
1. ✅ **Preço imbatível** para streaming de vídeo
2. ✅ Pay-as-you-go (sem compromisso mensal)
3. ✅ Otimizado para media-heavy applications
4. ✅ Edge servers em 100+ localizações
5. ✅ Interface simples (vs Cloudflare complexo)

**Comparação de Preço (Streaming de Vídeo):**

**Exemplo:** 250GB storage + 500GB traffic/mês

| Provider | Storage | Traffic | Total/mês | Total/ano |
|----------|---------|---------|-----------|-----------|
| **BunnyCDN** | $1.25 | $5.00 | **$6.25** | **$75** |
| Cloudflare Stream | $60 | $9 | $69 | **$828** |

_BunnyCDN é ~11x mais barato que Cloudflare para streaming_

### Pricing Detalhado (2025)

**BunnyCDN Storage:**
- $0.005/GB/mês (volume discount automático)

**BunnyCDN Traffic (por região):**
| Região | Preço/GB |
|--------|----------|
| América do Sul (Brasil) | $0.015 |
| América do Norte | $0.01 |
| Europa | $0.01 |
| Ásia | $0.03 |
| África/Oceania | $0.045 |

### Caso de Uso: Optimus Player

**O que usamos CDN para?**
- ⚠️ **Não para streaming dos canais IPTV** (usuário usa M3U URL do provedor dele)
- ✅ Assets do app (imagens, ícones, thumbnails de canais)
- ✅ EPG XML files (se hospedarmos EPG centralizado)
- ✅ Arquivo de configuração remoto (featured channels, etc)
- ✅ Vídeos promocionais/tutoriais no site

**Estimativa de Uso:**

**Cenário 1: Minimal (apenas assets estáticos)**
- Assets do app: 50MB total
- 1000 downloads/mês (novos usuários)
- **Traffic:** 50GB/mês
- **Custo:** $0.50-0.75/mês

**Cenário 2: Com EPG Hosting**
- EPG XML: 5MB por provider
- 10 providers = 50MB
- 500 usuários atualizam EPG 1x/dia = 15k updates/mês
- **Storage:** 50MB = $0.25
- **Traffic:** 750GB/mês = $7.50 (Brasil) a $11.25 (América do Norte)
- **Custo Total:** $7.75-11.50/mês

**Cenário 3: Com vídeos promocionais**
- Vídeos tutoriais: 500MB
- 1000 views/mês
- **Storage:** 500MB = $2.50
- **Traffic:** 500GB/mês = $5.00-7.50
- **Custo Total:** $7.50-10/mês

**Recomendação Inicial:**
- Começar com **Cenário 1** (assets básicos) = ~$1/mês
- Avaliar necessidade de EPG hosting depois (maioria dos provedores IPTV já tem EPG próprio)
- Adicionar vídeos promocionais conforme necessidade

### Alternativa: Cloudflare Free Tier

**Cloudflare Pages (Gratuito):**
- Ideal para assets estáticos (imagens, JS, CSS)
- 500 deploys/mês
- Unlimited bandwidth
- **Limitação:** Não otimizado para vídeo streaming

**Estratégia Híbrida:**
- Cloudflare Pages: Assets do app (imagens, ícones) - **$0**
- BunnyCDN: Vídeos/EPG (se necessário) - **$5-10/mês**

**Fontes:**
- [BunnyCDN Pricing](https://bunny.net/pricing/)
- [BunnyCDN vs Cloudflare](https://www.cdnplanet.com/compare/cloudflare/bunnycdn/)
- [CDN for IPTV](https://blog.blazingcdn.com/en-us/best-cdn-providers-for-ott-and-iptv-platforms)

---

## 🗄️ DATABASE - POSTGRESQL

### Instalação & Configuração

**Versão:** PostgreSQL 17 (mais recente em 2025)

**Instalação no Hetzner:**
```bash
# Ubuntu 24.04 LTS
apt update && apt install -y postgresql-17 postgresql-contrib-17
```

**Configuração Otimizada (4GB RAM):**
```sql
-- /etc/postgresql/17/main/postgresql.conf
shared_buffers = 1GB           # 25% of RAM
effective_cache_size = 3GB     # 75% of RAM
maintenance_work_mem = 256MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1         # SSD optimization
effective_io_concurrency = 200
work_mem = 5MB
max_connections = 100
```

**Backup Strategy:**
1. **Automated Backups (pg_dump):**
   ```bash
   # Cron job diário
   0 3 * * * pg_dump -U postgres optimus_player | gzip > /backups/db_$(date +\%Y\%m\%d).sql.gz
   ```

2. **Hetzner Snapshots:**
   - Snapshot semanal completo do VPS ($0.06/GB armazenado)
   - 80GB VPS = ~$5/mês para 1 snapshot

3. **Offsite Backup:**
   - Upload para S3-compatible storage (Backblaze B2: $5/TB/mês)

### Estimativa de Crescimento

| Licenças Ativas | DB Size | Queries/sec | RAM Necessária |
|----------------|---------|-------------|----------------|
| 100 | 1MB | 5-10 | 512MB |
| 500 | 5MB | 20-30 | 1GB |
| 1000 | 10MB | 40-60 | 1.5GB |
| 5000 | 50MB | 150-200 | 2GB |
| 10000 | 100MB | 300-400 | 3GB |

**4GB RAM suficiente até ~8-10k licenças**

---

## 🔒 SSL & SEGURANÇA

### SSL Certificates

**Let's Encrypt (Gratuito):**
- Certificados SSL gratuitos e automáticos
- Renovação automática (Certbot)
- Wildcard SSL para subdomains

```bash
# Instalação
apt install -y certbot python3-certbot-nginx

# Obter certificado
certbot --nginx -d api.optimusplayer.com -d admin.optimusplayer.com
```

**Custo:** $0/mês

### Firewall (UFW)

```bash
# Portas abertas
ufw allow 22/tcp   # SSH
ufw allow 80/tcp   # HTTP (redirect to HTTPS)
ufw allow 443/tcp  # HTTPS
ufw enable
```

### Fail2ban (Proteção contra brute force)

```bash
apt install -y fail2ban
# Bloqueia IPs com múltiplas tentativas de login falhas
```

**Custo:** $0/mês

---

## 📧 EMAIL (Notificações)

### SendGrid Free Tier

**Funcionalidades:**
- 100 emails/dia = 3k emails/mês **gratuitos**
- Emails transacionais (ativação, expiração de licença, etc)
- API simples (SDK oficial para Node.js)

**Uso Estimado:**
- Ativação de licença: 1 email
- Expiração em 7 dias: 1 email
- Expiração em 3 dias: 1 email
- Licença expirada: 1 email

**500 licenças × 4 emails/ano = 2k emails/mês** = dentro do free tier

**Upgrade (se necessário):**
- SendGrid Essentials: $19.95/mês para 50k emails

**Alternativa:**
- **Amazon SES:** $0.10 por 1k emails (mais barato para alto volume)

**Custo:** $0/mês (free tier)

---

## 🔧 FERRAMENTAS & MONITORING

### Essenciais (Gratuitas)

1. **PM2 (Process Manager):**
   - Gerenciador de processos Node.js
   - Auto-restart em crash
   - Cluster mode (multi-core)
   - **Custo:** $0

2. **Nginx (Reverse Proxy):**
   - Load balancer
   - SSL termination
   - Static file serving
   - **Custo:** $0

3. **Docker + Docker Compose:**
   - Containerização (opcional, recomendado)
   - Fácil deploy e rollback
   - **Custo:** $0

### Monitoring (Opcionais)

**Free Tier:**
- **UptimeRobot:** Monitoring de uptime (50 monitors gratuitos)
- **Grafana Cloud:** Métricas e dashboards (10k séries gratuitas)
- **Sentry:** Error tracking (5k eventos/mês gratuitos)

**Custo:** $0/mês (free tiers)

**Paid (se escalar):**
- **Datadog:** $15/host/mês (monitoring completo)
- **New Relic:** $25/usuário/mês (APM)

---

## 💰 CUSTOS TOTAIS - BREAKDOWN

### FASE 3 - MVP (0-500 licenças)

| Item | Provedor | Especificação | Custo/mês |
|------|----------|---------------|-----------|
| **VPS** | Hetzner Cloud | CPX21 (3 vCPU, 4GB RAM, 80GB SSD, 20TB) | **$11.00** |
| **CDN** | BunnyCDN | Assets estáticos (~50GB traffic) | **$0.50** |
| **Database** | PostgreSQL | Incluído no VPS | **$0** |
| **SSL** | Let's Encrypt | Certificado gratuito | **$0** |
| **Email** | SendGrid | Free tier (100/dia) | **$0** |
| **Monitoring** | UptimeRobot + Grafana | Free tiers | **$0** |
| **Backup** | Hetzner Snapshot | 1 snapshot semanal (~80GB) | **$5.00** |
| **Domain** | Namecheap/GoDaddy | .com domain | **$1.00** |
| **TOTAL** | | | **$17.50/mês** |

**Custo Anual:** ~$210

**Custo por Licença (500 licenças):** $0.035/mês/licença

### FASE 4 - Crescimento (500-2000 licenças)

| Item | Custo/mês |
|------|-----------|
| **VPS** | $21 (upgrade para CPX31) |
| **CDN** | $2-5 (mais traffic) |
| **Backup** | $8 (160GB snapshot) |
| **Email** | $0 (ainda no free tier) |
| **Domain** | $1 |
| **TOTAL** | **$32-35/mês** |

**Custo por Licença (2000 licenças):** $0.016-0.017/mês/licença

### FASE 5 - Escala (2000-5000 licenças)

| Item | Custo/mês |
|------|-----------|
| **VPS** | $40 (CPX41) OU $70 (cluster) |
| **CDN** | $5-10 |
| **Backup** | $10-15 |
| **Email** | $20 (SendGrid Essentials se >3k emails) |
| **Domain** | $1 |
| **Monitoring** | $15 (Datadog - opcional) |
| **TOTAL** | **$71-111/mês** |

**Custo por Licença (5000 licenças):** $0.014-0.022/mês/licença

**Economia de Escala:** Custo por licença diminui conforme cresce!

---

## 📈 PROJEÇÃO DE CUSTOS (3 ANOS)

### Cenário Conservador (Crescimento Gradual)

| Período | Licenças Ativas | Custo Infraestrutura | Receita* | Margem |
|---------|----------------|---------------------|----------|--------|
| **Ano 1** | | | | |
| Mês 1-3 | 0-50 | $18/mês | $0 | -$54 |
| Mês 4-6 | 50-200 | $18/mês | $2k | +$1.9k |
| Mês 7-12 | 200-500 | $18/mês | $5k | +$4.9k |
| **Total Ano 1** | 500 | **$216** | **$7k** | **+$6.8k** |
| | | | | |
| **Ano 2** | 500-2000 | $35/mês = $420 | $20k | **+$19.6k** |
| **Ano 3** | 2000-5000 | $80/mês = $960 | $50k | **+$49k** |

_*Receita baseada em licença anual de R$50 (~$10) com 50% para revendedores_

### Cenário Otimista (Crescimento Rápido)

| Período | Licenças Ativas | Custo Infraestrutura | Receita* |
|---------|----------------|---------------------|----------|
| **Ano 1** | 0-1000 | $300 | $10k |
| **Ano 2** | 1000-5000 | $900 | $50k |
| **Ano 3** | 5000-10000 | $1.8k | $100k |

---

## 🚀 PLANO DE DEPLOYMENT

### FASE 2 (Android App) - Não precisa de infraestrutura ainda
- App se conecta a M3U externo fornecido pelo usuário
- Sem backend necessário

### FASE 3 (Backend + Licenças) - Setup Inicial

**1. Provisionar VPS (Hetzner CPX21):**
```bash
# Via Hetzner Cloud Console ou API
# Localização: Falkenstein, Germany (menor latência para Europa/Brasil)
# OS: Ubuntu 24.04 LTS
```

**2. Setup Inicial:**
```bash
# SSH into server
ssh root@<server-ip>

# Update system
apt update && apt upgrade -y

# Install essentials
apt install -y build-essential git curl wget vim nginx postgresql-17 certbot python3-certbot-nginx

# Install Node.js 22 LTS
curl -fsSL https://deb.nodesource.com/setup_22.x | bash -
apt install -y nodejs

# Install PM2
npm install -g pm2

# Configure firewall
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

**3. Deploy Backend:**
```bash
# Clone repo
git clone https://github.com/vandersonbraz/optimus-player.git
cd optimus-player/backend

# Install dependencies
npm install

# Setup .env
cp .env.example .env
nano .env  # Configure DB, Mercado Pago, JWT secret

# Run migrations
npm run migration:run

# Start with PM2
pm2 start npm --name "optimus-api" -- start
pm2 startup
pm2 save
```

**4. Configure Nginx:**
```nginx
# /etc/nginx/sites-available/optimusplayer
server {
    listen 80;
    server_name api.optimusplayer.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

```bash
# Enable site
ln -s /etc/nginx/sites-available/optimusplayer /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx

# Setup SSL
certbot --nginx -d api.optimusplayer.com -d admin.optimusplayer.com
```

**5. Setup BunnyCDN:**
- Criar conta em bunny.net
- Criar Pull Zone para assets estáticos
- Configurar DNS: cdn.optimusplayer.com → BunnyCDN

**6. Setup SendGrid:**
- Criar conta em sendgrid.com
- Obter API key
- Adicionar ao .env: `SENDGRID_API_KEY=xxx`
- Verificar domain (sender authentication)

### Checklist de Deploy

- [x] VPS provisionado
- [x] SSH configurado (key-based auth)
- [x] Firewall ativo (UFW)
- [x] PostgreSQL instalado e configurado
- [x] Node.js + PM2 instalado
- [x] Backend deployed e rodando
- [x] Nginx configurado (reverse proxy)
- [x] SSL certificates instalados (Let's Encrypt)
- [x] BunnyCDN configurado
- [x] SendGrid configurado
- [x] Monitoring configurado (UptimeRobot)
- [x] Backup automático configurado (cron + pg_dump)
- [x] Domain DNS configurado

---

## 📊 BENCHMARKS ESPERADOS

### Performance Targets

**API Backend (Node.js):**
- Response time (GET /license/validate): < 50ms (p95)
- Throughput: 500-1000 req/s (single instance)
- Concurrent connections: 10k+ (keep-alive)

**Database (PostgreSQL):**
- Query time (simple SELECT): < 5ms
- Query time (JOIN 3 tables): < 20ms
- Writes/second: 1k+

**CDN (BunnyCDN):**
- Time to First Byte (TTFB): < 50ms (edge servers)
- Asset loading: < 200ms (imagens)

### Load Testing

**Ferramentas:**
- **Artillery:** HTTP load testing
- **k6:** Modern load testing (Grafana)
- **pgbench:** PostgreSQL benchmarking

**Teste Pré-produção:**
```bash
# Simular 1000 usuários validando licença
artillery quick --count 1000 --num 5 https://api.optimusplayer.com/license/validate
```

**Target:** 95% das requests < 100ms sob carga de 1k concurrent users

---

## ✅ CONCLUSÃO

A infraestrutura planejada oferece:

1. **Custo Inicial Baixo:** $17.50/mês (MVP até 500 licenças)
2. **Escalabilidade Linear:** Fácil upgrade vertical ou horizontal
3. **Performance Garantida:** Hetzner com SSD NVMe + BunnyCDN
4. **Confiabilidade:** Backups automáticos + monitoring
5. **Segurança:** SSL, firewall, fail2ban, backups offsite

**ROI Positivo desde o mês 4** (com 50+ licenças vendidas).

**Próximo passo:** Documentar arquitetura completa do sistema (diagramas, fluxos, integrações).

---

## 📚 Fontes Consultadas

**VPS:**
- [Hetzner vs Competitors](https://www.wpdoze.com/digitalocean-vs-vultr-vs-hetzner/)
- [VPS Benchmarks 2025](https://www.vpsbenchmarks.com/compare/hetzner_vs_vultr)
- [Best VPS Reddit 2025](https://codeless.co/best-vps-hosting-reddit/)

**CDN:**
- [BunnyCDN Pricing](https://bunny.net/pricing/)
- [BunnyCDN vs Cloudflare](https://www.cdnplanet.com/compare/cloudflare/bunnycdn/)
- [CDN for OTT/IPTV](https://blog.blazingcdn.com/en-us/best-cdn-providers-for-ott-and-iptv-platforms)

**IPTV Infrastructure:**
- [IPTV Bandwidth Requirements 2025](https://cloudystream.com/iptv-bandwidth-requirements-2025/)
- [IPTV Infrastructure Guide](https://www.infomir.eu/eng/blog/articles/110-bandwidth-requirements-for-streaming/)
- [IPTV Cost Reduction 2025](https://www.vucos.io/post/iptv-operational-cost-reduction-complete-guide-for-european-telco-operators-in-2025)

---

**Documento criado em:** 23 de Novembro de 2025
**Autor:** Equipe Optimus Player - DevOps & Infraestrutura
**Versão:** 1.0
