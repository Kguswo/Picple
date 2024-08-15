<script setup>
import { onUnmounted, ref } from 'vue';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import TemplateComp from '@/components/template/TemplateComp.vue';
import { usePhotoStore } from '@/stores/photoStore';
import { storeToRefs } from 'pinia';
import { alertChoose, alertConfirm, alertResult } from '@/api/baseApi';
import { savePhotoApi } from '@/api/photoApi';
import { useRouter } from 'vue-router';
import { useBoothStore } from '@/stores/boothStore';
import WebSocketService from '@/services/WebSocketService';

const router = useRouter();
const photoStore = usePhotoStore();
const boothStore = useBoothStore();
const { photoList, templateList, draggedPhoto, templateColor, backgroundColor, otherColor } = storeToRefs(photoStore);
const { session } = storeToRefs(boothStore);
const selectedTemplate = ref(null);
const templateDiv = ref(null);
const isLeaveSite = ref(null);

const sessionId = boothStore.getSessionInfo().sessionId;

onUnmounted(() => {
	endSession();
});

const selectTemplate = (item) => {
	selectedTemplate.value = item;
};

const onDragStart = (event, src, index) => {
	if (selectedTemplate.value) {
		draggedPhoto.value = {
			src,
			index,
		};
		event.dataTransfer.effectAllowed = 'move';
	}
};

const changeColor = () => {
	templateColor.value = !templateColor.value;
};

const screenshot = async () => {
	if (!selectedTemplate.value) {
		alertResult(false, '템플릿을 선택하지 않았습니다.');
		return;
	}
	const { value: accept } = await alertConfirm(
		'사진 촬영을 완료하시겠습니까?\n확인 버튼을 누르면 사진 부스가 종료됩니다.',
	);
	if (accept) {
		const formData = await templateDiv.value.screenshot();
		const { data } = await savePhotoApi(formData);
		if (!data.isSuccess) {
			await alertResult(false, '사진 만들기에 실패하였습니다.');
			return;
		}
		const result = await alertChoose(
			'사진 촬영이 완료되었습니다!',
			'원하는 버튼을 선택하세요.',
			'홈으로 이동',
			'캘린더로 이동',
		);
		isLeaveSite.value = true;
		if (result.isConfirmed) {
			router.push({ name: 'main' });
			return;
		}
		router.push({ name: 'calendarView' });
		return;
	}
};

const endSession = () => {
	if (session.value) {
		session.value.disconnect();
		session.value = null;
	}
	WebSocketService.close();
};
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="template-list">
				<span>템플릿 선택</span>
				<div
					v-for="(item, index) in templateList"
					:key="index"
					class="template-text"
				>
					<button
						@click="selectTemplate(item)"
						:class="{ 'selected-template-button': selectedTemplate === item }"
					>
						{{ item.row }} x {{ item.col }}
					</button>
				</div>
				<button
					@click="changeColor"
					class="color-button"
					:style="{ backgroundColor: otherColor, color: backgroundColor }"
				>
					{{ templateColor ? '검정색' : '흰색' }}
				</button>
			</div>

			<div class="booth-content-main">
				<BoothBack class="booth-camera-box booth-template-back">
					<TemplateComp
						v-if="selectedTemplate"
						:template="selectedTemplate"
						ref="templateDiv"
					/>
				</BoothBack>
				<BoothBack class="booth-select-box">
					<div class="select-box">
						<div class="select-text-box">
							<div>사진 선택</div>
						</div>
						<div class="select-photo-box">
							<div
								v-for="(photo, index) in photoList[sessionId]"
								:key="index"
								class="photo-div"
							>
								{{ index + 1 }}
								<img
									:src="photo.src"
									class="photo"
									alt="사진"
									draggable="true"
									@dragstart="(event) => onDragStart(event, photo.src, index)"
								/>
							</div>
						</div>
					</div>
				</BoothBack>
			</div>
			<div class="booth-complete">
				<button @click="screenshot">사진 완성하기</button>
			</div>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
.booth-content {
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 100%;
	height: 100%;
}

.template-list {
	width: 75%;
	display: flex;
	justify-content: space-around;
	margin: 20px 0;

	span {
		margin: auto 0;
	}

	button {
		width: 68px;
		height: 40px;
		padding: 5px 10px;
		font-size: 15px;
		border-radius: 3px;
	}
}

.template-text {
	button {
		background-color: transparent;
		border: 2px solid black;
	}

	.selected-template-button {
		background-color: #62abd9;
		color: white;
	}
}

.booth-content-main {
	display: flex;
	flex-wrap: wrap;
	align-content: center;
	justify-content: space-around;
	width: 100%;
	height: 100%;
	max-height: 99%;
	overflow: hidden;
}

.booth-camera-box {
	width: 75%;
	height: 100%;
	max-height: 100%;
	overflow: auto;
}

.booth-select-box {
	width: 20%;
	height: 100%;
	overflow: auto;
}

.select-box {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
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

.booth-complete {
	margin: 20px 0;

	button {
		width: 130px;
		height: 40px;
		padding: 5px 10px;
		font-size: 15px;
		border-radius: 3px;
	}
}
</style>
