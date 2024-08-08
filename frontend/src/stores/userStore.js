import { defineStore } from 'pinia';
import { ref } from 'vue';
import { jwtDecode } from 'jwt-decode';

export const useUserStore = defineStore(
	'userStore',
	() => {
		const userEmail = ref('');
		const userNickname = ref('');
		const verifiedEmail = ref('');

		const setUserInfo = (accessToken) => {
			localStorage.setItem('accessToken', accessToken);
			const token = jwtDecode(accessToken);
			userEmail.value = token.sub;
			userNickname.value = token.nickname;
		};

		const resetUserInfo = () => {
			userEmail.value = '';
			userNickname.value = '';
			localStorage.removeItem('accessToken');
		};

		const changeNickname = (nickname) => {
			userNickname.value = nickname;
		};

		return {
			userEmail,
			userNickname,
			verifiedEmail,
			setUserInfo,
			resetUserInfo,
			changeNickname,
		};
	},
	{
		persist: {
			key: 'userInfo',
			paths: ['userEmail', 'userNickname'],
		},
	},
);
