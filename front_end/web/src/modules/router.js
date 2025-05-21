import {createRouter, createWebHistory} from "vue-router"

const router=createRouter(
{
    routes:[{
        name: '首页',
        component: () => import('../pages/home.vue')
    }],
    history: createWebHistory()
}
)

export default router