import axios from "axios";

const instance = axios.create({
	baseURL: "http://localhost:8080",
});

// instance.interceptors.request.use(
// 	(config) => {
// 		const accessToken = localStorage.getItem("accessToken");
// 		config.headers["X-ACCESS-TOKEN"] = `${accessToken}`;
// 		return config;
// 	},
// 	(error) => Promise.reject(error)
// );

export default instance;
