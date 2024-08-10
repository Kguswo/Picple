import { axiosAuth } from '@/api/baseApi';
import axios from 'axios';

const usersBaseUrl = import.meta.env.VITE_API_USER;

const loginApi = (email, password) => {
	return axios.post(`${usersBaseUrl}/login`, { email, password }, { withCredentials: true });
};

const signupApi = (email, password, nickname) => {
	return axios.post(`${usersBaseUrl}/sign-up`, {
		email,
		password,
		nickname,
	});
};

const sendAuthNumberByFindApi = (email) => {
	return axios.post(`${usersBaseUrl}/mail/find`, { email });
};

const sendAuthNumberApi = (email) => {
	return axios.post(`${usersBaseUrl}/mail`, { email });
};

const verifyAuthNumberApi = (email, authNumber) => {
	return axios.post(`${usersBaseUrl}/mailcheck`, { email, authNumber });
};

const modifyAccountApi = (nickname) => {
	return axiosAuth.patch(`${usersBaseUrl}/modify/nickname`, { nickname });
};

const findPasswordApi = (email, password) => {
	return axios.patch(`${usersBaseUrl}/reset-password`, { email, password });
};

const modifyPasswordApi = (oldPassword, newPassword) => {
	return axiosAuth.patch(`${usersBaseUrl}/modify/password`, { oldPassword, newPassword });
};

const logoutApi = (email) => {
	return axiosAuth.post(`${usersBaseUrl}/logout`, {
		email,
	});
};

const deleteAccountApi = () => {
	return axiosAuth.delete(`${usersBaseUrl}`);
};

export {
	loginApi,
	signupApi,
	sendAuthNumberByFindApi,
	sendAuthNumberApi,
	verifyAuthNumberApi,
	modifyAccountApi,
	findPasswordApi,
	modifyPasswordApi,
	deleteAccountApi,
	logoutApi,
};
