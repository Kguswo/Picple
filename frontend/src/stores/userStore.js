import { defineStore } from 'pinia';
import { ref } from 'vue';
import { jwtDecode } from 'jwt-decode';

export const useUserStore = defineStore(
	'userStore',
	() => {
		const userEmail = ref('');
		const userNickname = ref('');
		const verifiedEmail = ref('');

		const setAccessToken = (accessToken) => {
			localStorage.setItem('accessToken', accessToken);
			const token = jwtDecode(accessToken);
			userEmail.value = token.sub;
			userNickname.value = token.nickname;
		};

		const resetUser = () => {
			userEmail.value = '';
			userNickname.value = '';
			localStorage.removeItem('accessToken');
		};

		const changeNickname = (nickname) => {
			user.value.nickname = nickname;
		};

		return {
			userEmail,
			userNickname,
			verifiedEmail,
			setAccessToken,
			resetUser,
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
