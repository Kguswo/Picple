<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { usePhotoStore } from '@/stores/photoStore';

import temp_1x1_4x3_479x360 from '@/assets/img/template/temp_1x1_4x3_479x360.jpg';
import temp_1x2_4x5_288x360 from '@/assets/img/template/temp_1x2_4x5_288x360.jpg';
import temp_1x3_3x4_270x360 from '@/assets/img/template/temp_1x3_3x4_270x360.png';
import temp_2x2_4x3_481x360 from '@/assets/img/template/temp_2x2_4x3_481x360.jpg';

const router = useRouter();
const route = useRoute();
const photoStore = usePhotoStore();

const selectedTemplate = ref('all');
const selectedImage = ref(null);

const templates = [
	{ text: '전체', key: 'all' },
	{ text: '1장', key: '1' },
	{ text: '2장', key: '2' },
	{ text: '3장', key: '3' },
	{ text: '4장', key: '4' },
];

const templateImages = {
	1: [temp_1x1_4x3_479x360],
	2: [temp_1x2_4x5_288x360],
	3: [temp_1x3_3x4_270x360],
	4: [temp_2x2_4x3_481x360],
};

const photos = photoStore.photoList;
console.log('BoothTemplateView에서 불러온 이미지 리스트:', photos);

const selectTemplate = (template) => {
	console.log(`템플릿 선택됨: ${template.key}`);
	selectedTemplate.value = template.key;
	selectedImage.value = null;
};

const selectImage = (image) => {
	console.log(`이미지 선택됨: ${image}`);
	selectedImage.value = image;
};

const shuffleArray = (array) => {
	let shuffledArray = array.slice();
	for (let i = shuffledArray.length - 1; i > 0; i--) {
		const j = Math.floor(Math.random() * (i + 1));
		[shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
	}
	return shuffledArray;
};

const imagesToShow = computed(() => {
	let images = [];
	if (selectedTemplate.value === 'all') {
		images = Object.values(templateImages).flat();
	} else {
		images = templateImages[selectedTemplate.value] || [];
	}
	return shuffleArray(images);
});

watch(
	selectedTemplate,
	(newVal) => {
		if (newVal === 'all') {
			imagesToShow.value = shuffleArray(Object.values(templateImages).flat());
		}
	},
	{ immediate: true },
);

const extractInfoFromFilename = (filename) => {
	const parts = filename.split('_');
	const [photoCount, ratio, size] = parts.slice(1);
	const [width, height] = size.split('x').map(Number);
	return {
		photoCount: photoCount.split('x').map(Number),
		ratio: ratio.split('x').map(Number),
		size: { width, height },
	};
};

const goToNext = () => {
	if (selectedImage.value) {
		const imageInfo = extractInfoFromFilename(selectedImage.value);
		console.log(`다음 화면으로 이동: 템플릿: ${selectedTemplate.value}, 이미지: ${selectedImage.value}`);
		console.log('다음 화면으로 이동할 때 이미지 리스트:', photos);
		router.push({
			name: 'insertImg',
			params: {
				templateKey: selectedTemplate.value,
			},
			query: {
				selectedImage: encodeURIComponent(selectedImage.value),
				imageInfo: JSON.stringify(imageInfo),
			},
		});
	}
};

const goToPrevious = () => {
	photoStore.clearPhotoList();
	router.push('/booth');
};

watch(
	() => route.query.selectedImage,
	(newImage) => {
		if (newImage) {
			selectedImage.value = decodeURIComponent(newImage);
		}
	},
	{ immediate: true },
);
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="close-btn">
				<button
					class="close"
					@click="navigateTo('main')"
				>
					X
				</button>
			</div>

			<div class="booth-content-main">
				<BoothBack class="booth-camera-box">
					<div class="selected-template-area">
						<div class="selected-template">
							<div class="template-images">
								<div
									v-for="image in imagesToShow"
									:key="image"
									class="image-wrapper"
									@click="selectImage(image)"
								>
									<img
										:src="image"
										:class="{
											selected: selectedImage === image,
										}"
										alt="Template Image"
									/>
								</div>
							</div>
							<div class="box-footer">
								<button @click="goToPrevious">이전</button>
								<button
									@click="goToNext"
									:disabled="!selectedImage"
								>
									다음
								</button>
							</div>
						</div>
					</div>
				</BoothBack>

				<BoothBack class="booth-select-box">
					<div class="select-box">
						<div class="select-text-box">
							<div>템플릿 선택</div>
						</div>
						<div class="select-temp-box">
							<div class="temp-area">
								<div
									v-for="template in templates"
									:key="template.text"
									class="array-area"
								>
									<button
										class="array-button"
										@click="selectTemplate(template)"
									>
										{{ template.text }}
									</button>
								</div>
							</div>
						</div>
					</div>
				</BoothBack>
			</div>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
@import '@/assets/css/boothsSelectTemp.css';
</style>
