<script setup>
import { usePhotoStore } from '@/stores/photoStore';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';

const props = defineProps({
	template: Object,
});

const photoStore = usePhotoStore();
const { draggedPhoto, templateColor } = storeToRefs(photoStore);
const droppedPhotos = ref([]);

const backgroundColor = computed(() => (templateColor.value ? 'white' : 'black'));
const otherColor = computed(() => (templateColor.value ? 'black' : 'white'));

const onDrop = (event, index) => {
	event.preventDefault();
	if (draggedPhoto.value) {
		droppedPhotos.value[index] = draggedPhoto.value;
		draggedPhoto.value = null;
	}
};
</script>

<template>
	<div
		class="template-container"
		:style="{ width: template.width, height: template.height, backgroundColor }"
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
				:style="{ border: `1px solid ${otherColor}` }"
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
}

.template-inner {
	flex: 1;
	display: grid;
	place-items: center;
}

.template-div {
	width: 200px;
	height: 120px;
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
