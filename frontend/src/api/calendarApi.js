import { axiosAuth } from '@/api/baseApi';
import { formatDate } from '@/assets/js/date';

const calendarsBaseUrl = import.meta.env.VITE_API_CALENDAR;

const calendarMonthlyCountApi = (year, month, endDate) => {
	return axiosAuth.get(
		`${calendarsBaseUrl}/monthly-counts?monthlyStartDate=${formatDate(year, month, 1)}&monthlyEndDate=${formatDate(
			year,
			month,
			endDate,
		)}`,
	);
};

const calendarDailyListApi = (createdAt) => {
	return axiosAuth.get(`${calendarsBaseUrl}/daily?createdAt=${createdAt}`);
};

const calendarDownloadApi = (calendarId) => {
	return axiosAuth.post(`${calendarsBaseUrl}/download/${calendarId}`);
};

const calendarShareApi = (calendarId) => {
	return axiosAuth.post(`${calendarsBaseUrl}/share/${calendarId}`);
};

const calendarContentApi = (calendarId, content) => {
	return axiosAuth.post(`${calendarsBaseUrl}/${calendarId}?content=${content}`);
};

const calendarDeleteApi = (calendarId) => {
	return axiosAuth.delete(`${calendarsBaseUrl}/${calendarId}`);
};

export {
	calendarMonthlyCountApi,
	calendarDailyListApi,
	calendarDownloadApi,
	calendarShareApi,
	calendarContentApi,
	calendarDeleteApi,
};
