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
      redirect: '/dashboard/department',
      component: () => import('@/views/DashboardView.vue'),
      meta: {requiresAuth: false},
      children:[
          {
            path: 'department',
            component: () => import('@/views/dashboard/Department.vue'),
          },
          {
            path: 'department/:department_id/course',
            component: () => import('@/views/dashboard/CourseView.vue'),
          },
          {
            path: 'department/:department_id/course/:id/resources',
            component: () => import('@/views/dashboard/course/CourseDetail.vue')
          },
          {
            path: 'department/:department_id/course/:id/forum',
            component: () => import('@/views/dashboard/course/CourseForum.vue')
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
