# CLAUDE.md - AI Assistant Guide for Projeto Android

> **Last Updated**: 2025-11-23
> **Project Type**: Static Educational Website
> **Language**: Portuguese (pt-BR)

## Project Overview

This is a static website project that tells the story of the Android mascot (Bugdroid). It's an educational article covering the history from Dan Morrill's first "Dandroids" to Irina Blok's final Bugdroid design, plus Android version naming conventions.

**Purpose**: Educational content about technology history
**Target Audience**: Portuguese-speaking tech enthusiasts and students
**Content Focus**: Android mascot history and version evolution

## Codebase Structure

```
projeto-android/
├── index.html              # Main HTML file (single-page article)
├── estilo/
│   └── style.css          # All styling (CSS custom properties, responsive design)
├── fontes/
│   └── idroid.otf         # Custom Android-themed font
├── imagens/
│   ├── bugdroid.png       # Final Android mascot
│   ├── dan-droids.png     # Original mascot concept (desktop)
│   ├── dan-droids-pq.png  # Original mascot concept (mobile)
│   ├── irina-blok.jpg     # Creator photo (desktop)
│   ├── irina-blok-pq.jpg  # Creator photo (mobile)
│   └── favicon.ico        # Site icon
├── LICENSE                # Project license
└── .gitattributes        # Git configuration
```

### File Responsibilities

- **index.html** (124 lines): Complete article structure with semantic HTML5
- **style.css** (214 lines): All visual styling using modern CSS features
- **Images**: Responsive images with multiple sizes for performance
- **Fonts**: Custom OTF font for Android branding

## Technology Stack

### Core Technologies
- **HTML5**: Semantic elements (`<header>`, `<nav>`, `<main>`, `<article>`, `<aside>`, `<footer>`)
- **CSS3**: Modern features with CSS custom properties (variables)
- **No JavaScript**: Pure HTML/CSS implementation
- **No build tools**: Direct browser-ready code

### External Dependencies
- **Google Fonts**: Bebas Neue (display font for headers)
- **YouTube**: Embedded video player
- **Custom Font**: idroid.otf (locally hosted)

### Browser Features Used
- CSS Custom Properties (CSS Variables)
- Responsive Images (`<picture>` element with `srcset`)
- CSS Grid (2-column layout in aside)
- CSS Gradients
- CSS Transitions
- Responsive iframes

## Design System

### Color Palette (Android Green Theme)
```css
--cor0: #c5ebd6  /* Lightest green (body background) */
--cor1: #83e1ad  /* Light green (accents, highlights) */
--cor2: #3ddc84  /* Medium green (Android brand color) */
--cor3: #2fa866  /* Medium-dark green (gradients) */
--cor4: #1a5c37  /* Dark green (text, headings) */
--cor5: #063d1e  /* Darkest green (header, footer, nav) */
```

**Usage Pattern**:
- `cor0`: Page background
- `cor1`: Highlights, links, aside background
- `cor2`: Reserved (Android official green)
- `cor3`: Gradient start, hover states
- `cor4`: Headings, emphasis, aside header
- `cor5`: Header/footer backgrounds, dark text

### Typography
```css
--fonte-padrao: Arial, Verdana, Helvetica, sans-serif  /* Body text */
--fonte-destaque: 'Bebas Neue', cursive                /* Page header */
--fonte-android: 'Android', cursive                    /* Article headings */
```

**Font Hierarchy**:
- **Page Header (h1)**: Bebas Neue, 3em, white
- **Article Heading (main h1)**: Android font, 1.8em, dark green
- **Subheadings (h2)**: Android font, 1.3em, gradient background
- **Body Text**: Arial fallback stack, 1em, justified, 2em line-height

### Spacing & Layout
- **Main Content**: 300px-1000px width, centered with auto margins
- **Text Indent**: 30px for paragraphs
- **Padding**: 20px standard content padding
- **Border Radius**: 10px for main elements, 5px for small elements

## Development Workflows

### Making Changes

1. **HTML Modifications** (index.html:1-124)
   - Maintain semantic structure
   - Keep language attribute as `pt-br`
   - Preserve accessibility attributes (`alt`, `title`, `abbr`)
   - Maintain responsive image structure with `<picture>` elements

2. **CSS Modifications** (style.css:1-214)
   - Use existing CSS variables for colors
   - Follow mobile-first responsive patterns
   - Maintain box-shadow consistency (design element)
   - Keep transition duration at 0.5s for consistency

3. **Testing Checklist**
   - Responsive design: Test at 670px breakpoint (mobile/desktop images)
   - Typography: Verify custom font loads
   - External links: Check `class="externo"` styling (link icon)
   - Video: Ensure responsive iframe maintains aspect ratio
   - Accessibility: Validate semantic HTML

### Git Workflow

**Current Branch**: `claude/claude-md-mibv7eqqb25qvln1-01Y7F5uoYGfQAqHLJruPYSqs`

**Standard Process**:
```bash
# Make changes to files
git add <modified-files>
git commit -m "Description of changes"
git push -u origin claude/claude-md-mibv7eqqb25qvln1-01Y7F5uoYGfQAqHLJruPYSqs
```

**Commit Message Conventions**:
- Use descriptive messages in English
- Reference specific elements changed (e.g., "Update style.css", "Fix padding issue")
- Keep messages concise but informative

## Code Conventions

### HTML Best Practices

1. **Semantic Structure**
   ```html
   <header>     <!-- Site header with branding -->
   <nav>        <!-- Navigation links -->
   <main>       <!-- Primary content wrapper -->
     <article>  <!-- Article content -->
       <aside>  <!-- Related information box -->
   <footer>     <!-- Site footer with credits -->
   ```

2. **Responsive Images Pattern**
   ```html
   <picture>
     <source media="(max-width: 670px)" srcset="imagens/image-pq.png">
     <img src="imagens/image.png" alt="Description">
   </picture>
   ```

3. **External Links**
   - Use `class="externo"` for external links
   - Include `target="_blank"` for new tab opening
   - CSS automatically adds link icon (🔗) after external links

4. **Abbreviations**
   ```html
   <abbr title="Full description">Short text</abbr>
   ```

### CSS Best Practices

1. **Use CSS Variables**
   ```css
   /* Good */
   color: var(--cor4);

   /* Avoid */
   color: #1a5c37;
   ```

2. **Responsive Video Container**
   ```css
   div.video {
     padding-bottom: 58%;  /* 16:9 aspect ratio padding hack */
     position: relative;
   }

   div.video > iframe {
     position: absolute;
     width: 90%;
     height: 90%;
   }
   ```

3. **Gradient Patterns**
   ```css
   /* Header background */
   background-image: linear-gradient(to bottom, var(--cor3), var(--cor5));

   /* H2 background */
   background-image: linear-gradient(to right, var(--cor1), transparent);
   ```

## Key Features

### 1. Responsive Design
- **Breakpoint**: 670px (mobile/tablet switch)
- **Mobile images**: `-pq.png` suffix (smaller file sizes)
- **Fluid layout**: Min-width 300px, max-width 1000px
- **Responsive video**: Maintains 16:9 aspect ratio at all sizes

### 2. Custom Typography
- **Local font**: idroid.otf (Android-themed)
- **Google Fonts**: Bebas Neue (performance impact minimal)
- **Fallback stack**: Standard web-safe fonts

### 3. Accessibility Features
- Semantic HTML5 elements
- Alt text on all images
- Abbreviation titles for Android versions
- High contrast text (dark on light)
- Readable line-height (2em)

### 4. Visual Design
- Android brand colors throughout
- Consistent box shadows for depth
- Smooth transitions (0.5s)
- Link hover effects
- External link indicators

## AI Assistant Guidelines

### When Making Changes

1. **Read Before Modifying**
   - Always read the file before editing
   - Understand existing patterns and conventions
   - Don't introduce new patterns without necessity

2. **Preserve Existing Structure**
   - Maintain semantic HTML structure
   - Keep CSS variable usage consistent
   - Don't refactor working code unnecessarily
   - Preserve Portuguese language content

3. **Responsive Considerations**
   - Test changes at 670px breakpoint
   - Maintain mobile-first approach
   - Keep responsive image pattern intact

4. **Color Usage**
   - Always use CSS variables for colors
   - Don't introduce new colors without updating `:root`
   - Maintain Android green theme consistency

5. **Typography Changes**
   - Respect the three-font hierarchy
   - Maintain relative sizing (em units)
   - Keep line-height at 2em for readability

### Common Tasks

#### Adding New Content Section
```html
<!-- Follow this pattern -->
<h2>New Section Title</h2>
<p>Content with <strong>emphasis</strong> as needed.</p>
```

#### Adding External Link
```html
<a href="https://example.com" class="externo" target="_blank">Link Text</a>
```

#### Adding New Color
```css
/* In :root selector */
:root {
  --new-color: #hexcode;
}
/* Then use it */
element {
  color: var(--new-color);
}
```

#### Modifying Responsive Breakpoint
- Current breakpoint: 670px
- Used in: `<picture>` media queries, potential CSS media queries
- Change consistently across all instances

### What NOT to Do

- **Don't add JavaScript** - This is intentionally HTML/CSS only
- **Don't add build tools** - Keep it simple and direct
- **Don't create new files unnecessarily** - Single-page design is intentional
- **Don't add frameworks** - Vanilla HTML/CSS is the requirement
- **Don't change language** - Content is Portuguese (pt-BR)
- **Don't remove semantic elements** - Accessibility is important
- **Don't use inline styles** - All styles belong in style.css
- **Don't use hardcoded colors** - Always use CSS variables

### Performance Considerations

1. **Image Optimization**
   - Mobile images are smaller file sizes
   - Consider compression when adding new images
   - Use appropriate formats (PNG for graphics, JPG for photos)

2. **Font Loading**
   - Google Fonts loaded with `display=swap`
   - Custom font is single file (.otf)
   - Consider font subsetting for production

3. **CSS Organization**
   - Styles are in single file for simplicity
   - No unused CSS (keep it clean)
   - Variables reduce redundancy

## Recent Changes

Based on git history:
- **135e3eb**: Update style.css
- **28cae36**: Update style.css
- **22aac60**: problema de padding corrigido (padding issue fixed)
- **125d6a8**: novo site (new site)

## Testing the Site

### Local Testing
```bash
# Option 1: Python HTTP server
python3 -m http.server 8000

# Option 2: PHP server
php -S localhost:8000

# Option 3: Node.js http-server
npx http-server

# Then open: http://localhost:8000
```

### Validation Checklist
- [ ] HTML validates (W3C validator)
- [ ] CSS validates (W3C CSS validator)
- [ ] Responsive images switch at 670px
- [ ] External links open in new tab
- [ ] Custom font loads correctly
- [ ] YouTube video embeds properly
- [ ] All images have alt text
- [ ] Links have appropriate hover states
- [ ] Mobile layout works (< 670px)
- [ ] Desktop layout works (> 670px)

## Content Attribution

- **Original Content**: Created by Gustavo Guanabara for CursoemVideo
- **Topic**: Android mascot history
- **License**: See LICENSE file
- **External Links**: Credit to Dan Morrill, Irina Blok, Android Community

## Related Resources

- [Inkscape](https://inkscape.org/pt-br/) - Vector graphics editor
- [Irina Blok's Portfolio](https://www.irinablok.com/android) - Bugdroid creator
- [Android History](https://www.android.com/intl/en_uk/history/) - Official Android version history
- [CursoemVideo](https://www.youtube.com/cursoemvideo) - Course creator

---

**For AI Assistants**: This is a learning project demonstrating fundamental web development skills. When making changes, prioritize code clarity and educational value. Maintain the simple, accessible structure that makes this a good learning example. Always test responsive behavior at the 670px breakpoint and preserve the Android green color theme.
