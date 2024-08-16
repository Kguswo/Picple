<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import BoardModalComp from '@/components/board/BoardModalComp.vue';
import { boardDeleteApi, boardLikeApi } from '@/api/boardApi';
import router from '@/router';
import { alertConfirm, alertResult } from '@/api/baseApi';

const props = defineProps({
	board: Object,
	count: Number,
	paging: Object,
});

const isModalOpen = ref(false);
const imgRef = ref(null);
const emptyPath =
	'm8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15';
const fullPath = 'M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314';

const currentPath = ref(props.board.liked ? fullPath : emptyPath);

const emit = defineEmits(['observe']);

onMounted(() => {
	observer.observe(imgRef.value);
});

onUnmounted(() => {
	observer.disconnect();
});

const observer = new IntersectionObserver(
	(entries, observer) => {
		entries.forEach((entry) => {
			if (entry.isIntersecting) {
				entry.target.src = entry.target.dataset.src;
				observer.unobserve(entry.target);
				if (props.count === (props.paging.page + 1) * props.paging.size) {
					emit('observe');
				}
			}
		});
	},
	{
		threshold: 0.5,
	},
);

const toggleLike = async () => {
	const { data } = await boardLikeApi(props.board.id);
	if (!data.isSuccess) {
		await alertResult(false, '오류가 발생하였습니다.');
		return;
	}
	if (props.board.liked) {
		--props.board.hit;
		props.board.liked = false;
		currentPath.value = emptyPath;
	} else {
		++props.board.hit;
		props.board.liked = true;
		currentPath.value = fullPath;
	}
};

const deleteBoard = async () => {
	const { value: accept } = await alertConfirm('정말 게시글을 삭제하시겠습니까?');
	if (accept) {
		const { data } = await boardDeleteApi(props.board.id);
		if (!data.isSuccess) {
			if (data.code == import.meta.env.VITE_CODE_REQUEST_ERROR) {
				await alertResult(false, '게시글은 작성만 삭제할 수 있습니다.');
				return;
			}
			await alertResult(false, '게시글 삭제에 실패하였습니다.');
			return;
		}
		await alertResult(true, '게시글이 삭제되었습니다.');
		router.go(0);
	}
};

const openModal = () => {
	isModalOpen.value = true;
};

const closeModal = () => {
	isModalOpen.value = false;
};
</script>

<template>
	<div class="photo-card">
		<div
			class="thumnail"
			@click="openModal"
		>
			<img
				:data-src="board.photoUrl"
				alt="사진"
				ref="imgRef"
				@contextmenu.prevent
				@dragstart.prevent
			/>
		</div>
		<div class="content">
			<div class="like">
				<svg
					@click="toggleLike"
					class="heart"
					:class="{ 'heart-fill': board.liked }"
					width="20"
					height="20"
					fill="red"
					viewBox="0 0 16 16"
				>
					<path :d="currentPath" />
				</svg>
				<span class="like-cnt">{{ board.hit }}</span>
			</div>
		</div>
	</div>

	<BoardModalComp
		v-if="isModalOpen"
		:board="board"
		@close="closeModal"
		@delete="deleteBoard"
	/>
</template>

<style scoped>
.photo-card {
	margin: 5px;
	width: 23%;
	height: 70%;
	border: 2px solid gray;
	background-color: white;
	box-shadow: 5px 5px 5px black;

	display: flex;
	flex-direction: column;
	align-items: center;

	* {
		font-family: 'PFStardust';
		font-weight: lighter;
	}
}

.thumnail {
	width: 90%;
	height: 80%;
	margin-bottom: 5px;

	img {
		margin-top: 10px;
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
}

.content {
	display: flex;
	align-items: center;
	justify-content: end;
	width: 90%;
	height: 20%;

	.like {
		display: flex;
		align-items: center;
		justify-content: center;

		svg {
			margin-right: 7px;
		}

		.like-cnt {
			font-size: 20px;
		}
	}
}

.heart {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
	path {
		cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
	}
	&:hover {
		fill: rgba(255, 0, 0, 0.3);
		transition: all 0.5s ease;
	}

	&:active {
		transform: translateY(-5px);
		transition: transform 0.3s ease;
	}
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
</style>
