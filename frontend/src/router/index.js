import { useUserStore } from '@/stores/userStore';
import { createRouter, createWebHistory } from 'vue-router';
import Swal from 'sweetalert2';

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
			path: '/signup',
			name: 'signup',
			component: () => import('@/views/account/SignupView.vue'),
			meta: { emailRequired: true, notAuthRequired: true },
		},
		{
			path: '/signup/email',
			name: 'signupEmail',
			component: () => import('@/views/account/SignupEmailView.vue'),
			meta: { notAuthRequired: true },
		},
		{
			path: '/modifyAccount',
			name: 'modifyAccount',
			component: () => import('@/views/account/ModifyAccountView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/modifyPassword/:path',
			name: 'modifyPassword',
			component: () => import('@/views/account/ModifyPasswordView.vue'),
			meta: { authRequired: true },
		},
		{
			path: '/findPassword',
			name: 'findPassword',
			component: () => import('@/views/account/FindPasswordView.vue'),
			meta: { notAuthRequired: true },
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
			path: '/booth',
			component: () => import('@/views/booth/BoothShootView.vue'),
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

router.beforeEach(async (to, from) => {
	const userStore = useUserStore();

	if (to.meta.emailRequired && !userStore.verifiedEmail) {
		await Swal.fire({ icon: 'error', title: '이메일 인증이 필요합니다.', width: 600 });
		return { name: 'login', replace: true };
	}
	if (to.meta.authRequired && !localStorage.getItem('accessToken')) {
		await Swal.fire({ icon: 'error', title: '로그인이 필요합니다.', width: 600 });
		return { name: 'login', replace: true };
	}
	if (to.meta.notAuthRequired && localStorage.getItem('accessToken')) {
		await Swal.fire({ icon: 'error', title: '올바르지 않은 접근입니다.', width: 600 });
		return { name: 'main', replace: true };
	}
});

export default router;
