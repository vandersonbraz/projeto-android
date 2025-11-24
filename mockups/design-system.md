# 🎨 Design System - Optimus Player

**Data:** 23 de Novembro de 2025
**Versão:** 1.0
**Plataforma:** Android (base para todas as outras)

---

## 🎯 Conceito Visual

O Optimus Player é um aplicativo IPTV **premium, moderno e poderoso**. O design transmite:

- **Velocidade** - Raio amarelo simboliza potência instantânea
- **Profissionalismo** - Interface limpa e organizada
- **Modernidade** - Dark theme com destaques vibrantes
- **Sofisticação** - Preto profundo + amarelo ouro = luxo

---

## 🎨 PALETA DE CORES

### Cores Primárias

```css
/* Fundos Principais */
--black-deep: #000000;        /* Fundo principal absoluto */
--black-carbon: #121212;      /* Fundo cards, containers */
--black-soft: #1E1E1E;        /* Fundo secundário */

/* Destaques Amarelos */
--gold-bright: #FFD700;       /* Botões primários, ícones ativos */
--gold-soft: #FFC107;         /* Hover states, destaques secundários */
--gold-dim: #E6B800;          /* Pressed state */
```

### Cores Secundárias

```css
/* Tons de Cinza */
--gray-dark: #424242;         /* Borders, dividers */
--gray-medium: #757575;       /* Textos secundários, ícones inativos */
--gray-light: #CCCCCC;        /* Textos terciários */
--white: #FFFFFF;             /* Textos principais, ícones principais */

/* Estados de Feedback */
--success: #4CAF50;           /* Ações bem-sucedidas */
--error: #F44336;             /* Erros, avisos críticos */
--warning: #FF9800;           /* Avisos importantes */
--info: #2196F3;              /* Informações, dicas */
```

### Gradientes

```css
/* Gradiente Principal (Background especial) */
linear-gradient(135deg, #000000 0%, #1E1E1E 100%);

/* Gradiente Amarelo (Botões, destaques) */
linear-gradient(90deg, #FFD700 0%, #FFC107 100%);

/* Gradiente de Overlay (Sobre imagens) */
linear-gradient(180deg, transparent 0%, rgba(0,0,0,0.8) 100%);
```

### Opacidades

```css
/* Overlays e Modals */
--overlay-light: rgba(0, 0, 0, 0.5);    /* 50% */
--overlay-medium: rgba(0, 0, 0, 0.7);   /* 70% */
--overlay-heavy: rgba(0, 0, 0, 0.9);    /* 90% */

/* Glassmorphism (Cards flutuantes) */
--glass: rgba(30, 30, 30, 0.8);
backdrop-filter: blur(10px);
```

---

## 📝 TIPOGRAFIA

### Fontes

**Primária (Títulos, Headings):**
- **Nome:** Poppins
- **Pesos:** Bold (700), SemiBold (600)
- **Uso:** Títulos de seções, nomes de canais, botões principais

**Secundária (Corpo, UI):**
- **Nome:** Roboto
- **Pesos:** Regular (400), Medium (500), Bold (700)
- **Uso:** Textos de corpo, descrições, labels

### Hierarquia de Texto

```css
/* Display (Splash, Títulos principais) */
--text-display: 32sp;
font-family: Poppins;
font-weight: 700;
line-height: 40sp;
letter-spacing: -0.5px;

/* H1 (Telas principais) */
--text-h1: 24sp;
font-family: Poppins;
font-weight: 700;
line-height: 32sp;

/* H2 (Seções) */
--text-h2: 20sp;
font-family: Poppins;
font-weight: 600;
line-height: 28sp;

/* H3 (Subtítulos) */
--text-h3: 18sp;
font-family: Poppins;
font-weight: 600;
line-height: 24sp;

/* Body Large (Textos importantes) */
--text-body-large: 16sp;
font-family: Roboto;
font-weight: 500;
line-height: 24sp;

/* Body (Textos principais) */
--text-body: 14sp;
font-family: Roboto;
font-weight: 400;
line-height: 20sp;

/* Caption (Textos secundários) */
--text-caption: 12sp;
font-family: Roboto;
font-weight: 400;
line-height: 16sp;
color: var(--gray-light);

/* Overline (Labels pequenos) */
--text-overline: 10sp;
font-family: Roboto;
font-weight: 500;
line-height: 14sp;
letter-spacing: 1px;
text-transform: uppercase;
```

---

## 📐 ESPAÇAMENTO

### Sistema de 8pt Grid

Todos os espaçamentos são múltiplos de **8dp** (density-independent pixels):

```css
--space-xs: 4dp;      /* Espaçamento mínimo */
--space-sm: 8dp;      /* Padding pequeno */
--space-md: 16dp;     /* Padding padrão */
--space-lg: 24dp;     /* Margem entre seções */
--space-xl: 32dp;     /* Margem grande */
--space-xxl: 48dp;    /* Espaçamento especial */
```

### Aplicação

```
┌─────────────────────────────────┐
│  [32dp margem superior]         │
│  ┌───────────────────────────┐  │
│  │ [16dp padding interno]    │  │
│  │                           │  │
│  │  Conteúdo do Card         │  │
│  │                           │  │
│  │ [16dp padding interno]    │  │
│  └───────────────────────────┘  │
│  [24dp entre cards]             │
│  ┌───────────────────────────┐  │
│  │ Próximo Card              │  │
└─────────────────────────────────┘
```

---

## 🔲 BORDAS E RAIOS

### Border Radius

```css
--radius-sm: 4dp;     /* Tags, badges */
--radius-md: 8dp;     /* Botões, inputs */
--radius-lg: 12dp;    /* Cards */
--radius-xl: 16dp;    /* Modals, bottom sheets */
--radius-pill: 999dp; /* Botões circulares, pills */
```

### Borders

```css
--border-thin: 1dp solid var(--gray-dark);
--border-medium: 2dp solid var(--gold-bright);
--border-focus: 2dp solid var(--gold-soft);
```

---

## 🌑 SOMBRAS E ELEVAÇÕES

### Material Elevation

```css
/* Elevação 1 (Cards sutis) */
box-shadow: 0 1dp 3dp rgba(0,0,0,0.2);

/* Elevação 2 (Botões, Cards interativos) */
box-shadow: 0 2dp 6dp rgba(0,0,0,0.3);

/* Elevação 3 (Modals, Bottom Sheets) */
box-shadow: 0 4dp 12dp rgba(0,0,0,0.4);

/* Elevação 4 (FAB, Menus flutuantes) */
box-shadow: 0 8dp 24dp rgba(0,0,0,0.5);

/* Sombra Amarela (Glow effect em botões) */
box-shadow: 0 4dp 16dp rgba(255, 215, 0, 0.3);
```

---

## 🎬 ANIMAÇÕES

### Timing Functions

```css
--ease-standard: cubic-bezier(0.4, 0.0, 0.2, 1);     /* Aceleração padrão */
--ease-decelerate: cubic-bezier(0.0, 0.0, 0.2, 1);   /* Entrada */
--ease-accelerate: cubic-bezier(0.4, 0.0, 1, 1);     /* Saída */
--ease-sharp: cubic-bezier(0.4, 0.0, 0.6, 1);        /* Ações rápidas */
```

### Durações

```css
--duration-fast: 150ms;      /* Hover, feedback instantâneo */
--duration-normal: 300ms;    /* Transições de tela, modals */
--duration-slow: 500ms;      /* Animações complexas */
```

### Animações Específicas

**Fade In:**
```css
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
animation: fadeIn 300ms ease-standard;
```

**Slide Up:**
```css
@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
animation: slideUp 300ms ease-decelerate;
```

**Scale Button (Pressed):**
```css
transform: scale(0.95);
transition: transform 150ms ease-sharp;
```

**Glow Pulse (Loading):**
```css
@keyframes glowPulse {
  0%, 100% { box-shadow: 0 0 10px rgba(255, 215, 0, 0.3); }
  50% { box-shadow: 0 0 20px rgba(255, 215, 0, 0.6); }
}
animation: glowPulse 2s ease-in-out infinite;
```

---

## 🧩 COMPONENTES

### Botões

**Botão Primário:**
```css
background: linear-gradient(90deg, #FFD700, #FFC107);
color: #000000;
padding: 12dp 24dp;
border-radius: 8dp;
font: Roboto Medium 16sp;
box-shadow: 0 2dp 6dp rgba(0,0,0,0.3);

/* Hover */
box-shadow: 0 4dp 12dp rgba(255, 215, 0, 0.4);
transform: translateY(-2dp);

/* Pressed */
transform: scale(0.95);
```

**Botão Secundário:**
```css
background: transparent;
color: #FFD700;
border: 2dp solid #FFD700;
padding: 12dp 24dp;
border-radius: 8dp;

/* Hover */
background: rgba(255, 215, 0, 0.1);
```

**Botão Outline:**
```css
background: transparent;
color: #FFFFFF;
border: 1dp solid #424242;
padding: 10dp 20dp;
border-radius: 8dp;

/* Hover */
border-color: #FFD700;
color: #FFD700;
```

### Cards

**Card Padrão:**
```css
background: #121212;
border-radius: 12dp;
padding: 16dp;
box-shadow: 0 2dp 6dp rgba(0,0,0,0.3);
border: 1dp solid #1E1E1E;
```

**Card de Canal (Grid):**
```css
width: 160dp;
height: 240dp;
background: linear-gradient(180deg, transparent, rgba(0,0,0,0.8));
border-radius: 8dp;
overflow: hidden;
position: relative;

/* Thumbnail */
img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Overlay */
.overlay {
  position: absolute;
  bottom: 0;
  padding: 12dp;
  background: linear-gradient(180deg, transparent, rgba(0,0,0,0.9));
}

/* Hover */
transform: scale(1.05);
box-shadow: 0 8dp 24dp rgba(255, 215, 0, 0.2);
```

### Inputs

**Text Field:**
```css
background: #1E1E1E;
border: 1dp solid #424242;
border-radius: 8dp;
padding: 12dp 16dp;
color: #FFFFFF;
font: Roboto Regular 16sp;

/* Focus */
border-color: #FFD700;
box-shadow: 0 0 0 2dp rgba(255, 215, 0, 0.2);

/* Error */
border-color: #F44336;
```

### Ícones

**Tamanhos:**
```css
--icon-xs: 16dp;
--icon-sm: 20dp;
--icon-md: 24dp;  /* Padrão */
--icon-lg: 32dp;
--icon-xl: 48dp;
```

**Cores:**
```css
/* Ativo */
color: #FFD700;

/* Inativo */
color: #757575;

/* Normal */
color: #FFFFFF;
```

### Navigation Bar

```css
height: 56dp;
background: #000000;
border-top: 1dp solid #1E1E1E;
padding: 0 16dp;

/* Ícones */
.nav-icon {
  color: #757575;
  transition: color 150ms;
}

.nav-icon.active {
  color: #FFD700;
}
```

### App Bar

```css
height: 56dp;
background: #000000;
padding: 0 16dp;
box-shadow: 0 2dp 4dp rgba(0,0,0,0.2);

/* Título */
font: Poppins SemiBold 20sp;
color: #FFFFFF;
```

---

## 📱 GRID E LAYOUT

### Breakpoints

```css
/* Phone Portrait */
--breakpoint-xs: 360dp;

/* Phone Landscape / Small Tablet */
--breakpoint-sm: 600dp;

/* Tablet Portrait */
--breakpoint-md: 840dp;

/* Tablet Landscape / Desktop */
--breakpoint-lg: 1024dp;

/* Large Desktop */
--breakpoint-xl: 1280dp;
```

### Grid de Canais (Home)

```css
/* Phone Portrait (2 colunas) */
@media (max-width: 599dp) {
  grid-template-columns: repeat(2, 1fr);
  gap: 12dp;
}

/* Phone Landscape (3 colunas) */
@media (min-width: 600dp) and (max-width: 839dp) {
  grid-template-columns: repeat(3, 1fr);
  gap: 16dp;
}

/* Tablet (4 colunas) */
@media (min-width: 840dp) {
  grid-template-columns: repeat(4, 1fr);
  gap: 20dp;
}
```

---

## 🎨 TEMAS ESPECIAIS

### Glassmorphism (Cards flutuantes)

```css
background: rgba(30, 30, 30, 0.8);
backdrop-filter: blur(10px);
border: 1dp solid rgba(255, 255, 255, 0.1);
box-shadow: 0 8dp 32dp rgba(0, 0, 0, 0.4);
```

### Neumorphism (Botões táteis - opcional)

```css
background: #121212;
box-shadow:
  8dp 8dp 16dp rgba(0, 0, 0, 0.5),
  -8dp -8dp 16dp rgba(40, 40, 40, 0.1);
```

---

## 🎯 ICONOGRAFIA

### Biblioteca

**Material Icons** (Google) - Estilo: Rounded

**Ícones Principais:**
- `home` - Home
- `live_tv` - TV Ao Vivo
- `movie` - Filmes
- `tv` - Séries
- `apps` - Categorias
- `settings` - Configurações
- `play_arrow` - Play
- `pause` - Pause
- `skip_next` - Próximo
- `skip_previous` - Anterior
- `volume_up` - Volume
- `cast` - Chromecast
- `lock` - Controle Parental
- `schedule` - EPG
- `info` - Informações
- `error_outline` - Erro/Aviso

**Logo Icon:**
- Raio estilizado (custom SVG)
- Triângulo play integrado

---

## 🚀 MOTION DESIGN

### Transições de Tela

**Padrão (Navegação para frente):**
```css
/* Tela atual: Slide para esquerda + Fade out */
transform: translateX(-100%);
opacity: 0;
transition: all 300ms ease-standard;

/* Nova tela: Slide da direita + Fade in */
transform: translateX(0);
opacity: 1;
transition: all 300ms ease-decelerate;
```

**Voltar (Navegação para trás):**
```css
/* Inverter direção */
transform: translateX(100%);
```

### Micro-interações

**Botão Click:**
```css
/* Down */
transform: scale(0.95);
transition: transform 100ms ease-sharp;

/* Up */
transform: scale(1);
transition: transform 150ms ease-standard;
```

**Card Hover:**
```css
transform: translateY(-4dp) scale(1.02);
box-shadow: 0 8dp 24dp rgba(255, 215, 0, 0.2);
transition: all 200ms ease-standard;
```

**Loading Spinner:**
```css
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
animation: spin 1s linear infinite;
border: 3dp solid rgba(255, 215, 0, 0.2);
border-top-color: #FFD700;
```

---

## ✅ CHECKLIST DE IMPLEMENTAÇÃO

### Designer/Desenvolvedor deve garantir:

- [ ] Todas as cores usam variáveis CSS definidas
- [ ] Tipografia segue a hierarquia (Display → H1 → Body)
- [ ] Espaçamentos são múltiplos de 8dp
- [ ] Border radius consistente por tipo de componente
- [ ] Animações usam timing functions definidas
- [ ] Ícones têm tamanho padronizado (24dp default)
- [ ] Contraste de texto atende WCAG AA (4.5:1 mínimo)
- [ ] Botões têm área de toque mínima de 48dp
- [ ] Feedback visual em todos os elementos interativos
- [ ] Dark theme aplicado em 100% da interface

---

## 🎨 REFERÊNCIAS DE ESTILO

**Inspirações:**
- Netflix (organização de conteúdo, cards de filmes)
- Spotify (dark theme, uso de gradientes)
- YouTube (player controls, EPG timeline)
- TiviMate (IPTV grid layout, channel organization)

**Princípios:**
- **Minimalismo** - Menos é mais
- **Hierarquia visual** - Destaque o que importa
- **Consistência** - Padrões repetidos
- **Feedback** - Usuário sempre sabe o que está acontecendo
- **Performance** - Animações 60fps, carregamento rápido

---

**Documento criado em:** 23 de Novembro de 2025
**Autor:** Equipe Optimus Player - Design & UX
**Versão:** 1.0
**Status:** ✅ Aprovado para implementação
