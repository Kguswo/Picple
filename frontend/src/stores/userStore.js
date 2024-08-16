import { defineStore } from 'pinia';
import { ref } from 'vue';
import { jwtDecode } from 'jwt-decode'; // 'jwt-decode'에서 중괄호는 제거해야 합니다.

export const useUserStore = defineStore(
	'userStore',
	() => {
		const userEmail = ref('');
		const userNickname = ref('');
		const verifiedEmail = ref('');
		const isLogined = ref(false);

		const setUserInfo = (accessToken) => {
			localStorage.setItem('accessToken', accessToken);
			const token = jwtDecode(accessToken);
			userEmail.value = token.sub;
			userNickname.value = token.nickname;
			isLogined.value = true;
		};

		const resetUserInfo = () => {
			localStorage.removeItem('accessToken');
			userEmail.value = '';
			userNickname.value = '';
			isLogined.value = false;
		};

		const changeNickname = (nickname) => {
			userNickname.value = nickname;
		};

		return {
			userEmail,
			userNickname,
			verifiedEmail,
			isLogined,
			setUserInfo,
			resetUserInfo,
			changeNickname,
		};
	},
	{
		persist: {
			key: 'userInfo',
			paths: ['userEmail', 'userNickname', 'isLogined'],
		},
	},
);
