import { instance, axiosPost, axiosPatch, axiosDelete } from '@/api/baseApi';

const usersBaseUrl = instance.defaults.baseURL + '/users';

// 로그인
const loginApi = async (email, password) => {
	return await axiosPost(`${usersBaseUrl}/login`, { email, password });
};

// 회원가입
const signupApi = async (email, password, nickname) => {
	return await axiosPost(`${usersBaseUrl}/sign-up`, {
		email,
		password,
		nickname,
	});
};

// 이메일 인증번호 전송
const sendAuthNumberApi = async (email) => {
	return await axiosPost(`${usersBaseUrl}/mail`, { email });
};

// 이메일 인증번호 확인
const verifyAuthNumberApi = async (email, authNumber) => {
	return await axiosPost(`${usersBaseUrl}/mailcheck`, { email, authNumber });
};

// 회원정보 수정
const modifyAccountApi = async (nickname) => {
	return await axiosPatch(`${usersBaseUrl}/modify/nickname`, { nickname });
};

// 비밀번호 수정
const modifyPasswordApi = async (oldPassword, newPassword) => {
	return await axiosPatch(`${usersBaseUrl}/modify/password`, { oldPassword, newPassword });
};

// 로그아웃
const logoutApi = async () => {
	return await axiosPost(`${usersBaseUrl}/logout`);
};

// 회원탈퇴
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
