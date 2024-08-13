<script setup>
import { ref } from 'vue';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import TemplateComp from '@/components/template/TemplateComp.vue';
import { usePhotoStore } from '@/stores/photoStore';
import { storeToRefs } from 'pinia';

const photoStore = usePhotoStore();
const { photoList, templateList, draggedPhoto, templateColor } = storeToRefs(photoStore);
const selectedTemplate = ref(null);

const selectTemplate = (item) => {
	selectedTemplate.value = item;
};

const onDragStart = (event, photo, index) => {
	if (selectedTemplate.value) {
		draggedPhoto.value = {
			src: photo,
			index,
		};
		event.dataTransfer.effectAllowed = 'move';
	}
};

const changeColor = () => {
	templateColor.value = !templateColor.value;
};
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="template-list">
				<div
					v-for="(item, index) in templateList"
					:key="index"
					class="template-text"
				>
					<button @click="selectTemplate(item)">{{ item.row }} x {{ item.col }}</button>
				</div>
				<button @click="changeColor">{{ templateColor ? '검정색' : '흰색' }}</button>
			</div>

			<div class="booth-content-main">
				<BoothBack class="booth-camera-box">
					<TemplateComp
						v-if="selectedTemplate"
						:template="selectedTemplate"
					/>
				</BoothBack>
				<BoothBack class="booth-select-box">
					<div class="select-box">
						<div class="select-text-box">
							<div>사진 선택</div>
						</div>
						<div class="select-photo-box">
							<div
								v-for="(photo, index) in photoList"
								:key="index"
								class="photo-div"
							>
								{{ index + 1 }}
								<img
									:src="photo"
									class="photo"
									alt="사진"
									draggable="true"
									@dragstart="(event) => onDragStart(event, photo, index)"
								/>
							</div>
						</div>
					</div>
				</BoothBack>
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
	justify-content: space-around;
	width: 100%;
	height: 100%;
	max-height: 99%;
	overflow: auto;
	overflow-x: hidden;
}

.booth-camera-box {
	width: 75%;
}

.booth-select-box {
	width: 20%;
}

.select-box {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	overflow: hidden;
}

.select-text-box {
	width: 100%;
	height: 13%;
	display: flex;
	justify-content: center;
	align-items: center;
}

.select-photo-box {
	width: 100%;
	display: flex;
	flex-direction: column;
	justify-content: flex-start;
	align-items: center;
	gap: 10px;
	overflow-y: auto;
	overflow-x: hidden;
}

.photo {
	width: 100px;
	height: 100px;
}

.template-list {
	width: 75%;
	display: flex;
	justify-content: space-around;
}

.template-text {
	button {
		width: 50px;
		height: 30px;
		border: 2px solid black;
		border-radius: 5px;
	}
}
</style>
