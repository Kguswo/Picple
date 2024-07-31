import instance from '@/api/baseApi';

const usersBaseUrl = instance.defaults.baseURL + '/users';

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

// 이메일 인증번호 전송
const sendCertNumberApi = async (email) => {
	try {
		const response = await instance.post(`${usersBaseUrl}/mail`, {
			email,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

// 이메일 인증번호 확인
const verifyCertNumberApi = async (email, certNumber) => {
	try {
		const response = await instance.post(`${usersBaseUrl}/mailcheck`, {
			email,
			authNumber: certNumber,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

// 회원정보 수정
const modifyAccountApi = async (nickname) => {
	try {
		const response = await instance.patch(`${usersBaseUrl}/modify/nickname`, {
			nickname,
		});
		return response.data;
	} catch (error) {
		return error;
	}
};

// 로그아웃
const logoutApi = async () => {
	try {
		const response = await instance.post(`${usersBaseUrl}/logout`);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 회원탈퇴
const deleteAccountApi = async () => {
	try {
		const response = await instance.delete(`${usersBaseUrl}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

export { loginApi, signupApi, sendCertNumberApi, verifyCertNumberApi, modifyAccountApi, deleteAccountApi, logoutApi };
