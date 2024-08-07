import axios from 'axios';
import Swal from 'sweetalert2';
import router from '@/router';

export const instance = axios.create({
	baseURL: import.meta.env.VITE_API_URL,
	withCredentials: true,
});

instance.interceptors.request.use(
	(config) => {
		config.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`;
		return config;
	},
	(error) => {
		return Promise.reject(error);
	},
);

instance.interceptors.response.use(
	async ({ data }) => {
		console.log(data);
		if (data.code == import.meta.env.VITE_NOT_FOUND_USER) {
			await Swal.fire({ icon: 'error', title: '아이디 또는 비밀번호가 일치하지 않습니다.', width: 700 });
			return new Promise(() => {});
		}
		if (data.code == import.meta.env.VITE_INVALID_JWT) {
			const response = await instance.post(`${instance.defaults.baseURL}/users/refresh-token`);
			console.log(response);
		}
		return data;
	},
	async (error) => {
		const { config, response } = error;
		console.log(config, response);
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return new Promise(() => {});
	},
);
