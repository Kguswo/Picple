import { axiosAuth } from '@/api/baseApi';

const boardsBaseUrl = import.meta.env.VITE_API_BOARD;
const likesBaseUrl = import.meta.env.VITE_API_LIKE;

const boardListApi = () => {
	return axiosAuth.get(`${boardsBaseUrl}`);
};

const boardSortApi = (nickname, criteria, sortDirection) => {
	return axiosAuth.get(
		`${boardsBaseUrl}/sorted?nickname=${nickname}&criteria=${criteria}&sortDirection=${sortDirection}`,
	);
};

const boardDeleteApi = (boardId) => {
	return axiosAuth.delete(`${boardsBaseUrl}/${boardId}`);
};

const boardLikeApi = (boardId) => {
	return axiosAuth.patch(`${likesBaseUrl}/${boardId}`);
};

export { boardListApi, boardSortApi, boardDeleteApi, boardLikeApi };
