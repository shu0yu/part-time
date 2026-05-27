import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '首页 - 大学生兼职平台' }
  },
  {
    path: '/jobs',
    name: 'Jobs',
    component: () => import('../views/JobsView.vue'),
    meta: { title: '兼职列表 - 大学生兼职平台' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '登录 - 大学生兼职平台' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { title: '注册 - 大学生兼职平台' }
  },
  {
    path: '/job/publish',
    name: 'JobPublish',
    component: () => import('../views/JobPublishView.vue'),
    meta: { title: '发布兼职 - 大学生兼职平台' }
  },
  {
    path: '/job/:id',
    name: 'JobDetail',
    component: () => import('../views/JobDetailView.vue'),
    meta: { title: '兼职详情 - 大学生兼职平台' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { title: '个人中心 - 大学生兼职平台' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('../views/ChatListView.vue'),
    meta: { title: '消息 - 大学生兼职平台' }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/AdminUserView.vue'),
    meta: { title: '用户管理 - 大学生兼职平台' }
  },
  {
    path: '/admin/jobs',
    name: 'AdminJobs',
    component: () => import('../views/AdminJobView.vue'),
    meta: { title: '岗位管理 - 大学生兼职平台' }
  },
  {
    path: '/admin/dispute',
    name: 'AdminDispute',
    component: () => import('../views/AdminDisputeView.vue'),
    meta: { title: '纠纷处理 - 大学生兼职平台' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫，设置页面标题
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '大学生兼职平台'
  next()
})

export default router
