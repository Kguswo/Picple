import instance from "@/api/baseApi";

const userBaseUrl = instance.defaults.baseURL + "/user";

const loginApi = async (email, password) => {
	try {
		const response = await instance.post(`${userBaseUrl}/login`, {
			email,
			password,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

const signupApi = async (email, password, nickname) => {
	try {
		const response = await instance.post(`${userBaseUrl}/sign-up`, {
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
