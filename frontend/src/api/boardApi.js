import { axiosAuth } from '@/api/baseApi';

const boardsBaseUrl = import.meta.env.VITE_API_BOARD;
const likesBaseUrl = import.meta.env.VITE_API_LIKE;

const boardDeleteApi = (boardId) => {
	return axiosAuth.delete(`${boardsBaseUrl}/${boardId}`);
};

const boardLikeApi = (boardId) => {
	return axiosAuth.patch(`${likesBaseUrl}/${boardId}`);
};

const boardListApi = (nickname, paging) => {
	return axiosAuth.get(
		`${boardsBaseUrl}?nickname=${nickname}&page=${paging.page}&size=${paging.size}&sort=${paging.sort}`,
	);
};

export { boardListApi, boardDeleteApi, boardLikeApi };
