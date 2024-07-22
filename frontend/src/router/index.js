import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: '/',
			name: 'main',
			component: () => import('@/views/MainView.vue'),
		},
		{
			path: '/calendar',
			name: 'CalendarView',
			component: () => import('@/views/CalendarView.vue'),
		},
		{
			path: '/login',
			name: 'login',
			component: () => import('@/views/account/LoginView.vue'),
		},
		{
			path: '/signup',
			name: 'signup',
			component: () => import('@/views/account/SignupView.vue'),
		},
		{
			path: '/signup/email',
			name: 'signupEmail',
			component: () => import('@/views/account/SignupEmailView.vue'),
		},
		{
			path: '/modifyAccount',
			name: 'modifyAccount',
			component: () => import('@/views/account/ModifyAccountView.vue'),
		},
		{
			path: '/modifyPassword/:path',
			name: 'modifyPassword',
			component: () => import('@/views/account/ModifyPasswordView.vue'),
		},
		{
			path: '/findPassword',
			name: 'findPassword',
			component: () => import('@/views/account/FindPasswordView.vue'),
		},
		{
			path: '/board',
			name: 'board',
			component: () => import('@/views/BoardView.vue'),
		},
		{
			path: '/boothCode',
			name: 'boothCode',
			component: () => import('@/views/booth/BoothCodeView.vue'),
		},
	],
});

export default router;
