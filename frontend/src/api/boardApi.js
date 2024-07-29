import { instance } from "@/api/baseApi";

const boardListApi = async () => {
	try {
		const response = await instance.get("/board");
		return response.data;
	} catch (error) {
		return error;
	}
};

export { boardListApi };
