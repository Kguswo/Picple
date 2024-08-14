<script setup>
import { onMounted, ref } from 'vue';
import ZoomableImage from '../zoomableImage/ZoomableImage.vue';

const props = defineProps({
	board: Object,
});

const emit = defineEmits(['close', 'delete']);

const isDropdownOpen = ref(false);
const modalDiv = ref(null);

onMounted(() => {
	modalDiv.value.focus();
});

const handleKeyup = (event) => {
	if (event.key === 'Escape') {
		closeModal();
		return;
	}
};

const deleteBoard = () => {
	emit('delete');
};

const closeModal = () => {
	isDropdownOpen.value = false;
	emit('close');
};

const toggleDropdown = () => {
	isDropdownOpen.value = !isDropdownOpen.value;
};

const formatDate = (dateString) => {
	const date = new Date(dateString);
	const year = date.getFullYear();
	const month = date.getMonth() + 1;
	const day = date.getDate();
	const hours = String(date.getHours()).padStart(2, '0');
	const minutes = String(date.getMinutes()).padStart(2, '0');

	return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}`;
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
							v-if="isDropdownOpen"
							class="dropdown-menu navbar-button"
							@click="deleteBoard"
						>
							삭제하기
						</button>
					</div>
				</div>
			</div>
			<div class="modal-body">
				<div class="photo-container">
					<div class="modal-img">
						<ZoomableImage
							:src="board.photoUrl"
							alt="사진없음"
						/>
						<div class="modal-text">
							<span class="modal-date">작성일 {{ formatDate(board.createdAt) }}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/modal.css';

.modal-header {
	justify-content: space-between;
}

.photo-container {
	justify-content: center;
}

.modal-img {
	img {
		height: 95%;
		width: auto;
	}
}

.modal-text {
	width: 100%;
	margin-top: 20px;
	text-align: right;

	.modal-date {
		font-size: 15px;
	}
}

.button-delete {
	background-color: none;
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

.close {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}
</style>
