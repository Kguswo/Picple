import instance from "@/api/baseApi";

const usersBaseUrl = instance.defaults.baseURL + "/users";

// 로그인
const loginApi = async (email, password) => {
	try {
		const response = await instance.post(`${usersBaseUrl}/login`, {
			email,
			password,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

// 회원가입
const signupApi = async (email, password, nickname) => {
	try {
		const response = await instance.post(`${usersBaseUrl}/sign-up`, {
			email,
			password,
			nickname,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

export { loginApi, signupApi };
