import path from 'path'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    // Output vite build results to target/classes/static/
    outDir: '../target/classes/static/'
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler' // or "modern"
      }
    }
  },
  server: {
    host: 'localhost',
    port: 4000,
    proxy: {
      '/api': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/open-api': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/management': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/management/, '')
      },
      '/login': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/sign-out': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/oauth2': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/assets': {
        target: 'http://localhost:4001',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/swagger-ui': {
        target: 'http://localhost:4001',
        changeOrigin: true
      },
      '/v3/api-docs': {
        target: 'http://localhost:4001',
        changeOrigin: true
      }
    }
  }
})
