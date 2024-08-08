import axios from 'axios';
import Swal from 'sweetalert2';
import router from '@/router';

export const axiosAuth = axios.create({ withCredentials: true });

axios.interceptors.response.use(
	async (response) => {
		return response;
	},
	async (error) => {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return new Promise(() => {});
	},
);

axiosAuth.interceptors.request.use(
	(config) => {
		config.headers['X-ACCESS-TOKEN'] = localStorage.getItem('accessToken');
		return config;
	},
	(error) => {},
);

axiosAuth.interceptors.response.use(
	async (response) => {
		const { config, data } = response;

		if (data.code == import.meta.env.VITE_CODE_INVALID_JWT) {
			const accessToken = await tokenRefresh();
			if (accessToken) {
				localStorage.setItem('accessToken', accessToken);
				config.headers['X-ACCESS-TOKEN'] = accessToken;
				return axiosAuth(config);
			}
			await Swal.fire({ icon: 'error', title: '로그인이 필요합니다.', width: 600 });
			router.push({ name: 'login' });
			return new Promise(() => {});
		}
		return response;
	},
	async (error) => {
		await Swal.fire({ icon: 'error', title: '서버에 문제가 발생했습니다[503].', width: 600 });
		return new Promise(() => {});
	},
);

const tokenRefresh = async () => {
	const { data } = await axios.post(
		`${import.meta.env.VITE_API_REFRESH}`,
		{},
		{
			withCredentials: true,
		},
	);
	if (data.code == import.meta.env.VITE_CODE_SUCCESS) {
		return data.result;
	}
	return null;
};

export const alertResult = async (isSuccess, title) => {
	await Swal.fire({ icon: isSuccess ? 'success' : 'error', title, width: 700 });
};

export const alertConfirm = async (title) => {
	return await Swal.fire({
		title,
		confirmButtonText: `Continue&nbsp;<i class="fa fa-arrow-right"></i>`,
		showCancelButton: true,
		width: 700,
	});
};

export const alertCheckBox = async (title, inputPlaceholder, inputValidator) => {
	return await Swal.fire({
		title,
		input: 'checkbox',
		inputValue: 0,
		inputPlaceholder,
		confirmButtonText: `Continue&nbsp;<i class="fa fa-arrow-right"></i>`,
		showCancelButton: true,
		inputValidator: (result) => {
			return !result && inputValidator;
		},
	});
};
