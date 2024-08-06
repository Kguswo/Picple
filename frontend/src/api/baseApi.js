import axios from 'axios';
import Swal from 'sweetalert2';
import router from '@/router';

export const instance = axios.create({
	baseURL: import.meta.env.VITE_API_URL,
});

instance.interceptors.request.use(
	(config) => {
		const accessToken = localStorage.getItem('accessToken');
		if (accessToken) {
			config.headers['X-ACCESS-TOKEN'] = accessToken;
		}
		return config;
	},
	(error) => {
		return Promise.reject(error);
	},
);

instance.interceptors.response.use(
	(response) => {
		return response.data;
	},
	async (error) => {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return Promise.reject(error);
	},
);
