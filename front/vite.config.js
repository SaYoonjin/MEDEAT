import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],

  server: {
    // ⭐ 외부 접근 허용 (ngrok 필수)
    host: true,

    // ⭐ ngrok 서브도메인 전체 허용 (Vite 5에서 가장 확실)
    allowedHosts: [
      '.ngrok-free.dev',
    ],

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
