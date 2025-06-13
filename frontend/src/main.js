import { createApp } from 'vue'
import { createPinia } from "pinia";
import App from '@/App.vue'
import router from '@/router'

import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './scripts/authorization.js'
import 'element-plus/dist/index.css'

const pinia = createPinia()
const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
