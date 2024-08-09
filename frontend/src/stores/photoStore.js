import { ref } from 'vue';
import { defineStore } from 'pinia';

export const usePhotoStore = defineStore('photo', () => {
	const photoList = ref([]);

	const setPhotoList = (photos) => {
		photoList.value = photos;
	};

	const clearPhotoList = () => {
		photoList.value = [];
	};

	return {
		photoList,
		setPhotoList,
		clearPhotoList,
	};
});
