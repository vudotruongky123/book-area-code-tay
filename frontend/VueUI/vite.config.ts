import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  // Thêm đoạn cấu hình dưới đây để chạy với Cloudflare Tunnel
  server: {
    host: '0.0.0.0', // Cho phép lắng nghe trên tất cả card mạng
    allowedHosts: true // Cho phép mọi host truy cập (bao gồm link của Cloudflare)
  }
})