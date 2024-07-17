import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
  {
    path: '/calendar',
    name: 'CalendarView',
    component: () => import("@/views/CalendarView.vue"),
  }
  ]
})

export default router;
