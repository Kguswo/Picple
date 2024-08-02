import { instance, axiosGet, axiosPatch, axiosDelete } from '@/api/baseApi';

const boardsBaseUrl = instance.defaults.baseURL + '/boards';
const likesBaseUrl = instance.defaults.baseURL + '/likes';

const boardListApi = async () => {
	return await axiosGet(`${boardsBaseUrl}`);
};

const boardSortApi = async (criteria) => {
	return await axiosGet(`${boardsBaseUrl}/sorted/${criteria}`);
};

const boardSearchApi = async (nickname) => {
	return await axiosGet(`${boardsBaseUrl}/user/${nickname}`);
};

const boardDeleteApi = async (boardId) => {
	return await axiosDelete(`${boardsBaseUrl}/${boardId}`);
};

const boardLikeApi = async (boardId) => {
	return await axiosPatch(`${likesBaseUrl}/${boardId}`);
};

export { boardListApi, boardSortApi, boardSearchApi, boardDeleteApi, boardLikeApi };
