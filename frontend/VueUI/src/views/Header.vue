<script setup lang="ts">
import { ref, onMounted } from 'vue'

const navItems = [
  { label: 'Home', href: '#' },
  { label: 'Library', href: '#library' },
  { label: 'Authors', href: '#authors' },
  { label: 'Genres', href: '#genres' },
  { label: 'Membership', href: '#membership' },
]

const utilityLinks = [{ label: 'Admin Desk', to: '/admin' }]

</script>
<template>
  <header class="topbar">
    <div class="topbar-side topbar-side-left">
      <a class="brand" href="#" aria-label="BOOK AREA home">
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

    <div class="topbar-side topbar-side-right">
      <RouterLink v-for="item in utilityLinks" :key="item.label" class="topbar-utility" :to="item.to">
        {{ item.label }}
      </RouterLink>
    </div>
  </header>
</template>