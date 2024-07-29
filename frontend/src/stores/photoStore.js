import { defineStore } from "pinia";

export const usePhotoStore = defineStore("photo", () => {
	const photoList = [];

	const setPhotoList = (photos) => {
		photoList = photos;
	};

	const clearPhotoList = () => {
		photoList = [];
	};

	return {
		photoList,
		setPhotoList,
		clearPhotoList,
	};
});
