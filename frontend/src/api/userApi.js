import { instance } from '@/api/baseApi';

const usersBaseUrl = instance.defaults.baseURL + '/users';

const loginApi = (email, password) => {
	return instance.post(`${usersBaseUrl}/login`, { email, password });
};

const signupApi = (email, password, nickname) => {
	return instance.post(`${usersBaseUrl}/sign-up`, {
		email,
		password,
		nickname,
	});
};

const sendAuthNumberApi = (email) => {
	return instance.post(`${usersBaseUrl}/mail`, { email });
};

const verifyAuthNumberApi = (email, authNumber) => {
	return instance.post(`${usersBaseUrl}/mailcheck`, { email, authNumber });
};

const modifyAccountApi = (nickname) => {
	return instance.patch(`${usersBaseUrl}/modify/nickname`, { nickname });
};

const modifyPasswordApi = (oldPassword, newPassword) => {
	return instance.patch(`${usersBaseUrl}/modify/password`, { oldPassword, newPassword });
};

const logoutApi = () => {
	return instance.post(`${usersBaseUrl}/logout`);
};

const deleteAccountApi = () => {
	return instance.delete(`${usersBaseUrl}`);
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
