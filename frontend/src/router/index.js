import { createRouter, createWebHistory } from 'vue-router';
import WebSocketService from '@/services/WebSocketService';
import { useUserStore } from '@/stores/userStore';
import { alertResult } from '@/api/baseApi';

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
			name: 'calendarView',
			component: () => import('@/views/CalendarView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/login',
			name: 'login',
			component: () => import('@/views/account/LoginView.vue'),
			meta: { notAuthRequired: true },
		},
		{
			path: '/email-verification',
			children: [
				{
					path: '/email-verification/signup',
					name: 'signupEmail',
					component: () => import('@/views/account/SignupEmailView.vue'),
					meta: { notAuthRequired: true },
				},
				{
					path: '/email-verification/find-password',
					name: 'findPasswordEmail',
					component: () => import('@/views/account/FindPasswordEmailView.vue'),
					meta: { notAuthRequired: true },
				},
			],
		},
		{
			path: '/signup',
			name: 'signup',
			component: () => import('@/views/account/SignupView.vue'),
			meta: { emailRequired: true, notAuthRequired: true },
		},
		{
			path: '/modify-account',
			name: 'modifyAccount',
			component: () => import('@/views/account/ModifyAccountView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/modify-password',
			children: [
				{
					path: '/modify-password/modify',
					name: 'modifyPassword',
					component: () => import('@/views/account/ModifyPasswordView.vue'),
					meta: { authRequired: true },
				},
				{
					path: '/modify-password/find',
					name: 'findPassword',
					component: () => import('@/views/account/ModifyPasswordView.vue'),
					meta: { emailRequired: true, notAuthRequired: true },
				},
			],
		},
		{
			path: '/board',
			name: 'board',
			component: () => import('@/views/BoardView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/create',
			name: 'createbooth',
			component: () => import('@/views/booth/BoothCreateView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/booth/:boothId',
			component: () => import('@/views/booth/BoothShootView.vue'),
			redirect: { name: 'background' },
			children: [
				{
					path: 'bg',
					name: 'background',
					component: () => import('@/components/booth/BoothSelectBackComp.vue'),
					props: true,
					meta: { authRequired: true },
				},
				{
					path: 'photo',
					name: 'showphoto',
					component: () => import('@/components/booth/BoothShowPhotoComp.vue'),
					props: true,
					meta: { authRequired: true },
				},
			],
			props: true,
			meta: { authRequired: true },
		},
		{
			path: '/boothCode',
			name: 'boothCode',
			component: () => import('@/views/booth/BoothCodeView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/boothVideoTest/:boothId',
			name: 'boothVideoTest',
			component: () => import('@/views/booth/BoothVideoTestView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/selectTemp',
			name: 'selectTemp',
			component: () => import('@/views/booth/BoothTemplateView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/insertImg/:templateKey',
			name: 'insertImg',
			component: () => import('@/views/booth/BoothInsertView.vue'),
			props: (route) => ({
				templateKey: route.params.templateKey,
				photos: route.params.photos ? JSON.parse(decodeURIComponent(route.params.photos)) : [],
			}),
			meta: { authRequired: true },
		},
		{
			path: '/:pathMatch(.*)*',
			name: 'notFound',
			component: () => import('@/views/NotFoundView.vue'),
		},
	],
});

// WebSocket 연결이 필요한 라우트 목록
const websocketRoutes = ['createbooth', 'booth', 'boothCode', 'selectTemp', 'insertImg'];

router.beforeEach(async (to, from) => {
	const userStore = useUserStore();

	if (to.meta.authRequired && !userStore.isLogined) {
		await alertResult(false, '로그인이 필요합니다.');
		return { name: 'login' };
	}

	if ((to.meta.notAuthRequired && userStore.isLogined) || (to.meta.emailRequired && !userStore.verifiedEmail)) {
		await alertResult(false, '잘못된 접근입니다.');
		return { name: 'main' };
	}

	if (websocketRoutes.includes(to.name)) {
		if (!WebSocketService.isConnected()) {
			try {
				await WebSocketService.connect(import.meta.env.VITE_WS);
			} catch (error) {
				console.error('Failed to connect to WebSocket:', error);
				// 에러 처리
			}
		}
	} else if (websocketRoutes.includes(from.name) && !websocketRoutes.includes(to.name)) {
		// WebSocketService.close();
	}

	if (
		(to.name === 'background' &&
			from.name !== 'createbooth' &&
			from.name !== 'boothVideoTest' &&
			from.name !== 'showphoto') ||
		(to.name === 'showphoto' && from.name !== 'background') ||
		(to.name === 'selectTemp' && from.name !== 'background' && from.name !== 'showphoto')
	) {
		await alertResult(false, '새로고침 시 부스에 재입장해야 합니다.');
		return { name: 'boothCode' };
	}
});

export default router;
