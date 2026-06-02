<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import Header from './views/Header.vue';

const route = useRoute()
</script>

<template>
  <div class="app-shell">
    <!-- Header -->
    <Header v-if="!route.meta.hideHeader" />

    <RouterView v-slot="{ Component }">
      <Transition name="route-fade" mode="out-in">
        <component :is="Component" :key="route.fullPath" />
      </Transition>
    </RouterView>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.route-fade-enter-active,
.route-fade-leave-active {
  transition: opacity 200ms ease-out;
}

.route-fade-enter-from,
.route-fade-leave-to {
  opacity: 0;
}
</style>
