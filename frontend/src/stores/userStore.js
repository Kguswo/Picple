import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore(
	'userStore',
	() => {
		const user = ref({
			email: '',
			nickname: '',
		});

		const verifiedEmail = ref('');

		const setUser = (email, nickname) => {
			user.value.email = email;
			user.value.nickname = nickname;
		};

		const resetUser = () => {
			user.value.email = '';
			user.value.nickname = '';
			localStorage.removeItem('accessToken');
			localStorage.removeItem('user');
		};

		const changeNickname = (nickname) => {
			user.value.nickname = nickname;
		};

		return {
			user,
			verifiedEmail,
			setUser,
			resetUser,
			changeNickname,
		};
	},
	{
		persist: {
			key: 'user',
			paths: ['user'],
		},
	},
);
