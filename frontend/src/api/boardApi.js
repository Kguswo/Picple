import instance from "@/api/baseApi";

const boardsBaseUrl = instance.defaults.baseURL + "/boards";
const likesBaseUrl = instance.defaults.baseURL + "/likes";

// 게시판 전체 조회
const boardListApi = async () => {
	try {
		const response = await instance.get(`${boardsBaseUrl}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 게시판 정렬 조회
const boardSortApi = async (criteria) => {
	try {
		const response = await instance.get(
			`${boardsBaseUrl}/sorted/${criteria}`
		);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 게시판 닉네임 검색 조회
const boardSearchApi = async (nickname) => {
	try {
		const response = await instance.get(
			`${boardsBaseUrl}/user/${nickname}`
		);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 게시글 삭제
const boardDeleteApi = async (boardId) => {
	try {
		const response = await instance.delete(`${boardsBaseUrl}/${boardId}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 게시글 좋아요
const boardLikeApi = async (boardId) => {
	try {
		const response = await instance.patch(`${likesBaseUrl}/${boardId}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

export {
	boardListApi,
	boardSortApi,
	boardSearchApi,
	boardDeleteApi,
	boardLikeApi,
};
