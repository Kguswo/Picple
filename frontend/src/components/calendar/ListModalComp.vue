<script setup>
import { defineProps, defineEmits, watch, ref, onMounted, computed } from 'vue';
import {
	calendarContentApi,
	calendarDailyListApi,
	calendarDeleteApi,
	calendarDownloadApi,
	calendarShareApi,
} from '@/api/calendarApi';
import { alertConfirm, alertResult } from '@/api/baseApi';
import ZoomableImage from '@/components/zoomableImage/ZoomableImage.vue';

const props = defineProps({
	selectedDate: String,
});

const emit = defineEmits(['close']);

const dailyList = ref([]);
const currentIndex = ref(0);
const descriptionField = ref(null);
const description = ref('');
const currentPhoto = ref(null);
const isDropdownOpen = ref(false);
const leftButton = ref(null);
const rightButton = ref(null);
const modalDiv = ref(null);

const originalDescription = ref('');
const newDescription = ref('');
const isDescriptionChanged = computed(() => {
	return description.value !== originalDescription.value;
});

const isLoading = ref(true);

const getDailyList = async () => {
	isLoading.value = true;
	const { data } = await calendarDailyListApi(props.selectedDate);
	if (!data.isSuccess) {
		await alertResult(false, '조회에 실패하였습니다.');
		isLoading.value = false;
		return;
	}
	dailyList.value = data.result;
	getCurrentPhoto();
	isLoading.value = false;
};

onMounted(async () => {
	await getDailyList();
	modalDiv.value.focus();
});

watch(currentIndex, () => getCurrentPhoto());

const saveContent = async () => {
	isDropdownOpen.value = false;
	const calendarId = currentPhoto.value.id;
	const { data } = await calendarContentApi(calendarId, description.value);
	if (data.isSuccess) {
		originalDescription.value = description.value;
	} else if (!data.isSuccess) {
		await alertResult(false, '저장에 실패하였습니다.');
		return;
	}
	currentPhoto.value.content = description.value;
	await alertResult(true, '저장이 완료되었습니다.');
};

const getCurrentPhoto = () => {
	if (dailyList.value.length > 0) {
		currentPhoto.value = dailyList.value[currentIndex.value];
		description.value = currentPhoto.value.content;
		originalDescription.value = currentPhoto.value.content;
	}
};

const prevPhoto = () => {
	isDropdownOpen.value = false;
	if (dailyList.value.length > 0) {
		currentIndex.value = (currentIndex.value - 1 + dailyList.value.length) % dailyList.value.length;
	}
};

const nextPhoto = () => {
	isDropdownOpen.value = false;
	if (dailyList.value.length > 0) {
		currentIndex.value = (currentIndex.value + 1) % dailyList.value.length;
	}
};

const toggleDropdown = () => {
	isDropdownOpen.value = !isDropdownOpen.value;
};

const downloadPhoto = async () => {
	const { data } = await calendarDownloadApi(currentPhoto.value.id);
	if (!data.isSuccess) {
		await alertResult(true, '다운로드에 실패하였습니다.');
		return;
	}
	await alertResult(true, '다운로드가 완료되었습니다.');
};

const sharePhoto = async () => {
	const { value: accept } = await alertConfirm('사진을 게시판에 공유하시겠습니까?');
	if (accept) {
		const { data } = await calendarShareApi(currentPhoto.value.id);
		if (!data.isSuccess) {
			if (data.code == import.meta.env.VITE_CODE_ALREADY_SHARED) {
				await alertResult(false, '이미 공유된 사진입니다.');
				return;
			}
			await alertResult(false, '공유에 실패하였습니다.');
			return;
		}
		await alertResult(true, '공유가 완료되었습니다.');
	}
};

const deletePhoto = async () => {
	const { value: accept } = await alertConfirm('사진을 정말 삭제하시겠습니까?');
	if (accept) {
		const { data } = await calendarDeleteApi(currentPhoto.value.id);
		if (!data.isSuccess) {
			await alertResult(false, '삭제에 실패하였습니다.');
			return;
		}
		await alertResult(true, '삭제가 완료되었습니다.');
		dailyList.value.splice(currentIndex.value--, 1);
		nextPhoto();
		getCurrentPhoto();
	}
};

const handleKeyup = (event) => {
	if (dailyList.value.length === 0) {
		return;
	}
	if (event.key === 'ArrowLeft') {
		leftButton.value.click();
		return;
	}
	if (event.key === 'ArrowRight') {
		rightButton.value.click();
		return;
	}
	if (event.key === 'Escape') {
		closeModal();
		return;
	}
};

const closeModal = () => {
	isDropdownOpen.value = false;
	emit('close');
};
</script>

<template>
	<div
		class="modal"
		ref="modalDiv"
		@keyup="handleKeyup"
		tabindex="0"
	>
		<div class="modal-content">
			<div class="modal-header">
				<span
					class="close"
					@click="closeModal"
					>&times;</span
				>
				<div class="header-title">
					{{ selectedDate }}
				</div>
				<div class="dropdown">
					<svg
						@click="toggleDropdown"
						class="dropdown-icon"
						xmlns="@/assets/icon/three-dots-vertical.svg"
						width="30"
						height="30"
						fill="black"
						viewBox="0 0 16 16"
					>
						<path
							d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"
						/>
					</svg>
					<div
						class="dropdown-content"
						:class="{ 'dropdown-show': isDropdownOpen }"
					>
						<button
							class="navbar-button"
							@click="downloadPhoto"
						>
							다운로드
						</button>
						<button
							class="navbar-button"
							@click="sharePhoto"
						>
							게시판에 공유하기
						</button>
						<button
							class="navbar-button"
							@click="deletePhoto"
						>
							삭제하기
						</button>
					</div>
				</div>
			</div>
			<div class="modal-body">
				<div
					v-if="isLoading"
					class="loading-container"
				>
					<img
						src="@/assets/icon/모래시계.gif"
						alt="loading..."
						class="loading-image"
					/>
				</div>
				<div
					v-else-if="dailyList.length > 0"
					class="photo-container"
				>
					<button
						class="nav-button"
						@click="prevPhoto"
						ref="leftButton"
					>
						<img
							src="@/assets/img/calendar/arrow-left.png"
							alt="Previous"
						/>
					</button>
					<div class="modal-img">
						<ZoomableImage
							:src="currentPhoto.photoUrl"
							:alt="'Photo ' + (currentIndex + 1)"
							class="zoomable-image"
						/>
						<form
							class="description-container"
							@submit.prevent
						>
							<input
								ref="descriptionField"
								v-model="description"
								placeholder="설명을 작성하세요"
								class="description"
								maxlength="25"
							/>
							<button
								type="button"
								class="form-button-small"
								@click="saveContent"
								:disabled="!isDescriptionChanged"
							>
								저장
							</button>
						</form>
					</div>
					<button
						class="nav-button"
						@click="nextPhoto"
						ref="rightButton"
					>
						<img
							src="@/assets/img/calendar/arrow-right.png"
							alt="Next"
						/>
					</button>
				</div>

				<div
					v-else
					class="no-photos"
				>
					해당 날짜에 사진이 존재하지 않습니다.
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/modal.css';

.header-title {
	width: 100%;
	font-size: 2vw;
	font-weight: bold;
	text-align: center;
}

.description-container {
	width: 100%;
	margin-top: 20px;
	position: relative;
	display: flex;
	justify-content: center;
	align-items: center;
}

.description {
	width: 100%;
	padding: 10px;
	border: none;
	border-bottom: 1px solid black;
	resize: none;
}

.description:focus {
	outline: none;
	border-bottom: 2px solid black;
}

.nav-button {
	width: 10%;
	background: none;
	border: none;

	img {
		height: 40px;
		cursor: pointer;

		&:active {
			transform: translateY(2px);
		}
	}
}

.form-button-small {
	position: absolute;
	right: 1%;
	border: none;
	border-radius: 5px;
	padding: 5px 10px;
	font-size: 15px;
	background-color: #62abd9;
	color: white;
	cursor: pointer;
}

.form-button-small:disabled {
	background-color: #cccccc;
}

.no-photos {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100%;
	font-size: 1.5rem;
	color: red;
}

.modal-content {
	width: 100%;
	max-width: 800px;
	height: 100%;
	display: flex;
	flex-direction: column;
}

.modal-body {
	position: relative;
	width: 100%;
	height: calc(100% - 60px);
	overflow: hidden;
}

.photo-container {
	display: flex;
	justify-content: space-between;
	align-items: center;
	width: 100%;
	height: 100%;
	padding: 10px;
	box-sizing: border-box;
}

.modal-img-container {
	width: 80%;
	height: 100%;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	overflow: hidden;
}

.modal-img {
	max-width: 100%;
	max-height: calc(100% - 60px);
	object-fit: contain;
	z-index: 1;
}

.description-container {
	width: 100%;
	margin-top: 10px;
}

.loading-container {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	background-color: rgba(255, 255, 255, 0.8);
}

.loading-image {
	width: 200px;
	height: 200px;
}

.dropdown {
	position: relative;
	z-index: 10;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown-contnet {
	position: absolute;
	right: 0;
	background-color: #f9f9f9;
	min-width: 160px;
	box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
	z-index: 11;
	display: none;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown-icon {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown-content button {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown-show {
	display: block;
}

.nav-button {
	width: 10%;
	background: none;
	border: none;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.nav-button img {
	height: 40px;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.form-button-small {
	position: absolute;
	right: 1%;
	border: none;
	border-radius: 5px;
	padding: 5px 10px;
	font-size: 15px;
	background-color: #62abd9;
	color: white;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.form-button-small:disabled {
	background-color: #cccccc;
	cursor: not-allowed;
}

.close {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.dropdown-icon,
.dropdown-content button {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

.zoomable-image {
	position: relative;
	width: 100%;
	height: 100%;
	overflow: hidden;
}

.zoomable-image > img {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
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
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important; /* 커서 스타일 강제 적용 */
}
</style>
