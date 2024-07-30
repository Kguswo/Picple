import instance from "@/api/baseApi";

const usersBaseUrl = instance.defaults.baseURL + "/users";

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
