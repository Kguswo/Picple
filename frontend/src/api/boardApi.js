import { instance } from '@/api/baseApi';

const boardsBaseUrl = instance.defaults.baseURL + '/boards';
const likesBaseUrl = instance.defaults.baseURL + '/likes';

const boardListApi = () => {
	return instance.get(`${boardsBaseUrl}`);
};

const boardSortApi = (nickname, criteria, sortDirection) => {
	return instance.get(
		`${boardsBaseUrl}/sorted?nickname=${nickname}&criteria=${criteria}&sortDirection=${sortDirection}`,
	);
};

const boardDeleteApi = (boardId) => {
	return instance.delete(`${boardsBaseUrl}/${boardId}`);
};

const boardLikeApi = (boardId) => {
	return instance.patch(`${likesBaseUrl}/${boardId}`);
};

export { boardListApi, boardSortApi, boardDeleteApi, boardLikeApi };
