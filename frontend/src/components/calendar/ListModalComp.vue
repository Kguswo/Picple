<script setup>
import { defineProps, defineEmits, watch, ref } from 'vue';
import { calendarContentApi, calendarDailyListApi, calendarDeleteApi, calendarShareApi } from '@/api/calendarApi';
import Swal from 'sweetalert2';

const props = defineProps({
	visible: Boolean,
	selectedDate: String,
});

const emit = defineEmits(['close']);

const dailyList = ref([]);
const currentIndex = ref(0);
const descriptionField = ref(null);
const description = ref('');
const currentPhoto = ref(null);
const isDropdownOpen = ref(false);

watch(
	() => props.visible,
	() => getDailyList(),
);

watch(currentIndex, () => getCurrentPhoto());

const getDailyList = async () => {
	try {
		const data = await calendarDailyListApi(props.selectedDate);
		if (!data) {
			return;
		}
		dailyList.value = data.result;
		getCurrentPhoto();
	} catch (error) {}
};

const saveContent = async () => {
	const calendarId = currentPhoto.value.id;
	try {
		const data = await calendarContentApi(calendarId, description.value);
		if (!data) {
			return;
		}
		currentPhoto.value.content = description.value;
		await Swal.fire({ icon: 'success', title: '저장이 완료되었습니다.', width: 600 });
	} catch (error) {}
};

const getCurrentPhoto = () => {
	currentPhoto.value = dailyList.value[currentIndex.value];
	description.value = currentPhoto.value.content;
};

const prevPhoto = () => {
	currentIndex.value = (currentIndex.value - 1 + dailyList.value.length) % dailyList.value.length;
};

const nextPhoto = () => {
	currentIndex.value = (currentIndex.value + 1) % dailyList.value.length;
};

const toggleDropdown = () => {
	isDropdownOpen.value = !isDropdownOpen.value;
};

const downloadPhoto = () => {};

const sharePhoto = async () => {
	try {
		const data = await calendarShareApi(currentPhoto.value.id);
		if (!data) {
			return;
		}
	} catch (error) {}
};

const deletePhoto = async () => {
	try {
		const data = await calendarDeleteApi(currentPhoto.value.id);
		if (!data) {
			return;
		}
	} catch (error) {}
};

const closeModal = () => {
	isDropdownOpen.value = false;
	emit('close');
};
</script>

<template>
	<div
		v-if="visible"
		class="modal"
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
						class="dropdown-icon"
						xmlns="@/assets/icon/three-dots-vertical.svg"
						width="30"
						height="30"
						fill="black"
						viewBox="0 0 16 16"
						@click="toggleDropdown"
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
					v-if="dailyList.length > 0"
					class="photo-container"
				>
					<button
						class="nav-button"
						@click="prevPhoto"
					>
						<img
							src="@/assets/img/calendar/arrow-left.png"
							alt="Previous"
						/>
					</button>
					<div class="modal-img">
						<img
							:src="currentPhoto.photoUrl"
							alt="사진"
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
								maxlength="20"
							/>
							<button
								type="button"
								class="form-button-small"
								@click="saveContent"
							>
								저장
							</button>
						</form>
					</div>
					<button
						class="nav-button"
						@click="nextPhoto"
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

.no-photos {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100%;
	font-size: 1.5rem;
	color: red;
}
</style>
