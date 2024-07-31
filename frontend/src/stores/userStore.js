import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore(
	"userStore",
	() => {
		const userInfo = ref({
			email: "",
			nickname: "",
		}); // 로그인한 유저의 정보

		const userEmail = ref(""); // 이메일 인증 페이지에서 인증한 이메일

		const setUserInfo = (email, nickname) => {
			userInfo.value.email = email;
			userInfo.value.nickname = nickname;
		};

		const resetUserInfo = () => {
			userInfo.value.email = "";
			userInfo.value.nickname = "";
		};

		return {
			userInfo,
			userEmail,
			setUserInfo,
			resetUserInfo,
		};
	},
	{
		persist: true,
	}
);
