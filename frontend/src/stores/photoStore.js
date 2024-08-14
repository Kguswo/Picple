import { computed, ref } from 'vue';
import { defineStore } from 'pinia';

export const usePhotoStore = defineStore('photo', () => {
	const photoList = ref([
		'https://picsum.photos/200/300?random=1',
		'https://picsum.photos/200/300?random=2',
		'https://picsum.photos/200/300?random=3',
		'https://picsum.photos/200/300?random=4',
		'https://picsum.photos/200/300?random=5',
		'https://picsum.photos/200/300?random=6',
		'https://picsum.photos/200/300?random=7',
	]);

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

	const setPhotoList = (photos) => {
		photoList.value = photos;
	};

	const clearPhotoList = () => {
		photoList.value = [];
	};

	return {
		photoList,
		templateList,
		draggedPhoto,
		templateColor,
		backgroundColor,
		otherColor,
		setPhotoList,
		clearPhotoList,
	};
});
