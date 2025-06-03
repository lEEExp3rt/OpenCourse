import {createRouter, createWebHistory} from "vue-router"

const index=createRouter({
  routes:[
    {
      path: '/',
      redirect: '/dashboard',
      meta: {requiresAuth: false},
    },
    {
      path: '/404',
      component: () => import('@/views/404View.vue'),
      meta: {requiresAuth: false},
    },
    {
      path: '/register',
      component: () => import('@/views/RegisterView.vue'),
      meta: {requiresAuth: false},
    },
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: {requiresAuth: false},
    },
    {
      path: '/dashboard',
      redirect: '/dashboard/course',
      component: () => import('@/views/DashboardView.vue'),
      meta: {requiresAuth: false},
      children:[
        {
          path: 'course',
          component: () => import('@/views/dashboard/CourseView.vue'),
          children:[
            {
              path: ':id',
              component: () => import('@/views/dashboard/CourseDetail.vue')
            }
          ]
        },
        {
          path: 'user',
          component: () => import('@/views/dashboard/UserView.vue'),
        },
      ]
    },
    // {
    //   path: '/:pathMatch(.*)*',
    //   redirect: '/404',
    //   meta: {requiresAuth: false},
    // }
  ],
  history: createWebHistory()
})

export default index
