import { instance } from '@/api/baseApi';

const calendarsBaseUrl = instance.defaults.baseURL + '/calendars';

const calendarMonthlyCountApi = (year, month, endDate) => {
	return instance.get(
		`${calendarsBaseUrl}/monthly-counts?monthlyStartDate=${year}-${String(month).padStart(
			2,
			'0',
		)}-01&monthlyEndDate=${year}-${String(month).padStart(2, '0')}-${String(endDate).padStart(2, '0')}`,
	);
};

const calendarDailyListApi = (createdAt) => {
	return instance.get(`${calendarsBaseUrl}/daily?createdAt=${createdAt}`);
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

export { calendarMonthlyCountApi, calendarDailyListApi, calendarShareApi, calendarContentApi, calendarDeleteApi };
