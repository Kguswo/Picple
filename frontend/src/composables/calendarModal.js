import { ref } from 'vue';

export const formatDate = (year, month, day) => {
	return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
};

export default function useCalendarModal(photos) {
	const currentIndex = ref(0);
	const currentPhotoExpanded = ref(false);

	const prevPhoto = () => {
		currentIndex.value = (currentIndex.value - 1 + photos.value.length) % photos.value.length;
	};

	const nextPhoto = () => {
		currentIndex.value = (currentIndex.value + 1) % photos.value.length;
	};

	const getPrevPhoto = () => {
		const prevIndex = (currentIndex.value - 1 + photos.value.length) % photos.value.length;
		return photos.value[prevIndex];
	};

	const getCurrentPhoto = () => {
		return photos.value[currentIndex.value];
	};

	const getNextPhoto = () => {
		const nextIndex = (currentIndex.value + 1) % photos.value.length;
		return photos.value[nextIndex];
	};

	const toggleCurrentPhoto = () => {
		currentPhotoExpanded.value = !currentPhotoExpanded.value;
	};

	const getPhotoClass = (photo) => {
		if (photo.type === 'portrait') {
			return 'portrait';
		} else if (photo.type === 'landscape') {
			return 'landscape';
		}
		return '';
	};
	return {
		currentPhotoExpanded,
		prevPhoto,
		nextPhoto,
		getPrevPhoto,
		getCurrentPhoto,
		getNextPhoto,
		toggleCurrentPhoto,
		getPhotoClass,
	};
}
