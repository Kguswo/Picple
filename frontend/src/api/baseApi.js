import axios from 'axios';

const instance = axios.create({
	baseURL: import.meta.env.VUE_APP_API_URL,
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
		return error;
	}
};

const axiosPost = async (url, data) => {
	try {
		const response = await instance.post(url, data);
		return response.data;
	} catch (error) {
		return error;
	}
};

const axiosPatch = async (url, data) => {
	try {
		const response = await instance.patch(url, data);
		return response.data;
	} catch (error) {
		return error;
	}
};

const axiosDelete = async (url) => {
	try {
		const response = await instance.delete(url);
		return response.data;
	} catch (error) {
		return error;
	}
};

export { instance, axiosGet, axiosPost, axiosPatch, axiosDelete };
