import { instance, axiosGet, axiosPost, axiosDelete } from '@/api/baseApi';

const calendarsBaseUrl = instance.defaults.baseURL + '/calendars';

const calendarMonthlyCountApi = async (year, month, endDate) => {
	return await axiosGet(
		`${calendarsBaseUrl}/monthly-counts?monthlyStartDate=${year}-${String(month).padStart(
			2,
			'0',
		)}-01&monthlyEndDate=${year}-${String(month).padStart(2, '0')}-${String(endDate).padStart(2, '0')}`,
	);
};

const calendarDailyListApi = async (createdAt) => {
	return await axiosGet(`${calendarsBaseUrl}/daily?createdAt=${createdAt}`);
};

const calendarShareApi = async (calendarId) => {
	return await axiosPost(`${calendarsBaseUrl}/share/${calendarId}`);
};

const calendarContentApi = async (calendarId, content) => {
	return await axiosPost(`${calendarsBaseUrl}/${calendarId}?content=${content}`);
};

const calendarDeleteApi = async (calendarId) => {
	return await axiosDelete(`${calendarsBaseUrl}/${calendarId}`);
};

export { calendarMonthlyCountApi, calendarDailyListApi, calendarShareApi, calendarContentApi, calendarDeleteApi };
