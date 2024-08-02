import axios from 'axios';
import Swal from 'sweetalert2';

const instance = axios.create({
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
	(error) => Promise.reject(error),
);

const axiosGet = async (url) => {
	try {
		const response = await instance.get(url);
		return response.data;
	} catch (error) {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return;
	}
};

const axiosPost = async (url, data) => {
	try {
		const response = await instance.post(url, data);
		return response.data;
	} catch (error) {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return;
	}
};

const axiosPatch = async (url, data) => {
	try {
		const response = await instance.patch(url, data);
		return response.data;
	} catch (error) {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return;
	}
};

const axiosDelete = async (url) => {
	try {
		const response = await instance.delete(url);
		return response.data;
	} catch (error) {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return;
	}
};

export { instance, axiosGet, axiosPost, axiosPatch, axiosDelete };
