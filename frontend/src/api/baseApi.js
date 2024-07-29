import axios from "axios";

export const instance = axios.create({
	baseURL: "/picple",
	timeout: 10000,
});
