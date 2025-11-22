# 🎨 MOCKUPS COMPLETOS - CALMARE APP

## Design System

### Cores
- **Primary:** #6C63FF (Roxo moderno zen)
- **Secondary:** #A29BFE (Roxo claro)
- **Accent:** #FF6B9D (Rosa suave - CTAs)
- **Background:** #F8F9FE (Off-white relaxante)
- **Card BG:** #FFFFFF
- **Dark Text:** #2D3436
- **Gray Text:** #636E72
- **Success:** #00D2A0

### Tipografia
- **Headings:** Poppins Bold/SemiBold
- **Body:** Inter Regular/Medium
- **Sizes:**
  - H1: 28sp
  - H2: 22sp
  - H3: 18sp
  - Body: 16sp
  - Caption: 14sp

### Spacing
- Margin lateral padrão: 20dp
- Padding cards: 16dp
- Espaçamento entre elementos: 12dp/16dp/24dp

---

## TELA 1: SPLASH SCREEN (1-2 segundos)

```
┌─────────────────────────────────────┐
│                                     │
│                                     │
│          Fundo gradiente            │
│         #6C63FF → #A29BFE           │
│                                     │
│              [LOGO]                 │
│                                     │
│            CALMARE                  │
│         (Poppins Bold 32sp)         │
│            #FFFFFF                  │
│                                     │
│         Sua paz interior            │
│        (Inter Regular 16sp)         │
│            #FFFFFF 80%              │
│                                     │
│                                     │
│         [Loading spinner]           │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Descrição Visual:**
- Gradiente vertical roxo suave
- Logo centralizado (ícone de lótus estilizada ou onda zen minimalista)
- Nome do app em branco, fonte elegante
- Tagline sutil abaixo
- Loading spinner discreto no rodapé

---

## TELA 2: ONBOARDING (3 slides) - Apenas primeira vez

### Slide 1
```
┌─────────────────────────────────────┐
│                                     │
│         [SKIP]            1/3       │
│                                     │
│                                     │
│         [ILUSTRAÇÃO]                │
│      Pessoa meditando               │
│      (estilo flat/moderno)          │
│        Cores: #6C63FF               │
│                                     │
│                                     │
│        Reduza o Stress              │
│      (Poppins SemiBold 24sp)        │
│                                     │
│   Técnicas comprovadas de           │
│   meditação e sons relaxantes       │
│    para acalmar sua mente           │
│      (Inter Regular 16sp)           │
│                                     │
│                                     │
│        ● ○ ○ (indicadores)          │
│                                     │
│    [PRÓXIMO - botão roxo cheio]     │
│                                     │
└─────────────────────────────────────┘
```

### Slide 2
```
Mesma estrutura, mas:
- Ilustração: fones de ouvido + ondas sonoras
- Título: "Sons da Natureza"
- Texto: "Chuva, oceano, floresta e muito mais para relaxamento profundo"
- Indicadores: ○ ● ○
```

### Slide 3
```
Mesma estrutura, mas:
- Ilustração: troféu ou estrela brilhante
- Título: "Desbloqueie Tudo"
- Texto: "Acesso ilimitado a centenas de meditações e sons com o Plano Premium"
- Indicadores: ○ ○ ●
- Botão: [COMEÇAR AGORA]
```

---

## TELA 3: HOME (Principal)

```
┌─────────────────────────────────────┐
│ ☰  CALMARE          🔔   [PREMIUM]  │  ← Header branco, sombra leve
├─────────────────────────────────────┤
│                                     │
│  Olá, Maria! 👋                     │  ← Saudação personalizada
│  Como você está se sentindo?        │    (Poppins SemiBold 22sp)
│                                     │
│  😌  😰  😴  😊  😔                 │  ← Emojis clicáveis (mood tracker)
│                                     │
├─────────────────────────────────────┤
│                                     │
│  🎯 Sessão Rápida                   │  ← Seção destaque
│  ┌───────────────────────────────┐  │
│  │   [IMAGEM: pessoa + natureza] │  │  Card grande com gradiente
│  │                               │  │  overlay roxo transparente
│  │   Respiração 5 Minutos        │  │
│  │   ▶ Começar agora             │  │  Botão play branco
│  │                               │  │
│  │   [🔒 PREMIUM]  ← se gratuito │  │  Badge sutil se for premium
│  └───────────────────────────────┘  │
│                                     │
│  ⚠️ [BANNER AD 320x50]  ← usuário free
│                                     │
├─────────────────────────────────────┤
│                                     │
│  🎵 Sons Populares                  │  ← Seção horizontal scroll
│                                     │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐  │  Cards pequenos em row
│  │ 🌧️  │ │ 🌊  │ │ 🌲  │ │ 🔥  │  │  com ícone + nome
│  │Chuva│ │Oceano││Floresta││Lareira│ │
│  └─────┘ └─────┘ └─────┘ └─────┘  │
│           → scroll horizontal       │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ✨ Meditações Guiadas              │  ← Lista vertical
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Ansiedade Zero          │  │  Cards lista com:
│  │  ⏱️ 10 min  ⭐ 4.8  👤 Sarah  │  │  - Thumbnail esquerda
│  │  [▶]                [🔒]      │  │  - Info central
│  └───────────────────────────────┘  │  - Play + lock direita
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Sono Profundo           │  │
│  │  ⏱️ 15 min  ⭐ 4.9  👤 João   │  │
│  │  [▶]                          │  │  ← Desbloqueado (free)
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Foco Total              │  │
│  │  ⏱️ 20 min  ⭐ 5.0  👤 Ana    │  │
│  │  [▶]                [🔒]      │  │
│  └───────────────────────────────┘  │
│                                     │
│           [Ver Tudo ↓]              │
│                                     │
└─────────────────────────────────────┘
│    🏠    🎵    ❤️    ⚙️             │  ← Bottom Navigation
└─────────────────────────────────────┘
```

**Descrição Visual Detalhada:**
- **Header fixo:** fundo branco, logo esquerda, ícone notificações, badge "PREMIUM" (ou botão "Assinar")
- **Saudação:** nome do usuário, fonte grande e amigável
- **Mood tracker:** 5 emojis em row, com sombra sutil, clicável (salva humor do dia)
- **Card destaque:** grande, imagem de fundo linda (pessoa meditando em natureza), overlay gradiente roxo 40%, texto branco, botão play destacado
- **BANNER AD:** retângulo 320x50dp, fundo cinza claro, só aparece para usuários free
- **Sons populares:** cards quadrados 80x80dp, ícone emoji grande, nome abaixo, scroll horizontal
- **Meditações:** lista vertical, cards brancos com sombra, thumbnail arredondada esquerda, info centro, botões direita
- **Bottom nav:** 4 ícones - Home (ativo #6C63FF), Sons (#636E72), Favoritos, Configurações

---

## TELA 4: BIBLIOTECA DE SONS

```
┌─────────────────────────────────────┐
│ ←  Sons                🔍  ⋮        │  ← Header
├─────────────────────────────────────┤
│                                     │
│  [Todos] [Natureza] [Ambientes]     │  ← Tabs/Chips filtros
│  [Música] [White Noise]             │    scroll horizontal
│                                     │
├─────────────────────────────────────┤
│                                     │
│  NATUREZA                           │  ← Categoria
│  ┌──────────┐ ┌──────────┐         │
│  │  [IMG]   │ │  [IMG]   │         │  Grid 2 colunas
│  │  ████    │ │  ████    │         │  Cards com imagem
│  │          │ │          │         │  overlay gradiente
│  │  🌧️      │ │  🌊      │         │
│  │  Chuva   │ │  Oceano  │         │
│  │  Leve    │ │  Ondas   │         │
│  │          │ │          │         │
│  │  [▶]  🔒 │ │  [▶]     │         │  Play + lock (se premium)
│  └──────────┘ └──────────┘         │
│                                     │
│  ┌──────────┐ ┌──────────┐         │
│  │  [IMG]   │ │  [IMG]   │         │
│  │  🌲      │ │  ⛈️      │         │
│  │  Floresta│ │ Trovão   │         │
│  │  Tropical│ │          │         │
│  │  [▶]     │ │  [▶]  🔒 │         │
│  └──────────┘ └──────────┘         │
│                                     │
│  AMBIENTES                          │
│  ┌──────────┐ ┌──────────┐         │
│  │  🔥      │ │  ☕      │         │
│  │  Lareira │ │ Cafeteria│         │
│  │  [▶]  🔒 │ │  [▶]  🔒 │         │
│  └──────────┘ └──────────┘         │
│                                     │
│  ⚠️ [BANNER AD] ← free              │
│                                     │
└─────────────────────────────────────┘
│    🏠    🎵    ❤️    ⚙️             │
└─────────────────────────────────────┘
```

**Descrição Visual:**
- Grid 2 colunas com gap 12dp
- Cards: imagem de fundo relacionada ao som, overlay gradiente bottom-up, ícone grande centralizado, nome em branco bold
- Badge lock sutil top-right para conteúdo premium
- Botão play circular bottom-right
- Banner ad no rodapé antes do bottom nav (só free)

---

## TELA 5: PLAYER DE SOM/MEDITAÇÃO

```
┌─────────────────────────────────────┐
│ ←  [X]                          ⋮   │  ← Voltar/Fechar
├─────────────────────────────────────┤
│                                     │
│                                     │
│        [IMAGEM GRANDE]              │  Imagem hero
│         Capa do som                 │  ocupa 50% da tela
│        (circular ou                 │  com leve animação
│         quadrado com                │  de pulso/ondas
│         bordas arredondadas)        │
│                                     │
│                                     │
│            Chuva Leve               │  Título (Poppins SemiBold 24sp)
│          Sons da Natureza           │  Categoria (Inter Regular 14sp)
│                                     │
│                                     │
│        ━━━━━━━●━━━━━━━━             │  Progress bar
│        3:24        -8:16            │  Tempo decorrido / restante
│                                     │
│                                     │
│      ⏮️    ⏯️    ⏭️                 │  Controles principais
│   (48dp) (64dp) (48dp)              │  Play/Pause maior
│                                     │
│                                     │
│   🔀     ❤️     ⏱️      🔊          │  Controles secundários
│  Loop  Favorito Timer  Volume       │  (32dp cada)
│                                     │
│                                     │
│  ⚠️ [INTERSTITIAL TRIGGER]          │  ← Não visível, mas ao
│     após X minutos (free)           │    fechar pode abrir ad
│                                     │
│                                     │
│  💬 Deixe-se levar pelo som...      │  ← Dica/mensagem zen
│     Respire profundamente           │
│                                     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 🌟 Experimente o Premium    │   │  CTA Premium card
│  │ Acesso ilimitado a tudo     │   │  (só aparece se free)
│  │                             │   │
│  │  [ASSINAR AGORA]            │   │  Botão destaque
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

**Descrição Visual:**
- Fundo gradiente suave (baseado na cor dominante da imagem)
- Imagem do som/meditação centralizada, grande (300x300dp), com sombra
- Controles minimalistas, ícones material design modernos
- Progress bar com cor accent (#FF6B9D)
- Card CTA premium fixo no bottom (só para free), fundo branco semi-transparente
- Se for meditação guiada, mostrar foto do instrutor

---

## TELA 6: PREMIUM / ASSINATURA (A MAIS IMPORTANTE!)

```
┌─────────────────────────────────────┐
│ [X]                                 │  ← Fechar (top-left)
├─────────────────────────────────────┤
│                                     │
│     Fundo gradiente roxo suave      │
│      #6C63FF → #A29BFE              │
│                                     │
│         [ÍCONE COROA]               │  Ícone premium dourado
│           Calmare                   │
│           PREMIUM                   │  (Poppins Bold 28sp branco)
│                                     │
│    Desbloqueie todo o potencial     │
│        da sua paz interior          │  Tagline (Inter 16sp branco 90%)
│                                     │
├─────────────────────────────────────┤
│  (Scroll vertical a partir daqui)   │
│                                     │
│  BENEFÍCIOS:                        │
│                                     │
│  ✅ Acesso ilimitado a 300+ sons    │  Lista de benefícios
│  ✅ Todas as meditações guiadas     │  com ícones verdes
│  ✅ Sem anúncios, nunca             │  (Inter Medium 16sp)
│  ✅ Modo offline (downloads)        │
│  ✅ Estatísticas detalhadas         │
│  ✅ Suporte prioritário             │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ESCOLHA SEU PLANO:                 │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  [○]  MENSAL                │   │  Card não selecionado
│  │                             │   │  (fundo branco, borda cinza)
│  │  R$ 19,90/mês               │   │
│  │  Renovação automática       │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  [●]  ANUAL  🏆             │   │  Card SELECIONADO
│  │                             │   │  (fundo #6C63FF, texto branco)
│  │  R$ 12,90/mês               │   │  (borda accent rosa)
│  │  Cobrado R$ 154,80/ano      │   │
│  │                             │   │
│  │  💰 Economize 35%!          │   │  Badge destaque
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  [○]  VITALÍCIO  ⚡         │   │  Compra única (opcional)
│  │                             │   │
│  │  R$ 89,90 uma única vez     │   │
│  │  Acesso para sempre         │   │
│  └─────────────────────────────┘   │
│                                     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   COMEÇAR AGORA             │   │  CTA principal
│  │   (Botão grande rosa/roxo)  │   │  (altura 56dp)
│  └─────────────────────────────┘   │
│                                     │
│  Cancele a qualquer momento         │  Disclaimer
│  pela Google Play Store             │  (texto pequeno cinza)
│                                     │
│  ─────────────────────────────      │
│                                     │
│  OU REMOVER APENAS OS ANÚNCIOS:     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  🚫 Remover Anúncios        │   │  Compra única alternativa
│  │                             │   │
│  │  R$ 14,90 única vez         │   │
│  │  Mantenha anúncios fora     │   │
│  │  para sempre                │   │
│  │                             │   │
│  │  [COMPRAR AGORA]            │   │  Botão secundário
│  └─────────────────────────────┘   │
│                                     │
│  Política de Privacidade • Termos   │  Links legais
│  Restaurar Compras                  │
│                                     │
└─────────────────────────────────────┘
```

**Descrição Visual Detalhada:**
- **Header:** gradiente roxo lindo com ícone de coroa dourada brilhante
- **Benefícios:** fundo branco, ícones checkmark verdes (#00D2A0), texto escuro, espaçamento generoso
- **Cards de planos:**
  - Não selecionado: branco, borda #E0E0E0
  - Selecionado: roxo #6C63FF, borda rosa #FF6B9D (3dp), texto branco
  - Badge "Economize X%" em fundo #00D2A0
- **CTA:** botão gradiente rosa-para-roxo, texto branco bold, sombra pronunciada
- **Seção remove ads:** separada por linha, botão outline (borda roxa)

---

## TELA 7: CONFIGURAÇÕES

```
┌─────────────────────────────────────┐
│ ←  Configurações                    │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │   [👤]  Maria Silva         │   │  Card perfil
│  │                             │   │
│  │   ✨ Usuário Premium        │   │  ← ou "Grátis"
│  │   Membro desde Jan 2025     │   │
│  │                             │   │
│  │   [GERENCIAR ASSINATURA]    │   │  Botão se premium
│  └─────────────────────────────┘   │  [ASSINAR PREMIUM] se free
│                                     │
│  GERAL                              │  ← Seção header
│  ┌─────────────────────────────┐   │
│  │  🔔 Notificações        [>] │   │  Item clicável
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  ⏰ Lembretes Diários   [>] │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  🌙 Modo Escuro    [Toggle] │   │  Switch on/off
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  💾 Downloads       [>]     │   │
│  └─────────────────────────────┘   │
│                                     │
│  ESTATÍSTICAS                       │
│  ┌─────────────────────────────┐   │
│  │  📊 Meu Progresso      [>]  │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  🏆 Conquistas         [>]  │   │
│  └─────────────────────────────┘   │
│                                     │
│  SUPORTE                            │
│  ┌─────────────────────────────┐   │
│  │  ❓ Central de Ajuda   [>]  │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  💬 Fale Conosco       [>]  │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  ⭐ Avaliar o App      [>]  │   │
│  └─────────────────────────────┘   │
│                                     │
│  LEGAL                              │
│  ┌─────────────────────────────┐   │
│  │  📄 Política Privacidade[>] │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  📋 Termos de Uso      [>]  │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │  🔄 Restaurar Compras  [>]  │   │
│  └─────────────────────────────┘   │
│                                     │
│          Versão 1.0.0               │  Versão do app
│                                     │
│  ⚠️ [BANNER AD] ← se free           │
│                                     │
└─────────────────────────────────────┘
│    🏠    🎵    ❤️    ⚙️             │
└─────────────────────────────────────┘
```

**Descrição Visual:**
- Card perfil no topo com avatar, nome, status premium
- Lista agrupada por seções (headers em cinza)
- Items brancos com sombra leve, ícones coloridos à esquerda, chevron direita
- Toggles com cores do tema (#6C63FF quando ativo)

---

## TELA 8: DETALHES DE MEDITAÇÃO

```
┌─────────────────────────────────────┐
│ ←                              ⋮    │
├─────────────────────────────────────┤
│                                     │
│     [IMAGEM HEADER GRANDE]          │  Hero image
│      Pessoa em lotus na             │  ocupa topo, overlay
│      natureza, fundo desfocado      │  gradiente bottom
│                                     │
│     Ansiedade Zero                  │  Título sobreposto
│     com Sarah                       │  em branco (bottom)
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ⏱️ 10 min   ⭐ 4.8   👥 12.4k      │  Meta info em chips
│                                     │
│  [🔒 PREMIUM EXCLUSIVO]             │  ← Badge se for premium
│                                     │
│  SOBRE ESTA MEDITAÇÃO               │
│                                     │
│  Técnica guiada de respiração       │  Descrição
│  consciente e mindfulness para      │  (Inter Regular 16sp)
│  reduzir ansiedade e acalmar        │
│  pensamentos acelerados. Ideal      │
│  para momentos de stress ou         │
│  antes de dormir.                   │
│                                     │
│  INSTRUTORA: Sarah Mindful          │  Card instrutor
│  ┌──────────────────────────────┐  │
│  │ [👤]  Sarah Mindful          │  │
│  │  🧘 Instrutora certificada   │  │
│  │  📚 85 meditações            │  │
│  │  [VER PERFIL →]              │  │
│  └──────────────────────────────┘  │
│                                     │
│  BENEFÍCIOS                         │
│  • Reduz ansiedade                  │  Lista de benefícios
│  • Melhora qualidade do sono        │
│  • Aumenta foco                     │
│                                     │
│  MEDITAÇÕES RELACIONADAS            │
│  ┌────┐  ┌────┐  ┌────┐            │  Scroll horizontal
│  │[🖼️]│  │[🖼️]│  │[🖼️]│            │  cards pequenos
│  │Sono│  │Foco│  │Paz │            │
│  └────┘  └────┘  └────┘            │
│                                     │
│  ⚠️ [BANNER AD] ← se free           │
│                                     │
├─────────────────────────────────────┤
│  [❤️ FAVORITAR]   [▶️ REPRODUZIR]  │  Botões fixos bottom
└─────────────────────────────────────┘
```

**Descrição Visual:**
- Hero image full-width com parallax scroll
- Chips informativos com ícones
- Descrição em texto corrido, bem espaçado
- Card do instrutor destacado
- CTA fixo no bottom (fundo branco com sombra top)

---

## TELA 9: FAVORITOS

```
┌─────────────────────────────────────┐
│    Favoritos                     ⋮  │
├─────────────────────────────────────┤
│                                     │
│  [Todos] [Sons] [Meditações]        │  Tabs filtro
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Chuva Leve             │  │  Lista igual à Home
│  │  🎵 Som • 15 min              │  │  mas só favoritados
│  │  [▶]              [❤️ cheio]  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Ansiedade Zero          │  │
│  │  🧘 Meditação • 10 min        │  │
│  │  [▶]              [❤️ cheio]  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ [🖼️]  Oceano Calmo            │  │
│  │  🎵 Som • Loop                │  │
│  │  [▶]              [❤️ cheio]  │  │
│  └───────────────────────────────┘  │
│                                     │
│  (se vazio)                         │
│                                     │
│         [ÍCONE CORAÇÃO VAZIO]       │  Empty state
│                                     │
│    Nenhum favorito ainda            │
│                                     │
│  Toque no ❤️ para salvar seus       │
│  sons e meditações preferidos       │
│                                     │
│                                     │
└─────────────────────────────────────┘
│    🏠    🎵    ❤️    ⚙️             │
└─────────────────────────────────────┘
```

---

## COMPONENTES ESPECIAIS

### MODAL: Timer (Sleep Timer)
```
┌──────────────────────────────┐
│  ⏱️ Timer para Dormir        │
│                              │
│  O som parará em:            │
│                              │
│  [  5 min  ]                 │
│  [ 10 min  ]  ← selecionado  │
│  [ 15 min  ]                 │
│  [ 30 min  ]                 │
│  [ 60 min  ]                 │
│                              │
│  [CANCELAR]    [INICIAR]     │
└──────────────────────────────┘
```

### MODAL: Estatísticas (Premium)
```
┌──────────────────────────────┐
│  📊 Suas Estatísticas        │
│                              │
│  Esta Semana                 │
│                              │
│  🧘 Meditações: 8 sessões    │
│  ⏱️ Tempo total: 1h 24min    │
│  🔥 Sequência: 5 dias        │
│                              │
│  [Ver Histórico Completo →]  │
└──────────────────────────────┘
```

---

## POSICIONAMENTO DE ANÚNCIOS (Usuário FREE)

1. **Banner Ads (320x50):**
   - Home: acima do Bottom Nav
   - Biblioteca: acima do Bottom Nav
   - Configurações: acima do Bottom Nav

2. **Interstitial Ads:**
   - Ao fechar o player após 5+ minutos de uso
   - Ao navegar entre 3-4 telas (frequência controlada)
   - NUNCA ao abrir app ou durante reprodução

3. **Rewarded Ads:**
   - Oferta: "Assista um vídeo para desbloquear esta meditação premium por 24h"
   - Aparece quando clicar em conteúdo premium sendo free

---

## FLUXOS IMPORTANTES

### Usuário FREE abre meditação PREMIUM:
```
Tela Detalhes → Clica Play → Modal aparece:

┌────────────────────────────────┐
│  🔒 Conteúdo Premium           │
│                                │
│  Esta meditação é exclusiva    │
│  para assinantes Premium       │
│                                │
│  💎 [ASSINAR PREMIUM]          │
│                                │
│  ou                            │
│                                │
│  📺 Assistir anúncio e         │
│     desbloquear por 24h        │
│                                │
│     [ASSISTIR VÍDEO]           │
│                                │
│  [Fechar]                      │
└────────────────────────────────┘
```

---

## RESUMO DE CORES POR TELA

- **Splash/Onboarding:** Gradiente roxo #6C63FF → #A29BFE
- **Home:** Fundo #F8F9FE, cards brancos, accent roxo
- **Player:** Gradiente baseado na imagem, controles brancos/roxos
- **Premium:** Gradiente roxo com dourado, CTAs rosa #FF6B9D
- **Configurações:** Fundo cinza claro #F8F9FE, cards brancos

---

## ANIMAÇÕES SUGERIDAS

- Transições de tela: slide horizontal (300ms)
- Botões: scale + elevation ao tocar
- Player: ondas pulsantes ao reproduzir (lottie/rive)
- Cards: elevação sutil ao scroll
- Modal: fade in + slide up from bottom

---

**FIM DOS MOCKUPS**

Total de telas criadas: 9 principais + 2 modais + 1 splash
Todas as interações mapeadas
Anúncios posicionados estrategicamente
Design system completo definido
