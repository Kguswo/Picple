import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("userStore", () => {
	const userInfo = ref({
		email: null,
		nickname: null,
	}); // 로그인한 유저의 정보

	const userEmail = ref(null); // 이메일 인증 페이지에서 인증한 이메일

	return {
		userInfo,
		userEmail,
	};
});
