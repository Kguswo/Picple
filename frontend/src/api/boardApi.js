import instance from "@/api/baseApi";

const boardBaseUrl = instance.defaults.baseURL + "/board";

// 게시판 전체 조회
const boardListApi = async () => {
	try {
		const response = await instance.get(`${boardBaseUrl}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

// 게시판 정렬
const boardSortApi = async (criteria) => {
	try {
		const response = await instance.get(
			`${boardBaseUrl}/sorted/${criteria}`
		);
		return response.data;
	} catch (error) {
		return error;
	}
};

export { boardListApi, boardSortApi };
