import { ref } from 'vue';
import photo1 from '@/assets/img/calendar/portrait_48.png';
import photo2 from '@/assets/img/calendar/landscape_84.png';
import photo3 from '@/assets/img/calendar/portrait_messi3.png';
import photo4 from '@/assets/img/calendar/portrait_jj4.jpeg';
import photo5 from '@/assets/img/calendar/landscape_lion.png';

const allPhotos = [
	{ date: '2024-07-10', src: photo1, type: 'portrait' },
	{ date: '2024-07-12', src: photo2, type: 'landscape' },
	{ date: '2024-07-12', src: photo3, type: 'portrait' },
	{ date: '2024-07-12', src: photo4, type: 'portrait' },
	{ date: '2024-07-10', src: photo5, type: 'landscape' },
];

export default function useCalendarModal(props, emit) {
	const photos = ref([]);
	const currentIndex = ref(0);
	const currentPhotoExpanded = ref(false);

	const updatePhotos = (date) => {
		const filteredPhotos = allPhotos.filter((photo) => photo.date === date);
		if (filteredPhotos.length === 0) {
			alert('해당 날짜에는 이미지가 없습니다.');
			emit('close');
		} else {
			photos.value = filteredPhotos;
			currentIndex.value = 0;
		}
	};

	const close = () => {
		emit('close');
	};

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
		photos,
		currentIndex,
		currentPhotoExpanded,
		close,
		prevPhoto,
		nextPhoto,
		getPrevPhoto,
		getCurrentPhoto,
		getNextPhoto,
		toggleCurrentPhoto,
		getPhotoClass,
		updatePhotos,
	};
}

export { allPhotos };
