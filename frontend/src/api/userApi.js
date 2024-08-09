import { instance, axiosPost, axiosPatch, axiosDelete } from '@/api/baseApi';

const usersBaseUrl = instance.defaults.baseURL + '/users';

const loginApi = async (email, password) => {
	return await axiosPost(`${usersBaseUrl}/login`, { email, password });
};

const signupApi = async (email, password, nickname) => {
	return await axiosPost(`${usersBaseUrl}/sign-up`, {
		email,
		password,
		nickname,
	});
};

const sendAuthNumberApi = async (email) => {
	return await axiosPost(`${usersBaseUrl}/mail`, { email });
};

const verifyAuthNumberApi = async (email, authNumber) => {
	return await axiosPost(`${usersBaseUrl}/mailcheck`, { email, authNumber });
};

const modifyAccountApi = async (nickname) => {
	return await axiosPatch(`${usersBaseUrl}/modify/nickname`, { nickname });
};

const modifyPasswordApi = async (oldPassword, newPassword) => {
	return await axiosPatch(`${usersBaseUrl}/modify/password`, { oldPassword, newPassword });
};

const logoutApi = async () => {
	return await axiosPost(`${usersBaseUrl}/logout`);
};

const deleteAccountApi = async () => {
	return await axiosDelete(`${usersBaseUrl}`);
};

export {
	loginApi,
	signupApi,
	sendAuthNumberApi,
	verifyAuthNumberApi,
	modifyAccountApi,
	modifyPasswordApi,
	deleteAccountApi,
	logoutApi,
};
