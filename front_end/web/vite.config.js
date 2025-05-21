import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// vite.config.ts
import Components from 'unplugin-vue-components/vite'
import {
  AntDesignVueResolver,
  ElementPlusResolver,
  VantResolver,
} from 'unplugin-vue-components/resolvers'


// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(),Components({
    resolvers: [
      AntDesignVueResolver(),
      ElementPlusResolver(),
      VantResolver(),
    ],
  })],
})
