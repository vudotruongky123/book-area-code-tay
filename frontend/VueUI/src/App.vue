<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="app-shell">
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
}

.route-fade-enter-active,
.route-fade-leave-active {
  transition:
    opacity 220ms ease-out,
    transform 220ms ease-out;
}

.route-fade-enter-from,
.route-fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
