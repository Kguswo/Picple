<script setup>
import { ref } from 'vue';

const props = defineProps({
	board: Object,
	isOpen: Boolean,
});

const emit = defineEmits(['close', 'delete']);

const isDropdownOpen = ref(false);

const closeModal = () => {
	isDropdownOpen.value = false;
	emit('close');
};

const toggleDropdown = () => {
	isDropdownOpen.value = !isDropdownOpen.value;
};

const deleteBoard = () => {
	emit('delete');
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
		v-if="isOpen"
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
						<img
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
		width: 100%;
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
</style>
