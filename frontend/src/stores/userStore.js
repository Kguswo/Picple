import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("userStore", () => {
	const user = ref({
		userId: "",
		nickname: "",
	});

	const isLogin = () => {
		return user.value.userId;
	};

	const login = (userId, nickname) => {
		user.value.userId = userId;
		user.value.nickname = nickname;
	};

	return {
		user,
		isLogin,
		login,
	};
});
