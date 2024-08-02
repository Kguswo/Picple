<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue';
import { useRoute } from 'vue-router';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { usePhotoStore } from '@/stores/photoStore';
import html2canvas from 'html2canvas';

const photoStore = usePhotoStore();
const images = ref(photoStore.photoList);

const route = useRoute();
const selectedImage = ref(decodeURIComponent(route.query.selectedImage));

const draggedImage = ref(null);
const draggedImageIndex = ref(null);
const droppedImages = ref([]);
const templateImageRef = ref(null);
const templateImageSize = ref({ width: 0, height: 0 });
const capturedImages = ref([]);
const templateRef = ref(null);

const templateInfo = {
	temp_1x1_4x3_479x360: {
		layout: [{ x: 0.5, y: 0.5, width: 1, height: 1 }],
	},
	temp_1x2_4x5_288x360: {
		layout: [
			{ x: 0.5, y: 0.25, width: 1, height: 0.48 },
			{ x: 0.5, y: 0.75, width: 1, height: 0.48 },
		],
	},
	temp_1x3_3x4_270x360: {
		layout: [
			{ x: 0.5, y: 0.17, width: 1, height: 0.31 },
			{ x: 0.5, y: 0.5, width: 1, height: 0.31 },
			{ x: 0.5, y: 0.83, width: 1, height: 0.31 },
		],
	},
	temp_2x2_4x3_481x360: {
		layout: [
			{ x: 0.25, y: 0.25, width: 0.48, height: 0.48 },
			{ x: 0.75, y: 0.25, width: 0.48, height: 0.48 },
			{ x: 0.25, y: 0.75, width: 0.48, height: 0.48 },
			{ x: 0.75, y: 0.75, width: 0.48, height: 0.48 },
		],
	},
};

const extractSizeFromFilename = (filename) => {
	const regex = /(\d+)x(\d+)\.(?:jpg|png|jpeg)$/i;
	const match = filename.match(regex);
	if (match) {
		return {
			width: parseInt(match[1], 10),
			height: parseInt(match[2], 10),
		};
	}
	return { width: 0, height: 0 };
};

const selectedImageSize = computed(() => {
	const size = extractSizeFromFilename(selectedImage.value);
	return size;
});

const imagePositions = computed(() => {
	if (!selectedImage.value || !templateImageSize.value.width || !templateImageSize.value.height) return [];

	const template = templateInfo[selectedImage.value.split('/').pop().split('.')[0]];
	if (!template) return [];

	return template.layout.map((item) => calculateImagePosition(templateImageSize.value, item));
});

function calculateImagePosition(templateSize, layoutItem) {
	const { width: tempWidth, height: tempHeight } = templateSize;
	const { x, y, width, height } = layoutItem;

	const scaleFactor = 0.85;

	return {
		left: x * tempWidth - (width * tempWidth * scaleFactor) / 2,
		top: y * tempHeight - (height * tempHeight * scaleFactor) / 2,
		width: width * tempWidth * scaleFactor,
		height: height * tempHeight * scaleFactor,
	};
}

const onDragStart = (event, image, index) => {
	draggedImage.value = image;
	draggedImageIndex.value = index;
	event.dataTransfer.effectAllowed = 'move';
};

const onDrop = (event, index) => {
	event.preventDefault();
	if (draggedImage.value) {
		if (droppedImages.value[index]) {
			const oldImage = droppedImages.value[index];
			images.value.find((img) => img.src === oldImage.src).visible = true;
		}

		droppedImages.value[index] = draggedImage.value;
		images.value.find((img) => img.src === draggedImage.value.src).visible = false;

		draggedImage.value = null;
		draggedImageIndex.value = null;
	}
};

const onDragOver = (event) => {
	event.preventDefault();
	event.dataTransfer.dropEffect = 'move';
};

const onDragStartFromTemplate = (event, index) => {
	if (droppedImages.value[index]) {
		draggedImage.value = droppedImages.value[index];
		draggedImageIndex.value = index;
		event.dataTransfer.effectAllowed = 'move';
	}
};

const onDropBackToList = (event) => {
	event.preventDefault();
	if (!draggedImage.value) {
		return;
	}
	images.value.find((img) => img.src === draggedImage.value.src).visible = true;

	if (draggedImageIndex.value !== null) {
		droppedImages.value[draggedImageIndex.value] = null;
	}

	draggedImage.value = null;
	draggedImageIndex.value = null;
};

const onSave = async () => {
	if (templateRef.value) {
		try {
			const canvas = await html2canvas(templateRef.value);
			const imageData = canvas.toDataURL('image/png');
			capturedImages.value.push(imageData);
		} catch (error) {}
	}
};

watch(
	() => route.query.selectedImage,
	(newImage) => {
		selectedImage.value = decodeURIComponent(newImage);
		const template = templateInfo[selectedImage.value.split('/').pop().split('.')[0]];
		if (template) {
			droppedImages.value = new Array(template.layout.length).fill(null);
		}
		images.value.forEach((img) => (img.visible = true));
		nextTick(() => {
			updateTemplateDimensions();
		});
	},
);

const updateTemplateDimensions = () => {
	const size = extractSizeFromFilename(selectedImage.value);
	templateImageSize.value = {
		width: size.width,
		height: size.height,
	};
};

const onTemplateImageLoad = (event) => {
	templateImageSize.value = {
		width: event.target.naturalWidth,
		height: event.target.naturalHeight,
	};
	updateTemplateDimensions();
};

onMounted(() => {
	nextTick(() => {
		updateTemplateDimensions();
	});
});
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="booth-content-main">
				<BoothBack class="booth-temp-box">
					<div
						class="selected-image-area"
						ref="templateRef"
					>
						<img
							ref="templateImageRef"
							:src="selectedImage"
							alt="선택된 템플릿 이미지"
							class="template-image"
							@load="onTemplateImageLoad"
						/>
						<div
							v-for="(position, index) in imagePositions"
							:key="index"
							class="dropped-image-wrapper"
							:class="{ 'has-image': droppedImages[index] }"
							:style="{
								left: `${position.left}px`,
								top: `${position.top}px`,
								width: `${position.width}px`,
								height: `${position.height}px`,
							}"
							@drop="onDrop($event, index)"
							@dragover.prevent
						>
							<img
								v-if="droppedImages[index]"
								:src="droppedImages[index].src"
								class="dropped-image-content"
								draggable="true"
								@dragstart="onDragStartFromTemplate($event, index)"
							/>
							<div
								v-else
								class="debug-info"
							>
								Drop Zone {{ index + 1 }}
							</div>
						</div>
					</div>
				</BoothBack>
				<BoothBack
					class="booth-img-box"
					@dragover="onDragOver"
					@drop="onDropBackToList"
				>
					<div class="photo-list">
						<div
							v-for="(image, index) in images"
							:key="index"
							class="image-container"
							:style="{
								display: image.visible ? 'block' : 'none',
							}"
						>
							<img
								:src="image.src"
								draggable="true"
								@dragstart="(event) => onDragStart(event, image, index)"
								class="draggable-image"
							/>
						</div>
					</div>
				</BoothBack>
			</div>
			<div class="booth-footer">
				<button
					@click="onSave"
					class="save-button"
				>
					저장
				</button>
			</div>
			<div class="captured-images">
				<h3>캡처된 이미지</h3>
				<div
					v-for="(image, index) in capturedImages"
					:key="index"
					class="captured-image-container"
				>
					<img
						:src="image"
						:alt="`Captured Image ${index + 1}`"
						class="captured-image"
					/>
				</div>
			</div>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
.booth-content {
	display: flex;
	flex-direction: column;
	justify-content: space-evenly;
	align-items: center;
	width: 100%;
	height: 100%;
}

.booth-content-main {
	display: flex;
	flex-wrap: wrap;
	align-content: center;
	justify-content: space-evenly;
	width: 100%;
	height: 95%;
}

.selected-image-area {
	position: relative;
	display: inline-block;
}

.template-image {
	display: block;
	max-width: 100%;
	height: auto;
}

.dropped-image-wrapper {
	position: absolute;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
	border: 2px dashed #ccc;
}

.dropped-image-wrapper.has-image {
	border: none;
}

.dropped-image-content {
	width: 100%;
	height: 100%;
	object-fit: cover;
	cursor: grab;
}

.debug-info {
	font-size: 12px;
	color: #666;
	text-align: center;
}

.photo-list {
	display: flex;
	justify-content: center;
	align-items: center;
	flex-wrap: wrap;
	width: 100%;
	overflow-y: scroll;
	scrollbar-width: none;
}

.image-container {
	margin: 10px;
}

.draggable-image {
	width: 240px;
	height: auto;
}

.booth-footer {
	display: flex;
	justify-content: center;
	margin-top: 20px;
}

.save-button {
	padding: 10px 20px;
	background-color: #4caf50;
	color: white;
	border: none;
	border-radius: 5px;
	cursor: pointer;
	font-size: 16px;
}

.save-button:hover {
	background-color: #45a049;
}

.captured-images {
	margin-top: 20px;
	text-align: center;
}

.captured-image-container {
	margin: 10px;
	display: inline-block;
}

.captured-image {
	max-width: 200px;
	max-height: 200px;
	border: 1px solid #ddd;
	border-radius: 4px;
	padding: 5px;
}
</style>
