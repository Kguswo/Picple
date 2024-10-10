import { computed, ref } from 'vue';
import { defineStore } from 'pinia';

export const usePhotoStore = defineStore(
	'photo',
	() => {
		const photoList = ref({}); // sessionId를 키로 가지는 객체로 변경

		const templateList = ref([
			{ row: 1, col: 1 },
			{ row: 2, col: 1 },
			{ row: 3, col: 1 },
			{ row: 4, col: 1 },
			{ row: 2, col: 2 },
		]);

		const draggedPhoto = ref(null);

		const templateColor = ref(true);

		const backgroundColor = computed(() => (templateColor.value ? 'white' : 'black'));
		const otherColor = computed(() => (templateColor.value ? 'black' : 'white'));

		const setPhotoList = (sessionId, photos) => {
			photoList.value[sessionId] = photos;
		};

		const addPhoto = (sessionId, photo) => {
			if (!photoList.value[sessionId]) {
				photoList.value[sessionId] = [];
			}
			photoList.value[sessionId].push(photo);
		};

		const clearPhotoList = (sessionId) => {
			if (photoList.value[sessionId]) {
				photoList.value[sessionId] = [];
			}
		};

		const getPhotosBySession = (sessionId) => {
			return photoList.value[sessionId] || [];
		};

		return {
			photoList,
			templateList,
			draggedPhoto,
			templateColor,
			backgroundColor,
			otherColor,
			addPhoto,
			getPhotosBySession,
			setPhotoList,
			clearPhotoList,
		};
	},
	{
		persist: {
			key: 'photo',
			paths: ['photoList'],
			storage: sessionStorage,
		},
	},
);
