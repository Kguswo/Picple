import { axiosAuth } from '@/api/baseApi';

const backgroundBaseUrl = import.meta.env.VITE_API_BACKGROUND;

export const createAiBackgroundApi = (prompt) => {
	return axiosAuth.post(`${backgroundBaseUrl}/ai`, { prompt });
};

export const getBackgroundApi = () => {
	return axiosAuth.get(`${backgroundBaseUrl}/user`);
};
