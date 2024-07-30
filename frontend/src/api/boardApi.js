import instance from "@/api/baseApi";

const boardBaseUrl = instance.defaults.baseURL + "/board";

const boardListApi = async () => {
	try {
		const response = await instance.get(`${boardBaseUrl}`);
		return response.data;
	} catch (error) {
		return error;
	}
};

export { boardListApi };
