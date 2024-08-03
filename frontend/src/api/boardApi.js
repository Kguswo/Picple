import { instance } from '@/api/baseApi';

const boardsBaseUrl = instance.defaults.baseURL + '/boards';
const likesBaseUrl = instance.defaults.baseURL + '/likes';

const boardListApi = () => {
	return instance.get(`${boardsBaseUrl}`);
};

const boardSortApi = (criteria) => {
	return instance.get(`${boardsBaseUrl}/sorted/${criteria}`);
};

const boardSearchApi = (nickname) => {
	return instance.get(`${boardsBaseUrl}/user/${nickname}`);
};

const boardDeleteApi = (boardId) => {
	return instance.delete(`${boardsBaseUrl}/${boardId}`);
};

const boardLikeApi = (boardId) => {
	return instance.patch(`${likesBaseUrl}/${boardId}`);
};

export { boardListApi, boardSortApi, boardSearchApi, boardDeleteApi, boardLikeApi };
