import { instance, axiosGet, axiosPost, axiosDelete } from '@/api/baseApi';

const calendarsBaseUrl = instance.defaults.baseURL + '/calendars';

const calendarMonthlyCountApi = async (startDate, endDate) => {
	return await axiosGet(`${calendarsBaseUrl}/monthly-counts?monthlyStartDate=${startDate}&monthlyEndDate=${endDate}`);
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
