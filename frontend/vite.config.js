import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'
import Unfonts from 'unplugin-fonts/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiUrl = env.API_URL || process.env.API_URL || 'http://localhost:8080'

  return {
    plugins: [
      vue(),
      Unfonts({
        google: {
          families: [
            {
              name: 'Inter',
              styles: 'wght@400;500;600;700',
            },
            {
              name: 'Calistoga',
              styles: 'wght@400',
            },
            {
              name: 'JetBrains Mono',
              styles: 'wght@400;500',
            },
          ],
        },
      }),
      AutoImport({
        resolvers: [ElementPlusResolver()],
        imports: ['vue', 'vue-router', 'pinia'],
        dts: 'src/auto-imports.d.ts'
      }),
      Components({
        resolvers: [ElementPlusResolver()],
        dts: 'src/components.d.ts'
      })
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      host: '0.0.0.0',
      port: 5173,
      watch: {
        usePolling: true
      },
      proxy: {
        '/api': {
          target: apiUrl,
          changeOrigin: true
        }
      }
    },
    build: {
      chunkSizeWarningLimit: 1500,
      rollupOptions: {
        output: {
          manualChunks: {
            'element-plus': ['element-plus'],
            'echarts': ['echarts'],
            'vendor': ['vue', 'vue-router', 'pinia', 'axios']
          }
        }
      }
    }
  }
})
