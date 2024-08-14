<script setup>
import { usePhotoStore } from '@/stores/photoStore';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import html2canvas from 'html2canvas';
import { alertResult } from '@/api/baseApi';

const props = defineProps({
	template: Object,
});

const emit = defineEmits(['screenshot']);

const photoStore = usePhotoStore();
const { draggedPhoto, backgroundColor, otherColor } = storeToRefs(photoStore);
const droppedPhotos = ref([]);
const templateDiv = ref(null);

const photoWidth = 300;
const photoHeight = 200;
const interval = 20;
const logoHeight = 40;
const templateWidth = computed(() => props.template.col * (photoWidth + interval));
const templateHeight = computed(() => props.template.row * (photoHeight + interval) + logoHeight);

const onDrop = (event, index) => {
	event.preventDefault();
	if (draggedPhoto.value) {
		droppedPhotos.value[index] = draggedPhoto.value;
		draggedPhoto.value = null;
	}
};

const screenshot = async () => {
	try {
		const canvas = await html2canvas(templateDiv.value, { useCORS: true });
		const dataUrl = canvas.toDataURL('image/png');
		const blob = dataURLtoBlob(dataUrl);

		const formData = new FormData();
		formData.append('file', blob, 'image.png');
		return formData;
	} catch (error) {
		alertResult(false, '사진 저장 중 오류가 발생하였습니다.');
	}
};

const dataURLtoBlob = (dataURL) => {
	const [header, data] = dataURL.split(',');
	const mimeString = header.split(':')[1].split(';')[0];
	const byteString = atob(data);
	const ab = new ArrayBuffer(byteString.length);
	const ia = new Uint8Array(ab);
	for (let i = 0; i < byteString.length; i++) {
		ia[i] = byteString.charCodeAt(i);
	}
	return new Blob([ab], { type: mimeString });
};

defineExpose({
	screenshot,
});
</script>

<template>
	<div
		class="template-container"
		:style="{
			width: `${templateWidth}px`,
			height: `${templateHeight}px`,
			backgroundColor,
			border: `1px solid gray`,
		}"
		ref="templateDiv"
	>
		<div
			class="template-inner"
			:style="{
				gridTemplateColumns: `repeat(${template.col}, 1fr)`,
			}"
		>
			<div
				v-for="index in template.row * template.col"
				class="template-div"
				:style="{ border: `1px solid ${otherColor}`, width: `${photoWidth}px`, height: `${photoHeight}px` }"
				@drop="onDrop($event, index)"
				@dragover.prevent
			>
				<img
					v-if="droppedPhotos[index]"
					:src="droppedPhotos[index].src"
					class="photo"
					draggable="true"
					@dragstart="onDragStartFromTemplate($event, index)"
				/>
				<span
					v-else
					class="placeholder"
					:style="{ color: otherColor }"
					>여기에 사진을 놓으세요</span
				>
			</div>
		</div>
		<div class="template-logo">
			<img
				src="@/assets/img/mainView/picpleLogo.png"
				alt="logo"
			/>
		</div>
	</div>
</template>

<style scoped>
.template-container {
	display: flex;
	flex-direction: column;
	height: 100%;
	margin: 0 auto;
}

.template-inner {
	flex: 1;
	display: grid;
	place-items: center;
}

.template-div {
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
}

.photo {
	object-fit: cover;
	width: 100%;
	height: 100%;
}

.template-logo {
	flex-shrink: 0;
	height: 40px;
	display: flex;
	justify-content: center;

	img {
		height: 100%;
		width: 100px;
	}
}
</style>
