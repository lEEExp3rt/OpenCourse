import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './modules/router'
import pinia from './modules/store'
import ElementPlus from 'element-plus'
const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
