<script setup>
import { defineProps, defineEmits, watch, ref } from 'vue';
import { calendarContentApi, calendarDailyListApi } from '@/api/calendarApi';
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

watch(
	() => props.selectedDate,
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

const closeModal = () => {
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
				<div class="header-title">
					{{ selectedDate }}
				</div>
				<span
					class="close"
					@click="closeModal"
					>&times;</span
				>
			</div>
			<div class="modal-body">
				<div
					v-if="dailyList.length > 0"
					class="photo-container"
				>
					<button
						class="nav-button prev-button"
						@click="prevPhoto"
					>
						<img
							src="@/assets/img/calendar/arrow-left.png"
							alt="Previous"
						/>
					</button>
					<div class="current-photo">
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
						class="nav-button next-button"
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
.modal {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 500;
}

.modal-content {
	padding: 20px;
	background-color: #fefefe;
	margin: 10vh auto;
	border: 1px solid #888;
	width: 50%;
	height: 80%;
	border-radius: 10px;
}

.modal-header {
	height: 10%;
	padding-bottom: 10px;
	border-bottom: 4px solid black;
	display: flex;
	align-items: center;
}

.header-title {
	width: 100%;
	font-size: 2vw;
	font-weight: bold;
	text-align: center;
}

.close {
	color: #aaa;
	float: right;
	font-size: 40px;
	line-height: 28px;
	font-weight: bold;
	height: 28px;
}

.close:hover,
.close:focus {
	color: black;
	text-decoration: none;
	cursor: pointer;
}

.modal-body {
	height: 90%;
}

.photo-container {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.current-photo {
	height: 90%;
	display: flex;
	align-items: center;
	text-align: center;
	justify-content: center;
	flex-wrap: wrap;

	img {
		height: 90%;
		width: 95%;
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
		width: calc(100% - 30px); /* 아이콘 공간을 위해 조정 */
		padding: 10px;
		border: none;
		border-bottom: 1px solid black;
		resize: none;
	}

	.description:focus {
		outline: none;
		border-bottom: 2px solid black;
	}
}

.nav-button {
	width: 10%;
	background: none;
	border: none;

	img {
		height: 50px;
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
