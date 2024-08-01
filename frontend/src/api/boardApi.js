import { instance, axiosGet, axiosPatch, axiosDelete } from '@/api/baseApi';

const boardsBaseUrl = instance.defaults.baseURL + '/boards';
const likesBaseUrl = instance.defaults.baseURL + '/likes';

// 게시판 전체 조회
const boardListApi = async () => {
	return await axiosGet(`${boardsBaseUrl}`);
};

// 게시판 정렬 조회
const boardSortApi = async (criteria) => {
	return await axiosGet(`${boardsBaseUrl}/sorted/${criteria}`);
};

// 게시판 닉네임 검색 조회
const boardSearchApi = async (nickname) => {
	return await axiosGet(`${boardsBaseUrl}/user/${nickname}`);
};

// 게시글 삭제
const boardDeleteApi = async (boardId) => {
	return await axiosDelete(`${boardsBaseUrl}/${boardId}`);
};

// 게시글 좋아요 / 좋아요 취소
const boardLikeApi = async (boardId) => {
	return await axiosPatch(`${likesBaseUrl}/${boardId}`);
};

export { boardListApi, boardSortApi, boardSearchApi, boardDeleteApi, boardLikeApi };
