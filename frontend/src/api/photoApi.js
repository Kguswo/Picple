import { axiosAuth } from '@/api/baseApi';

const photosBaseUrl = import.meta.env.VITE_API_PHOTO;

export const savePhotoApi = (file) => {
	return axiosAuth.post(`${photosBaseUrl}`, file, {
		header: {
			'Content-Type': 'multipart/form-data',
		},
	});
};
