import { createRouter, createWebHistory } from 'vue-router'
const loginroute = '/login'
const quizroute = '/quiz'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: quizroute },
    { path: loginroute,name:'login', component: LoginView },
    { path: quizroute,name: 'quiz', component: QuizView , meta:{ requiresAuth: true}},
    { path: '/result', component: ResultView, meta:{ requiresAuth: true} },

  ],
})
// before each route check if user is authenticated if not redirect to login if needed
router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (!authStore.initialized) {
    await authStore.fetchMe()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return loginroute
  }

  if (to.path === '/login' && authStore.isAuthenticated) {
    return quizroute
  }
})
export default router
