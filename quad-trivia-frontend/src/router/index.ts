import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import QuizView from '../views/QuizView.vue'
import ResultView from '../views/ResultView.vue'
import { useAuthStore } from '../stores/authenticationStore'
import { RoutePath } from './routePath'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: RoutePath.Quizroute },
    { path: RoutePath.Loginroute,name:'login', component: LoginView },
    { path: RoutePath.Quizroute,name: 'quiz', component: QuizView , meta:{ requiresAuth: true}},
    { path: RoutePath.Resultroute, component: ResultView, meta:{ requiresAuth: true} },
  ],
})
// before each route check if user is authenticated if not redirect to login if needed
router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (!authStore.initialized) {
    await authStore.fetchMe()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return RoutePath.Loginroute
  }

  if (to.path === RoutePath.Loginroute && authStore.isAuthenticated) {
    return RoutePath.Quizroute
  }
})
export default router
