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
			path: '/create',
			name: 'createbooth',
			component: () => import('@/views/booth/BoothCreateView.vue'),
		},
		{
			path: '/booth',
			component: () => import('@/views/booth/BoothShootView.vue'),
			children: [
				{
					path: 'bg',
					name: 'background',
					component: () => import('@/components/booth/BoothSelectBackComp.vue'),
					props: true,
				},
				{
					path: 'photo',
					name: 'showphoto',
					component: () => import('@/components/booth/BoothShowPhoto.vue'),
					props: true,
				},
			],
			props: true,
		},
		{
			path: '/boothCode',
			name: 'boothCode',
			component: () => import('@/views/booth/BoothCodeView.vue'),
		},
		{
			path: '/selectTemp',
			name: 'selectTemp',
			component: () => import('@/views/booth/BoothTemplateView.vue'),
		},
		{
			path: '/insertImg/:templateKey',
			name: 'insertImg',
			component: () => import('@/views/booth/BoothInsertView.vue'),
			props: (route) => ({
				templateKey: route.params.templateKey,
				photos: route.params.photos ? JSON.parse(decodeURIComponent(route.params.photos)) : [],
			}),
		},
	],
});

export default router;
