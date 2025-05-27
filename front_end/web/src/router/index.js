import {createRouter, createWebHistory} from "vue-router"

const index=createRouter({
  routes:[
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      name: '登录',
      path: '/login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      name: '主页',
      path: '/dashboard',
      component: () => import('../views/DashboardView.vue'),
      children:[
        {
          path: '',
          redirect: '/course',
        },
        {
          name: '课程中心',
          path: '/course',
          component: () => import('../views/dashboard/CourseView.vue'),
        },
        {
          name: '个人中心',
          path: '/user',
          component: () => import('../views/dashboard/UserView.vue'),
        },
      ]
    },
  ],
  history: createWebHistory()
})

export default index
