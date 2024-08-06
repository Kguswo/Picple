import { instance } from '@/api/baseApi';
import { formatDate } from '@/assets/js/date';

const calendarsBaseUrl = instance.defaults.baseURL + '/calendars';

const calendarMonthlyCountApi = (year, month, endDate) => {
	return instance.get(
		`${calendarsBaseUrl}/monthly-counts?monthlyStartDate=${formatDate(year, month, 1)}&monthlyEndDate=${formatDate(
			year,
			month,
			endDate,
		)}`,
	);
};

const calendarDailyListApi = (createdAt) => {
	return instance.get(`${calendarsBaseUrl}/daily?createdAt=${createdAt}`);
};

const calendarDownloadApi = (calendarId) => {
	// return instance.get(`${calendarsBaseUrl}/photoResources?calendarId=${calendarId}`);
};

const calendarShareApi = (calendarId) => {
	return instance.post(`${calendarsBaseUrl}/share/${calendarId}`);
};

const calendarContentApi = (calendarId, content) => {
	return instance.post(`${calendarsBaseUrl}/${calendarId}?content=${content}`);
};

const calendarDeleteApi = (calendarId) => {
	return instance.delete(`${calendarsBaseUrl}/${calendarId}`);
};

export {
	calendarMonthlyCountApi,
	calendarDailyListApi,
	calendarDownloadApi,
	calendarShareApi,
	calendarContentApi,
	calendarDeleteApi,
};
