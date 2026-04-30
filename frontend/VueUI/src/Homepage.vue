<script setup lang="ts">
const navItems = [
  { label: 'Home', href: '#home' },
  { label: 'Library', href: '#library' },
  { label: 'Authors', href: '#authors' },
  { label: 'Genres', href: '#genres' },
  { label: 'Membership', href: '#membership' },
]

const heroFacts = [
  { value: '10,000+', label: 'BOOKS' },
  { value: 'Offline', label: 'READING' },
  { value: 'Personal', label: 'SHELVES' },
]

const featuredBooks = [
  {
    eyebrow: 'Novel',
    title: 'The Lantern Archive',
    author: 'Aiko Sera',
    summary:
      'A city librarian protects a shelf of vanished memories that can only be read after midnight.',
    cover:
      'linear-gradient(180deg, rgba(56, 30, 14, 0.12), rgba(32, 19, 11, 0.72)), radial-gradient(circle at 50% 20%, rgba(245, 221, 154, 0.95), rgba(245, 221, 154, 0) 34%), linear-gradient(180deg, #e8c376 0%, #a05e2d 48%, #3a2417 100%)',
  },
  {
    eyebrow: 'Essay',
    title: 'Cloud Notes for Rainy Trains',
    author: 'Mina Okabe',
    summary:
      'Observations on quiet commuting, weather, and the strange intimacy of reading in motion.',
    cover:
      'linear-gradient(180deg, rgba(42, 23, 18, 0.1), rgba(30, 18, 12, 0.76)), linear-gradient(135deg, #efe4c6 0%, #d09c54 44%, #6b4128 100%), radial-gradient(circle at 72% 18%, rgba(255, 238, 184, 0.92), rgba(255, 238, 184, 0) 28%)',
  },
  {
    eyebrow: 'Manga',
    title: 'Sunset District Reading Club',
    author: 'Ren Mori',
    summary:
      'Teenage night readers gather above a suspended rail station and build a library of their own.',
    cover:
      'linear-gradient(180deg, rgba(31, 18, 12, 0.08), rgba(23, 14, 10, 0.82)), linear-gradient(180deg, #f3d394 0%, #b87037 56%, #28170f 100%), radial-gradient(circle at 54% 26%, rgba(255, 240, 198, 0.9), rgba(255, 240, 198, 0) 31%)',
  },
]

const authorNotes = [
  {
    name: 'Aiko Sera',
    note: 'Tender speculative fiction, city archives, memory and ritual.',
  },
  {
    name: 'Mina Okabe',
    note: 'Essays with weather, stillness, and everyday cinematic detail.',
  },
  {
    name: 'Ren Mori',
    note: 'Long-form manga with elevated trains, rooftops, and chosen families.',
  },
]

const continueReading = [
  {
    title: 'After the Last Monsoon',
    chapter: 'Chapter 12, Glass Bridges',
    progress: 68,
    eta: '27 min left',
  },
  {
    title: 'The Paper Observatory',
    chapter: 'Volume 2, Night Reading Room',
    progress: 41,
    eta: '1 hr left',
  },
  {
    title: 'Letters to a Silent Harbor',
    chapter: 'Essay 5, On Returning',
    progress: 84,
    eta: '11 min left',
  },
]

const collections = [
  {
    title: 'Moonlit Railways',
    description:
      'Novels and manga shaped by night platforms, soft departures, and cities seen through train windows.',
    tags: ['Travel', 'Melancholy', 'Slow burn'],
  },
  {
    title: 'Paper Cities',
    description:
      'Architectural fiction, vertical libraries, essay worlds, and skyline-bound imagination for long weekends.',
    tags: ['Speculative', 'Urban', 'Atmospheric'],
  },
  {
    title: 'Quiet Fires',
    description:
      'Stories with low light, emotional precision, private rituals, and characters who change by reading.',
    tags: ['Literary', 'Intimate', 'Reflective'],
  },
]

const membershipBenefits = [
  {
    title: 'Read anywhere, then return softly',
    description:
      'Download chapters for flights, trains, and offline evenings without losing your place.',
  },
  {
    title: 'Build shelves that feel personal',
    description:
      'Collect reading moods, note-taking themes, and private stacks for the books you keep close.',
  },
  {
    title: 'Live inside your reading rhythm',
    description:
      'Progress memory, night mode, and calm typography keep the interface out of the story.',
  },
  {
    title: 'Receive editor-made recommendations',
    description:
      'Weekly curation brings forward novels, essays, and manga with a premium literary point of view.',
  },
]
</script>

<template>
  <div class="page-shell">
    <header class="topbar">
      <div class="topbar-side topbar-side-left">
        <a class="brand" href="#home" aria-label="BOOK AREA home">
          <span class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 96 96" role="presentation">
              <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M24 73V36c0-13.25 10.75-24 24-24s24 10.75 24 24v37" stroke-width="1.8" />
                <path d="M24 73c9.4-2.4 16.45-2.73 24 3.75" stroke-width="1.8" />
                <path d="M72 73c-9.4-2.4-16.45-2.73-24 3.75" stroke-width="1.8" />
                <path d="M18 84l6-11h48l6 11c-10.4-2.1-19.15-2.3-30 4-10.85-6.3-19.6-6.1-30-4Z" stroke-width="1.8" />
                <path d="M48 76v11" stroke-width="1.6" />
                <path d="M27 79c9.2-1.65 14.6-1.05 21 3" stroke-width="1.5" />
                <path d="M69 79c-9.2-1.65-14.6-1.05-21 3" stroke-width="1.5" />
                <path d="M36 44v22" stroke-width="1.5" />
                <path d="M40 47v18" stroke-width="1.5" />
                <path d="M44 50v15" stroke-width="1.5" />
                <path d="M60 44v22" stroke-width="1.5" />
                <path d="M56 47v18" stroke-width="1.5" />
                <path d="M52 50v15" stroke-width="1.5" />
                <path d="M34 40l10 8" stroke-width="1.3" />
                <path d="M62 40l-10 8" stroke-width="1.3" />
              </g>
              <circle cx="48" cy="42" r="7" fill="currentColor" opacity="0.92" />
              <path d="M48 49.5l7 10.5H41z" fill="currentColor" opacity="0.22" />
              <g fill="currentColor">
                <path d="M48 25l1.6 2.8 2.8 1.6-2.8 1.6-1.6 2.8-1.6-2.8-2.8-1.6 2.8-1.6z" />
                <path d="M59 31l1 1.7 1.7 1-1.7 1-1 1.7-1-1.7-1.7-1 1.7-1z" />
                <path d="M37 33l1 1.7 1.7 1-1.7 1-1 1.7-1-1.7-1.7-1 1.7-1z" />
              </g>
            </svg>
          </span>
          <span class="brand-wordmark">BOOK AREA</span>
        </a>
      </div>

      <nav class="topbar-nav" aria-label="Primary">
        <a v-for="item in navItems" :key="item.label" :href="item.href">{{ item.label }}</a>
      </nav>

      <div class="topbar-side topbar-side-right" aria-hidden="true"></div>
    </header>

    <main class="homepage">
      <section id="home" class="hero">
        <div class="hero-media" aria-hidden="true"></div>
        <div class="hero-inner">
          <div class="hero-rail" aria-hidden="true">
            <span class="hero-rail-line"></span>
            <p>quiet shelves, lasting stories</p>
            <span class="hero-rail-mark"></span>
          </div>

          <div class="hero-copy">
            <p class="eyebrow">BOOK AREA</p>
            <h1 class="hero-headline">
              <span>READ</span>
              <span>STORIES</span>
              <span>BEYOND</span>
              <span>TIME</span>
            </h1>
            <p class="hero-text">
              A quiet digital library for timeless books, thoughtful reading, and literary
              discovery.
            </p>

            <div class="hero-actions">
              <a class="button button-primary" href="#library">Start Reading</a>
              <a class="button button-secondary" href="#genres">Explore Library</a>
            </div>

            <dl class="hero-meta">
              <div v-for="fact in heroFacts" :key="fact.label">
                <dt>{{ fact.label }}</dt>
                <dd>{{ fact.value }}</dd>
              </div>
            </dl>
          </div>
        </div>
      </section>

      <section id="library" class="content-section section-featured">
        <div class="section-heading">
          <div class="section-mark">
            <span>01</span>
          </div>
          <div>
            <p class="eyebrow">Featured Books</p>
            <h2>Frontlist picks for readers who like atmosphere as much as plot.</h2>
          </div>
        </div>

        <div class="featured-layout">
          <div class="book-grid">
            <article v-for="book in featuredBooks" :key="book.title" class="book-card">
              <div class="book-cover" :style="{ '--cover': book.cover }">
                <span>{{ book.eyebrow }}</span>
              </div>
              <div class="book-copy">
                <p class="book-author">{{ book.author }}</p>
                <h3>{{ book.title }}</h3>
                <p>{{ book.summary }}</p>
              </div>
            </article>
          </div>

          <aside id="authors" class="authors-panel">
            <p class="panel-label">Featured Authors</p>
            <ul class="author-list">
              <li v-for="author in authorNotes" :key="author.name">
                <strong>{{ author.name }}</strong>
                <p>{{ author.note }}</p>
              </li>
            </ul>
          </aside>
        </div>
      </section>

      <section class="content-section section-progress">
        <div class="section-heading">
          <div class="section-mark">
            <span>02</span>
          </div>
          <div>
            <p class="eyebrow">Continue Reading</p>
            <h2>Pick up exactly where the evening left off.</h2>
          </div>
        </div>

        <div class="progress-layout">
          <article class="reading-spotlight">
            <p class="panel-label">Tonight's shelf</p>
            <h3>Return to your saved world in one calm tap.</h3>
            <p>
              Your notes, reading pace, and chapter memory stay quietly in place across every
              device.
            </p>
          </article>

          <div class="reading-list">
            <article v-for="item in continueReading" :key="item.title" class="reading-item">
              <div class="reading-item-top">
                <div>
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.chapter }}</p>
                </div>
                <span>{{ item.eta }}</span>
              </div>
              <div class="progress-track" :aria-label="`${item.progress}% complete`">
                <span :style="{ width: `${item.progress}%` }"></span>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section id="genres" class="content-section section-collections">
        <div class="section-heading">
          <div class="section-mark">
            <span>03</span>
          </div>
          <div>
            <p class="eyebrow">Curated Collections</p>
            <h2>Browse by feeling, architecture, weather, and the kind of silence you want.</h2>
          </div>
        </div>

        <div class="collection-grid">
          <article v-for="collection in collections" :key="collection.title" class="collection-card">
            <p class="panel-label">Collection</p>
            <h3>{{ collection.title }}</h3>
            <p>{{ collection.description }}</p>
            <ul class="collection-tags">
              <li v-for="tag in collection.tags" :key="tag">{{ tag }}</li>
            </ul>
          </article>
        </div>
      </section>

      <section id="membership" class="content-section section-membership">
        <div class="section-heading">
          <div class="section-mark">
            <span>04</span>
          </div>
          <div>
            <p class="eyebrow">Membership Benefits</p>
            <h2>A premium reading layer built for return visits, not distraction.</h2>
          </div>
        </div>

        <div class="membership-layout">
          <div class="benefit-list">
            <article v-for="benefit in membershipBenefits" :key="benefit.title" class="benefit-item">
              <h3>{{ benefit.title }}</h3>
              <p>{{ benefit.description }}</p>
            </article>
          </div>

          <aside class="membership-panel">
            <p class="panel-label">BOOK AREA Membership</p>
            <h3>Join a digital library designed to feel more like a private reading room.</h3>
            <p>
              Unlock unlimited reading, curated weekly shelves, listening mode, and private notes
              that travel with you from mobile to desktop.
            </p>
            <a class="button button-primary" href="#home">Start Reading</a>
          </aside>
        </div>
      </section>
    </main>
  </div>
</template>
