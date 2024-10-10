<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
	src: String,
	alt: String,
});

const isFullscreen = ref(false);
const scale = ref(1);
const isDragging = ref(false);
const isDragMode = ref(false);
const startX = ref(0);
const startY = ref(0);
const translateX = ref(0);
const translateY = ref(0);
const isMouseOver = ref(false);

const imageStyle = computed(() => ({
	transform: `scale(${scale.value}) translate(${translateX.value}px, ${translateY.value}px)`,
	cursor: isDragMode.value
		? isDragging.value
			? 'url(@/assets/img/app/hoverCursorIcon.png) 5 5, grabbing'
			: 'url(@/assets/img/app/hoverCursorIcon.png) 5 5, grab'
		: isMouseOver.value
		? 'url(@/assets/img/app/hoverCursorIcon.png) 5 5, pointer'
		: 'url(@/assets/img/app/hoverCursorIcon.png) 5 5, pointer',
	transition: isDragging.value ? 'none' : 'transform 0.1s ease-out',
}));

const enterFullscreen = () => {
	if (!isFullscreen.value) {
		isFullscreen.value = true;
	}
};

const exitFullscreen = () => {
	isFullscreen.value = false;
	resetZoom();
};

const toggleDragMode = () => {
	isDragMode.value = !isDragMode.value;
};

const zoomIn = () => {
	scale.value = Math.min(scale.value + 0.3, 3);
};

const zoomOut = () => {
	scale.value = Math.max(scale.value - 0.3, 1);
	if (scale.value === 1) {
		resetZoom();
	}
};

const resetZoom = () => {
	scale.value = 1;
	translateX.value = 0;
	translateY.value = 0;
	isDragMode.value = false;
};

const startDrag = (event) => {
	if (isDragMode.value && scale.value > 1) {
		event.preventDefault();
		isDragging.value = true;
		startX.value = event.clientX - translateX.value;
		startY.value = event.clientY - translateY.value;
	}
};

const drag = (event) => {
	if (isDragging.value) {
		event.preventDefault();
		translateX.value = event.clientX - startX.value;
		translateY.value = event.clientY - startY.value;
	}
};

const stopDrag = () => {
	isDragging.value = false;
};

const handleWheel = (event) => {
	if (isFullscreen.value) {
		event.preventDefault();
		if (event.deltaY < 0) {
			zoomIn();
		} else {
			zoomOut();
		}
	}
};
</script>

<template>
	<div
		:class="['zoomable-image', { fullscreen: isFullscreen }]"
		@mousedown="startDrag"
		@mousemove="drag"
		@mouseup="stopDrag"
		@mouseleave="stopDrag"
	>
		<img
			:src="src"
			:alt="alt"
			:style="imageStyle"
			@click="enterFullscreen"
			@wheel="handleWheel"
			@dragstart.prevent
		/>
		<div
			v-if="isFullscreen"
			class="zoomable-controls"
		>
			<button @click="zoomIn">+</button>
			<button @click="zoomOut">-</button>
			<button @click="resetZoom">초기화</button>
			<button @click="toggleDragMode">
				{{ isDragMode ? '드래드 취소' : '드래그' }}
			</button>
			<button @click="exitFullscreen">닫기</button>
		</div>
		<br />
	</div>
</template>

<style scoped>
.zoomable-image {
	position: relative;
	width: 100%;
	height: 100%;
	overflow: hidden;
}

.zoomable-image.fullscreen {
	position: fixed;
	top: 0;
	left: 0;
	width: 100vw;
	height: 100vh;
	background-color: rgba(0, 0, 0, 0.9);
	z-index: 1000;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: visible;
}

.zoomable-image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: contain;
	user-select: none;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.custom-cursor {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, auto;
}

.zoomable-controls {
	position: absolute;
	bottom: 20px;
	left: 50%;
	transform: translateX(-50%);
	display: flex;
	gap: 10px;
}

.zoomable-controls button {
	padding: 5px 10px;
	background-color: rgba(255, 255, 255, 0.7);
	border: none;
	border-radius: 5px;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer;
}

.zoomable-controls button:hover {
	background-color: rgba(255, 255, 255, 0.9);
}
</style>
